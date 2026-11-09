package com.example.ui.components.preview

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

/**
 * Modos de visualización del código de tiempo
 */
enum class TimecodeDisplayMode(val label: String) {
    SMPTE("SMPTE"),
    FRAMES("FRAMES"),
    MILLISECONDS("MS"),
    PERCENT("PERCENT")
}

/**
 * Sensibilidad del dial de jog / shuttle
 */
enum class ScrubSensitivity(val label: String, val framesPerStep: Int) {
    FINE_FRAME("1 FRAME", 1),
    FIVE_FRAMES("5 FRAMES", 5),
    ONE_SECOND("1 SEC", 30)
}

/**
 * Tipo de Keyframe para control y animación temporal
 */
enum class KeyframeType(
    val label: String,
    val badgeColor: Color,
    val accentColor: Color
) {
    TRANSFORM("Transform", Color(0xFFF59E0B), Color(0xFFFDE68A)), // Ámbar
    FILTER("Filter / FX", Color(0xFF8B5CF6), Color(0xFFDDD6FE)),   // Púrpura
    AUDIO("Audio", Color(0xFF10B981), Color(0xFFA7F3D0)),          // Esmeralda
    CUT("Split / Cut", Color(0xFFEF4444), Color(0xFFFECACA)),      // Rojo
    GOP("I-Frame GOP", Color(0xFF38BDF8), Color(0xFFBAE6FD))       // Celeste
}

/**
 * Modelo de Keyframe para la línea de tiempo
 */
data class TimelineKeyframe(
    val id: String = java.util.UUID.randomUUID().toString(),
    val frameIndex: Int,
    val timestampMs: Long,
    val label: String = "Keyframe",
    val type: KeyframeType = KeyframeType.TRANSFORM,
    val isAutoGop: Boolean = false
)

/**
 * Genera keyframes iniciales de demostración distribuidos a lo largo del proyecto
 */
fun generateDefaultKeyframes(totalFrames: Int, fps: Int, durationMs: Long): List<TimelineKeyframe> {
    val list = mutableListOf<TimelineKeyframe>()
    val frameDur = 1000.0 / fps.coerceAtLeast(1)

    // Keyframe 1: Inicio
    list.add(
        TimelineKeyframe(
            frameIndex = 0,
            timestampMs = 0L,
            label = "Start Transform",
            type = KeyframeType.TRANSFORM
        )
    )

    // Keyframe 2: Tercio inicial
    val k2Frame = (totalFrames * 0.28f).roundToInt().coerceIn(1, (totalFrames - 2).coerceAtLeast(1))
    list.add(
        TimelineKeyframe(
            frameIndex = k2Frame,
            timestampMs = (k2Frame * frameDur).toLong(),
            label = "Color FX Boost",
            type = KeyframeType.FILTER
        )
    )

    // Keyframe 3: Mitad / Audio
    val k3Frame = (totalFrames * 0.62f).roundToInt().coerceIn(k2Frame + 1, (totalFrames - 1).coerceAtLeast(k2Frame + 1))
    list.add(
        TimelineKeyframe(
            frameIndex = k3Frame,
            timestampMs = (k3Frame * frameDur).toLong(),
            label = "Audio Peak",
            type = KeyframeType.AUDIO
        )
    )

    return list
}

/**
 * Componente integral de navegación temporal con precisión de fotograma individual.
 *
 * Proporciona:
 * - Timeline interactivo con fotogramas estilo tira de película de 35mm
 * - Escala de regla con marcas de segundos, medios segundos y fotogramas
 * - Cabezal de reproducción magnético con aguja iluminada de alta precisión
 * - Lupa flotante HUD con código SMPTE, delta de fotogramas e identificación GOP (I/P)
 * - Rueda táctil Jog / Shuttle Dial con rotación angular real y retroalimentación háptica
 * - Micro-paso de fotogramas (-10F, -1F, +1F, +10F) y salto a extremos
 * - Sincronización bidireccional inmediata con ExoPlayer
 */
