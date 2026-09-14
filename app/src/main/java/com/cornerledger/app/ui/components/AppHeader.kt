package com.cornerledger.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornerledger.app.ui.theme.ChipBackground
import com.cornerledger.app.ui.theme.TextPrimary

@Composable
fun HeaderIconButton(onClick: () -> Unit, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .size(36.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(ChipBackground)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@Composable
fun BackIcon() {
    Text("←", color = TextPrimary, fontSize = 18.sp)
}

@Composable
fun MenuIcon() {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        repeat(3) {
            Box(
                Modifier
                    .width(16.dp)
                    .height(2.dp)
                    .background(TextPrimary, RoundedCornerShape(2.dp)),
            )
        }
    }
}

@Composable
fun HomeHeader(onMenu: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .padding(top = 14.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text("Corner Ledger", style = MaterialTheme.typography.titleLarge)
        HeaderIconButton(onClick = onMenu) { MenuIcon() }
    }
}

@Composable
fun BackHeader(title: String, onBack: () -> Unit, onMenu: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .padding(top = 14.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            HeaderIconButton(onClick = onBack) { BackIcon() }
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        HeaderIconButton(onClick = onMenu) { MenuIcon() }
    }
}
