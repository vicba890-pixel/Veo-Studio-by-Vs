package com.example.ui.components.theme

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.DarkThemeConfig
import com.example.ui.theme.LocalThemeController

/**
 * Diálogo de Configuración de Tema Material 3:
 * - Selección de Modo Claro / Modo Oscuro / Sistema
 * - Conmutador de Color Dinámico (Monet Engine)
 * - Muestra interactiva de paleta activa en tiempo real
 */
@Composable
fun ThemeSettingsDialog(
    onDismissRequest: () -> Unit
) {
    val themeController = LocalThemeController.current
    val currentConfig = themeController.darkThemeConfig
    val isDynamicEnabled = themeController.dynamicColor
    val isDynamicSupported = themeController.isDynamicColorSupported
    val currentColorScheme = MaterialTheme.colorScheme

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("theme_settings_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = "Ajustes de Tema",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Tema & Apariencia",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Material Design 3 Theme Provider",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                // Modo de Tema (Claro / Oscuro / Sistema)
                Text(
                    text = "Modo de Visualización",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DarkThemeConfig.values().forEach { config ->
                        val isSelected = currentConfig == config
                        val (icon, title) = when (config) {
                            DarkThemeConfig.LIGHT -> Pair(Icons.Default.LightMode, "Claro")
                            DarkThemeConfig.DARK -> Pair(Icons.Default.DarkMode, "Oscuro")
                            DarkThemeConfig.FOLLOW_SYSTEM -> Pair(Icons.Default.BrightnessAuto, "Sistema")
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(68.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { themeController.onSetDarkThemeConfig(config) }
                                .testTag("theme_mode_option_${config.name.lowercase()}"),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(
                                1.5.dp,
                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = title,
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Color Dinámico (Monet)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.tertiaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Color Dinámico",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (isDynamicSupported) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                                    ) {
                                        Text(
                                            text = if (isDynamicSupported) "Android 12+" else "No soportado",
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 9.sp,
                                            color = if (isDynamicSupported) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                }
                                Text(
                                    text = "Armoniza los colores con el fondo de pantalla del dispositivo",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp,
                                    lineHeight = 14.sp
                                )
                            }
                        }

                        Switch(
                            checked = isDynamicEnabled,
                            onCheckedChange = { themeController.onSetDynamicColor(it) },
                            enabled = isDynamicSupported,
                            modifier = Modifier.testTag("dynamic_color_switch")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Muestra de Paleta Activa M3
                Text(
                    text = "Paleta Activa Material 3",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ColorSwatchPill("Primary", currentColorScheme.primary, currentColorScheme.onPrimary, Modifier.weight(1f))
                    ColorSwatchPill("Secondary", currentColorScheme.secondary, currentColorScheme.onSecondary, Modifier.weight(1f))
                    ColorSwatchPill("Tertiary", currentColorScheme.tertiary, currentColorScheme.onTertiary, Modifier.weight(1f))
                    ColorSwatchPill("Surface", currentColorScheme.surface, currentColorScheme.onSurface, Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismissRequest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("theme_settings_close_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Aceptar",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorSwatchPill(
    name: String,
    bg: Color,
    onBg: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(8.dp),
        color = bg,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = name,
                color = onBg,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}