@Composable
fun FramePreciseScrubber(
    currentPositionMs: Long,
    durationMs: Long,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    fps: Int = 30,
    trimStart: Float = 0f,
    trimEnd: Float = 1f,
    onSeekTo: (Long) -> Unit,
    onPlayPauseToggle: () -> Unit,
    onStepFrame: (Int) -> Unit,
    onSpeedChange: ((Float) -> Unit)? = null,
    currentPlaybackSpeed: Float = 1.0f,
    showJogWheelInitial: Boolean = true,
    initialKeyframes: List<TimelineKeyframe>? = null,
    onKeyframesChange: ((List<TimelineKeyframe>) -> Unit)? = null,
    onKeyframeSelected: ((TimelineKeyframe) -> Unit)? = null
) {
    val haptic = LocalHapticFeedback.current
    val effectiveDuration = durationMs.coerceAtLeast(1L)
    val effectiveFps = fps.coerceIn(12, 120)
    val totalFrames = ((effectiveDuration * effectiveFps) / 1000L).toInt().coerceAtLeast(1)
    val currentFrame = ((currentPositionMs.toFloat() / effectiveDuration) * totalFrames).roundToInt().coerceIn(0, totalFrames)

    // Estados locales de interfaz
    var displayMode by remember { mutableStateOf(TimecodeDisplayMode.SMPTE) }
    var scrubSensitivity by remember { mutableStateOf(ScrubSensitivity.FINE_FRAME) }
    var showJogWheel by remember { mutableStateOf(showJogWheelInitial) }
    var isDraggingScrubber by remember { mutableStateOf(false) }
    var dragStartFrame by remember { mutableIntStateOf(0) }
    var lastHapticFrame by remember { mutableIntStateOf(-1) }

    // Estados de Zoom Horizontal Dinámico y Ventana Visible
    var zoomScale by remember { mutableFloatStateOf(1.0f) }
    var windowStartNorm by remember { mutableFloatStateOf(0f) }
    var isPinchingTimeline by remember { mutableStateOf(false) }

    // Estados de Control de Keyframes
    var isKeyframeSnappingEnabled by remember { mutableStateOf(true) }
    var selectedKeyframeType by remember { mutableStateOf(KeyframeType.TRANSFORM) }
    var showKeyframeDrawer by remember { mutableStateOf(false) }
    var keyframes by remember(totalFrames, effectiveDuration) {
        mutableStateOf(initialKeyframes ?: generateDefaultKeyframes(totalFrames, effectiveFps, effectiveDuration))
    }
    var lastSnappedKeyframeId by remember { mutableStateOf<String?>(null) }
    var snappedKeyframeLabel by remember { mutableStateOf<String?>(null) }

    // Keyframe activo en la posición actual del cabezal (tolerancia de 1 frame)
    val activeKeyframeAtPlayhead = remember(keyframes, currentFrame) {
        keyframes.find { abs(it.frameIndex - currentFrame) <= 1 }
    }

    // Auto-scroll del viewport cuando se reproduce o avanza fotograma estando en zoom
    LaunchedEffect(currentPositionMs, zoomScale) {
        if (zoomScale > 1.0f) {
            val progressNorm = (currentPositionMs.toFloat() / effectiveDuration).coerceIn(0f, 1f)
            val windowSpan = 1f / zoomScale
            val windowEndNorm = windowStartNorm + windowSpan
            val margin = windowSpan * 0.15f
            val maxStart = (1f - windowSpan).coerceAtLeast(0f)
            if (progressNorm < windowStartNorm + margin) {
                windowStartNorm = (progressNorm - margin).coerceIn(0f, maxStart)
            } else if (progressNorm > windowEndNorm - margin) {
                windowStartNorm = (progressNorm - windowSpan + margin).coerceIn(0f, maxStart)
            }
        } else {
            windowStartNorm = 0f
        }
    }

    // Formateador de tiempo según modo activo
    val formattedTimecode = remember(currentPositionMs, displayMode, currentFrame, effectiveDuration, effectiveFps) {
        when (displayMode) {
            TimecodeDisplayMode.SMPTE -> {
                val totalSeconds = (currentPositionMs / 1000).toInt()
                val hours = totalSeconds / 3600
                val minutes = (totalSeconds % 3600) / 60
                val seconds = totalSeconds % 60
                val frameWithinSecond = ((currentPositionMs % 1000) * effectiveFps / 1000).toInt()
                String.format("%02d:%02d:%02d:%02d", hours, minutes, seconds, frameWithinSecond)
            }
            TimecodeDisplayMode.FRAMES -> {
                "F #$currentFrame / $totalFrames"
            }
            TimecodeDisplayMode.MILLISECONDS -> {
                String.format("%,d ms", currentPositionMs)
            }
            TimecodeDisplayMode.PERCENT -> {
                val pct = (currentPositionMs.toFloat() / effectiveDuration * 100f).coerceIn(0f, 100f)
                String.format("%.1f%%", pct)
            }
        }
    }

    val totalDurationTimecode = remember(effectiveDuration, effectiveFps) {
        val totalSeconds = (effectiveDuration / 1000).toInt()
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        val frame = ((effectiveDuration % 1000) * effectiveFps / 1000).toInt()
        String.format("%02d:%02d:%02d:%02d", hours, minutes, seconds, frame)
    }

    // Identificación teórica del tipo de frame (I-Frame cada 15/30 frames)
    val isKeyframe = currentFrame % (effectiveFps / 2).coerceAtLeast(1) == 0

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0F141C))
            .border(1.dp, Color(0xFF233045), RoundedCornerShape(12.dp))
            .padding(10.dp)
            .testTag("frame_precise_scrubber_root")
    ) {

        // 1. BARRA SUPERIOR DE TELEMETRÍA Y CONTROL DE CÓDIGO DE TIEMPO
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Lectura principal de código de tiempo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Indicador de tipo de frame (I-Frame / Keyframe)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (activeKeyframeAtPlayhead != null) activeKeyframeAtPlayhead.type.badgeColor
                            else if (isKeyframe) Color(0xFF10B981)
                            else Color(0xFF3B82F6).copy(alpha = 0.8f)
                        )
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (activeKeyframeAtPlayhead != null) "◆ KEY" else if (isKeyframe) "I-KEY" else "P-FRAME",
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Texto de tiempo con cambio de modo interactivo al tocar
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF17202E))
                        .border(0.5.dp, Color(0xFF334155), RoundedCornerShape(6.dp))
                        .clickable {
                            displayMode = when (displayMode) {
                                TimecodeDisplayMode.SMPTE -> TimecodeDisplayMode.FRAMES
                                TimecodeDisplayMode.FRAMES -> TimecodeDisplayMode.MILLISECONDS
                                TimecodeDisplayMode.MILLISECONDS -> TimecodeDisplayMode.PERCENT
                                TimecodeDisplayMode.PERCENT -> TimecodeDisplayMode.SMPTE
                            }
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                        .testTag("timecode_display_box")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = formattedTimecode,
                            color = Color(0xFF38BDF8),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "/ $totalDurationTimecode",
                            color = Color(0xFF64748B),
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // Opciones, FPS y selector de rueda Jog
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Badge de FPS
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF1E293B))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "$effectiveFps FPS",
                        color = Color(0xFF94A3B8),
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Botón alternar Jog Dial
                IconButton(
                    onClick = {
                        showJogWheel = !showJogWheel
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    },
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(if (showJogWheel) Color(0xFF38BDF8).copy(alpha = 0.2f) else Color.Transparent)
                        .testTag("toggle_jog_wheel_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Adjust,
                        contentDescription = "Rueda de Navegación Cuadro a Cuadro",
                        tint = if (showJogWheel) Color(0xFF38BDF8) else Color(0xFF94A3B8),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // 2. BARRA DE HERRAMIENTAS: ZOOM DINÁMICO Y CONTROL DE KEYFRAMES
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Controles de Keyframes (Prev, Toggle Add/Remove, Next, Snap)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                // Ir a keyframe previo
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF1E293B))
                        .clickable {
                            val prev = keyframes.filter { it.frameIndex < currentFrame }.maxByOrNull { it.frameIndex }
                            if (prev != null) {
                                onSeekTo(prev.timestampMs)
                                onKeyframeSelected?.invoke(prev)
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                        }
                        .padding(horizontal = 5.dp, vertical = 3.dp)
                        .testTag("scrub_prev_keyframe_btn")
                ) {
                    Text(
                        text = "◀ KEY",
                        color = if (keyframes.any { it.frameIndex < currentFrame }) Color(0xFF38BDF8) else Color(0xFF64748B),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Botón Agregar / Eliminar Keyframe en posición actual
                if (activeKeyframeAtPlayhead != null) {
                    // Eliminar Keyframe existente
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFEF4444).copy(alpha = 0.25f))
                            .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(4.dp))
                            .clickable {
                                val updated = keyframes.filter { it.id != activeKeyframeAtPlayhead.id }
                                keyframes = updated
                                onKeyframesChange?.invoke(updated)
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                            .testTag("scrub_remove_keyframe_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text("◆", color = Color(0xFFEF4444), fontSize = 9.sp, fontWeight = FontWeight.Black)
                            Text("DEL KEY", color = Color(0xFFFECACA), fontSize = 8.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                    }
                } else {
                    // Agregar nuevo Keyframe
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(selectedKeyframeType.badgeColor.copy(alpha = 0.2f))
                            .border(1.dp, selectedKeyframeType.badgeColor, RoundedCornerShape(4.dp))
                            .clickable {
                                val newKey = TimelineKeyframe(
                                    frameIndex = currentFrame,
                                    timestampMs = currentPositionMs,
                                    label = "${selectedKeyframeType.label} #${keyframes.size + 1}",
                                    type = selectedKeyframeType
                                )
                                val updated = (keyframes + newKey).sortedBy { it.frameIndex }
                                keyframes = updated
                                onKeyframesChange?.invoke(updated)
                                onKeyframeSelected?.invoke(newKey)
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                            .testTag("scrub_add_keyframe_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text("+", color = selectedKeyframeType.badgeColor, fontSize = 9.sp, fontWeight = FontWeight.Black)
                            Text("KEYFRAME", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                    }
                }

                // Ir a keyframe siguiente
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF1E293B))
                        .clickable {
                            val next = keyframes.filter { it.frameIndex > currentFrame }.minByOrNull { it.frameIndex }
                            if (next != null) {
                                onSeekTo(next.timestampMs)
                                onKeyframeSelected?.invoke(next)
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                        }
                        .padding(horizontal = 5.dp, vertical = 3.dp)
                        .testTag("scrub_next_keyframe_btn")
                ) {
                    Text(
                        text = "KEY ▶",
                        color = if (keyframes.any { it.frameIndex > currentFrame }) Color(0xFF38BDF8) else Color(0xFF64748B),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Alternar snapping magnético a keyframes
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isKeyframeSnappingEnabled) Color(0xFF10B981).copy(alpha = 0.2f) else Color(0xFF1E293B))
                        .border(0.5.dp, if (isKeyframeSnappingEnabled) Color(0xFF10B981) else Color.Transparent, RoundedCornerShape(4.dp))
                        .clickable {
                            isKeyframeSnappingEnabled = !isKeyframeSnappingEnabled
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                        .padding(horizontal = 5.dp, vertical = 3.dp)
                        .testTag("scrub_toggle_snap_btn")
                ) {
                    Text(
                        text = if (isKeyframeSnappingEnabled) "SNAP ON" else "SNAP OFF",
                        color = if (isKeyframeSnappingEnabled) Color(0xFF34D399) else Color(0xFF64748B),
                        fontSize = 7.5.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Controles de Zoom Horizontal dinámico (Pinch to Zoom & Botones de precisión)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                // Botón Zoom Out (-)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF1E293B))
                        .clickable {
                            val newZoom = (zoomScale / 1.4f).coerceIn(1.0f, 10.0f)
                            zoomScale = newZoom
                            if (newZoom == 1.0f) windowStartNorm = 0f
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                        .testTag("scrub_zoom_out_btn")
                ) {
                    Text("-", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                // Preset FIT / 1.0x
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (zoomScale == 1.0f) Color(0xFF38BDF8) else Color(0xFF1E293B))
                        .clickable {
                            zoomScale = 1.0f
                            windowStartNorm = 0f
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                        .padding(horizontal = 5.dp, vertical = 3.dp)
                        .testTag("scrub_zoom_fit_btn")
                ) {
                    Text(
                        text = "FIT",
                        color = if (zoomScale == 1.0f) Color(0xFF0F172A) else Color(0xFF94A3B8),
                        fontSize = 8.sp,
                        fontWeight = if (zoomScale == 1.0f) FontWeight.Black else FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Preset 2.5x
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (abs(zoomScale - 2.5f) < 0.2f) Color(0xFF38BDF8) else Color(0xFF1E293B))
                        .clickable {
                            zoomScale = 2.5f
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                        .padding(horizontal = 5.dp, vertical = 3.dp)
                        .testTag("scrub_zoom_2_5x_btn")
                ) {
                    Text(
                        text = "2.5x",
                        color = if (abs(zoomScale - 2.5f) < 0.2f) Color(0xFF0F172A) else Color(0xFF94A3B8),
                        fontSize = 8.sp,
                        fontWeight = if (abs(zoomScale - 2.5f) < 0.2f) FontWeight.Black else FontWeight.Normal,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Preset 5.0x
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (abs(zoomScale - 5.0f) < 0.2f) Color(0xFF38BDF8) else Color(0xFF1E293B))
                        .clickable {
                            zoomScale = 5.0f
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                        .padding(horizontal = 5.dp, vertical = 3.dp)
                        .testTag("scrub_zoom_5x_btn")
                ) {
                    Text(
                        text = "5x",
                        color = if (abs(zoomScale - 5.0f) < 0.2f) Color(0xFF0F172A) else Color(0xFF94A3B8),
                        fontSize = 8.sp,
                        fontWeight = if (abs(zoomScale - 5.0f) < 0.2f) FontWeight.Black else FontWeight.Normal,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Preset 10.0x (Fotograma individual micro)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (abs(zoomScale - 10.0f) < 0.2f) Color(0xFF38BDF8) else Color(0xFF1E293B))
                        .clickable {
                            zoomScale = 10.0f
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                        .padding(horizontal = 5.dp, vertical = 3.dp)
                        .testTag("scrub_zoom_10x_btn")
                ) {
                    Text(
                        text = "10x",
                        color = if (abs(zoomScale - 10.0f) < 0.2f) Color(0xFF0F172A) else Color(0xFF94A3B8),
                        fontSize = 8.sp,
                        fontWeight = if (abs(zoomScale - 10.0f) < 0.2f) FontWeight.Black else FontWeight.Normal,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Botón Zoom In (+)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF1E293B))
                        .clickable {
                            val newZoom = (zoomScale * 1.4f).coerceIn(1.0f, 10.0f)
                            zoomScale = newZoom
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                        .testTag("scrub_zoom_in_btn")
                ) {
                    Text("+", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                // Indicador de nivel de zoom actual
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF0F172A))
                        .border(0.5.dp, Color(0xFF334155), RoundedCornerShape(4.dp))
                        .padding(horizontal = 5.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = String.format(java.util.Locale.US, "%.1fx", zoomScale),
                        color = Color(0xFF38BDF8),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.testTag("scrub_zoom_level_badge")
                    )
                }
            }
        }

        // 3. TIMELINE MULTI-CAPA CON PINCH-TO-ZOOM GESTURE DETECTOR, REGLA ESCALABLE Y DIAMANTES KEYFRAME
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF070A0F))
                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp))
                .testTag("filmstrip_scrubber_canvas_box")
        ) {
            val windowSpan = 1f / zoomScale
            val startNorm = windowStartNorm
            val endNorm = (windowStartNorm + windowSpan).coerceAtMost(1f)
            val visibleStartMs = (startNorm * effectiveDuration).toLong()
            val visibleEndMs = (endNorm * effectiveDuration).toLong()
            val visibleStartFrame = (startNorm * totalFrames).roundToInt().coerceIn(0, totalFrames)
            val visibleEndFrame = (endNorm * totalFrames).roundToInt().coerceIn(0, totalFrames)
            val visibleFrames = (visibleEndFrame - visibleStartFrame).coerceAtLeast(1)

            // Superficie de dibujo con detector de gesto horizontal Pinch-to-Zoom y arrastre de precisión
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(effectiveDuration, effectiveFps, totalFrames, zoomScale, windowStartNorm, isKeyframeSnappingEnabled, keyframes) {
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            var initialPinchDistance = -1f
                            var baseZoomOnPinchStart = zoomScale
                            var initialCentroidNorm = 0f
                            var isMultiTouch = false

                            do {
                                val event = awaitPointerEvent()
                                val activePointers = event.changes.filter { it.pressed }

                                if (activePointers.size >= 2) {
                                    // GESTO HORIZONTAL MULTITOUCH PINCH-TO-ZOOM
                                    isMultiTouch = true
                                    isPinchingTimeline = true
                                    isDraggingScrubber = false

                                    val p1 = activePointers[0].position
                                    val p2 = activePointers[1].position
                                    val currentDistance = abs(p1.x - p2.x)

                                    if (initialPinchDistance < 0f) {
                                        initialPinchDistance = currentDistance.coerceAtLeast(15f)
                                        baseZoomOnPinchStart = zoomScale
                                        val centroidX = (p1.x + p2.x) / 2f
                                        val currentSpan = 1f / zoomScale
                                        initialCentroidNorm = windowStartNorm + (centroidX / size.width).coerceIn(0f, 1f) * currentSpan
                                    } else {
                                        val ratio = currentDistance / initialPinchDistance
                                        val newZoom = (baseZoomOnPinchStart * ratio).coerceIn(1.0f, 10.0f)
                                        if (abs(newZoom - zoomScale) > 0.02f) {
                                            zoomScale = newZoom
                                            val newWindowSpan = 1f / newZoom
                                            val centroidX = (p1.x + p2.x) / 2f
                                            val maxStart = (1f - newWindowSpan).coerceAtLeast(0f)
                                            windowStartNorm = (initialCentroidNorm - (centroidX / size.width) * newWindowSpan).coerceIn(0f, maxStart)
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        }
                                    }

                                    // Consumir eventos del gesto de pinzamiento
                                    event.changes.forEach { it.consume() }
                                } else if (activePointers.size == 1 && !isMultiTouch) {
                                    // TOQUE / ARRASTRE DE UN DEDO: SEEK Y SCRUB CON MAGNÉTICA A KEYFRAMES
                                    val pointer = activePointers[0]
                                    pointer.consume()
                                    isDraggingScrubber = true

                                    val localX = pointer.position.x
                                    val localNorm = (localX / size.width).coerceIn(0f, 1f)
                                    val currentSpan = 1f / zoomScale
                                    val globalNorm = (windowStartNorm + localNorm * currentSpan).coerceIn(0f, 1f)
                                    val rawFrame = (globalNorm * totalFrames).roundToInt().coerceIn(0, totalFrames)

                                    // Snapping magnético a keyframes cercanos
                                    val targetFrame = if (isKeyframeSnappingEnabled) {
                                        val snapThreshold = (2 * zoomScale).roundToInt().coerceIn(2, 6)
                                        val candidate = keyframes.find { abs(it.frameIndex - rawFrame) <= snapThreshold }
                                        if (candidate != null) {
                                            if (lastSnappedKeyframeId != candidate.id) {
                                                lastSnappedKeyframeId = candidate.id
                                                snappedKeyframeLabel = candidate.label
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            }
                                            candidate.frameIndex
                                        } else {
                                            lastSnappedKeyframeId = null
                                            snappedKeyframeLabel = null
                                            rawFrame
                                        }
                                    } else {
                                        rawFrame
                                    }

                                    val frameDurationMs = 1000.0 / effectiveFps
                                    val targetMs = (targetFrame * frameDurationMs).toLong().coerceIn(0L, effectiveDuration)
                                    onSeekTo(targetMs)
                                }
                            } while (event.changes.any { it.pressed })

                            isDraggingScrubber = false
                            isPinchingTimeline = false
                        }
                    }
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // A. Perforaciones superiores e inferiores de tira de película de 35mm
                val holeWidth = 6.dp.toPx()
                val holeHeight = 4.dp.toPx()
                val holeGap = 12.dp.toPx()
                val totalHoles = (canvasWidth / (holeWidth + holeGap)).toInt() + 1

                for (i in 0 until totalHoles) {
                    val x = i * (holeWidth + holeGap) + 4.dp.toPx()
                    drawRoundRect(
                        color = Color(0xFF1E293B),
                        topLeft = Offset(x, 2.dp.toPx()),
                        size = Size(holeWidth, holeHeight),
                        cornerRadius = CornerRadius(1.dp.toPx(), 1.dp.toPx())
                    )
                    drawRoundRect(
                        color = Color(0xFF1E293B),
                        topLeft = Offset(x, canvasHeight - holeHeight - 2.dp.toPx()),
                        size = Size(holeWidth, holeHeight),
                        cornerRadius = CornerRadius(1.dp.toPx(), 1.dp.toPx())
                    )
                }

                // B. Fondo de bloques de fotogramas adaptativos según el nivel de Zoom
                val filmTop = 7.dp.toPx()
                val filmHeight = canvasHeight - 14.dp.toPx()

                if (zoomScale >= 6.0f) {
                    // Zoom alto: cada fotograma individual tiene su celda cinematográfica propia expandida
                    val cellWidth = canvasWidth / visibleFrames
                    for (f in visibleStartFrame..visibleEndFrame) {
                        val fx = (f - visibleStartFrame) * cellWidth
                        val isKeyframeCell = keyframes.any { it.frameIndex == f }
                        val blockColor = if (isKeyframeCell) {
                            Color(0xFF2E2415)
                        } else {
                            Color(
                                red = 0.08f + 0.03f * sin(f.toFloat() * 0.5f),
                                green = 0.11f + 0.03f * cos(f.toFloat() * 0.5f),
                                blue = 0.16f + 0.03f * sin(f * 0.3f),
                                alpha = 0.90f
                            )
                        }
                        drawRect(
                            color = blockColor,
                            topLeft = Offset(fx + 1.dp.toPx(), filmTop),
                            size = Size((cellWidth - 1.5.dp.toPx()).coerceAtLeast(1f), filmHeight)
                        )
                        drawLine(
                            color = if (isKeyframeCell) Color(0xFFF59E0B).copy(alpha = 0.7f) else Color(0xFF1E293B),
                            start = Offset(fx, filmTop),
                            end = Offset(fx, filmTop + filmHeight),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                } else {
                    // Zoom estándar / intermedio: bloques proporcionales escalados dinámicamente
                    val frameBlockWidth = (44.dp.toPx() * (zoomScale * 0.5f).coerceAtLeast(1f)).coerceAtMost(canvasWidth / 3)
                    val numBlocks = (canvasWidth / frameBlockWidth).toInt() + 1
                    for (b in 0 until numBlocks) {
                        val bx = b * frameBlockWidth
                        val blockColor = Color(
                            red = 0.08f + 0.04f * sin(b.toFloat() + startNorm * 10f),
                            green = 0.12f + 0.05f * cos(b.toFloat() + startNorm * 10f),
                            blue = 0.18f + 0.04f * sin(b * 0.7f + startNorm * 10f),
                            alpha = 0.85f
                        )
                        drawRect(
                            color = blockColor,
                            topLeft = Offset(bx + 1.dp.toPx(), filmTop),
                            size = Size(frameBlockWidth - 2.dp.toPx(), filmHeight)
                        )
                        drawLine(
                            color = Color(0xFF1E293B),
                            start = Offset(bx, filmTop),
                            end = Offset(bx, filmTop + filmHeight),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                }

                // C. Onda de audio simulada escalada a la ventana visible
                val waveY = filmTop + filmHeight * 0.65f
                val waveMaxH = filmHeight * 0.28f
                val waveStep = 3.dp.toPx()
                val numWaveBars = (canvasWidth / waveStep).toInt()
                val currentNorm = (currentPositionMs.toFloat() / effectiveDuration).coerceIn(0f, 1f)

                for (w in 0 until numWaveBars) {
                    val wx = w * waveStep
                    val localNorm = wx / canvasWidth
                    val globalNorm = startNorm + localNorm * windowSpan
                    val amp = abs(sin(globalNorm * 42.0 * PI) * cos(globalNorm * 18.0 * PI) * sin(globalNorm * 9.0 * PI)).toFloat()
                    val barH = (amp * waveMaxH).coerceAtLeast(1.5.dp.toPx())

                    val barColor = if (globalNorm <= currentNorm) {
                        Color(0xFF38BDF8).copy(alpha = 0.65f)
                    } else {
                        Color(0xFF475569).copy(alpha = 0.45f)
                    }

                    drawLine(
                        color = barColor,
                        start = Offset(wx, waveY - barH / 2),
                        end = Offset(wx, waveY + barH / 2),
                        strokeWidth = 1.5.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }

                // D. ESCALA DE REGLA TEMPORAL DINÁMICA SEGÚN ZOOM (DYNAMIC TIMELINE MARKERS)
                val rulerY = filmTop + 2.dp.toPx()

                if (zoomScale >= 7.0f) {
                    // MODO MICRO-FOTOGRAMA: Marcadores individuales para cada fotograma
                    for (f in visibleStartFrame..visibleEndFrame) {
                        val fx = ((f - visibleStartFrame).toFloat() / visibleFrames) * canvasWidth
                        val isSecond = f % effectiveFps == 0
                        val isFiveFrames = f % 5 == 0
                        val tickH = when {
                            isSecond -> 9.dp.toPx()
                            isFiveFrames -> 6.dp.toPx()
                            else -> 3.dp.toPx()
                        }
                        val tickColor = when {
                            isSecond -> Color(0xFF38BDF8)
                            isFiveFrames -> Color(0xFF7DD3FC).copy(alpha = 0.8f)
                            else -> Color(0xFF64748B).copy(alpha = 0.5f)
                        }
                        drawLine(
                            color = tickColor,
                            start = Offset(fx, rulerY),
                            end = Offset(fx, rulerY + tickH),
                            strokeWidth = if (isSecond) 2.dp.toPx() else 1.2.dp.toPx()
                        )
                    }
                } else if (zoomScale >= 2.5f) {
                    // MODO PRECISIÓN INTERMEDIA: Marcas por fotograma, cada 5 frames y segundos
                    val stepFrames = if (zoomScale >= 4.0f) 1 else 2
                    for (f in visibleStartFrame..visibleEndFrame step stepFrames) {
                        val fx = ((f - visibleStartFrame).toFloat() / visibleFrames) * canvasWidth
                        val isSecond = f % effectiveFps == 0
                        val isFiveFrames = f % 5 == 0
                        val tickH = when {
                            isSecond -> 8.dp.toPx()
                            isFiveFrames -> 5.dp.toPx()
                            else -> 2.5.dp.toPx()
                        }
                        val tickColor = when {
                            isSecond -> Color(0xFF38BDF8)
                            isFiveFrames -> Color(0xFF94A3B8)
                            else -> Color(0xFF475569).copy(alpha = 0.4f)
                        }
                        drawLine(
                            color = tickColor,
                            start = Offset(fx, rulerY),
                            end = Offset(fx, rulerY + tickH),
                            strokeWidth = if (isSecond) 1.5.dp.toPx() else 1.dp.toPx()
                        )
                    }
                } else {
                    // MODO VISTA GENERAL (1.0x - 2.4x): Marcas de medio segundo y segundos
                    val totalTicks = (visibleFrames.coerceAtMost(120)).coerceAtLeast(10)
                    for (t in 0..totalTicks) {
                        val tx = (t.toFloat() / totalTicks) * canvasWidth
                        val tickFrame = visibleStartFrame + ((t.toFloat() / totalTicks) * visibleFrames).toInt()
                        val isSecond = tickFrame % effectiveFps == 0
                        val isHalfSecond = tickFrame % (effectiveFps / 2) == 0
                        val tickH = when {
                            isSecond -> 6.dp.toPx()
                            isHalfSecond -> 4.dp.toPx()
                            else -> 2.dp.toPx()
                        }
                        val tickColor = when {
                            isSecond -> Color(0xFF38BDF8).copy(alpha = 0.8f)
                            isHalfSecond -> Color(0xFF94A3B8).copy(alpha = 0.6f)
                            else -> Color(0xFF475569).copy(alpha = 0.35f)
                        }
                        drawLine(
                            color = tickColor,
                            start = Offset(tx, rulerY),
                            end = Offset(tx, rulerY + tickH),
                            strokeWidth = if (isSecond) 1.5.dp.toPx() else 1.dp.toPx()
                        )
                    }
                }

                // E. MARCADORES DE DIAMANTES DE KEYFRAMES (KEYFRAME DIAMOND MARKERS)
                val diamondRadius = 5.5.dp.toPx()
                for (key in keyframes) {
                    val kNorm = (key.timestampMs.toFloat() / effectiveDuration).coerceIn(0f, 1f)
                    if (kNorm >= startNorm - 0.02f && kNorm <= endNorm + 0.02f) {
                        val kx = ((kNorm - startNorm) / windowSpan) * canvasWidth
                        val ky = rulerY + 8.dp.toPx()
                        val isCurrent = abs(key.frameIndex - currentFrame) <= 1

                        // Halo brillante si el cabezal está posicionado sobre este keyframe
                        if (isCurrent) {
                            drawCircle(
                                color = key.type.badgeColor.copy(alpha = 0.35f),
                                radius = diamondRadius * 2.2f,
                                center = Offset(kx, ky)
                            )
                        }

                        // Dibujo del diamante (rombo)
                        val diamondPath = Path().apply {
                            moveTo(kx, ky - diamondRadius)
                            lineTo(kx + diamondRadius, ky)
                            lineTo(kx, ky + diamondRadius)
                            lineTo(kx - diamondRadius, ky)
                            close()
                        }

                        // Relleno de color según tipo de keyframe
                        drawPath(diamondPath, color = key.type.badgeColor)
                        // Borde blanco de alto contraste
                        drawPath(diamondPath, color = Color.White, style = Stroke(1.2.dp.toPx()))

                        // Centro brillante
                        drawCircle(
                            color = if (isCurrent) Color.White else key.type.accentColor,
                            radius = 1.8.dp.toPx(),
                            center = Offset(kx, ky)
                        )
                    }
                }

                // F. Rangos de recorte inactivo (Trim Start / Trim End)
                if (trimStart > 0f) {
                    val tsX = ((trimStart - startNorm) / windowSpan) * canvasWidth
                    if (tsX > 0f) {
                        drawRect(
                            color = Color.Black.copy(alpha = 0.65f),
                            topLeft = Offset(0f, filmTop),
                            size = Size(tsX.coerceAtMost(canvasWidth), filmHeight)
                        )
                        drawLine(
                            color = Color(0xFFF59E0B),
                            start = Offset(tsX.coerceIn(0f, canvasWidth), filmTop),
                            end = Offset(tsX.coerceIn(0f, canvasWidth), filmTop + filmHeight),
                            strokeWidth = 2.5.dp.toPx()
                        )
                    }
                }
                if (trimEnd < 1f) {
                    val teX = ((trimEnd - startNorm) / windowSpan) * canvasWidth
                    if (teX < canvasWidth) {
                        drawRect(
                            color = Color.Black.copy(alpha = 0.65f),
                            topLeft = Offset(teX.coerceAtLeast(0f), filmTop),
                            size = Size((canvasWidth - teX).coerceAtLeast(0f), filmHeight)
                        )
                        drawLine(
                            color = Color(0xFFF59E0B),
                            start = Offset(teX.coerceIn(0f, canvasWidth), filmTop),
                            end = Offset(teX.coerceIn(0f, canvasWidth), filmTop + filmHeight),
                            strokeWidth = 2.5.dp.toPx()
                        )
                    }
                }

                // G. Cabezal de reproducción (Playhead Cursor) de alta visibilidad
                val playheadNorm = (currentPositionMs.toFloat() / effectiveDuration).coerceIn(0f, 1f)
                val playheadX = ((playheadNorm - startNorm) / windowSpan) * canvasWidth

                if (playheadX in -5f..(canvasWidth + 5f)) {
                    // Resplandor del cabezal
                    drawLine(
                        color = Color(0xFFEF4444).copy(alpha = 0.35f),
                        start = Offset(playheadX, 0f),
                        end = Offset(playheadX, canvasHeight),
                        strokeWidth = 5.dp.toPx()
                    )
                    // Línea central fina roja
                    drawLine(
                        color = Color(0xFFEF4444),
                        start = Offset(playheadX, 0f),
                        end = Offset(playheadX, canvasHeight),
                        strokeWidth = 2.dp.toPx()
                    )

                    // Puntero superior en rombo / diamante
                    val diamondW = 8.dp.toPx()
                    val diamondH = 8.dp.toPx()
                    val path = Path().apply {
                        moveTo(playheadX, 0f)
                        lineTo(playheadX + diamondW / 2, diamondH / 2)
                        lineTo(playheadX, diamondH)
                        lineTo(playheadX - diamondW / 2, diamondH / 2)
                        close()
                    }
                    drawPath(path, Color(0xFFEF4444))
                    drawPath(path, Color.White, style = Stroke(1.dp.toPx()))
                }
            }

            // H. Tooltip flotante ampliado durante el arrastre o pinzamiento (HUD de inspección)
            if (isDraggingScrubber || isPinchingTimeline) {
                val frameDelta = currentFrame - dragStartFrame
                val deltaSign = if (frameDelta >= 0) "+$frameDelta" else "$frameDelta"

                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 2.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF0F172A).copy(alpha = 0.95f))
                        .border(1.dp, if (snappedKeyframeLabel != null) Color(0xFFF59E0B) else Color(0xFF38BDF8), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                        .shadow(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (snappedKeyframeLabel != null) Color(0xFFF59E0B) else Color(0xFFEF4444))
                        )
                        Text(
                            text = formattedTimecode,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        if (isPinchingTimeline) {
                            Text(
                                text = "ZOOM ${String.format(java.util.Locale.US, "%.1fx", zoomScale)}",
                                color = Color(0xFF38BDF8),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        } else if (snappedKeyframeLabel != null) {
                            Text(
                                text = "🧲 $snappedKeyframeLabel",
                                color = Color(0xFFFBBF24),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        } else {
                            Text(
                                text = "($deltaSign F)",
                                color = if (frameDelta >= 0) Color(0xFF10B981) else Color(0xFFF59E0B),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // 4. MINI-MAP OVERVIEW TRACK (CUANDO EL ZOOM ES > 1.0X PARA NAVEGACIÓN RÁPIDA)
        if (zoomScale > 1.0f) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF0B1017))
                    .border(0.5.dp, Color(0xFF1E293B), RoundedCornerShape(3.dp))
                    .pointerInput(zoomScale, effectiveDuration) {
                        detectDragGestures { change, _ ->
                            change.consume()
                            val tapNorm = (change.position.x / size.width).coerceIn(0f, 1f)
                            val windowSpan = 1f / zoomScale
                            val maxStart = (1f - windowSpan).coerceAtLeast(0f)
                            windowStartNorm = (tapNorm - windowSpan / 2f).coerceIn(0f, maxStart)
                        }
                    }
                    .testTag("scrub_minimap_navigator")
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val mw = size.width
                    val mh = size.height
                    val windowSpan = 1f / zoomScale
                    val winX = windowStartNorm * mw
                    val winW = (windowSpan * mw).coerceAtLeast(12.dp.toPx())

                    // Dibujar puntos miniatura para cada keyframe
                    for (k in keyframes) {
                        val kn = (k.timestampMs.toFloat() / effectiveDuration).coerceIn(0f, 1f)
                        drawCircle(
                            color = k.type.badgeColor,
                            radius = 1.5.dp.toPx(),
                            center = Offset(kn * mw, mh / 2)
                        )
                    }

                    // Ventana visible resaltada
                    drawRoundRect(
                        color = Color(0xFF38BDF8).copy(alpha = 0.25f),
                        topLeft = Offset(winX, 1.dp.toPx()),
                        size = Size(winW, mh - 2.dp.toPx()),
                        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                    )
                    drawRoundRect(
                        color = Color(0xFF38BDF8),
                        topLeft = Offset(winX, 1.dp.toPx()),
                        size = Size(winW, mh - 2.dp.toPx()),
                        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
                        style = Stroke(1.dp.toPx())
                    )

                    // Cabezal de reproducción miniatura
                    val curNorm = (currentPositionMs.toFloat() / effectiveDuration).coerceIn(0f, 1f)
                    drawLine(
                        color = Color(0xFFEF4444),
                        start = Offset(curNorm * mw, 0f),
                        end = Offset(curNorm * mw, mh),
                        strokeWidth = 1.5.dp.toPx()
                    )
                }
            }
        }

        // 5. CINTA DE GESTIÓN Y DETALLES DE KEYFRAMES
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("scrub_keyframe_manager_row"),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Chips de tipo de keyframe para agregar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = "TIPO:",
                    fontSize = 7.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B),
                    fontFamily = FontFamily.Monospace
                )
                listOf(KeyframeType.TRANSFORM, KeyframeType.FILTER, KeyframeType.AUDIO, KeyframeType.CUT).forEach { kt ->
                    val isSel = selectedKeyframeType == kt
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (isSel) kt.badgeColor.copy(alpha = 0.3f) else Color(0xFF1E293B))
                            .border(0.5.dp, if (isSel) kt.badgeColor else Color.Transparent, RoundedCornerShape(3.dp))
                            .clickable { selectedKeyframeType = kt }
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = kt.label,
                            fontSize = 7.5.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSel) kt.accentColor else Color(0xFF94A3B8)
                        )
                    }
                }
            }

            // Indicador de recuento de keyframes
            Text(
                text = "${keyframes.size} KEYFRAMES",
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF38BDF8),
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 3. RUEDA TÁCTIL JOG / SHUTTLE DIAL (ROTARY CONTROLLER PARA EDICIÓN CUADRO A CUADRO)
        AnimatedVisibility(
            visible = showJogWheel,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            TactileJogShuttleWheel(
                currentFrame = currentFrame,
                totalFrames = totalFrames,
                fps = effectiveFps,
                sensitivity = scrubSensitivity,
                onSensitivityChange = { scrubSensitivity = it },
                onStepFrame = { delta ->
                    onStepFrame(delta)
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
        }

        // 4. BARRA DE TRANSPORTE Y MICRO-PASO DE FOTOGRAMAS (-10F, -1F, PLAY/PAUSE, +1F, +10F)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Controles de salto a extremos
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                // Ir al inicio (Frame 0)
                IconButton(
                    onClick = {
                        val targetMs = (trimStart * effectiveDuration).toLong()
                        onSeekTo(targetMs)
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    },
                    modifier = Modifier.size(32.dp).testTag("scrub_skip_start_btn")
                ) {
                    Icon(Icons.Default.SkipPrevious, "Ir al inicio", tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                }

                // Retroceder 10 fotogramas
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF1E293B))
                        .clickable {
                            onStepFrame(-10)
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                        .padding(horizontal = 6.dp, vertical = 5.dp)
                        .testTag("scrub_step_back_10_btn")
                ) {
                    Text("-10F", color = Color(0xFFE2E8F0), fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }

                // Retroceder 1 fotograma exacto
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF1E293B))
                        .clickable {
                            onStepFrame(-1)
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                        .padding(horizontal = 7.dp, vertical = 5.dp)
                        .testTag("scrub_step_back_1_btn")
                ) {
                    Text("-1F", color = Color(0xFF38BDF8), fontSize = 9.5.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                }
            }

            // Botón central de Reproducir / Pausar
            FilledIconButton(
                onClick = onPlayPauseToggle,
                modifier = Modifier
                    .size(38.dp)
                    .testTag("scrub_play_pause_btn"),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (isPlaying) Color(0xFFEF4444) else Color(0xFF38BDF8),
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pausar" else "Reproducir",
                    modifier = Modifier.size(20.dp)
                )
            }

            // Controles de avance de fotogramas
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                // Avanzar 1 fotograma exacto
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF1E293B))
                        .clickable {
                            onStepFrame(1)
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                        .padding(horizontal = 7.dp, vertical = 5.dp)
                        .testTag("scrub_step_fwd_1_btn")
                ) {
                    Text("+1F", color = Color(0xFF38BDF8), fontSize = 9.5.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                }

                // Avanzar 10 fotogramas
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF1E293B))
                        .clickable {
                            onStepFrame(10)
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                        .padding(horizontal = 6.dp, vertical = 5.dp)
                        .testTag("scrub_step_fwd_10_btn")
                ) {
                    Text("+10F", color = Color(0xFFE2E8F0), fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }

                // Ir al final (Trim End / Fin)
                IconButton(
                    onClick = {
                        val targetMs = (trimEnd * effectiveDuration).toLong()
                        onSeekTo(targetMs)
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    },
                    modifier = Modifier.size(32.dp).testTag("scrub_skip_end_btn")
                ) {
                    Icon(Icons.Default.SkipNext, "Ir al final", tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                }
            }
        }

        // 5. SELECTOR DE VELOCIDAD DE PREVIEW Y MODO DE SENSIBILIDAD
        if (onSpeedChange != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "VELOCIDAD / SHUTTLE:",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B),
                    fontFamily = FontFamily.Monospace
                )

                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    listOf(0.25f, 0.5f, 1.0f, 2.0f, 4.0f).forEach { speed ->
                        val isSel = currentPlaybackSpeed == speed
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isSel) Color(0xFF38BDF8) else Color(0xFF1E293B))
                                .clickable { onSpeedChange(speed) }
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${speed}x",
                                fontSize = 8.sp,
                                fontWeight = if (isSel) FontWeight.Black else FontWeight.Normal,
                                color = if (isSel) Color(0xFF0F172A) else Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Rueda táctil Jog / Shuttle con rotación continua y marcas de precisión.
 * Permite desplazar fotograma a fotograma arrastrando en arco circular
 * o a través de la cinta infinita horizontal.
 */
