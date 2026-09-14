package com.cornerledger.app.ui

import com.cornerledger.app.data.CustomerEntity
import com.cornerledger.app.data.EntryEntity
import com.cornerledger.app.data.EntrySign
import com.cornerledger.app.data.EntryType
import com.cornerledger.app.voice.CustomerNameRef

enum class Screen { HOME, CUSTOMERS, DETAIL }

/** Which customer a name field currently resolves to. */
sealed class NameMatch {
    object Unresolved : NameMatch()
    data class Existing(val id: String) : NameMatch()
    object New : NameMatch()
}

data class ManualFormState(
    val query: String = "",
    val selection: NameMatch = NameMatch.Unresolved,
    val type: EntryType = EntryType.CHARGE,
    val adjustSign: EntrySign = EntrySign.PLUS,
    val amount: String = "",
    val paidNow: String = "",
)

data class VoiceSheetState(
    val rawText: String,
    val name: String,
    val matchedCustomerId: String?,
    val alternatives: List<CustomerNameRef>,
    val type: EntryType, // CHARGE or PAYMENT only — voice never supports ADJUSTMENT
    val amount: String,
)

data class EditSheetState(
    val customerId: String,
    val entryId: String,
    val type: EntryType,
    val sign: EntrySign,
    val amount: String,
)

data class ToastState(
    val message: String,
    val customerId: String?,
    val entryId: String?,
)

data class LedgerUiState(
    val screen: Screen = Screen.HOME,
    val customers: List<CustomerEntity> = emptyList(),
    val selectedCustomerId: String? = null,
    val selectedHistory: List<EntryEntity> = emptyList(),
    val menuOpen: Boolean = false,
    val manualOpen: Boolean = false,
    val listening: Boolean = false,
    val voiceSupported: Boolean = true,
    val transcript: String = "",
    val manualForm: ManualFormState = ManualFormState(),
    val voiceSheet: VoiceSheetState? = null,
    val editSheet: EditSheetState? = null,
    val toast: ToastState? = null,
) {
    val selectedCustomer: CustomerEntity?
        get() = selectedCustomerId?.let { id -> customers.find { it.id == id } }
}
