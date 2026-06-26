package com.pabloisla.mipostulacion.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = IndigoDark,
    onPrimary = FondoOscuro,
    primaryContainer = IndigoOscuro,
    onPrimaryContainer = IndigoContainer,
    secondary = Aqua,
    onSecondary = FondoOscuro,
    secondaryContainer = SuperficieAltaOscura,
    onSecondaryContainer = TextoPrincipalDark,
    tertiary = Ambar,
    onTertiary = FondoOscuro,
    tertiaryContainer = SuperficieAltaOscura,
    onTertiaryContainer = Ambar,
    background = FondoOscuro,
    onBackground = TextoPrincipalDark,
    surface = SuperficieOscura,
    onSurface = TextoPrincipalDark,
    surfaceVariant = SuperficieAltaOscura,
    onSurfaceVariant = TextoSecundarioDark,
    surfaceContainer = SuperficieAltaOscura,
    surfaceContainerHigh = Color(0xFF2D2B40),
    surfaceContainerLow = Color(0xFF181725),
    outline = BordeOscuro,
    outlineVariant = BordeOscuro,
    error = EstadoRechazado,
    onError = Color.White,
    errorContainer = Color(0xFF4A2323),
    onErrorContainer = Color(0xFFFFB4AB)
)

private val LightColorScheme = lightColorScheme(
    primary = Indigo,
    onPrimary = Color.White,
    primaryContainer = IndigoContainer,
    onPrimaryContainer = IndigoOscuro,
    secondary = Aqua,
    onSecondary = Color.White,
    secondaryContainer = AquaContainer,
    onSecondaryContainer = Color(0xFF065048),
    tertiary = Ambar,
    onTertiary = Color.White,
    tertiaryContainer = AmbarContainer,
    onTertiaryContainer = Color(0xFF7A4B05),
    background = FondoClaro,
    onBackground = TextoPrincipal,
    surface = SuperficieClara,
    onSurface = TextoPrincipal,
    surfaceVariant = SuperficieAltaClara,
    onSurfaceVariant = TextoSecundario,
    surfaceContainer = SuperficieAltaClara,
    surfaceContainerHigh = Color(0xFFEAE7F8),
    surfaceContainerLow = Color(0xFFFBFAFE),
    outline = BordeClaro,
    outlineVariant = BordeClaro,
    error = EstadoRechazado,
    onError = Color.White,
    errorContainer = ErrorContainerSuave,
    onErrorContainer = Color(0xFF8C1D1A)
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
        shapes = MiPostulacionShapes,
        content = content
    )
}
