package com.cornerledger.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cornerledger.app.ui.theme.AccentLight
import com.cornerledger.app.ui.theme.ChipBackground
import com.cornerledger.app.ui.theme.TextPrimary
import com.cornerledger.app.ui.theme.ToastBorder

@Composable
fun SaveToast(message: String, onEditClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(ChipBackground)
            .border(1.dp, ToastBorder, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            message,
            color = TextPrimary,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.weight(1f),
        )
        Text(
            "Edit",
            color = AccentLight,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.clickable(onClick = onEditClick).padding(start = 10.dp),
        )
    }
}
