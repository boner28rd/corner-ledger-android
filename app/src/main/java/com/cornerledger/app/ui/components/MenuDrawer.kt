package com.cornerledger.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.cornerledger.app.ui.Screen
import com.cornerledger.app.ui.theme.MenuActiveBg
import com.cornerledger.app.ui.theme.ScrimColor
import com.cornerledger.app.ui.theme.Surface
import com.cornerledger.app.ui.theme.SurfaceBorder
import com.cornerledger.app.ui.theme.TextFaint
import com.cornerledger.app.ui.theme.TextPrimary

@Composable
fun MenuDrawer(
    activeScreen: Screen,
    onHomeClick: () -> Unit,
    onCustomersClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(Modifier.fillMaxSize()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(ScrimColor)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onDismiss,
                    ),
            )
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .fillMaxWidth(0.74f)
                    .background(Surface)
                    .border(1.dp, SurfaceBorder)
                    .padding(horizontal = 18.dp, vertical = 26.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    "MENU",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextFaint,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                )
                MenuItem(icon = "🏠", label = "Home", active = activeScreen == Screen.HOME, onClick = onHomeClick)
                MenuItem(
                    icon = "👥",
                    label = "Customers",
                    active = activeScreen == Screen.CUSTOMERS || activeScreen == Screen.DETAIL,
                    onClick = onCustomersClick,
                )
            }
        }
    }
}

@Composable
private fun MenuItem(icon: String, label: String, active: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (active) MenuActiveBg else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 13.dp, horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(icon, style = MaterialTheme.typography.titleSmall)
        Text(label, color = TextPrimary, style = MaterialTheme.typography.titleSmall)
    }
}
