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
    primary = Color(0xFFA0C9FF),
    onPrimary = Color(0xFF00325F),
    primaryContainer = Color(0xFF004887),
    onPrimaryContainer = SleekBlueContainer,
    secondary = Color(0xFFA0D49D),
    onSecondary = Color(0xFF0B3910),
    secondaryContainer = SleekGreenSecondary,
    onSecondaryContainer = SleekGreenContainer,
    tertiary = SleekGreenSecondary,
    background = SleekBackgroundDark,
    surface = SleekSurfaceDark,
    surfaceVariant = SleekSurfaceVariantDark,
    onBackground = SleekOnSurfaceDark,
    onSurface = SleekOnSurfaceDark,
    onSurfaceVariant = SleekOnSurfaceVariantDark,
    outline = Color(0xFF44474E)
)

private val LightColorScheme = lightColorScheme(
    primary = SleekBluePrimary,
    onPrimary = SleekBlueOnPrimary,
    primaryContainer = SleekBlueContainer,
    onPrimaryContainer = SleekBlueOnContainer,
    secondary = SleekGreenSecondary,
    onSecondary = Color.White,
    secondaryContainer = SleekGreenContainer,
    onSecondaryContainer = SleekGreenOnContainer,
    tertiary = SleekGreenSecondary,
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
