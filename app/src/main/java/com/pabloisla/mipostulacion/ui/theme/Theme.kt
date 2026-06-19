package com.pabloisla.mipostulacion.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AzulPrimarioDark,
    onPrimary = AzulFondoDark,
    primaryContainer = AzulPrimario,
    onPrimaryContainer = Color.White,
    secondary = AzulAcento,
    onSecondary = AzulFondoDark,
    secondaryContainer = AzulSuperficieDark,
    onSecondaryContainer = TextoPrincipalDark,
    background = AzulFondoDark,
    onBackground = TextoPrincipalDark,
    surface = AzulSuperficieDark,
    onSurface = TextoPrincipalDark,
    surfaceVariant = AzulSuperficieDark,
    onSurfaceVariant = TextoSecundarioDark
)

private val LightColorScheme = lightColorScheme(
    primary = AzulPrimario,
    onPrimary = Color.White,
    primaryContainer = AzulSuperficie,
    onPrimaryContainer = AzulPrimarioOscuro,
    secondary = AzulAcento,
    onSecondary = Color.White,
    secondaryContainer = AzulSuperficie,
    onSecondaryContainer = AzulPrimarioOscuro,
    background = AzulFondo,
    onBackground = TextoPrincipal,
    surface = Color.White,
    onSurface = TextoPrincipal,
    surfaceVariant = AzulSuperficie,
    onSurfaceVariant = TextoSecundario
)

@Composable
fun MiPostulacionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}