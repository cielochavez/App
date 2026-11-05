package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = lightColorScheme(
    primary = AthenaPink,
    onPrimary = PureWhite,
    primaryContainer = AthenaPinkLight,
    onPrimaryContainer = NeutralDark,
    secondary = AthenaGoldDark,
    onSecondary = PureWhite,
    tertiary = RoseBronze,
    background = PureWhite,
    onBackground = NeutralDark,
    surface = PureWhite,
    onSurface = NeutralDark,
    surfaceVariant = SilkCard,
    onSurfaceVariant = NeutralMedium,
    outline = WarmBorder
)

private val LightColorScheme = lightColorScheme(
    primary = AthenaPink,
    onPrimary = PureWhite,
    primaryContainer = AthenaPinkLight,
    onPrimaryContainer = NeutralDark,
    secondary = AthenaGoldDark,
    onSecondary = PureWhite,
    tertiary = RoseBronze,
    background = PureWhite,
    onBackground = NeutralDark,
    surface = PureWhite,
    onSurface = NeutralDark,
    surfaceVariant = SilkCard,
    onSurfaceVariant = NeutralMedium,
    outline = WarmBorder
)

@Composable
fun AthenaStyleTheme(
    darkTheme: Boolean = false, // Fondo blanco boutique activo por defecto
    dynamicColor: Boolean = false, // Mantener paleta boutique delicada consistente
    content: @Composable () -> Unit,
) {
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
