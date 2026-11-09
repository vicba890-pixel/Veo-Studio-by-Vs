package com.example.ui.components.preview

import androidx.annotation.OptIn
import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.data.vert.VertEngineConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.abs

/**
 * Controlador de reproducción de video profesional basado en Media3 ExoPlayer:
 * - Play / Pause / Replay con soporte de rango de corte (Trimming) automático.
 * - Búsqueda (Seek) fluida con deslizador de precisión y marcas de búfer.
 * - Navegación granular por fotograma individual (-1F, +1F) y salto rápido (-5s, +5s, -10s, +10s).
 * - Salto magnético a puntos de corte: Ir a Trim In (Inicio) e Ir a Trim Out (Fin).
 * - Selector de velocidad de reproducción (0.25x, 0.5x, 1.0x, 1.5x, 2.0x).
 * - Control de volumen y silencio con retroalimentación instantánea.
 * - Modo de repetición en bucle (Loop) restringido al rango recortado o video completo.
 * - Diseñado para integrarse bidireccionalmente con VmsbResponsiveVideoTimeline.
 */
@OptIn(UnstableApi::class)
@Composable
fun VmsbVideoPlaybackController(
    exoPlayer: ExoPlayer?,
    currentPositionMs: Long,
    durationMs: Long,
    trimStart: Float = 0f,
    trimEnd: Float = 1f,
    isPlaying: Boolean = false,
    playbackSpeed: Float = 1.0f,
    isLooping: Boolean = true,
    isMuted: Boolean = false,
    fps: Int = 30,
    vertConfig: VertEngineConfig? = null,
    onVertToggle: (() -> Unit)? = null,
    onPlayPauseToggle: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onSpeedChange: (Float) -> Unit,
    onLoopToggle: () -> Unit,
    onMuteToggle: () -> Unit,
    modifier: Modifier = Modifier,
    bufferedPositionMs: Long = 0L
) {
    val haptic = LocalHapticFeedback.current
    val effectiveDuration = durationMs.coerceAtLeast(1000L)
    val effectiveFps = fps.coerceIn(12, 120)
    val frameDurationMs = ((1000.0 / effectiveFps) + 0.5).toLong().coerceAtLeast(16L)

    var showSpeedDialog by remember { mutableStateOf(false) }
    var isScrubbingSlider by remember { mutableStateOf(false) }
    var scrubTargetMs by remember { mutableLongStateOf(0L) }

    val activeDisplayPositionMs = if (isScrubbingSlider) scrubTargetMs else currentPositionMs
    val progressFraction = (activeDisplayPositionMs.toFloat() / effectiveDuration.toFloat()).coerceIn(0f, 1f)
    val bufferedFraction = (bufferedPositionMs.toFloat() / effectiveDuration.toFloat()).coerceIn(0f, 1f)

    // Vigilancia del rango de corte: si se reproduce más allá de trimEnd, volver a trimStart o pausar
    LaunchedEffect(exoPlayer, isPlaying, trimStart, trimEnd, isLooping, effectiveDuration) {
        while (isActive) {
            if (exoPlayer != null && exoPlayer.isPlaying) {
                val currentPos = exoPlayer.currentPosition
                val trimEndMs = (trimEnd * effectiveDuration).toLong()
                val trimStartMs = (trimStart * effectiveDuration).toLong()

                if (trimEnd < 0.999f && currentPos >= trimEndMs) {
                    if (isLooping) {
                        exoPlayer.seekTo(trimStartMs)
                        onSeekTo(trimStartMs)
                    } else {
                        exoPlayer.pause()
                        exoPlayer.seekTo(trimEndMs)
                        onSeekTo(trimEndMs)
                    }
                }
            }
            delay(50)
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0B0F19))
            .border(1.2.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
            .padding(12.dp)
            .testTag("vmsb_video_playback_controller_root")
    ) {
        val isCompactWidth = maxWidth < 500.dp

        Column(modifier = Modifier.fillMaxWidth()) {

            // ==========================================
            // 1. BARRA DE BÚSQUEDA (SEEK BAR) CON BUFFER Y RANGO TRIM
            // ==========================================
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatTimecodeWithFrames(activeDisplayPositionMs, effectiveFps),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF38BDF8),
                        fontFamily = FontFamily.Monospace
                    )

                    // Indicador de recorte activo
                    val trimStartMs = (trimStart * effectiveDuration).toLong()
                    val trimEndMs = (trimEnd * effectiveDuration).toLong()
                    Text(
                        text = "RANGO: [${formatTimecodeShort(trimStartMs)} — ${formatTimecodeShort(trimEndMs)}]",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFFBBF24),
                        fontFamily = FontFamily.Monospace
                    )

                    Text(
                        text = formatTimecodeWithFrames(effectiveDuration, effectiveFps),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Deslizador de búsqueda interactivo con canal de búfer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Línea de Búfer
                    LinearProgressIndicator(
                        progress = { bufferedFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = Color.White.copy(alpha = 0.35f),
                        trackColor = Color(0xFF1E293B)
                    )

                    // Slider de búsqueda de fotograma continuo
                    Slider(
                        value = progressFraction,
                        onValueChange = { frac ->
                            isScrubbingSlider = true
                            val target = (frac * effectiveDuration).toLong()
                            scrubTargetMs = target
                            exoPlayer?.seekTo(target)
                            onSeekTo(target)
                        },
                        onValueChangeFinished = {
                            isScrubbingSlider = false
                            exoPlayer?.seekTo(scrubTargetMs)
                            onSeekTo(scrubTargetMs)
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF38BDF8),
                            activeTrackColor = Color(0xFF38BDF8),
                            inactiveTrackColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("vmsb_controller_seek_slider")
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // ==========================================
            // 2. CONSOLA DE ACCIONES PRINCIPALES Y NAVEGACIÓN
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // A. Navegación a puntos de corte y salto hacia atrás
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Salto a Trim In
                    IconButton(
                        onClick = {
                            val inMs = (trimStart * effectiveDuration).toLong()
                            exoPlayer?.seekTo(inMs)
                            onSeekTo(inMs)
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("vmsb_controller_jump_in_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "Saltar a Inicio de Recorte",
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Rebobinar 5 segundos
                    IconButton(
                        onClick = {
                            val target = (activeDisplayPositionMs - 5000L).coerceAtLeast(0L)
                            exoPlayer?.seekTo(target)
                            onSeekTo(target)
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("vmsb_controller_rewind_5s_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FastRewind,
                            contentDescription = "Rebobinar 5s",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Paso atrás 1 fotograma (-1 Frame)
                    IconButton(
                        onClick = {
                            val target = (activeDisplayPositionMs - frameDurationMs).coerceAtLeast(0L)
                            exoPlayer?.seekTo(target)
                            onSeekTo(target)
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("vmsb_controller_step_back_frame_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Paso -1 Fotograma",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // B. Botón central prominente Play / Pause
                val playBounceScale by animateFloatAsState(
                    targetValue = if (isPlaying) 1.0f else 1.06f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "playBounceScale"
                )

                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .scale(playBounceScale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF38BDF8),
                                    Color(0xFF0284C7)
                                )
                            )
                        )
                        .border(1.5.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true, color = Color.White),
                            onClick = {
                                // Si está fuera del rango de recorte, saltar primero al inicio de corte
                                val currentNorm = (activeDisplayPositionMs.toFloat() / effectiveDuration.toFloat()).coerceIn(0f, 1f)
                                if (!isPlaying && (currentNorm < trimStart || currentNorm >= trimEnd)) {
                                    val inMs = (trimStart * effectiveDuration).toLong()
                                    exoPlayer?.seekTo(inMs)
                                    onSeekTo(inMs)
                                }
                                onPlayPauseToggle()
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                        )
                        .testTag("vmsb_controller_play_pause_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pausar" else "Reproducir",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }

                // C. Salto hacia adelante y navegación a Trim Out
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Paso adelante 1 fotograma (+1 Frame)
                    IconButton(
                        onClick = {
                            val target = (activeDisplayPositionMs + frameDurationMs).coerceAtMost(effectiveDuration)
                            exoPlayer?.seekTo(target)
                            onSeekTo(target)
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("vmsb_controller_step_forward_frame_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Paso +1 Fotograma",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Avanzar 5 segundos
                    IconButton(
                        onClick = {
                            val target = (activeDisplayPositionMs + 5000L).coerceAtMost(effectiveDuration)
                            exoPlayer?.seekTo(target)
                            onSeekTo(target)
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("vmsb_controller_forward_5s_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FastForward,
                            contentDescription = "Avanzar 5s",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Salto a Trim Out
                    IconButton(
                        onClick = {
                            val outMs = (trimEnd * effectiveDuration).toLong()
                            exoPlayer?.seekTo(outMs)
                            onSeekTo(outMs)
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("vmsb_controller_jump_out_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Saltar a Fin de Recorte",
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ==========================================
            // 3. BARRA INFERIOR DE AJUSTES: VELOCIDAD, SILENCIO Y BUCLE
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Selector de Velocidad de Reproducción
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val speeds = listOf(0.5f, 1.0f, 1.5f, 2.0f)
                    speeds.forEach { spd ->
                        val isSelected = abs(playbackSpeed - spd) < 0.05f
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) Color(0xFF38BDF8) else Color(0xFF1E293B))
                                .clickable {
                                    onSpeedChange(spd)
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                }
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                                .testTag("vmsb_controller_speed_${spd}x")
                        ) {
                            Text(
                                text = "${spd}x",
                                fontSize = 8.5.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                color = if (isSelected) Color(0xFF0F172A) else Color(0xFF94A3B8),
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Controles de Audio y Bucle (Loop) y VERT
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Botón VERT Reframe Toggle
                    if (onVertToggle != null) {
                        val isVertActive = vertConfig != null
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isVertActive) Color(0xFF0284C7).copy(alpha = 0.35f) else Color(0xFF1E293B))
                                .border(
                                    width = 1.dp,
                                    color = if (isVertActive) Color(0xFF38BDF8) else Color(0xFF334155),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .clickable {
                                    onVertToggle()
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                }
                                .padding(horizontal = 7.dp, vertical = 4.dp)
                                .testTag("vmsb_controller_vert_btn")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                Text(
                                    text = "VERT",
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isVertActive) Color(0xFF38BDF8) else Color(0xFF94A3B8),
                                    fontFamily = FontFamily.Monospace
                                )
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .clip(CircleShape)
                                        .background(if (isVertActive) Color(0xFF34D399) else Color(0xFF64748B))
                                )
                            }
                        }
                    }

                    // Botón Silencio / Audio
                    IconButton(
                        onClick = onMuteToggle,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("vmsb_controller_mute_btn")
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            contentDescription = if (isMuted) "Activar Sonido" else "Silenciar",
                            tint = if (isMuted) Color(0xFFEF4444) else Color(0xFF38BDF8),
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    // Botón Repetición en Bucle (Loop)
                    IconButton(
                        onClick = onLoopToggle,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("vmsb_controller_loop_btn")
                    ) {
                        Icon(
                            imageVector = if (isLooping) Icons.Default.RepeatOne else Icons.Default.Repeat,
                            contentDescription = if (isLooping) "Bucle Activado" else "Bucle Desactivado",
                            tint = if (isLooping) Color(0xFF10B981) else Color(0xFF64748B),
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Formatea milisegundos a código de tiempo con fotogramas: MM:SS:FF
 */
private fun formatTimecodeWithFrames(timeMs: Long, fps: Int): String {
    if (timeMs <= 0L) return "00:00:00"
    val totalSeconds = (timeMs / 1000).toInt()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val frame = (((timeMs % 1000) * fps) / 1000).toInt()
    return String.format("%02d:%02d:%02d", minutes, seconds, frame)
}

/**
 * Formatea milisegundos a código de tiempo corto MM:SS
 */
private fun formatTimecodeShort(timeMs: Long): String {
    if (timeMs <= 0L) return "00:00"
    val totalSeconds = (timeMs / 1000).toInt()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