@Composable
fun TactileJogShuttleWheel(
    currentFrame: Int,
    totalFrames: Int,
    fps: Int,
    sensitivity: ScrubSensitivity,
    onSensitivityChange: (ScrubSensitivity) -> Unit,
    onStepFrame: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var accumulatedRotationDegrees by remember { mutableFloatStateOf(0f) }
    var relativeJogOffset by remember { mutableIntStateOf(0) }
    val degreesPerStep = when (sensitivity) {
        ScrubSensitivity.FINE_FRAME -> 10f // 36 clicks por vuelta = 36 cuadros
        ScrubSensitivity.FIVE_FRAMES -> 8f
        ScrubSensitivity.ONE_SECOND -> 6f
    }

    Card(
        modifier = modifier.testTag("jog_shuttle_wheel_container"),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131B29)),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF233045))
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            // Cabecera del Jog
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(
                        imageVector = Icons.Default.Album,
                        contentDescription = null,
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "JOG SHUTTLE CONTROLLER",
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE2E8F0),
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Selector de sensibilidad
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    ScrubSensitivity.values().forEach { s ->
                        val isSel = s == sensitivity
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isSel) Color(0xFF38BDF8).copy(alpha = 0.25f) else Color(0xFF1E293B))
                                .border(0.5.dp, if (isSel) Color(0xFF38BDF8) else Color.Transparent, RoundedCornerShape(4.dp))
                                .clickable { onSensitivityChange(s) }
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = s.label,
                                fontSize = 7.5.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) Color(0xFF38BDF8) else Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Rueda circular central interactiva y cinta de desplazamiento
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Dial circular giratorio
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color(0xFF2C3E55), Color(0xFF172333), Color(0xFF0F1722))
                            )
                        )
                        .border(1.5.dp, Color(0xFF38BDF8).copy(alpha = 0.6f), CircleShape)
                        .pointerInput(sensitivity) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                // Arrastre horizontal/vertical mapeado a rotación angular
                                val deltaDegrees = (dragAmount.x - dragAmount.y) * 0.85f
                                accumulatedRotationDegrees += deltaDegrees

                                while (accumulatedRotationDegrees >= degreesPerStep) {
                                    accumulatedRotationDegrees -= degreesPerStep
                                    onStepFrame(sensitivity.framesPerStep)
                                    relativeJogOffset += sensitivity.framesPerStep
                                }
                                while (accumulatedRotationDegrees <= -degreesPerStep) {
                                    accumulatedRotationDegrees += degreesPerStep
                                    onStepFrame(-sensitivity.framesPerStep)
                                    relativeJogOffset -= sensitivity.framesPerStep
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Marcas radiales del dial
                    Canvas(modifier = Modifier.fillMaxSize().rotate(accumulatedRotationDegrees)) {
                        val center = Offset(size.width / 2, size.height / 2)
                        val radius = size.width / 2 - 4.dp.toPx()
                        val numNotches = 24

                        for (i in 0 until numNotches) {
                            val angleRad = (i * 360f / numNotches) * (PI / 180f)
                            val isMainNotch = i % 6 == 0
                            val notchLen = if (isMainNotch) 6.dp.toPx() else 3.5.dp.toPx()
                            val innerR = radius - notchLen

                            val startX = (center.x + innerR * cos(angleRad)).toFloat()
                            val startY = (center.y + innerR * sin(angleRad)).toFloat()
                            val endX = (center.x + radius * cos(angleRad)).toFloat()
                            val endY = (center.y + radius * sin(angleRad)).toFloat()

                            drawLine(
                                color = if (isMainNotch) Color(0xFF38BDF8) else Color(0xFF64748B),
                                start = Offset(startX, startY),
                                end = Offset(endX, endY),
                                strokeWidth = if (isMainNotch) 1.5.dp.toPx() else 1.dp.toPx()
                            )
                        }

                        // Indicador luminoso de posición del dial
                        drawCircle(
                            color = Color(0xFFEF4444),
                            radius = 3.dp.toPx(),
                            center = Offset(center.x, 7.dp.toPx())
                        )
                    }

                    // Tapa central metálica
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF334155), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "JOG",
                            fontSize = 7.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF38BDF8),
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Cinta horizontal infinita y métrica de desplazamiento
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "OFFSET: ${if (relativeJogOffset >= 0) "+$relativeJogOffset" else "$relativeJogOffset"} FRAMES",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (relativeJogOffset != 0) Color(0xFF38BDF8) else Color(0xFF64748B),
                            fontFamily = FontFamily.Monospace
                        )

                        if (relativeJogOffset != 0) {
                            Text(
                                text = "RESET",
                                fontSize = 7.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF4444),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color(0xFF1E293B))
                                    .clickable { relativeJogOffset = 0 }
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Cinta de arrastre continuo horizontal (Scrub Ribbon)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(34.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF0A0F17))
                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(6.dp))
                            .pointerInput(sensitivity) {
                                detectHorizontalDragGestures { change, dragAmount ->
                                    change.consume()
                                    val stepPx = 8.dp.toPx()
                                    accumulatedRotationDegrees += dragAmount * 1.5f
                                    val steps = (dragAmount / stepPx).toInt()
                                    if (steps != 0) {
                                        onStepFrame(steps * sensitivity.framesPerStep)
                                        relativeJogOffset += steps * sensitivity.framesPerStep
                                    } else if (abs(dragAmount) >= 2f) {
                                        val singleStep = if (dragAmount > 0) 1 else -1
                                        onStepFrame(singleStep * sensitivity.framesPerStep)
                                        relativeJogOffset += singleStep * sensitivity.framesPerStep
                                    }
                                }
                            }
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val tickSpacing = 10.dp.toPx()
                            val offsetShift = (accumulatedRotationDegrees % tickSpacing)

                            var x = offsetShift - tickSpacing
                            var idx = 0
                            while (x < w + tickSpacing) {
                                val isMajor = idx % 5 == 0
                                val tickH = if (isMajor) h * 0.6f else h * 0.35f
                                drawLine(
                                    color = if (isMajor) Color(0xFF38BDF8) else Color(0xFF334155),
                                    start = Offset(x, (h - tickH) / 2),
                                    end = Offset(x, (h + tickH) / 2),
                                    strokeWidth = if (isMajor) 1.5.dp.toPx() else 1.dp.toPx()
                                )
                                x += tickSpacing
                                idx++
                            }

                            // Cursor central rojo de alineación
                            drawLine(
                                color = Color(0xFFEF4444),
                                start = Offset(w / 2, 0f),
                                end = Offset(w / 2, h),
                                strokeWidth = 2.dp.toPx()
                            )
                        }
                    }
                }
            }
        }
    }
}
