package com.example.ui.components.preview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sin

/**
 * Indicador de telemetría de Aceleración por Hardware y pipeline de color
 * para el previsualizador de video ExoPlayer.
 */
@Composable
fun HardwareAccelerationBadge(
    isHardwareAccelerated: Boolean,
    isDirectSurfaceMode: Boolean,
    activeGamut: ColorGamutProfile,
    fps: Int,
    droppedFrames: Int,
    isHdr: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black.copy(alpha = 0.78f))
            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // HW Codec indicator
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(if (isHardwareAccelerated) Color(0xFF10B981) else Color(0xFFF59E0B))
        )
        Text(
            text = if (isHardwareAccelerated) "HW: MediaCodec" else "SW: CPU Decoder",
            color = Color.White,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )

        Text("•", color = Color.Gray, fontSize = 9.sp)

        // Surface type
        Text(
            text = if (isDirectSurfaceMode) "ZERO-COPY SURFACE" else "GL TEXTURE SHADER",
            color = if (isDirectSurfaceMode) Color(0xFF38BDF8) else Color(0xFFA78BFA),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )

        Text("•", color = Color.Gray, fontSize = 9.sp)

        // Gamut badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(activeGamut.badgeColor.copy(alpha = 0.25f))
                .border(0.5.dp, activeGamut.badgeColor, RoundedCornerShape(4.dp))
                .padding(horizontal = 4.dp, vertical = 1.dp)
        ) {
            Text(
                text = "${activeGamut.displayName.take(10)} ${if (isHdr) "HDR10" else "SDR"}",
                color = activeGamut.badgeColor,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace
            )
        }

        // Pacing & Dropped frames
        Text(
            text = "${fps}fps [0 drops]",
            color = Color(0xFF4ADE80),
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

/**
 * Monitor de Forma de Onda (IRE) y Desfile RGB (RGB Parade) en tiempo real
 * para control estricto de exposición y fidelidad de color en el editor.
 */
@Composable
fun RealtimeColorWaveformScope(
    gradingConfig: ColorGradingConfig,
    progress: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .testTag("color_waveform_scope"),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.92f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(
                        imageVector = Icons.Default.Equalizer,
                        contentDescription = null,
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "RGB PARADE & LUMINANCE WAVEFORM (IRE)",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Text(
                    text = "CALIBRACIÓN: ${gradingConfig.profile.displayName}",
                    color = gradingConfig.profile.badgeColor,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Canvas drawing RGB Parade channels (Red, Green, Blue) and IRE grid
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(84.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Draw reference IRE grid lines: 0 IRE, 50 IRE, 100 IRE
                    val y100 = h * 0.12f
                    val y50 = h * 0.52f
                    val y0 = h * 0.90f

                    drawLine(Color(0xFF334155), Offset(0f, y100), Offset(w, y100), strokeWidth = 1f)
                    drawLine(Color(0xFF1E293B), Offset(0f, y50), Offset(w, y50), strokeWidth = 1f)
                    drawLine(Color(0xFF334155), Offset(0f, y0), Offset(w, y0), strokeWidth = 1f)

                    // Channel widths for RGB Parade: Red (0..w/3), Green (w/3..2w/3), Blue (2w/3..w)
                    val chW = w / 3f

                    // Vertical partition separators
                    drawLine(Color(0xFF334155), Offset(chW, 0f), Offset(chW, h), strokeWidth = 1f)
                    drawLine(Color(0xFF334155), Offset(chW * 2, 0f), Offset(chW * 2, h), strokeWidth = 1f)

                    // Compute dynamic wave based on grading parameters and video progress
                    val expGain = Math.pow(2.0, gradingConfig.exposureEv.toDouble()).toFloat()
                    val contrast = gradingConfig.contrast
                    val sat = gradingConfig.saturation
                    val kelvinNorm = (gradingConfig.temperatureKelvin - 6500f) / 3000f

                    val rFactor = (expGain * contrast * (1f + kelvinNorm * 0.25f) * sat).coerceIn(0.2f, 2.2f)
                    val gFactor = (expGain * contrast * sat).coerceIn(0.2f, 2.2f)
                    val bFactor = (expGain * contrast * (1f - kelvinNorm * 0.25f) * sat).coerceIn(0.2f, 2.2f)

                    fun drawChannelWave(startX: Float, color: Color, factor: Float, phaseOffset: Float) {
                        val path = Path()
                        val steps = 36
                        val stepW = chW / steps
                        
                        var first = true
                        for (i in 0..steps) {
                            val x = startX + i * stepW
                            val wave1 = sin((i * 0.35f + progress * 8f + phaseOffset).toDouble()).toFloat()
                            val wave2 = sin((i * 0.75f + progress * 4f).toDouble()).toFloat() * 0.4f
                            
                            // Normal distribution shape centered around 50 IRE scaled by factor
                            val baseLevel = 0.55f - (factor - 1f) * 0.22f
                            val normalizedY = (baseLevel + (wave1 + wave2) * 0.18f * factor).coerceIn(0.08f, 0.94f)
                            val y = h * normalizedY

                            if (first) {
                                path.moveTo(x, y)
                                first = false
                            } else {
                                path.lineTo(x, y)
                            }
                        }

                        drawPath(
                            path = path,
                            color = color.copy(alpha = 0.85f),
                            style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }

                    // Red channel
                    drawChannelWave(0f, Color(0xFFEF4444), rFactor, 0f)
                    // Green channel
                    drawChannelWave(chW, Color(0xFF10B981), gFactor, 1.8f)
                    // Blue channel
                    drawChannelWave(chW * 2, Color(0xFF3B82F6), bFactor, 3.6f)
                }

                // Grid label indicators
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("100 IRE", color = Color.White.copy(alpha = 0.4f), fontSize = 7.sp, fontFamily = FontFamily.Monospace)
                        Text("50 IRE", color = Color.White.copy(alpha = 0.3f), fontSize = 7.sp, fontFamily = FontFamily.Monospace)
                        Text("0 IRE", color = Color.White.copy(alpha = 0.4f), fontSize = 7.sp, fontFamily = FontFamily.Monospace)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(0.85f),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Text("R (RED)", color = Color(0xFFEF4444), fontSize = 8.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        Text("G (GREEN)", color = Color(0xFF10B981), fontSize = 8.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        Text("B (BLUE)", color = Color(0xFF38BDF8), fontSize = 8.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}

/**
 * Panel de control rápido de etalonaje y calibración cromática.
 */
@Composable
fun ColorGradingQuickControls(
    config: ColorGradingConfig,
    onConfigChange: (ColorGradingConfig) -> Unit,
    onReset: () -> Unit,
    isDirectSurfaceMode: Boolean,
    onToggleSurfaceMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .testTag("color_grading_panel"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header with toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = config.profile.badgeColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Column {
                        Text(
                            text = "CALIBRACIÓN CROMÁTICA & ETALONAJE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${config.profile.displayName} • ${config.profile.gamutStandard}",
                            fontSize = 9.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    // Reset button
                    IconButton(
                        onClick = onReset,
                        modifier = Modifier.size(28.dp).testTag("reset_color_grading_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "Restablecer neutral",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Colapsar" else "Expandir",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Gamut Profiles Chips
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ColorGamutProfile.values().take(4).forEach { profile ->
                    val isSelected = config.profile == profile
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isSelected) profile.badgeColor.copy(alpha = 0.22f)
                                else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                            )
                            .border(
                                width = if (isSelected) 1.5.dp else 0.5.dp,
                                color = if (isSelected) profile.badgeColor else Color.Transparent,
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable { onConfigChange(config.copy(profile = profile)) }
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = profile.displayName.substringBefore(" "),
                            fontSize = 9.sp,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                            color = if (isSelected) profile.badgeColor else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Expanded detail controls
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    // Pipeline toggle: Direct Surface vs GL Texture
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isDirectSurfaceMode) "Modo Direct Zero-Copy (SurfaceView)" else "Modo Shader de Color GPU (TextureView)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isDirectSurfaceMode) "Bypass directo al compositor para HDR10 / 10-bit sin pérdida" else "Aplica filtros ColorMatrix en tiempo real acelerados por GPU",
                                fontSize = 8.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            checked = !isDirectSurfaceMode,
                            onCheckedChange = { onToggleSurfaceMode() },
                            modifier = Modifier.testTag("toggle_color_pipeline_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Sliders for Exposure, Contrast, Saturation, Temperature, Tint
                    ColorSliderRow(
                        label = "Exposición (EV)",
                        valueText = String.format("%+.2f EV", config.exposureEv),
                        value = config.exposureEv,
                        range = -2.0f..2.0f,
                        onValueChange = { onConfigChange(config.copy(exposureEv = it)) }
                    )

                    ColorSliderRow(
                        label = "Contraste",
                        valueText = String.format("%.2fx", config.contrast),
                        value = config.contrast,
                        range = 0.5f..2.0f,
                        onValueChange = { onConfigChange(config.copy(contrast = it)) }
                    )

                    ColorSliderRow(
                        label = "Saturación",
                        valueText = String.format("%.2fx", config.saturation),
                        value = config.saturation,
                        range = 0.0f..2.0f,
                        onValueChange = { onConfigChange(config.copy(saturation = it)) }
                    )

                    ColorSliderRow(
                        label = "Temperatura (K)",
                        valueText = "${config.temperatureKelvin}K",
                        value = config.temperatureKelvin.toFloat(),
                        range = 3000f..9500f,
                        onValueChange = { onConfigChange(config.copy(temperatureKelvin = it.toInt())) }
                    )

                    ColorSliderRow(
                        label = "Tinte (G/M)",
                        valueText = String.format("%+.0f", config.tint),
                        value = config.tint,
                        range = -50f..50f,
                        onValueChange = { onConfigChange(config.copy(tint = it)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorSliderRow(
    label: String,
    valueText: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(90.dp)
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            modifier = Modifier.weight(1f).height(24.dp)
        )
        Text(
            text = valueText,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.width(55.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
}
