package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
    darkColorScheme(
        primary = Emerald500,
        onPrimary = Color.Black,
        primaryContainer = Emerald800,
        onPrimaryContainer = Emerald100,
        secondary = Gold400,
        onSecondary = Color.Black,
        secondaryContainer = Gold600,
        onSecondaryContainer = Gold100,
        tertiary = Emerald100,
        background = CanvasDark,
        onBackground = TextPrimaryDark,
        surface = SurfaceDark,
        onSurface = TextPrimaryDark,
        surfaceVariant = SurfaceVariantDark,
        onSurfaceVariant = TextSecondaryDark,
        outline = OutlineDark
    )

private val LightColorScheme =
    lightColorScheme(
        primary = Emerald800,
        onPrimary = Color.White,
        primaryContainer = Emerald50,
        onPrimaryContainer = Emerald900,
        secondary = Gold600,
        onSecondary = Color.White,
        secondaryContainer = Gold100,
        onSecondaryContainer = Color(0xFF5A3E00),
        tertiary = Emerald700,
        background = CanvasLight,
        onBackground = TextPrimaryLight,
        surface = SurfaceLight,
        onSurface = TextPrimaryLight,
        surfaceVariant = SurfaceVariantLight,
        onSurfaceVariant = TextSecondaryLight,
        outline = OutlineLight
    )

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent emerald currency branding
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }
            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
