package com.cornerledger.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.cornerledger.app.data.EntryType
import com.cornerledger.app.ui.VoiceSheetState
import com.cornerledger.app.ui.theme.Accent
import com.cornerledger.app.ui.theme.AccentSoftText
import com.cornerledger.app.ui.theme.Background
import com.cornerledger.app.ui.theme.ChipBackground
import com.cornerledger.app.ui.theme.SurfaceBorder
import com.cornerledger.app.ui.theme.TextFaint
import com.cornerledger.app.ui.theme.TextMuted
import com.cornerledger.app.ui.theme.TextPrimary
import com.cornerledger.app.voice.CustomerNameRef

@Composable
fun VoiceConfirmSheet(
    sheet: VoiceSheetState,
    onNameChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onTypeChange: (EntryType) -> Unit,
    onAlternativePick: (CustomerNameRef) -> Unit,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    val valid = sheet.name.isNotBlank() && (sheet.amount.toDoubleOrNull() ?: 0.0) > 0.0

    LedgerBottomSheetOverlay(onDismiss = onCancel) {
        Text("Confirm entry", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
        Text(
            "You said: \"${sheet.rawText}\"",
            style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
            color = TextMuted,
            modifier = Modifier.padding(top = 4.dp, bottom = 10.dp),
        )

        Column {
            FieldLabel(if (sheet.matchedCustomerId != null) "Existing customer" else "New customer")
            LedgerTextField(
                value = sheet.name,
                onValueChange = onNameChange,
                textStyle = MaterialTheme.typography.titleSmall,
                modifier = Modifier.fillMaxWidth(),
            )
            if (sheet.alternatives.isNotEmpty()) {
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    sheet.alternatives.forEach { alt ->
                        AlternativeChip(name = alt.name, onClick = { onAlternativePick(alt) })
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(top = 14.dp)) {
            FieldLabel("Type")
            SegmentedRow {
                SegmentedOption("Charge", sheet.type == EntryType.CHARGE) { onTypeChange(EntryType.CHARGE) }
                SegmentedOption("Payment", sheet.type == EntryType.PAYMENT) { onTypeChange(EntryType.PAYMENT) }
            }
        }

        Column(modifier = Modifier.padding(top = 14.dp)) {
            FieldLabel("Amount (£)")
            LedgerTextField(
                value = sheet.amount,
                onValueChange = onAmountChange,
                numeric = true,
                textStyle = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = ChipBackground, contentColor = TextPrimary),
                border = BorderStroke(1.dp, SurfaceBorder),
            ) {
                Text("Cancel", style = MaterialTheme.typography.titleSmall)
            }
            Button(
                onClick = onConfirm,
                enabled = valid,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Accent,
                    contentColor = Background,
                    disabledContainerColor = ChipBackground,
                    disabledContentColor = TextFaint,
                ),
            ) {
                Text("Confirm & save", style = MaterialTheme.typography.titleSmall)
            }
        }
    }
}

@Composable
private fun AlternativeChip(name: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(100))
            .background(ChipBackground)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(100))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp),
    ) {
        Text("$name?", color = AccentSoftText, style = MaterialTheme.typography.labelMedium)
    }
}
