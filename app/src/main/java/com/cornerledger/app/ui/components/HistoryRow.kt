package com.cornerledger.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cornerledger.app.data.EntryEntity
import com.cornerledger.app.data.entryLabel
import com.cornerledger.app.ui.theme.Credit
import com.cornerledger.app.ui.theme.Owed
import com.cornerledger.app.ui.theme.TextMuted
import com.cornerledger.app.ui.theme.TextPrimary
import com.cornerledger.app.util.dateDisplay
import com.cornerledger.app.util.formatAmount

@Composable
fun HistoryRow(entry: EntryEntity, onClick: () -> Unit) {
    val amountColor = if (entry.delta >= 0) Owed else Credit
    val amountDisplay = (if (entry.delta >= 0) "+" else "−") + formatAmount(entry.amount)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 13.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(
                entryLabel(entry.type, entry.sign),
                color = TextPrimary,
                fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(dateDisplay(entry.createdAt), color = TextMuted, style = MaterialTheme.typography.bodySmall)
        }
        Text(amountDisplay, color = amountColor, fontWeight = FontWeight.Black, style = MaterialTheme.typography.bodyLarge)
    }
}
