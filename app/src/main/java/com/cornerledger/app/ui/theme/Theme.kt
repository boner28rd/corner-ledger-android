package com.cornerledger.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// The prototype is dark-only by design (a shop-till app used indoors); we always
// apply the dark palette regardless of system theme.
private val CornerLedgerColorScheme = darkColorScheme(
    primary = Accent,
    onPrimary = Background,
    secondary = AccentLight,
    onSecondary = Background,
    background = Background,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = ChipBackground,
    onSurfaceVariant = TextMuted,
    outline = SurfaceBorder,
    error = Owed,
    onError = Background,
)

@Composable
fun CornerLedgerTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? android.app.Activity)?.window ?: return@SideEffect
            window.statusBarColor = Background.toArgb()
            window.navigationBarColor = Background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }
    MaterialTheme(
        colorScheme = CornerLedgerColorScheme,
        typography = CornerLedgerTypography,
        content = content,
    )
}
