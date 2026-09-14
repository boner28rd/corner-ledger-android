package com.cornerledger.app.ui

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cornerledger.app.CornerLedgerApplication
import com.cornerledger.app.data.CustomerEntity
import com.cornerledger.app.data.EntryEntity
import com.cornerledger.app.data.EntrySign
import com.cornerledger.app.data.EntrySource
import com.cornerledger.app.data.EntryType
import com.cornerledger.app.data.LedgerRepository
import com.cornerledger.app.voice.CustomerNameRef
import com.cornerledger.app.voice.SpeechRecognizerController
import com.cornerledger.app.voice.VoiceCommandParser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
class LedgerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LedgerRepository = (application as CornerLedgerApplication).repository

    private data class TransientState(
        val screen: Screen = Screen.HOME,
        val selectedCustomerId: String? = null,
        val menuOpen: Boolean = false,
        val manualOpen: Boolean = false,
        val listening: Boolean = false,
        val voiceSupported: Boolean = true,
        val transcript: String = "",
        val manualForm: ManualFormState = ManualFormState(),
        val voiceSheet: VoiceSheetState? = null,
        val editSheet: EditSheetState? = null,
        val toast: ToastState? = null,
    )

    private val transientState = MutableStateFlow(TransientState())

    private val customersFlow = repository.observeCustomers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val selectedHistoryFlow = transientState
        .map { it.selectedCustomerId }
        .distinctUntilChanged()
        .flatMapLatest { id -> if (id == null) flowOf(emptyList()) else repository.observeHistory(id) }

    val uiState: StateFlow<LedgerUiState> = combine(
        transientState,
        customersFlow,
        selectedHistoryFlow,
    ) { transient, customers, history ->
        LedgerUiState(
            screen = transient.screen,
            customers = customers,
            selectedCustomerId = transient.selectedCustomerId,
            selectedHistory = history,
            menuOpen = transient.menuOpen,
            manualOpen = transient.manualOpen,
            listening = transient.listening,
            voiceSupported = transient.voiceSupported,
            transcript = transient.transcript,
            manualForm = transient.manualForm,
            voiceSheet = transient.voiceSheet,
            editSheet = transient.editSheet,
            toast = transient.toast,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LedgerUiState())

    private var toastJob: Job? = null

    private val speech: SpeechRecognizerController by lazy {
        SpeechRecognizerController(
            context = getApplication<Application>(),
            onPartialResult = { text -> transientState.update { it.copy(transcript = text) } },
            onFinalResult = { text ->
                transientState.update { it.copy(transcript = text) }
                parseAndOpenSheet(text)
            },
            onListeningChanged = { listening -> transientState.update { it.copy(listening = listening) } },
        ).also { controller -> transientState.update { s -> s.copy(voiceSupported = controller.isSupported) } }
    }

    init {
        // Force eager creation so `voiceSupported` reflects reality before the user ever taps the mic.
        speech
    }

    // ---- navigation ----

    fun openMenu() = transientState.update { it.copy(menuOpen = true) }
    fun closeMenu() = transientState.update { it.copy(menuOpen = false) }

    fun goHome() = transientState.update {
        it.copy(screen = Screen.HOME, menuOpen = false, selectedCustomerId = null)
    }

    fun openCustomers() = transientState.update {
        it.copy(screen = Screen.CUSTOMERS, menuOpen = false, selectedCustomerId = null)
    }

    fun openDetail(customerId: String) = transientState.update {
        it.copy(screen = Screen.DETAIL, selectedCustomerId = customerId)
    }

    fun toggleManual() = transientState.update { it.copy(manualOpen = !it.manualOpen) }

    // ---- manual entry ----

    fun onManualQueryChange(query: String) = transientState.update {
        it.copy(manualForm = it.manualForm.copy(query = query, selection = NameMatch.Unresolved))
    }

    fun pickManualCustomer(customer: CustomerEntity) = transientState.update {
        it.copy(manualForm = it.manualForm.copy(query = customer.name, selection = NameMatch.Existing(customer.id)))
    }

    fun addNewManual() = transientState.update {
        it.copy(manualForm = it.manualForm.copy(selection = NameMatch.New))
    }

    fun setManualType(type: EntryType) = transientState.update {
        it.copy(manualForm = it.manualForm.copy(type = type))
    }

    fun setManualAdjustSign(sign: EntrySign) = transientState.update {
        it.copy(manualForm = it.manualForm.copy(adjustSign = sign))
    }

    fun onManualPaidNowChange(value: String) = transientState.update {
        it.copy(manualForm = it.manualForm.copy(paidNow = value))
    }

    fun pressManualKey(key: String) = transientState.update { s ->
        val amt = s.manualForm.amount
        val newAmt = when (key) {
            "back" -> amt.dropLast(1)
            "." -> if (amt.contains('.')) amt else "$amt."
            else -> {
                val decIdx = amt.indexOf('.')
                if (decIdx != -1 && amt.length - decIdx > 2) amt else amt + key
            }
        }
        s.copy(manualForm = s.manualForm.copy(amount = newAmt))
    }

    fun saveManual() {
        val form = transientState.value.manualForm
        val name = form.query.trim()
        val amount = form.amount.toDoubleOrNull() ?: 0.0
        if (name.isEmpty() || amount <= 0.0) return
        val customerId = (form.selection as? NameMatch.Existing)?.id

        viewModelScope.launch {
            val result = repository.applyEntry(
                customerId = customerId,
                name = name,
                type = form.type,
                amount = amount,
                sign = form.adjustSign,
                paidNow = form.paidNow.toDoubleOrNull(),
                source = EntrySource.MANUAL,
            )
            transientState.update {
                it.copy(manualForm = ManualFormState())
            }
            showToast(result.message, result.customerId, result.lastEntryId)
        }
    }

    // ---- voice ----

    fun toggleListening() {
        if (transientState.value.listening) {
            speech.stop()
        } else {
            transientState.update { it.copy(transcript = "") }
            speech.start()
        }
    }

    fun onTranscriptChange(text: String) = transientState.update { it.copy(transcript = text) }

    fun parseTranscript() = parseAndOpenSheet(transientState.value.transcript)

    private fun parseAndOpenSheet(raw: String) {
        val customerRefs = customersFlow.value.map { CustomerNameRef(it.id, it.name) }
        val parsed = VoiceCommandParser.parse(raw, customerRefs) ?: return
        transientState.update {
            it.copy(
                voiceSheet = VoiceSheetState(
                    rawText = parsed.rawText,
                    name = parsed.name,
                    matchedCustomerId = parsed.matchedCustomerId,
                    alternatives = parsed.alternatives,
                    type = parsed.type,
                    amount = String.format(Locale.UK, "%.2f", parsed.amount),
                ),
            )
        }
    }

    fun onVoiceNameChange(name: String) = transientState.update {
        it.voiceSheet?.let { v -> it.copy(voiceSheet = v.copy(name = name, matchedCustomerId = null)) } ?: it
    }

    fun onVoiceAmountChange(amount: String) = transientState.update {
        it.voiceSheet?.let { v -> it.copy(voiceSheet = v.copy(amount = amount)) } ?: it
    }

    fun setVoiceType(type: EntryType) = transientState.update {
        it.voiceSheet?.let { v -> it.copy(voiceSheet = v.copy(type = type)) } ?: it
    }

    fun pickVoiceAlternative(candidate: CustomerNameRef) = transientState.update {
        it.voiceSheet?.let { v ->
            it.copy(voiceSheet = v.copy(name = candidate.name, matchedCustomerId = candidate.id, alternatives = emptyList()))
        } ?: it
    }

    fun cancelVoice() = transientState.update { it.copy(voiceSheet = null, transcript = "") }

    fun confirmVoice() {
        val v = transientState.value.voiceSheet ?: return
        val name = v.name.trim()
        val amount = v.amount.toDoubleOrNull() ?: 0.0
        if (name.isEmpty() || amount <= 0.0) return

        viewModelScope.launch {
            val result = repository.applyEntry(
                customerId = v.matchedCustomerId,
                name = name,
                type = v.type,
                amount = amount,
                sign = EntrySign.PLUS,
                source = EntrySource.VOICE,
                rawTranscript = v.rawText,
            )
            transientState.update { it.copy(voiceSheet = null, transcript = "") }
            showToast(result.message, result.customerId, result.lastEntryId)
        }
    }

    // ---- edit entry ----

    fun openEdit(customerId: String, entry: EntryEntity) = transientState.update {
        it.copy(
            editSheet = EditSheetState(
                customerId = customerId,
                entryId = entry.id,
                type = entry.type,
                sign = entry.sign,
                amount = String.format(Locale.UK, "%.2f", entry.amount),
            ),
        )
    }

    fun onEditAmountChange(amount: String) = transientState.update {
        it.editSheet?.let { e -> it.copy(editSheet = e.copy(amount = amount)) } ?: it
    }

    fun closeEdit() = transientState.update { it.copy(editSheet = null) }

    fun saveEdit() {
        val e = transientState.value.editSheet ?: return
        val amount = e.amount.toDoubleOrNull() ?: 0.0
        viewModelScope.launch {
            repository.updateEntryAmount(e.entryId, amount)
            transientState.update { it.copy(editSheet = null) }
        }
    }

    fun deleteEdit() {
        val e = transientState.value.editSheet ?: return
        viewModelScope.launch {
            repository.deleteEntry(e.entryId)
            transientState.update { it.copy(editSheet = null) }
        }
    }

    // ---- export ----

    fun exportData() {
        viewModelScope.launch {
            try {
                val json = repository.exportJson()
                val clipboard = getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("Corner Ledger data", json))
                showToast("Ledger data copied to clipboard", null, null)
            } catch (e: Exception) {
                showToast("Could not copy — clipboard blocked", null, null)
            }
        }
    }

    // ---- toast ----

    fun editToastEntry() {
        val toast = transientState.value.toast ?: return
        val customerId = toast.customerId
        val entryId = toast.entryId
        transientState.update { it.copy(toast = null) }
        if (customerId == null || entryId == null) return
        viewModelScope.launch {
            val entry = repository.getEntry(entryId) ?: return@launch
            openEdit(customerId, entry)
        }
    }

    private fun showToast(message: String, customerId: String?, entryId: String?) {
        toastJob?.cancel()
        transientState.update { it.copy(toast = ToastState(message, customerId, entryId)) }
        toastJob = viewModelScope.launch {
            delay(5_000)
            transientState.update { it.copy(toast = null) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        speech.destroy()
    }
}
