package com.cornerledger.app.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cornerledger.app.ui.components.BackHeader
import com.cornerledger.app.ui.components.EditEntrySheet
import com.cornerledger.app.ui.components.HomeHeader
import com.cornerledger.app.ui.components.MenuDrawer
import com.cornerledger.app.ui.components.SaveToast
import com.cornerledger.app.ui.components.VoiceConfirmSheet
import com.cornerledger.app.ui.screens.CustomerDetailScreen
import com.cornerledger.app.ui.screens.CustomersScreen
import com.cornerledger.app.ui.screens.HomeScreen
import com.cornerledger.app.ui.theme.Background
import com.cornerledger.app.ui.theme.CornerLedgerTheme

@Composable
fun CornerLedgerApp(viewModel: LedgerViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var micPermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED,
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        micPermissionGranted = granted
        if (granted) viewModel.toggleListening()
    }

    BackHandler(
        enabled = state.screen != Screen.HOME || state.menuOpen || state.voiceSheet != null || state.editSheet != null,
    ) {
        when {
            state.editSheet != null -> viewModel.closeEdit()
            state.voiceSheet != null -> viewModel.cancelVoice()
            state.menuOpen -> viewModel.closeMenu()
            state.screen == Screen.DETAIL -> viewModel.openCustomers()
            state.screen == Screen.CUSTOMERS -> viewModel.goHome()
        }
    }

    CornerLedgerTheme {
        Box(
            Modifier
                .fillMaxSize()
                .background(Background)
                .safeDrawingPadding(),
        ) {
            Column(Modifier.fillMaxSize()) {
                when (state.screen) {
                    Screen.HOME -> HomeHeader(onMenu = viewModel::openMenu)
                    Screen.CUSTOMERS -> BackHeader("Customers", onBack = viewModel::goHome, onMenu = viewModel::openMenu)
                    Screen.DETAIL -> BackHeader(
                        title = state.selectedCustomer?.name.orEmpty(),
                        onBack = viewModel::openCustomers,
                        onMenu = viewModel::openMenu,
                    )
                }

                Box(Modifier.weight(1f)) {
                    when (state.screen) {
                        Screen.HOME -> HomeScreen(
                            state = state,
                            onMicClick = {
                                if (micPermissionGranted) {
                                    viewModel.toggleListening()
                                } else {
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            },
                            onTranscriptChange = viewModel::onTranscriptChange,
                            onParseClick = viewModel::parseTranscript,
                            onToggleManual = viewModel::toggleManual,
                            onQueryChange = viewModel::onManualQueryChange,
                            onPickSuggestion = viewModel::pickManualCustomer,
                            onAddNew = viewModel::addNewManual,
                            onTypeChange = viewModel::setManualType,
                            onAdjustSignChange = viewModel::setManualAdjustSign,
                            onPaidNowChange = viewModel::onManualPaidNowChange,
                            onKeyPress = viewModel::pressManualKey,
                            onSave = viewModel::saveManual,
                        )

                        Screen.CUSTOMERS -> CustomersScreen(
                            customers = state.customers,
                            onCustomerClick = { customer -> viewModel.openDetail(customer.id) },
                            onExportClick = viewModel::exportData,
                        )

                        Screen.DETAIL -> {
                            val customer = state.selectedCustomer
                            if (customer != null) {
                                CustomerDetailScreen(
                                    customer = customer,
                                    history = state.selectedHistory,
                                    onEntryClick = { entry -> viewModel.openEdit(customer.id, entry) },
                                )
                            }
                        }
                    }
                }
            }

            state.toast?.let { toast ->
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 18.dp),
                ) {
                    SaveToast(message = toast.message, onEditClick = viewModel::editToastEntry)
                }
            }
        }

        if (state.menuOpen) {
            MenuDrawer(
                activeScreen = state.screen,
                onHomeClick = viewModel::goHome,
                onCustomersClick = viewModel::openCustomers,
                onDismiss = viewModel::closeMenu,
            )
        }

        state.voiceSheet?.let { sheet ->
            VoiceConfirmSheet(
                sheet = sheet,
                onNameChange = viewModel::onVoiceNameChange,
                onAmountChange = viewModel::onVoiceAmountChange,
                onTypeChange = viewModel::setVoiceType,
                onAlternativePick = viewModel::pickVoiceAlternative,
                onCancel = viewModel::cancelVoice,
                onConfirm = viewModel::confirmVoice,
            )
        }

        state.editSheet?.let { sheet ->
            EditEntrySheet(
                sheet = sheet,
                onAmountChange = viewModel::onEditAmountChange,
                onDelete = viewModel::deleteEdit,
                onCancel = viewModel::closeEdit,
                onSave = viewModel::saveEdit,
            )
        }
    }
}
