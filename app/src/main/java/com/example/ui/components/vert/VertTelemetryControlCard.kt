package com.example.ui.components.vert

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.vert.VertEngine
import com.example.data.vert.VertEngineConfig
import com.example.data.vert.VertFrameTelemetry
import com.example.data.vert.VertReframeMode

/**
 * Componente nativo VERT: HUD de telemetría y selector dinámico sin fricción.
 * Permite alternar entre los modos de reencuadre vertical inteligente (9:16, 4:5, 1:1, Dual),
 * ver la latencia edge en tiempo real (<1ms), el tracking del sujeto y el anclaje VMSB.
 */
@Composable
fun VertTelemetryControlCard(
    config: VertEngineConfig,
    telemetry: VertFrameTelemetry,
    onConfigChange: (VertEngineConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF090D1A)),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                listOf(Color(0xFF38BDF8), Color(0xFF818CF8), Color(0xFF10B981))
            ),
            width = 1.2.dp
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("vert_telemetry_control_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header con insignia VERT NATIVO
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Color(0xFF0284C7), Color(0xFF6366F1)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("V", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "VERT© Native Engine",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF10B981).copy(alpha = 0.2f))
                                    .border(0.8.dp, Color(0xFF10B981), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "ZERO-FRICTION",
                                    fontSize = 7.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF34D399),
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                        Text(
                            text = "Vertical Edge Real-Time Transformation & Telemetry",
                            fontSize = 9.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                // Switch de Autotracking
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = if (config.autoTrackingEnabled) "AUTO-TRACK" else "MANUAL",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = if (config.autoTrackingEnabled) Color(0xFF38BDF8) else Color(0xFF64748B)
                    )
                    Switch(
                        checked = config.autoTrackingEnabled,
                        onCheckedChange = { checked ->
                            onConfigChange(config.copy(autoTrackingEnabled = checked))
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF38BDF8),
                            checkedTrackColor = Color(0xFF0284C7).copy(alpha = 0.4f),
                            uncheckedThumbColor = Color(0xFF64748B),
                            uncheckedTrackColor = Color(0xFF1E293B)
                        ),
                        modifier = Modifier
                            .scale(0.75f)
                            .testTag("vert_autotrack_toggle")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Selector de Modos de Encuadre Vertical
            Text(
                text = "MODO DE ENCUADRE VERTICAL",
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                VertReframeMode.values().forEach { mode ->
                    val isSelected = config.mode == mode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color(0xFF0284C7).copy(alpha = 0.25f) else Color(0xFF0F172A))
                            .border(
                                width = if (isSelected) 1.2.dp else 0.8.dp,
                                color = if (isSelected) Color(0xFF38BDF8) else Color(0xFF1E293B),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                onConfigChange(config.copy(mode = mode))
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                            .padding(vertical = 8.dp, horizontal = 4.dp)
                            .testTag("vert_mode_${mode.name.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = mode.iconEmoji, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = mode.title.split(" ").firstOrNull() ?: "",
                                fontSize = 9.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Cuadrícula de Métricas de Telemetría Edge en Vivo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF030712))
                    .border(0.8.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Métrica 1: Latencia Edge
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "LATENCIA EDGE",
                        fontSize = 7.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B),
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = String.format("%.2f ms", telemetry.computeLatencyMs),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF34D399),
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Métrica 2: Desplazamiento X
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "PAN OFFSET X",
                        fontSize = 7.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B),
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = String.format("%.1f%%", telemetry.horizontalCropOffset * 100f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF38BDF8),
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Métrica 3: Confianza de Sujeto
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "CONFIANZA POI",
                        fontSize = 7.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B),
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = String.format("%.1f%%", telemetry.focusConfidence * 100f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFA855F7),
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Métrica 4: Firma Berne / C2PA
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "FIRMA C2PA",
                        fontSize = 7.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B),
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = telemetry.c2paHashShort,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFBBF24),
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Toggles auxiliares: Guía de encuadre & Suavizado cinemático
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AssistChip(
                        onClick = {
                            onConfigChange(config.copy(showGuideOverlay = !config.showGuideOverlay))
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        },
                        label = {
                            Text(
                                text = if (config.showGuideOverlay) "Guías Visibles" else "Guías Ocultas",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (config.showGuideOverlay) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                modifier = Modifier.size(13.dp),
                                tint = if (config.showGuideOverlay) Color(0xFF38BDF8) else Color(0xFF64748B)
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (config.showGuideOverlay) Color(0xFF0F172A) else Color(0xFF030712)
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (config.showGuideOverlay) Color(0xFF38BDF8) else Color(0xFF1E293B)
                        ),
                        modifier = Modifier.testTag("vert_toggle_guides_chip")
                    )

                    AssistChip(
                        onClick = {
                            val nextSmoothing = if (config.smoothingFactor > 0.8f) 0.5f else 0.9f
                            onConfigChange(config.copy(smoothingFactor = nextSmoothing))
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        },
                        label = {
                            Text(
                                text = "Suavizado: ${(config.smoothingFactor * 100).toInt()}%",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                modifier = Modifier.size(13.dp),
                                tint = Color(0xFFFBBF24)
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFF0F172A)),
                        border = BorderStroke(
                            width = 1.dp,
                            color = Color(0xFF1E293B)
                        ),
                        modifier = Modifier.testTag("vert_smoothing_chip")
                    )
                }

                // Indicador de Estado del Motor
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF34D399))
                    )
                    Text(
                        text = telemetry.trackingStatus,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF34D399),
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

