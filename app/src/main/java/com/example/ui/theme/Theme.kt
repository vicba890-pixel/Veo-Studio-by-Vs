package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

/**
 * Configuración del modo de tema (Manual Claro / Manual Oscuro / Automático Sistema).
 */
enum class DarkThemeConfig(val label: String, val description: String) {
    FOLLOW_SYSTEM("Sistema", "Se adapta automáticamente al tema del sistema"),
    LIGHT("Claro", "Fuerza el tema claro de alta fidelidad"),
    DARK("Oscuro", "Fuerza el tema oscuro cinemático")
}

/**
 * Controlador de Tema accesible a través de CompositionLocal en toda la jerarquía de la app.
 */
data class ThemeController(
    val darkThemeConfig: DarkThemeConfig,
    val isDark: Boolean,
    val dynamicColor: Boolean,
    val isDynamicColorSupported: Boolean,
    val onSetDarkThemeConfig: (DarkThemeConfig) -> Unit,
    val onToggleDarkTheme: () -> Unit,
    val onSetDynamicColor: (Boolean) -> Unit,
    val onToggleDynamicColor: () -> Unit
)

val LocalThemeController = staticCompositionLocalOf<ThemeController> {
    error("LocalThemeController no ha sido inicializado")
}

val StudioLightColorScheme = lightColorScheme(
    primary = StudioPrimary,
    onPrimary = StudioOnPrimary,
    primaryContainer = StudioPrimaryContainer,
    onPrimaryContainer = StudioOnPrimaryContainer,
    secondary = StudioSecondary,
    onSecondary = StudioOnPrimary,
    secondaryContainer = StudioSecondaryContainer,
    onSecondaryContainer = StudioOnSecondaryContainer,
    tertiary = StudioTertiary,
    onTertiary = StudioOnPrimary,
    tertiaryContainer = StudioTertiaryContainer,
    onTertiaryContainer = StudioOnTertiaryContainer,
    background = StudioBackground,
    onBackground = StudioOnBackground,
    surface = StudioSurface,
    onSurface = StudioOnSurface,
    surfaceVariant = StudioSurfaceVariant,
    onSurfaceVariant = StudioOnSurfaceVariant,
    outline = StudioOutline,
    outlineVariant = StudioSurfaceVariantAlt
)

val StudioDarkColorScheme = darkColorScheme(
    primary = StudioDarkPrimary,
    onPrimary = StudioDarkOnPrimary,
    primaryContainer = StudioDarkPrimaryContainer,
    onPrimaryContainer = StudioDarkOnPrimaryContainer,
    secondary = StudioDarkSecondary,
    onSecondary = StudioDarkOnSecondary,
    secondaryContainer = StudioDarkSecondaryContainer,
    onSecondaryContainer = StudioDarkOnSecondaryContainer,
    tertiary = StudioDarkTertiary,
    onTertiary = StudioDarkOnTertiary,
    tertiaryContainer = StudioDarkTertiaryContainer,
    onTertiaryContainer = StudioDarkOnTertiaryContainer,
    background = StudioDarkBackground,
    onBackground = StudioDarkOnBackground,
    surface = StudioDarkSurface,
    onSurface = StudioDarkOnSurface,
    surfaceVariant = StudioDarkSurfaceVariant,
    onSurfaceVariant = StudioDarkOnSurfaceVariant,
    outline = StudioDarkOutline,
    outlineVariant = StudioDarkOutlineVariant
)

// Alias retrocompatible
private val HighDensityColorScheme = StudioLightColorScheme

/**
 * Material 3 Theme Provider con soporte para Dynamic Color (Android 12+ Monet engine)
 * y conmutación manual o por sistema de Modo Oscuro / Claro.
 */
@Composable
fun MyApplicationTheme(
    darkThemeConfig: DarkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
    dynamicColor: Boolean = true,
    onSetDarkThemeConfig: (DarkThemeConfig) -> Unit = {},
    onToggleDarkTheme: () -> Unit = {},
    onSetDynamicColor: (Boolean) -> Unit = {},
    onToggleDynamicColor: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val systemInDark = isSystemInDarkTheme()
    val isDark = when (darkThemeConfig) {
        DarkThemeConfig.FOLLOW_SYSTEM -> systemInDark
        DarkThemeConfig.LIGHT -> false
        DarkThemeConfig.DARK -> true
    }

    val isDynamicColorSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    val colorScheme: ColorScheme = when {
        dynamicColor && isDynamicColorSupported -> {
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDark -> StudioDarkColorScheme
        else -> StudioLightColorScheme
    }

    val themeController = remember(darkThemeConfig, isDark, dynamicColor, isDynamicColorSupported) {
        ThemeController(
            darkThemeConfig = darkThemeConfig,
            isDark = isDark,
            dynamicColor = dynamicColor,
            isDynamicColorSupported = isDynamicColorSupported,
            onSetDarkThemeConfig = onSetDarkThemeConfig,
            onToggleDarkTheme = onToggleDarkTheme,
            onSetDynamicColor = onSetDynamicColor,
            onToggleDynamicColor = onToggleDynamicColor
        )
    }

    CompositionLocalProvider(LocalThemeController provides themeController) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

/**
 * Sobrecarga retrocompatible de MyApplicationTheme para llamadas directas que pasen booleanos.
 */
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val config = if (darkTheme) DarkThemeConfig.DARK else DarkThemeConfig.LIGHT
    MyApplicationTheme(
        darkThemeConfig = config,
        dynamicColor = dynamicColor,
        content = content
    )
}
