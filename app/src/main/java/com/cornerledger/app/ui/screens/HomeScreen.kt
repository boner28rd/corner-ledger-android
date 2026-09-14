package com.cornerledger.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.cornerledger.app.data.CustomerEntity
import com.cornerledger.app.data.EntrySign
import com.cornerledger.app.data.EntryType
import com.cornerledger.app.ui.LedgerUiState
import com.cornerledger.app.ui.components.ManualEntryCard
import com.cornerledger.app.ui.components.VoiceCard
import com.cornerledger.app.ui.theme.TextMuted

@Composable
fun HomeScreen(
    state: LedgerUiState,
    onMicClick: () -> Unit,
    onTranscriptChange: (String) -> Unit,
    onParseClick: () -> Unit,
    onToggleManual: () -> Unit,
    onQueryChange: (String) -> Unit,
    onPickSuggestion: (CustomerEntity) -> Unit,
    onAddNew: () -> Unit,
    onTypeChange: (EntryType) -> Unit,
    onAdjustSignChange: (EntrySign) -> Unit,
    onPaidNowChange: (String) -> Unit,
    onKeyPress: (String) -> Unit,
    onSave: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp)
            .padding(top = 6.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        if (!state.manualOpen) {
            VoiceCard(
                listening = state.listening,
                voiceSupported = state.voiceSupported,
                transcript = state.transcript,
                onMicClick = onMicClick,
                onTranscriptChange = onTranscriptChange,
                onParseClick = onParseClick,
            )
        }

        Text(
            text = if (state.manualOpen) "Use voice instead" else "Enter manually instead",
            style = MaterialTheme.typography.labelLarge,
            color = TextMuted,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable(onClick = onToggleManual)
                .padding(2.dp),
        )

        if (state.manualOpen) {
            ManualEntryCard(
                state = state,
                onQueryChange = onQueryChange,
                onPickSuggestion = onPickSuggestion,
                onAddNew = onAddNew,
                onTypeChange = onTypeChange,
                onAdjustSignChange = onAdjustSignChange,
                onPaidNowChange = onPaidNowChange,
                onKeyPress = onKeyPress,
                onSave = onSave,
            )
        }
    }
}
