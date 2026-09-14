package com.cornerledger.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.cornerledger.app.data.CustomerEntity
import com.cornerledger.app.ui.theme.AccentSoftText
import com.cornerledger.app.ui.theme.AvatarBackground
import com.cornerledger.app.ui.theme.Credit
import com.cornerledger.app.ui.theme.Owed
import com.cornerledger.app.ui.theme.TextFaint
import com.cornerledger.app.ui.theme.TextMuted
import com.cornerledger.app.ui.theme.TextPrimary
import com.cornerledger.app.util.formatAmount
import com.cornerledger.app.util.initialsOf

@Composable
fun CustomerRow(customer: CustomerEntity, onClick: () -> Unit) {
    val balance = customer.balance
    val balanceColor = if (balance > 0) Owed else if (balance < 0) Credit else TextMuted
    val statusLabel = if (balance > 0) "owes" else if (balance < 0) "in credit" else "settled up"
    val balanceDisplay = when {
        balance > 0 -> formatAmount(balance)
        balance < 0 -> "+" + formatAmount(balance)
        else -> formatAmount(0.0)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(AvatarBackground),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                initialsOf(customer.name),
                color = AccentSoftText,
                fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.titleSmall,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                customer.name,
                color = TextPrimary,
                fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(statusLabel, color = TextMuted, style = MaterialTheme.typography.bodySmall)
        }
        Text(balanceDisplay, color = balanceColor, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Black)
        Text("›", color = TextFaint, style = MaterialTheme.typography.titleMedium)
    }
}
