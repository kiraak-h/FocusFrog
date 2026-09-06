package com.example.focusfrog.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = FrogGreen,
    onPrimary = LightSurface,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = FrogDarkGreen,
    secondary = PondBlue,
    background = CreamBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnBackground,
    tertiary = BugAmber
)

private val DarkColorScheme = darkColorScheme(
    primary = FrogGreen,
    onPrimary = DarkBackground,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = LightPrimaryContainer,
    secondary = PondBlue,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnBackground,
    tertiary = BugAmber
)

private val NightSkyColorScheme = darkColorScheme(
    primary = FrogGreen,
    onPrimary = NightSkySurface,
    primaryContainer = NightSkyPrimaryContainer,
    onPrimaryContainer = NightSkyOnBackground,
    secondary = PondBlue,
    background = NightSkyBackground,
    onBackground = NightSkyOnBackground,
    surface = NightSkySurface,
    onSurface = NightSkyOnBackground,
    tertiary = BugAmber
)

private val RainforestColorScheme = darkColorScheme(
    primary = FrogGreen,
    onPrimary = RainforestSurface,
    primaryContainer = RainforestPrimaryContainer,
    onPrimaryContainer = LightPrimaryContainer,
    secondary = PondBlue,
    background = RainforestBackground,
    onBackground = RainforestOnBackground,
    surface = RainforestSurface,
    onSurface = RainforestOnBackground,
    tertiary = BugAmber
)

@Composable
fun FocusFrogTheme(
    equippedTheme: String = "Pond",
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        equippedTheme.contains("Night Sky", ignoreCase = true) || equippedTheme.contains("theme_night_sky", ignoreCase = true) -> NightSkyColorScheme
        equippedTheme.contains("Rainforest", ignoreCase = true) || equippedTheme.contains("theme_rainforest", ignoreCase = true) -> RainforestColorScheme
        equippedTheme.contains("Pond", ignoreCase = true) || equippedTheme.contains("theme_pond", ignoreCase = true) -> if (darkTheme) DarkColorScheme else LightColorScheme
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
