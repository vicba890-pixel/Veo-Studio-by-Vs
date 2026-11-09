package com.example.ui.components.smartcrop

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.smartcrop.SmartCropAspectRatio
import com.example.data.smartcrop.SmartCropSuggestion
import kotlin.math.sin

/**
 * Overlay visual de Smart-Cropping en tiempo real para el reproductor Media3 / ExoPlayer.
 * Dibuja la retícula del sujeto detectado por Gemini, la ventana de recorte 9:16 / 4:5,
 * y muestra un banner flotante interactivo con la recomendación de reencuadre.
 */
@Composable
fun BoxScope.GeminiSmartCropPreviewOverlay(
    suggestion: SmartCropSuggestion?,
    isAnalyzing: Boolean,
    currentPosMs: Long,
    durationMs: Long,
    onApplyAspect: (SmartCropAspectRatio) -> Unit,
    onOpenSmartCropSheet: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (suggestion == null && !isAnalyzing) return

    val infiniteTransition = rememberInfiniteTransition(label = "ReticlePulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseAlpha"
    )

    // Animación de rotación o escaneo cuando está analizando
    val scanY by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ScanLineY"
    )

    // 1. DIBUJO EN CANVAS: RETÍCULA DEL SUJETO & VENTANA DE RECORTE 9:16 / 4:5
    Canvas(
        modifier = modifier
            .fillMaxSize()
    ) {
        val w = size.width
        val h = size.height

        if (isAnalyzing) {
            // Línea de escaneo dinámico de Gemini AI
            val lineY = h * scanY
            drawLine(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.Transparent, Color(0xFF38BDF8), Color(0xFF818CF8), Color.Transparent)
                ),
                start = Offset(0f, lineY),
                end = Offset(w, lineY),
                strokeWidth = 3f
            )
            return@Canvas
        }

        if (suggestion == null) return@Canvas

        // Coordenadas calculadas en tiempo real (seguimiento cinemático en vivo)
        val baseBox = suggestion.detectedSubject.boundingBox
        val wave = if (suggestion.isRealtimeTrackingActive && durationMs > 0) {
            (sin(currentPosMs / 950.0) * 0.12f).toFloat()
        } else 0f

        val liveCenterX = (baseBox.centerX + wave).coerceIn(0.20f, 0.80f)
        val liveCenterY = baseBox.centerY

        val boxWidthPx = baseBox.width * w
        val boxHeightPx = baseBox.height * h
        val boxLeftPx = (liveCenterX * w) - (boxWidthPx / 2f)
        val boxTopPx = (liveCenterY * h) - (boxHeightPx / 2f)

        // Ventana de recorte sugerida (9:16 o 4:5)
        val targetAspect = suggestion.suggestedAspectRatio.numericAspect
        val sourceAspect = w / h.coerceAtLeast(1f)
        val cropFraction = (targetAspect / sourceAspect).coerceIn(0.15f, 1f)
        val cropWidthPx = cropFraction * w
        val cropLeftPx = (liveCenterX * w - cropWidthPx / 2f).coerceIn(0f, w - cropWidthPx)
        val cropRightPx = cropLeftPx + cropWidthPx

        // Máscara oscura fuera de la ventana de recorte
        val maskColor = Color.Black.copy(alpha = 0.45f)
        if (cropLeftPx > 0) {
            drawRect(color = maskColor, topLeft = Offset(0f, 0f), size = Size(cropLeftPx, h))
        }
        if (cropRightPx < w) {
            drawRect(color = maskColor, topLeft = Offset(cropRightPx, 0f), size = Size(w - cropRightPx, h))
        }

        // Borde de la ventana de recorte 9:16 / 4:5
        val cropColor = Color(suggestion.suggestedAspectRatio.badgeColor)
        drawRoundRect(
            color = cropColor.copy(alpha = pulseAlpha),
            topLeft = Offset(cropLeftPx, 0f),
            size = Size(cropWidthPx, h),
            cornerRadius = CornerRadius(14f, 14f),
            style = Stroke(
                width = 3f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(24f, 12f), 0f)
            )
        )

        // Bounding Box del Sujeto Detectado por Gemini (Esquinas de mira táctica)
        val cornerLen = 22f
        val reticleColor = Color(0xFFFBBF24)

        // Esquina Superior Izquierda
        drawLine(reticleColor, Offset(boxLeftPx, boxTopPx), Offset(boxLeftPx + cornerLen, boxTopPx), 3.5f)
        drawLine(reticleColor, Offset(boxLeftPx, boxTopPx), Offset(boxLeftPx, boxTopPx + cornerLen), 3.5f)

        // Esquina Superior Derecha
        drawLine(reticleColor, Offset(boxLeftPx + boxWidthPx, boxTopPx), Offset(boxLeftPx + boxWidthPx - cornerLen, boxTopPx), 3.5f)
        drawLine(reticleColor, Offset(boxLeftPx + boxWidthPx, boxTopPx), Offset(boxLeftPx + boxWidthPx, boxTopPx + cornerLen), 3.5f)

        // Esquina Inferior Izquierda
        drawLine(reticleColor, Offset(boxLeftPx, boxTopPx + boxHeightPx), Offset(boxLeftPx + cornerLen, boxTopPx + boxHeightPx), 3.5f)
        drawLine(reticleColor, Offset(boxLeftPx, boxTopPx + boxHeightPx), Offset(boxLeftPx, boxTopPx + boxHeightPx - cornerLen), 3.5f)

        // Esquina Inferior Derecha
        drawLine(reticleColor, Offset(boxLeftPx + boxWidthPx, boxTopPx + boxHeightPx), Offset(boxLeftPx + boxWidthPx - cornerLen, boxTopPx + boxHeightPx), 3.5f)
        drawLine(reticleColor, Offset(boxLeftPx + boxWidthPx, boxTopPx + boxHeightPx), Offset(boxLeftPx + boxWidthPx, boxTopPx + boxHeightPx - cornerLen), 3.5f)

        // Punto de anclaje focal central
        drawCircle(
            color = Color(0xFFFBBF24).copy(alpha = 0.85f),
            center = Offset(liveCenterX * w, liveCenterY * h),
            radius = 12f,
            style = Stroke(width = 2f)
        )
        drawCircle(
            color = Color(0xFFFBBF24),
            center = Offset(liveCenterX * w, liveCenterY * h),
            radius = 3.5f
        )
    }

    // 2. BANNER FLOTANTE SUPERIOR: SUGERENCIA DE GEMINI & ACCIÓN RÁPIDA
    Column(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .fillMaxWidth()
            .padding(top = 12.dp, start = 12.dp, end = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isAnalyzing) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF030712).copy(alpha = 0.90f)),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Color(0xFF38BDF8), Color(0xFF818CF8))), width = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color(0xFF38BDF8), strokeWidth = 2.dp)
                    Text(
                        text = "Gemini AI analizando sujeto y encuadre óptimo...",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE2E8F0),
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        } else if (suggestion != null) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF030712).copy(alpha = 0.92f)),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(
                        listOf(Color(suggestion.suggestedAspectRatio.badgeColor), Color(0xFF6366F1))
                    ),
                    width = 1.2.dp
                ),
                modifier = Modifier.clickable { onOpenSmartCropSheet() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(suggestion.suggestedAspectRatio.badgeColor).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Gemini AI",
                                tint = Color(suggestion.suggestedAspectRatio.badgeColor),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "SMART-CROP: ${suggestion.suggestedAspectRatio.ratioLabel}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(suggestion.suggestedAspectRatio.badgeColor)
                                )
                                Text(
                                    text = "${(suggestion.confidence * 100).toInt()}% CONF",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                            Text(
                                text = "Sujeto: ${suggestion.detectedSubject.label} • ${suggestion.suggestedAspectRatio.targetPlatformLabel}",
                                fontSize = 10.sp,
                                color = Color(0xFFCBD5E1),
                                maxLines = 1
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = { onApplyAspect(suggestion.suggestedAspectRatio) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(suggestion.suggestedAspectRatio.badgeColor),
                                contentColor = Color(0xFF022C22)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "APLICAR ${suggestion.suggestedAspectRatio.ratioLabel}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        IconButton(
                            onClick = onOpenSmartCropSheet,
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Configurar",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
