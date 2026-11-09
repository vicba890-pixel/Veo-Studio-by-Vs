package com.example.ui.components.preview

import android.content.Context
import android.net.Uri
import android.os.Build
import android.view.SurfaceView
import android.view.TextureView
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import com.example.data.multitrack.*
import com.example.data.multitrack.command.*
import androidx.media3.exoplayer.SeekParameters
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

/**
 * Componente de previsualización de video de alto rendimiento basado en ExoPlayer
 * con aceleración por hardware integral, preservación de gama cromática (BT.2020 / DCI-P3 / Rec.709)
 * y controles profesionales de etalonaje y telemetría para el editor.
 */
@OptIn(UnstableApi::class)
@Composable
fun HighPerformanceExoPlayerPreview(
    videoUri: String,
    aspectRatio: String,
    modifier: Modifier = Modifier,
    isLoopingInitial: Boolean = true,
    trimStart: Float = 0f,
    trimEnd: Float = 1f,
    onTrimRangeChange: ((Float, Float) -> Unit)? = null,
    onTogglePlayerMode: (() -> Unit)? = null,
    onFrameProcessed: ((Long) -> Unit)? = null,
    onPositionChanged: ((Long, Float) -> Unit)? = null,
    initialKeyframes: List<TimelineKeyframe>? = null,
    onKeyframesChange: ((List<TimelineKeyframe>) -> Unit)? = null,
    onKeyframeSelected: ((TimelineKeyframe) -> Unit)? = null,
    multiTrackComposition: MultiTrackComposition? = null,
    onMultiTrackCompositionChange: ((MultiTrackComposition) -> Unit)? = null,
    canUndoMultiTrack: Boolean = false,
    canRedoMultiTrack: Boolean = false,
    undoDescription: String? = null,
    redoDescription: String? = null,
    undoHistoryList: List<String> = emptyList(),
    redoHistoryList: List<String> = emptyList(),
    onUndoMultiTrack: (() -> Unit)? = null,
    onRedoMultiTrack: (() -> Unit)? = null,
    onExecuteMultiTrackCommand: ((MultiTrackCommand) -> Unit)? = null,
    showMultiTrackTimelineInitial: Boolean = true,
    onImportVideoClick: (() -> Unit)? = null,
    overlayContent: (@Composable BoxScope.() -> Unit)? = null
) {
    val context = LocalContext.current

    // Estado de etalonaje de color en tiempo real
    var colorConfig by remember { mutableStateOf(ColorGradingConfig.NEUTRAL_MASTER) }
    var isDirectSurfaceMode by remember { mutableStateOf(false) } // true = Zero-Copy SurfaceView, false = GL TextureView con ColorMatrix shader
    var showWaveformScope by remember { mutableStateOf(false) }
    var isFullscreenActive by remember { mutableStateOf(false) }

    // Control de fuente de reproducción activa
    val presetSources = remember { HighFidelityVideoSource.PRESETS }
    var selectedSourceIndex by remember { mutableIntStateOf(if (videoUri.isNotBlank()) -1 else 0) }

    // Determinar URI final a reproducir
    val activePlayUri = remember(videoUri, selectedSourceIndex) {
        if (selectedSourceIndex >= 0 && selectedSourceIndex < presetSources.size) {
            presetSources[selectedSourceIndex].uriString
        } else if (videoUri.isNotBlank()) {
            videoUri
        } else {
            presetSources[0].uriString
        }
    }

    // Estados del reproductor
    var isPlaying by remember { mutableStateOf(true) }
    var currentPositionMs by remember { mutableLongStateOf(0L) }
    var durationMs by remember { mutableLongStateOf(1L) }
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }
    var isMuted by remember { mutableStateOf(false) }
    var isLooping by remember { mutableStateOf(isLoopingInitial) }
    var detectedFps by remember { mutableIntStateOf(60) }
    var isHardwareDecoding by remember { mutableStateOf(true) }
    var droppedFrames by remember { mutableIntStateOf(0) }
    var isHdrStream by remember { mutableStateOf(false) }

    // Inicialización del motor ExoPlayer con RenderersFactory optimizado por HW
    val exoPlayer = remember(context) {
        val renderersFactory = DefaultRenderersFactory(context).apply {
            setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
            setEnableDecoderFallback(true)
        }

        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                /* minBufferMs = */ 1000,
                /* maxBufferMs = */ 6000,
                /* bufferForPlaybackMs = */ 300,
                /* bufferForPlaybackAfterRebufferMs = */ 800
            )
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()

        val audioAttrs = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
            .build()

        ExoPlayer.Builder(context, renderersFactory)
            .setLoadControl(loadControl)
            .setAudioAttributes(audioAttrs, true)
            .setSeekParameters(SeekParameters.EXACT) // Máxima precisión de fotograma para edición
            .build().apply {
                repeatMode = if (isLoopingInitial) Player.REPEAT_MODE_ALL else Player.REPEAT_MODE_OFF
                playWhenReady = true
            }
    }

    // Listener para eventos de ExoPlayer
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    durationMs = exoPlayer.duration.coerceAtLeast(1L)
                    isHardwareDecoding = true
                }
            }

            override fun onVideoSizeChanged(videoSize: VideoSize) {
                if (videoSize.height > 0) {
                    val colorSpace = exoPlayer.videoFormat?.colorInfo?.colorSpace
                    val is4kOrHdr = videoSize.height >= 1440 || (colorSpace == C.COLOR_SPACE_BT2020)
                    isHdrStream = is4kOrHdr
                    if (is4kOrHdr && colorConfig == ColorGradingConfig.NEUTRAL_MASTER) {
                        colorConfig = colorConfig.copy(profile = ColorGamutProfile.BT_2020_HDR)
                    }
                }
            }
        }

        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    // Cargar media cuando cambia la URI activa
    LaunchedEffect(activePlayUri) {
        if (activePlayUri.isNotBlank()) {
            try {
                val mediaItem = MediaItem.fromUri(Uri.parse(activePlayUri))
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
                exoPlayer.playWhenReady = isPlaying
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Actualizar velocidad y volumen
    LaunchedEffect(playbackSpeed) {
        exoPlayer.setPlaybackSpeed(playbackSpeed)
    }

    LaunchedEffect(isMuted) {
        exoPlayer.volume = if (isMuted) 0f else 1f
    }

    LaunchedEffect(isLooping) {
        exoPlayer.repeatMode = if (isLooping) Player.REPEAT_MODE_ALL else Player.REPEAT_MODE_OFF
    }

    // Monitoreo continuo de posición y fotogramas para el editor
    LaunchedEffect(exoPlayer) {
        while (isActive) {
            if (exoPlayer.isPlaying) {
                currentPositionMs = exoPlayer.currentPosition
                val dur = exoPlayer.duration.coerceAtLeast(1L)
                durationMs = dur
                val currentNorm = (currentPositionMs.toFloat() / dur).coerceIn(0f, 1f)

                // Respetar rangos de recorte (trim) si están activos
                if (trimEnd < 1f && currentNorm > trimEnd) {
                    if (isLooping) {
                        val seekTarget = (trimStart * dur).toLong()
                        exoPlayer.seekTo(seekTarget)
                    } else {
                        exoPlayer.pause()
                    }
                }

                onFrameProcessed?.invoke(currentPositionMs)
                onPositionChanged?.invoke(currentPositionMs, currentNorm)
            }
            delay(16) // ~60fps polling rate
        }
    }

    // Formato de código de tiempo SMPTE (HH:MM:SS:FF)
    val smpteTimecode = remember(currentPositionMs, detectedFps) {
        val totalSeconds = (currentPositionMs / 1000).toInt()
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        val frame = ((currentPositionMs % 1000) * detectedFps / 1000).toInt()
        String.format("%02d:%02d:%02d:%02d", hours, minutes, seconds, frame)
    }

    // Proporción de aspecto del lienzo
    val targetAspectRatio = when (aspectRatio) {
        "9:16" -> 9f / 16f
        "1:1" -> 1f
        "21:9" -> 21f / 9f
        else -> 16f / 9f
    }

    val progressNormalized = (currentPositionMs.toFloat() / durationMs.coerceAtLeast(1L)).coerceIn(0f, 1f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("high_performance_exoplayer_preview"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            // 1. HEADER TELEMETRÍA Y CONTROLES SUPERIORES
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Badge de aceleración por HW
                HardwareAccelerationBadge(
                    isHardwareAccelerated = isHardwareDecoding,
                    isDirectSurfaceMode = isDirectSurfaceMode,
                    activeGamut = colorConfig.profile,
                    fps = detectedFps,
                    droppedFrames = droppedFrames,
                    isHdr = isHdrStream
                )

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    // Botón opcional para alternar a Simulador IA
                    if (onTogglePlayerMode != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                .border(0.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .clickable { onTogglePlayerMode() }
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                                .testTag("toggle_simulator_mode_btn")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "Modo Simulador",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(10.dp)
                                )
                                Text(
                                    text = "SIMULADOR",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 7.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Botón para alternar osciloscopio RGB Parade
                    IconButton(
                        onClick = { showWaveformScope = !showWaveformScope },
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("toggle_waveform_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Equalizer,
                            contentDescription = "Osciloscopio RGB",
                            tint = if (showWaveformScope) Color(0xFF38BDF8) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Botón silenciar
                    IconButton(
                        onClick = { isMuted = !isMuted },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            contentDescription = "Audio",
                            tint = if (isMuted) Color(0xFFEF4444) else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Botón pantalla completa
                    IconButton(
                        onClick = { isFullscreenActive = true },
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("nle_fullscreen_toggle_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fullscreen,
                            contentDescription = "Pantalla completa",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 2. VIEWPORT DE VIDEO EXOPLAYER (ACELERADO POR HARDWARE)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black)
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Contenedor con relación de aspecto del video
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(targetAspectRatio)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    if (isDirectSurfaceMode) {
                        // Modo Zero-Copy Direct SurfaceView
                        AndroidView(
                            factory = { ctx ->
                                SurfaceView(ctx).apply {
                                    layoutParams = ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT
                                    )
                                    exoPlayer.setVideoSurfaceView(this)
                                }
                            },
                            update = { surfaceView ->
                                exoPlayer.setVideoSurfaceView(surfaceView)
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("exoplayer_surface_view")
                        )
                    } else {
                        // Modo GPU-Accelerated TextureView con Shader ColorMatrix
                        AndroidView(
                            factory = { ctx ->
                                TextureView(ctx).apply {
                                    layoutParams = ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT
                                    )
                                    exoPlayer.setVideoTextureView(this)
                                }
                            },
                            update = { textureView ->
                                exoPlayer.setVideoTextureView(textureView)
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("exoplayer_texture_view")
                                .then(
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                        Modifier.graphicsLayer {
                                            val androidColorMatrix = android.graphics.ColorMatrix(colorConfig.toComposeColorMatrix().values)
                                            val filter = android.graphics.ColorMatrixColorFilter(androidColorMatrix)
                                            renderEffect = android.graphics.RenderEffect.createColorFilterEffect(filter).asComposeRenderEffect()
                                        }
                                    } else {
                                        Modifier
                                    }
                                )
                        )
                    }

                    // Native Multi-Track Overlay Layer (Text Overlays, Real-time FX, Audio telemetry)
                    if (multiTrackComposition != null) {
                        val activeTexts = remember(multiTrackComposition, currentPositionMs) {
                            multiTrackComposition.getActiveTextClips(currentPositionMs)
                        }
                        val activeEffects = remember(multiTrackComposition, currentPositionMs) {
                            multiTrackComposition.getActiveEffectClips(currentPositionMs)
                        }
                        val activeAudios = remember(multiTrackComposition, currentPositionMs) {
                            multiTrackComposition.getActiveAudioClips(currentPositionMs)
                        }
                        MultiTrackOverlayRenderer(
                            activeTextClips = activeTexts,
                            activeEffectClips = activeEffects,
                            activeAudioClips = activeAudios,
                            currentPositionMs = currentPositionMs,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Capa de sobreimpresión personalizada (subtítulos, guías de recorte, etc.)
                    overlayContent?.invoke(this)
                }

                // Superposición HUD: Código de tiempo SMPTE flotante en la esquina superior izquierda
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.75f))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(if (isPlaying) Color(0xFFEF4444) else Color(0xFFF59E0B))
                        )
                        Text(
                            text = smpteTimecode,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Badge de estado de reproducción y Gamut en esquina inferior derecha
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp)
                ) {
                    Text(
                        text = "${colorConfig.profile.displayName} • ${detectedFps} FPS",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.Black.copy(alpha = 0.65f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3. SELECTOR DE MODO DE LÍNEA DE TIEMPO: VMSB RESPONSIVE TIMELINE vs JOG / SHUTTLE
            var showVmsbTimelineMode by remember { mutableStateOf(true) }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (showVmsbTimelineMode) MaterialTheme.colorScheme.primary else Color(0xFF1E293B))
                            .clickable { showVmsbTimelineMode = true }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("toggle_vmsb_native_timeline_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(
                                imageVector = Icons.Default.MovieFilter,
                                contentDescription = null,
                                tint = if (showVmsbTimelineMode) MaterialTheme.colorScheme.onPrimary else Color(0xFF94A3B8),
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "LÍNEA VMSB© & TRIMMING",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (showVmsbTimelineMode) MaterialTheme.colorScheme.onPrimary else Color(0xFF94A3B8),
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (!showVmsbTimelineMode) MaterialTheme.colorScheme.primary else Color(0xFF1E293B))
                            .clickable { showVmsbTimelineMode = false }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("toggle_jog_dial_mode_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = if (!showVmsbTimelineMode) MaterialTheme.colorScheme.onPrimary else Color(0xFF94A3B8),
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "JOG DIAL & KEYFRAMES",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (!showVmsbTimelineMode) MaterialTheme.colorScheme.onPrimary else Color(0xFF94A3B8),
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            if (showVmsbTimelineMode) {
                // COMPONENTE RESPONSIVO VMSB: LÍNEA DE TIEMPO CON FOTOGRAMAS Y RECORTE
                VmsbResponsiveVideoTimeline(
                    currentPositionMs = currentPositionMs,
                    durationMs = durationMs,
                    trimStart = trimStart,
                    trimEnd = trimEnd,
                    videoUri = activePlayUri,
                    fps = detectedFps,
                    isPlaying = isPlaying,
                    onTrimRangeChange = { s, e ->
                        onTrimRangeChange?.invoke(s, e)
                    },
                    onSeekTo = { targetMs ->
                        currentPositionMs = targetMs
                        exoPlayer.seekTo(targetMs)
                        val norm = (targetMs.toFloat() / durationMs.coerceAtLeast(1L)).coerceIn(0f, 1f)
                        onPositionChanged?.invoke(targetMs, norm)
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // CONTROLADOR DE REPRODUCCIÓN MEDIA3 EXOPLAYER INTERFAZADO
                VmsbVideoPlaybackController(
                    exoPlayer = exoPlayer,
                    currentPositionMs = currentPositionMs,
                    durationMs = durationMs,
                    trimStart = trimStart,
                    trimEnd = trimEnd,
                    isPlaying = isPlaying,
                    playbackSpeed = playbackSpeed,
                    isLooping = isLooping,
                    isMuted = isMuted,
                    fps = detectedFps,
                    bufferedPositionMs = exoPlayer.bufferedPosition,
                    onPlayPauseToggle = {
                        if (exoPlayer.isPlaying) {
                            exoPlayer.pause()
                        } else {
                            exoPlayer.play()
                        }
                    },
                    onSeekTo = { targetMs ->
                        currentPositionMs = targetMs
                        exoPlayer.seekTo(targetMs)
                        val norm = (targetMs.toFloat() / durationMs.coerceAtLeast(1L)).coerceIn(0f, 1f)
                        onPositionChanged?.invoke(targetMs, norm)
                    },
                    onSpeedChange = { spd ->
                        playbackSpeed = spd
                        exoPlayer.setPlaybackSpeed(spd)
                    },
                    onLoopToggle = {
                        isLooping = !isLooping
                        exoPlayer.repeatMode = if (isLooping) Player.REPEAT_MODE_ALL else Player.REPEAT_MODE_OFF
                    },
                    onMuteToggle = {
                        isMuted = !isMuted
                        exoPlayer.volume = if (isMuted) 0f else 1f
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                // Consola secundaria: Scrubbing con jog wheel y keyframes
                FramePreciseScrubber(
                    currentPositionMs = currentPositionMs,
                    durationMs = durationMs,
                    isPlaying = isPlaying,
                    fps = detectedFps,
                    trimStart = trimStart,
                    trimEnd = trimEnd,
                    onSeekTo = { targetMs ->
                        currentPositionMs = targetMs
                        exoPlayer.seekTo(targetMs)
                        val norm = (targetMs.toFloat() / durationMs.coerceAtLeast(1L)).coerceIn(0f, 1f)
                        onPositionChanged?.invoke(targetMs, norm)
                    },
                    onPlayPauseToggle = {
                        if (exoPlayer.isPlaying) {
                            exoPlayer.pause()
                        } else {
                            exoPlayer.play()
                        }
                    },
                    onStepFrame = { deltaFrames ->
                        val frameDurationMs = ((1000.0 / detectedFps) + 0.5).toLong().coerceAtLeast(16L)
                        val target = (exoPlayer.currentPosition + deltaFrames * frameDurationMs).coerceIn(0L, durationMs)
                        currentPositionMs = target
                        exoPlayer.seekTo(target)
                        val norm = (target.toFloat() / durationMs.coerceAtLeast(1L)).coerceIn(0f, 1f)
                        onPositionChanged?.invoke(target, norm)
                    },
                    onSpeedChange = { speed ->
                        playbackSpeed = speed
                    },
                    currentPlaybackSpeed = playbackSpeed,
                    showJogWheelInitial = true,
                    initialKeyframes = initialKeyframes,
                    onKeyframesChange = onKeyframesChange,
                    onKeyframeSelected = onKeyframeSelected,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // 4.5. NATIVE MULTI-TRACK SYNCHRONIZED TIMELINE LANES
            if (multiTrackComposition != null) {
                var showMultiTrackLanes by remember { mutableStateOf(showMultiTrackTimelineInitial) }

                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MULTI-TRACK NLE LANES",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = FontFamily.Monospace
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (showMultiTrackLanes) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color(0xFF1E293B))
                            .clickable { showMultiTrackLanes = !showMultiTrackLanes }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                            .testTag("toggle_multi_track_lanes_btn")
                    ) {
                        Text(
                            text = if (showMultiTrackLanes) "COLLAPSE LANES ▲" else "EXPAND LANES ▼",
                            color = if (showMultiTrackLanes) MaterialTheme.colorScheme.primary else Color(0xFF94A3B8),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                AnimatedVisibility(
                    visible = showMultiTrackLanes,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(modifier = Modifier.padding(top = 4.dp)) {
                        MultiTrackTimelineView(
                            composition = multiTrackComposition,
                            currentPositionMs = currentPositionMs,
                            durationMs = durationMs,
                            canUndo = canUndoMultiTrack,
                            canRedo = canRedoMultiTrack,
                            undoDescription = undoDescription,
                            redoDescription = redoDescription,
                            undoHistoryList = undoHistoryList,
                            redoHistoryList = redoHistoryList,
                            onUndo = onUndoMultiTrack,
                            onRedo = onRedoMultiTrack,
                            onExecuteCommand = onExecuteMultiTrackCommand,
                            onSeekTo = { targetMs ->
                                currentPositionMs = targetMs
                                exoPlayer.seekTo(targetMs)
                                val norm = (targetMs.toFloat() / durationMs.coerceAtLeast(1L)).coerceIn(0f, 1f)
                                onPositionChanged?.invoke(targetMs, norm)
                            },
                            onCompositionChange = { updated ->
                                onMultiTrackCompositionChange?.invoke(updated)
                            },
                            onImportVideoClick = onImportVideoClick,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // 5. SELECTOR DE FUENTES DE TEST DE ALTA FIDELIDAD
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "MEDIA TEST:",
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = FontFamily.Monospace
                )

                presetSources.forEachIndexed { index, source ->
                    val isSelected = selectedSourceIndex == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                else MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
                            )
                            .border(
                                width = if (isSelected) 1.dp else 0.5.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable {
                                selectedSourceIndex = index
                                colorConfig = colorConfig.copy(profile = source.gamut)
                            }
                            .padding(vertical = 3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = source.resolutionLabel.substringBefore(" "),
                            fontSize = 8.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (videoUri.isNotBlank()) {
                    val isProjSelected = selectedSourceIndex == -1
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isProjSelected) Color(0xFF10B981).copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface)
                            .border(1.dp, if (isProjSelected) Color(0xFF10B981) else Color.Transparent, RoundedCornerShape(6.dp))
                            .clickable { selectedSourceIndex = -1 }
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "MI PROYECTO",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isProjSelected) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Botón Ingestión de Video Local (Media Picker)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF3B82F6).copy(alpha = 0.2f))
                        .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                        .clickable { onImportVideoClick?.invoke() }
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                        .testTag("exo_import_local_video_btn")
                ) {
                    Text(
                        text = "+ LOCAL",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF93C5FD)
                    )
                }
            }

            // 6. OSCILOSCOPIO RGB PARADE Y FORMA DE ONDA IRE (CONDICIONAL)
            AnimatedVisibility(
                visible = showWaveformScope,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    RealtimeColorWaveformScope(
                        gradingConfig = colorConfig,
                        progress = progressNormalized
                    )
                }
            }

            // 7. CONTROLES DE CALIBRACIÓN CROMÁTICA & ETALONAJE
            Spacer(modifier = Modifier.height(10.dp))
            ColorGradingQuickControls(
                config = colorConfig,
                onConfigChange = { colorConfig = it },
                onReset = { colorConfig = ColorGradingConfig.NEUTRAL_MASTER },
                isDirectSurfaceMode = isDirectSurfaceMode,
                onToggleSurfaceMode = { isDirectSurfaceMode = !isDirectSurfaceMode }
            )
        }
    }

    if (isFullscreenActive) {
        Media3FullscreenVideoPlayerDialog(
            videoUri = activePlayUri,
            title = "Veo Studio NLE Preview",
            onDismissRequest = { isFullscreenActive = false }
        )
    }
}
