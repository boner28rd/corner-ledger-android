package com.cornerledger.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cornerledger.app.ui.theme.Accent
import com.cornerledger.app.ui.theme.AccentLight
import com.cornerledger.app.ui.theme.Background
import com.cornerledger.app.ui.theme.SegmentActiveBg
import com.cornerledger.app.ui.theme.SurfaceBorder
import com.cornerledger.app.ui.theme.TextMuted

@Composable
fun SegmentedRow(modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), content = content)
}

@Composable
fun RowScope.SegmentedOption(label: String, active: Boolean, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(vertical = 11.dp, horizontal = 4.dp),
        border = BorderStroke(1.dp, if (active) Accent else SurfaceBorder),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (active) SegmentActiveBg else Background,
            contentColor = if (active) AccentLight else TextMuted,
        ),
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge)
    }
}