/**
 * Capa de superposición (Overlay) que se coloca sobre el visor del reproductor
 * para dibujar el marco de encuadre VERT 9:16 y la zona de anclaje de sujeto con cero latencia.
 */
@Composable
fun VertFramingGuideOverlay(
    config: VertEngineConfig,
    telemetry: VertFrameTelemetry,
    modifier: Modifier = Modifier
) {
    if (!config.showGuideOverlay) return

    val pulseAlpha by rememberInfiniteTransition(label = "vert_pulse").animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "vert_alpha"
    )

    Canvas(modifier = modifier.fillMaxSize().testTag("vert_framing_guide_canvas")) {
        val w = size.width
        val h = size.height

        // Dimensión del recuadro vertical 9:16
        val targetAspect = config.mode.targetAspect
        val boxWidth = (h * targetAspect).coerceAtMost(w)
        val boxHeight = h

        // Desplazamiento horizontal centrado en el sujeto detectado
        val maxOffset = (w - boxWidth).coerceAtLeast(0f)
        val leftX = maxOffset * telemetry.horizontalCropOffset

        // 1. Zonas oscurecidas fuera del encuadre (Letterbox lateral dinámico)
        // Izquierda
        if (leftX > 0f) {
            drawRect(
                color = Color.Black.copy(alpha = 0.65f),
                topLeft = Offset(0f, 0f),
                size = Size(leftX, h)
            )
        }
        // Derecha
        val rightX = leftX + boxWidth
        if (rightX < w) {
            drawRect(
                color = Color.Black.copy(alpha = 0.65f),
                topLeft = Offset(rightX, 0f),
                size = Size(w - rightX, h)
            )
        }

        // 2. Borde del visor VERT 9:16
        drawRoundRect(
            color = Color(0xFF38BDF8).copy(alpha = pulseAlpha),
            topLeft = Offset(leftX, 0f),
            size = Size(boxWidth, boxHeight),
            cornerRadius = CornerRadius(12f, 12f),
            style = Stroke(
                width = 3.5f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(24f, 12f), 0f)
            )
        )

        // 3. Retícula de regla de tercios vertical dentro del recuadro
        val oneThirdY = boxHeight / 3f
        val twoThirdsY = boxHeight * 2f / 3f
        val oneThirdX = leftX + (boxWidth / 3f)
        val twoThirdsX = leftX + (boxWidth * 2f / 3f)

        // Líneas horizontales de tercio
        drawLine(
            color = Color.White.copy(alpha = 0.2f),
            start = Offset(leftX, oneThirdY),
            end = Offset(leftX + boxWidth, oneThirdY),
            strokeWidth = 1.2f
        )
        drawLine(
            color = Color.White.copy(alpha = 0.2f),
            start = Offset(leftX, twoThirdsY),
            end = Offset(leftX + boxWidth, twoThirdsY),
            strokeWidth = 1.2f
        )

        // Líneas verticales de tercio
        drawLine(
            color = Color.White.copy(alpha = 0.2f),
            start = Offset(oneThirdX, 0f),
            end = Offset(oneThirdX, boxHeight),
            strokeWidth = 1.2f
        )
        drawLine(
            color = Color.White.copy(alpha = 0.2f),
            start = Offset(twoThirdsX, 0f),
            end = Offset(twoThirdsX, boxHeight),
            strokeWidth = 1.2f
        )

        // 4. Marca de mira del centro de sujeto cinemático (POI Reticle)
        val centerX = leftX + (boxWidth * 0.5f)
        val centerY = boxHeight * 0.42f // Centro focal de rostro/sujeto
        val reticleRadius = 20f

        drawCircle(
            color = Color(0xFFFBBF24).copy(alpha = 0.9f),
            center = Offset(centerX, centerY),
            radius = reticleRadius,
            style = Stroke(width = 2.5f)
        )
        drawCircle(
            color = Color(0xFFFBBF24),
            center = Offset(centerX, centerY),
            radius = 3.5f
        )
    }
}
