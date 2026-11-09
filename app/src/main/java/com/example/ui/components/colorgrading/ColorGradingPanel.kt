package com.example.ui.components.colorgrading

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.colorgrading.CinematicLutPreset
import com.example.data.colorgrading.ColorGradingPresets
import com.example.data.colorgrading.ColorGradingState

/**
 * Panel de Color Grading profesional con presets de LUTs cinemáticos y controles manuales
 * de Brillo, Contraste, Saturación y Temperatura.
 */
@Composable
fun ColorGradingPanel(
    state: ColorGradingState,
    onStateChange: (ColorGradingState) -> Unit,
    onResetNeutral: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("color_grading_panel_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF090D1A)),
        border = BorderStroke(
            1.2.dp,
            Brush.horizontalGradient(
                listOf(Color(0xFFF59E0B), Color(0xFFEC4899), Color(0xFF38BDF8))
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header del Panel
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFFF59E0B), Color(0xFFD97706))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ColorLens,
                            contentDescription = "Color Grading",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "COLOR GRADING STUDIO",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                color = Color.White
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFF59E0B).copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = state.selectedLut.name.take(16),
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFFFBBF24)
                                )
                            }
                        }
                        Text(
                            text = "LUTs Cinemáticos 35mm & Ajustes Manuales en Tiempo Real",
                            fontSize = 10.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Botón Split A/B
                    IconButton(
                        onClick = { onStateChange(state.copy(isSplitPreviewActive = !state.isSplitPreviewActive)) },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (state.isSplitPreviewActive) Color(0xFF38BDF8).copy(alpha = 0.25f) else Color(0xFF1E293B))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Compare,
                            contentDescription = "Comparación A/B",
                            tint = if (state.isSplitPreviewActive) Color(0xFF38BDF8) else Color(0xFF94A3B8),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Botón Reset
                    IconButton(
                        onClick = onResetNeutral,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B))
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "Restablecer",
                            tint = Color(0xFFE2E8F0),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ==========================================
            // SECCIÓN 1: CARROUSEL DE PRESETS CINEMÁTICOS
            // ==========================================
            Text(
                text = "PRESETS CINEMÁTICOS (LUTS 35MM)",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF94A3B8)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ColorGradingPresets.ALL_PRESETS.forEach { lut ->
                    val isSelected = state.selectedLut.id == lut.id
                    LutPresetChip(
                        lut = lut,
                        isSelected = isSelected,
                        onClick = {
                            onStateChange(
                                state.copy(
                                    selectedLut = lut,
                                    brightness = lut.defaultBrightness,
                                    contrast = lut.defaultContrast,
                                    saturation = lut.defaultSaturation,
                                    temperature = lut.defaultTemperature
                                )
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ==========================================
            // SECCIÓN 2: DESLIZADORES MANUALES PRECISOS
            // ==========================================
            Text(
                text = "AJUSTES MANUALES DE COLOR",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF94A3B8)
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Slider 1: Brillo
            ColorSliderRow(
                title = "Brillo",
                icon = Icons.Default.WbSunny,
                value = state.brightness,
                valueRange = -0.5f..0.5f,
                valueDisplay = "${if (state.brightness >= 0) "+" else ""}${(state.brightness * 100).toInt()}%",
                accentColor = Color(0xFFFBBF24),
                onValueChange = { onStateChange(state.copy(brightness = it)) },
                onReset = { onStateChange(state.copy(brightness = 0.0f)) }
            )

            // Slider 2: Contraste
            ColorSliderRow(
                title = "Contraste",
                icon = Icons.Default.Contrast,
                value = state.contrast,
                valueRange = 0.5f..1.8f,
                valueDisplay = String.format("%.2fx", state.contrast),
                accentColor = Color(0xFF38BDF8),
                onValueChange = { onStateChange(state.copy(contrast = it)) },
                onReset = { onStateChange(state.copy(contrast = 1.0f)) }
            )

            // Slider 3: Saturación
            ColorSliderRow(
                title = "Saturación",
                icon = Icons.Default.Palette,
                value = state.saturation,
                valueRange = 0.0f..2.0f,
                valueDisplay = "${(state.saturation * 100).toInt()}%",
                accentColor = Color(0xFFEC4899),
                onValueChange = { onStateChange(state.copy(saturation = it)) },
                onReset = { onStateChange(state.copy(saturation = 1.0f)) }
            )

            // Slider 4: Temperatura
            ColorSliderRow(
                title = "Temperatura",
                icon = Icons.Default.Thermostat,
                value = state.temperature,
                valueRange = -1.0f..1.0f,
                valueDisplay = when {
                    state.temperature > 0.05f -> "+${(state.temperature * 100).toInt()} Cálido"
                    state.temperature < -0.05f -> "${(state.temperature * 100).toInt()} Frío"
                    else -> "Neutro 5600K"
                },
                accentColor = if (state.temperature >= 0) Color(0xFFF97316) else Color(0xFF06B6D4),
                onValueChange = { onStateChange(state.copy(temperature = it)) },
                onReset = { onStateChange(state.copy(temperature = 0.0f)) }
            )
        }
    }
}

@Composable
private fun LutPresetChip(
    lut: CinematicLutPreset,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(135.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag("lut_preset_chip_${lut.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF1E293B) else Color(0xFF0F172A)
        ),
        border = BorderStroke(
            if (isSelected) 1.5.dp else 0.8.dp,
            if (isSelected) Color(0xFFFBBF24) else Color(0xFF334155)
        )
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Barra de gradiente
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(18.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Brush.horizontalGradient(lut.gradientColors))
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(lut.iconEmoji, fontSize = 12.sp)
                Text(
                    text = lut.name,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                    color = if (isSelected) Color(0xFFFBBF24) else Color.White,
                    maxLines = 1
                )
            }
            Text(
                text = lut.subtitle,
                fontSize = 8.5.sp,
                color = Color(0xFF94A3B8),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ColorSliderRow(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    valueDisplay: String,
    accentColor: Color,
    onValueChange: (Float) -> Unit,
    onReset: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.width(115.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(15.dp))
            Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE2E8F0))
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = accentColor,
                activeTrackColor = accentColor,
                inactiveTrackColor = Color(0xFF1E293B)
            ),
            modifier = Modifier.weight(1f)
        )

        Row(
            modifier = Modifier.width(75.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = valueDisplay,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = accentColor
            )
            IconButton(onClick = onReset, modifier = Modifier.size(20.dp)) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Reset",
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(11.dp)
                )
            }
        }
    }
}
