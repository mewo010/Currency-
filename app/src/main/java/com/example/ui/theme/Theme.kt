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

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF75A9F9),
    onPrimary = Color(0xFF002B73),
    primaryContainer = Color(0xFF0C3B88),
    onPrimaryContainer = Color(0xFFD6E4FC),
    secondary = SleekAmberAccent,
    onSecondary = Color(0xFF452B00),
    secondaryContainer = Color(0xFF5E3C00),
    onSecondaryContainer = Color(0xFFFFDF9E),
    tertiary = Color(0xFF4EE2A0),
    onTertiary = Color(0xFF003822),
    background = SleekBackgroundDark,
    surface = SleekSurfaceDark,
    surfaceVariant = SleekSurfaceVariantDark,
    onBackground = SleekOnSurfaceDark,
    onSurface = SleekOnSurfaceDark,
    onSurfaceVariant = SleekOnSurfaceVariantDark,
    outline = SleekOutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = SleekBluePrimary,
    onPrimary = SleekBlueOnPrimary,
    primaryContainer = SleekBlueContainer,
    onPrimaryContainer = SleekBlueOnContainer,
    secondary = SleekAmberAccent,
    onSecondary = Color.White,
    secondaryContainer = SleekAmberContainer,
    onSecondaryContainer = Color(0xFF5E3C00),
    tertiary = SleekGreenSecondary,
    onTertiary = Color.White,
    background = SleekBackgroundLight,
    surface = SleekSurfaceLight,
    surfaceVariant = SleekSurfaceVariantLight,
    onBackground = SleekOnSurfaceLight,
    onSurface = SleekOnSurfaceLight,
    onSurfaceVariant = SleekOnSurfaceVariantLight,
    outline = SleekOutlineLight
)

@Composable
fun GlobalCashTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
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
