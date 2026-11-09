package com.example.ui.components.preview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.multitrack.*
import kotlin.math.sin

/**
 * Renderizador de alta fidelidad para superposiciones en tiempo real de texto, audio y efectos visuales,
 * sincronizado con el scrubber y la posición actual de reproducción.
 */
@Composable
fun MultiTrackOverlayRenderer(
    activeTextClips: List<TimelineClip>,
    activeEffectClips: List<TimelineClip>,
    activeAudioClips: List<TimelineClip> = emptyList(),
    currentPositionMs: Long = 0L,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("multi_track_overlay_renderer")
    ) {

        // ==========================================
        // 1. CAPA DE EFECTOS VISUALES Y FILTROS (FX)
        // ==========================================
        activeEffectClips.forEach { effectClip ->
            val payload = effectClip.effectPayload ?: return@forEach
            val intensity = payload.intensity.coerceIn(0f, 1f)

            when (payload.effectType) {
                EffectType.CINEMATIC_TEAL_ORANGE -> {
                    // Gradiente cinemático Teal en sombras y Ámbar en luces
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF0D9488).copy(alpha = 0.18f * intensity), // Teal
                                        Color.Transparent,
                                        Color(0xFFD97706).copy(alpha = 0.22f * intensity)  // Amber Orange
                                    )
                                )
                            )
                            .testTag("active_fx_teal_orange")
                    )
                }

                EffectType.VHS_RETRO_GLITCH -> {
                    // Raster de líneas de exploración VHS y desfase RGB
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("active_fx_vhs_glitch")
                    ) {
                        val stepY = 6.dp.toPx()
                        var y = 0f
                        while (y < size.height) {
                            drawLine(
                                color = Color.Black.copy(alpha = 0.12f * intensity),
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = 1.5f
                            )
                            y += stepY
                        }

                        // Barra glitch horizontal dinámica según timestamp
                        val glitchY = ((currentPositionMs * 0.7f) % size.height)
                        drawRect(
                            color = Color(0xFF06B6D4).copy(alpha = 0.25f * intensity),
                            topLeft = Offset(0f, glitchY),
                            size = Size(size.width, 14.dp.toPx())
                        )
                    }

                    // OSD Retro VHS en esquina superior izquierda
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "PLAY ▶ VHS SP ${(currentPositionMs / 1000) % 60}s",
                            color = Color(0xFF22C55E).copy(alpha = 0.85f * intensity),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                EffectType.FILM_GRAIN_NOIR -> {
                    // Ruido y viñeta estilo película 35mm
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("active_fx_film_grain")
                    ) {
                        // Viñeta perimetral suave
                        drawRect(
                            brush = Brush.radialGradient(
                                colors = listOf(Color.Transparent, Color(0xFF090D16).copy(alpha = 0.45f * intensity)),
                                center = Offset(size.width / 2f, size.height / 2f),
                                radius = size.width * 0.7f
                            )
                        )
                        // Puntos procedimentales de grano
                        val grainCount = (size.width * size.height / 8000).toInt().coerceIn(40, 200)
                        val seed = currentPositionMs / 50
                        for (i in 0 until grainCount) {
                            val px = (sin((seed + i * 13).toDouble()) * 0.5 + 0.5) * size.width
                            val py = (sin((seed + i * 29).toDouble()) * 0.5 + 0.5) * size.height
                            drawCircle(
                                color = Color.White.copy(alpha = 0.15f * intensity),
                                radius = 1.2f,
                                center = Offset(px.toFloat(), py.toFloat())
                            )
                        }
                    }
                }

                EffectType.VIGNETTE_FOCUS -> {
                    // Viñeta circular oscura de enfoque
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.55f * intensity)
                                    )
                                )
                            )
                            .testTag("active_fx_vignette")
                    )
                }

                EffectType.BLOOM_DREAMY -> {
                    // Halo dorado de difusión suave
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFFFEF08A).copy(alpha = 0.16f * intensity),
                                        Color(0xFFFDE047).copy(alpha = 0.08f * intensity),
                                        Color(0xFFEAB308).copy(alpha = 0.14f * intensity)
                                    )
                                )
                            )
                            .testTag("active_fx_bloom")
                    )
                }

                EffectType.NEON_CYBERPUNK -> {
                    // Bordes de neón magenta y cyan
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(
                                width = 2.dp,
                                brush = Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFFE11D48).copy(alpha = 0.7f * intensity),
                                        Color(0xFF06B6D4).copy(alpha = 0.7f * intensity)
                                    )
                                ),
                                shape = androidx.compose.ui.graphics.RectangleShape
                            )
                            .testTag("active_fx_cyberpunk")
                    )
                }

                EffectType.SEPIA_VINTAGE -> {
                    // Tono sepia nostálgico
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF78350F).copy(alpha = 0.20f * intensity))
                            .testTag("active_fx_sepia")
                    )
                }
            }
        }

        // ==========================================
        // 2. CAPA DE SUPERPOSICIONES DE TEXTO
        // ==========================================
        activeTextClips.forEach { textClip ->
            val payload = textClip.textPayload ?: return@forEach
            val progress = textClip.progressAt(currentPositionMs)

            // Cálculo de animación suave de entrada y salida
            val clipAlpha = when (payload.animation) {
                TextAnimation.FADE_IN -> {
                    val fadeInFactor = (progress * 5f).coerceIn(0f, 1f)
                    val fadeOutFactor = ((1f - progress) * 5f).coerceIn(0f, 1f)
                    fadeInFactor * fadeOutFactor
                }
                TextAnimation.PULSE -> {
                    0.82f + 0.18f * sin(progress * Math.PI.toFloat() * 6f).coerceIn(-1f, 1f)
                }
                else -> 1f
            }

            val alignment = when (payload.position) {
                TextPosition.TOP -> Alignment.TopCenter
                TextPosition.CENTER -> Alignment.Center
                TextPosition.BOTTOM -> Alignment.BottomCenter
                TextPosition.LOWER_THIRD -> Alignment.BottomStart
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = if (payload.position == TextPosition.TOP) 16.dp else 0.dp,
                        bottom = if (payload.position == TextPosition.BOTTOM) 18.dp else if (payload.position == TextPosition.LOWER_THIRD) 14.dp else 0.dp,
                        start = if (payload.position == TextPosition.LOWER_THIRD) 14.dp else 12.dp,
                        end = 12.dp
                    ),
                contentAlignment = alignment
            ) {
                if (payload.position == TextPosition.LOWER_THIRD) {
                    // Diseño Broadcast Lower-Third con acento dual
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(payload.backgroundColorArgb).copy(alpha = clipAlpha * 0.92f))
                            .border(1.dp, Color(0xFF38BDF8).copy(alpha = clipAlpha * 0.6f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("active_text_overlay_${textClip.id}")
                    ) {
                        // Barra vertical de acento cyan
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(22.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFF38BDF8))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = payload.text,
                                color = Color(payload.textColorArgb).copy(alpha = clipAlpha),
                                fontSize = payload.fontSizeSp.sp,
                                fontWeight = if (payload.isBold) FontWeight.ExtraBold else FontWeight.Normal,
                                fontFamily = FontFamily.SansSerif
                            )
                            Text(
                                text = "VEO NATIVE SYNC LAYER",
                                color = Color(0xFF94A3B8).copy(alpha = clipAlpha * 0.8f),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                } else {
                    // Diseño estándar (Top / Center / Bottom) con cápsula estilizada
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .then(
                                if (payload.hasBackground) {
                                    Modifier
                                        .background(Color(payload.backgroundColorArgb).copy(alpha = clipAlpha * 0.88f))
                                        .border(1.dp, Color.White.copy(alpha = clipAlpha * 0.15f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                } else {
                                    Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                }
                            )
                            .testTag("active_text_overlay_${textClip.id}")
                    ) {
                        Text(
                            text = payload.text,
                            color = Color(payload.textColorArgb).copy(alpha = clipAlpha),
                            fontSize = payload.fontSizeSp.sp,
                            fontWeight = if (payload.isBold) FontWeight.Bold else FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                }
            }
        }

        // ==========================================
        // 3. CAPA DISCRETA DE ESTADO DE AUDIO ACTIVO
        // ==========================================
        if (activeAudioClips.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xE60F172A))
                    .border(0.5.dp, Color(0xFF10B981).copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 3.dp)
                    .testTag("active_audio_status_badge")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(RoundedCornerShape(2.5.dp))
                            .background(Color(0xFF10B981))
                    )
                    Text(
                        text = "AUDIO ACTIVE (${activeAudioClips.size})",
                        color = Color(0xFF6EE7B7),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
