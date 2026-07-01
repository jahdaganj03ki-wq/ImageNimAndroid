package com.imagenim.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = NVIDIAGreen,
    onPrimary = SurfaceLight,
    primaryContainer = Green80,
    secondary = GreenGrey40,
    secondaryContainer = GreenGrey80,
    surface = SurfaceLight,
    background = SurfaceLight
)

private val DarkColorScheme = darkColorScheme(
    primary = NVIDIAGreen,
    onPrimary = DarkGreen,
    primaryContainer = DarkGreen,
    secondary = GreenGrey80,
    secondaryContainer = GreenGrey40,
    surface = SurfaceDark,
    background = SurfaceDark
)

@Composable
fun ImageNimTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
