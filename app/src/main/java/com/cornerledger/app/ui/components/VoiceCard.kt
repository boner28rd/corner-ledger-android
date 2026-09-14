package com.cornerledger.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornerledger.app.ui.theme.Accent
import com.cornerledger.app.ui.theme.AccentSoftText
import com.cornerledger.app.ui.theme.Background
import com.cornerledger.app.ui.theme.ChipBackground
import com.cornerledger.app.ui.theme.Surface
import com.cornerledger.app.ui.theme.SurfaceBorder
import com.cornerledger.app.ui.theme.TextFaint
import com.cornerledger.app.ui.theme.TextMuted
import com.cornerledger.app.ui.theme.TextPrimary

@Composable
fun VoiceCard(
    listening: Boolean,
    voiceSupported: Boolean,
    transcript: String,
    onMicClick: () -> Unit,
    onTranscriptChange: (String) -> Unit,
    onParseClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Surface)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(20.dp))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        MicButton(listening = listening, onClick = onMicClick)
        Text(
            text = when {
                listening -> "Listening…"
                voiceSupported -> "Tap to speak"
                else -> "Tap to speak (or type below)"
            },
            style = MaterialTheme.typography.titleSmall,
            color = TextPrimary,
        )
        Text(
            text = "e.g. \"Jane, payment, 30\" or \"Tom, account, 15\"",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
            textAlign = TextAlign.Center,
        )
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            LedgerTextField(
                value = transcript,
                onValueChange = onTranscriptChange,
                placeholder = "Or type a command…",
                modifier = Modifier.fillMaxWidth(),
            )
            Button(
                onClick = onParseClick,
                enabled = transcript.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ChipBackground,
                    contentColor = AccentSoftText,
                    disabledContainerColor = ChipBackground,
                    disabledContentColor = TextFaint,
                ),
            ) {
                Text("Parse command", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun MicButton(listening: Boolean, onClick: () -> Unit) {
    Box(contentAlignment = Alignment.Center) {
        if (listening) {
            val transition = rememberInfiniteTransition(label = "mic-pulse")
            val t by transition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(tween(1400, easing = LinearEasing), RepeatMode.Restart),
                label = "pulse",
            )
            Box(
                Modifier
                    .size(72.dp)
                    .scale(1f + 0.6f * t)
                    .clip(CircleShape)
                    .background(Accent.copy(alpha = 0.45f * (1f - t))),
            )
        }
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Accent)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Text(if (listening) "■" else "🎙", fontSize = 26.sp, color = Background)
        }
    }
}
