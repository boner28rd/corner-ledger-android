package com.cornerledger.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cornerledger.app.data.CustomerEntity
import com.cornerledger.app.data.EntrySign
import com.cornerledger.app.data.EntryType
import com.cornerledger.app.ui.LedgerUiState
import com.cornerledger.app.ui.NameMatch
import com.cornerledger.app.ui.theme.Accent
import com.cornerledger.app.ui.theme.Background
import com.cornerledger.app.ui.theme.ChipBackground
import com.cornerledger.app.ui.theme.Surface
import com.cornerledger.app.ui.theme.SurfaceBorder
import com.cornerledger.app.ui.theme.TextFaint
import com.cornerledger.app.ui.theme.TextMuted
import com.cornerledger.app.ui.theme.TextPrimary
import com.cornerledger.app.util.normalizeName

@Composable
fun ManualEntryCard(
    state: LedgerUiState,
    onQueryChange: (String) -> Unit,
    onPickSuggestion: (CustomerEntity) -> Unit,
    onAddNew: () -> Unit,
    onTypeChange: (EntryType) -> Unit,
    onAdjustSignChange: (EntrySign) -> Unit,
    onPaidNowChange: (String) -> Unit,
    onKeyPress: (String) -> Unit,
    onSave: () -> Unit,
) {
    val form = state.manualForm
    val query = form.query.trim()
    val suggestions = remember(query, form.selection, state.customers) {
        if (form.selection != NameMatch.Unresolved || query.isEmpty()) {
            emptyList()
        } else {
            state.customers.filter { normalizeName(it.name).contains(normalizeName(query)) }.take(4)
        }
    }
    val showAddNew = query.isNotEmpty() &&
        form.selection == NameMatch.Unresolved &&
        state.customers.none { normalizeName(it.name) == normalizeName(query) }
    val manualValid = query.isNotEmpty() && (form.amount.toDoubleOrNull() ?: 0.0) > 0.0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Surface)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(20.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Column {
            FieldLabel("Customer")
            LedgerTextField(
                value = form.query,
                onValueChange = onQueryChange,
                placeholder = "Type a name…",
                textStyle = MaterialTheme.typography.titleSmall,
                modifier = Modifier.fillMaxWidth(),
            )
            if (suggestions.isNotEmpty() || showAddNew) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(ChipBackground)
                        .border(1.dp, SurfaceBorder, RoundedCornerShape(14.dp)),
                ) {
                    suggestions.forEach { customer ->
                        SuggestionRow(text = customer.name, onClick = { onPickSuggestion(customer) })
                    }
                    if (showAddNew) {
                        AddNewRow(query = form.query, onClick = onAddNew)
                    }
                }
            }
        }

        Column {
            FieldLabel("Type")
            SegmentedRow {
                SegmentedOption("Charge", form.type == EntryType.CHARGE) { onTypeChange(EntryType.CHARGE) }
                SegmentedOption("Payment", form.type == EntryType.PAYMENT) { onTypeChange(EntryType.PAYMENT) }
                SegmentedOption("Adjust", form.type == EntryType.ADJUSTMENT) { onTypeChange(EntryType.ADJUSTMENT) }
            }
        }

        if (form.type == EntryType.ADJUSTMENT) {
            SegmentedRow {
                SegmentedOption("+ Add to balance", form.adjustSign == EntrySign.PLUS) { onAdjustSignChange(EntrySign.PLUS) }
                SegmentedOption("− Remove from balance", form.adjustSign == EntrySign.MINUS) { onAdjustSignChange(EntrySign.MINUS) }
            }
        }

        if (form.type == EntryType.CHARGE) {
            Column {
                FieldLabel("Paid now (optional)")
                LedgerTextField(
                    value = form.paidNow,
                    onValueChange = onPaidNowChange,
                    placeholder = "£0.00",
                    numeric = true,
                    textStyle = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        AmountDisplay(amountText = form.amount)

        Keypad(onKeyPress = onKeyPress)

        Button(
            onClick = onSave,
            enabled = manualValid,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Accent,
                contentColor = Background,
                disabledContainerColor = ChipBackground,
                disabledContentColor = TextFaint,
            ),
        ) {
            Text("Save entry", style = MaterialTheme.typography.titleSmall)
        }
    }
}

@Composable
fun AmountDisplay(amountText: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Background)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(14.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Amount", style = MaterialTheme.typography.labelMedium, color = TextMuted)
        Text(
            text = if (amountText.isEmpty()) "£0.00" else "£$amountText",
            style = MaterialTheme.typography.displaySmall,
            color = TextPrimary,
        )
    }
}

@Composable
private fun SuggestionRow(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
        color = TextPrimary,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
    )
}

@Composable
private fun AddNewRow(query: String, onClick: () -> Unit) {
    Text(
        text = "+ Add \"$query\" as new customer",
        style = MaterialTheme.typography.labelLarge,
        color = Accent,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
    )
}
