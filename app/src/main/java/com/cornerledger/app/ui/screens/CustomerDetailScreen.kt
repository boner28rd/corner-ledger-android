package com.cornerledger.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cornerledger.app.data.CustomerEntity
import com.cornerledger.app.data.EntryEntity
import com.cornerledger.app.ui.components.HistoryRow
import com.cornerledger.app.ui.theme.Credit
import com.cornerledger.app.ui.theme.DividerColor
import com.cornerledger.app.ui.theme.Owed
import com.cornerledger.app.ui.theme.Surface
import com.cornerledger.app.ui.theme.SurfaceBorder
import com.cornerledger.app.ui.theme.TextFaint
import com.cornerledger.app.ui.theme.TextMuted
import com.cornerledger.app.util.formatAmount

@Composable
fun CustomerDetailScreen(
    customer: CustomerEntity,
    history: List<EntryEntity>,
    onEntryClick: (EntryEntity) -> Unit,
) {
    val balance = customer.balance
    val balanceColor = if (balance > 0) Owed else if (balance < 0) Credit else TextMuted
    val balanceLabel = if (balance > 0) "Balance owed" else if (balance < 0) "In credit" else "Settled up"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp)
            .padding(top = 6.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Surface)
                .border(1.dp, SurfaceBorder, RoundedCornerShape(20.dp))
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                balanceLabel.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = TextMuted,
            )
            Text(
                formatAmount(balance),
                style = MaterialTheme.typography.headlineLarge,
                color = balanceColor,
                modifier = Modifier.padding(top = 6.dp),
            )
        }

        Text(
            "HISTORY",
            style = MaterialTheme.typography.labelLarge,
            color = TextMuted,
        )

        if (history.isEmpty()) {
            Text(
                "No entries yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextFaint,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 30.dp),
                textAlign = TextAlign.Center,
            )
        } else {
            Column {
                history.forEachIndexed { index, entry ->
                    HistoryRow(entry = entry, onClick = { onEntryClick(entry) })
                    if (index != history.lastIndex) {
                        HorizontalDivider(color = DividerColor, thickness = 1.dp)
                    }
                }
            }
        }
    }
}
