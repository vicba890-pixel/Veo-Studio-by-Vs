package com.example.ui.components.preview

import android.content.Context
import android.net.Uri
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.SeekParameters
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

/**
 * =========================================================================================
 * VMSB© CUSTOM MEDIA3 EXOPLAYER VIDEO PLAYER FOR EDITING INTERFACE
 * =========================================================================================
 *
 * Professional video editing player component built on top of androidx.media3.exoplayer.
 * Engineered specifically for non-linear video editing (NLE), frame-accurate inspection,
 * timeline synchronization, and real-time color grading / smart-crop previews.
 *
 * Primary Capabilities:
 * - Play / Pause: Center HUD button with spring scale, bottom bar control, and gesture tap.
 * - Seek Controls: High-precision scrubbing slider, buffered position indicator,
 *   ±1 frame stepping (-1F / +1F) for frame-accurate cuts, and ±10s quick jump.
 * - Fullscreen Toggle: Seamless transition between embedded editing canvas and edge-to-edge
 *   theater dialog with immersive controls.
 * - Editing HUD: SMPTE timecode (HH:MM:SS:FF), playback rate selector (0.25x - 2.0x),
 *   audio volume/mute, aspect ratio resize modes, and custom overlay slots.
 */

/**
 * Formats milliseconds into precise SMPTE timecode string: HH:MM:SS:FF (or MM:SS).
 */
fun formatSmpteTimecode(timeMs: Long, fps: Int = 30): String {
    if (timeMs <= 0) return "00:00:00:00"
    val safeFps = fps.coerceIn(12, 120)
    val totalSeconds = timeMs / 1000
    val frames = ((timeMs % 1000) * safeFps / 1000).toInt()
    val seconds = (totalSeconds % 60).toInt()
    val minutes = ((totalSeconds / 60) % 60).toInt()
    val hours = (totalSeconds / 3600).toInt()

    return String.format("%02d:%02d:%02d:%02d", hours, minutes, seconds, frames)
}

/**
 * Custom Media3 ExoPlayer component with Play/Pause, Seek, and Fullscreen Toggle controls.
 */
@OptIn(UnstableApi::class)
@Composable
fun CustomMedia3VideoPlayer(
    videoUri: String,
    modifier: Modifier = Modifier,
    title: String? = null,
    aspectRatio: Float = 16f / 9f,
    autoPlay: Boolean = false,
    isLoopingInitial: Boolean = true,
    initialMuted: Boolean = false,
    initialSpeed: Float = 1.0f,
    fps: Int = 30,
    showControlsInitially: Boolean = true,
    controlsTimeoutMs: Long = 3500L,
    onPositionChanged: ((currentMs: Long, durationMs: Long) -> Unit)? = null,
    onPlayPauseChange: ((isPlaying: Boolean) -> Unit)? = null,
    onSeekCompleted: ((positionMs: Long) -> Unit)? = null,
    onFullscreenToggle: ((isFullscreen: Boolean) -> Unit)? = null,
    overlayContent: (@Composable BoxScope.() -> Unit)? = null
) {
    val context = LocalContext.current
    val frameDurationMs = remember(fps) { (1000.0 / fps.coerceIn(12, 120)).toLong().coerceAtLeast(16L) }

    // Internal playback states
    var isPlaying by remember { mutableStateOf(false) }
    var playbackState by remember { mutableIntStateOf(Player.STATE_IDLE) }
    var currentPositionMs by remember { mutableLongStateOf(0L) }
    var durationMs by remember { mutableLongStateOf(0L) }
    var bufferedPositionMs by remember { mutableLongStateOf(0L) }
    var isMuted by remember { mutableStateOf(initialMuted) }
    var playbackSpeed by remember { mutableFloatStateOf(initialSpeed) }
    var isLooping by remember { mutableStateOf(isLoopingInitial) }
    var scaleMode by remember { mutableStateOf(PlayerScaleMode.FIT) }
    var isFullscreen by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var videoWidth by remember { mutableIntStateOf(0) }
    var videoHeight by remember { mutableIntStateOf(0) }

    // Controls visibility
    var areControlsVisible by remember { mutableStateOf(showControlsInitially) }
    var lastInteractionTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var isScrubbing by remember { mutableStateOf(false) }
    var scrubTargetMs by remember { mutableLongStateOf(0L) }

    // Double-tap visual feedback
    var showDoubleTapSeekLeft by remember { mutableStateOf(false) }
    var showDoubleTapSeekRight by remember { mutableStateOf(false) }

    // Initialize ExoPlayer with SeekParameters.EXACT for frame precision
    val exoPlayer = remember(context) {
        val renderersFactory = DefaultRenderersFactory(context).apply {
            setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
            setEnableDecoderFallback(true)
        }
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
            .build()

        ExoPlayer.Builder(context, renderersFactory)
            .setAudioAttributes(audioAttributes, true)
            .setSeekParameters(SeekParameters.EXACT)
            .build().apply {
                playWhenReady = autoPlay
                repeatMode = if (isLoopingInitial) Player.REPEAT_MODE_ALL else Player.REPEAT_MODE_OFF
            }
    }

    // Attach ExoPlayer state listener
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
                onPlayPauseChange?.invoke(playing)
                if (playing) {
                    lastInteractionTime = System.currentTimeMillis()
                }
            }

            override fun onPlaybackStateChanged(state: Int) {
                playbackState = state
                if (state == Player.STATE_READY) {
                    durationMs = exoPlayer.duration.coerceAtLeast(0L)
                    errorMessage = null
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                errorMessage = "Playback error: ${error.errorCodeName}"
            }

            override fun onVideoSizeChanged(videoSize: VideoSize) {
                videoWidth = videoSize.width
                videoHeight = videoSize.height
            }
        }

        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    // Load media item when URI changes
    LaunchedEffect(videoUri) {
        if (videoUri.isNotBlank()) {
            try {
                errorMessage = null
                val mediaItem = MediaItem.fromUri(Uri.parse(videoUri))
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
                exoPlayer.playWhenReady = autoPlay
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to load video"
            }
        }
    }

    // Keep speed, volume, and repeat mode synced with player
    LaunchedEffect(playbackSpeed) {
        exoPlayer.setPlaybackSpeed(playbackSpeed)
    }

    LaunchedEffect(isMuted) {
        exoPlayer.volume = if (isMuted) 0f else 1f
    }

    LaunchedEffect(isLooping) {
        exoPlayer.repeatMode = if (isLooping) Player.REPEAT_MODE_ALL else Player.REPEAT_MODE_OFF
    }

    // High frequency position tracker while playing
    LaunchedEffect(isPlaying, isScrubbing) {
        while (isActive) {
            if (isPlaying && !isScrubbing) {
                currentPositionMs = exoPlayer.currentPosition
                durationMs = exoPlayer.duration.coerceAtLeast(0L)
                bufferedPositionMs = exoPlayer.bufferedPosition
                onPositionChanged?.invoke(currentPositionMs, durationMs)
            }
            delay(100)
        }
    }

    // Auto-hide controls timer during playback
    LaunchedEffect(areControlsVisible, isPlaying, isScrubbing, lastInteractionTime) {
        if (areControlsVisible && isPlaying && !isScrubbing) {
            delay(controlsTimeoutMs)
            areControlsVisible = false
        }
    }

    // Helper functions for seek operations
    fun seekToPosition(targetMs: Long) {
        val bounded = targetMs.coerceIn(0L, durationMs.coerceAtLeast(0L))
        exoPlayer.seekTo(bounded)
        currentPositionMs = bounded
        onPositionChanged?.invoke(bounded, durationMs)
        onSeekCompleted?.invoke(bounded)
        lastInteractionTime = System.currentTimeMillis()
    }

    fun stepFrames(frameCount: Int) {
        val target = currentPositionMs + (frameCount * frameDurationMs)
        seekToPosition(target)
    }

    // Reusable core surface composable
    val playerSurface: @Composable (isFullScreenView: Boolean) -> Unit = { isFullScreenView ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            // 1. AndroidView wrapping Media3 PlayerView
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = false
                        resizeMode = scaleMode.resizeMode
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        setShutterBackgroundColor(android.graphics.Color.TRANSPARENT)
                    }
                },
                update = { playerView ->
                    playerView.player = exoPlayer
                    playerView.resizeMode = scaleMode.resizeMode
                },
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("custom_media3_player_surface")
            )

            // 2. Gesture detection for tap controls & double-tap seek
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                areControlsVisible = !areControlsVisible
                                lastInteractionTime = System.currentTimeMillis()
                            },
                            onDoubleTap = { offset ->
                                lastInteractionTime = System.currentTimeMillis()
                                if (offset.x < size.width / 2) {
                                    // Rewind 10s
                                    seekToPosition(currentPositionMs - 10000L)
                                    showDoubleTapSeekLeft = true
                                } else {
                                    // Forward 10s
                                    seekToPosition(currentPositionMs + 10000L)
                                    showDoubleTapSeekRight = true
                                }
                            }
                        )
                    }
            )

            // Double tap visual animations
            if (showDoubleTapSeekLeft) {
                LaunchedEffect(Unit) {
                    delay(600)
                    showDoubleTapSeekLeft = false
                }
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.45f)
                        .align(Alignment.CenterStart)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.FastRewind,
                            contentDescription = "Retroceder 10 segundos",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = "-10s",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            if (showDoubleTapSeekRight) {
                LaunchedEffect(Unit) {
                    delay(600)
                    showDoubleTapSeekRight = false
                }
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.45f)
                        .align(Alignment.CenterEnd)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.FastForward,
                            contentDescription = "Avanzar 10 segundos",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = "+10s",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // 3. Buffering Indicator
            if (playbackState == Player.STATE_BUFFERING) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 3.dp,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("custom_media3_buffering_indicator")
                )
            }

            // 4. Error Message Display
            if (errorMessage != null) {
                Surface(
                    color = Color.Black.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFEF4444)),
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.Center)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = "Error de reproducción",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage ?: "Error desconocido",
                            color = Color.White,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                errorMessage = null
                                exoPlayer.prepare()
                                exoPlayer.play()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Reintentar", fontSize = 12.sp)
                        }
                    }
                }
            }

            // 5. Custom External Editorial Overlays (LUTs, Guides, Safe Margins)
            overlayContent?.invoke(this)

            // 6. Complete Editorial Controls Overlay
            AnimatedVisibility(
                visible = areControlsVisible || !isPlaying || isScrubbing,
                enter = fadeIn(animationSpec = tween(200)),
                exit = fadeOut(animationSpec = tween(200)),
                modifier = Modifier.fillMaxSize()
            ) {
                CustomPlayerControlsOverlay(
                    title = title,
                    isPlaying = isPlaying,
                    playbackState = playbackState,
                    currentPositionMs = if (isScrubbing) scrubTargetMs else currentPositionMs,
                    durationMs = durationMs,
                    bufferedPositionMs = bufferedPositionMs,
                    isMuted = isMuted,
                    playbackSpeed = playbackSpeed,
                    isLooping = isLooping,
                    scaleMode = scaleMode,
                    isFullscreen = isFullScreenView,
                    fps = fps,
                    onPlayPauseToggle = {
                        lastInteractionTime = System.currentTimeMillis()
                        if (exoPlayer.isPlaying) {
                            exoPlayer.pause()
                        } else {
                            if (exoPlayer.playbackState == Player.STATE_ENDED) {
                                exoPlayer.seekTo(0L)
                            }
                            exoPlayer.play()
                        }
                    },
                    onSeekStarted = {
                        isScrubbing = true
                        scrubTargetMs = currentPositionMs
                        lastInteractionTime = System.currentTimeMillis()
                    },
                    onSeekChanged = { newMs ->
                        scrubTargetMs = newMs
                        lastInteractionTime = System.currentTimeMillis()
                    },
                    onSeekFinished = { targetMs ->
                        isScrubbing = false
                        seekToPosition(targetMs)
                    },
                    onStepForward = {
                        stepFrames(1)
                    },
                    onStepBackward = {
                        stepFrames(-1)
                    },
                    onRewind10s = {
                        seekToPosition(currentPositionMs - 10000L)
                    },
                    onForward10s = {
                        seekToPosition(currentPositionMs + 10000L)
                    },
                    onMuteToggle = {
                        isMuted = !isMuted
                        lastInteractionTime = System.currentTimeMillis()
                    },
                    onSpeedChange = { newSpeed ->
                        playbackSpeed = newSpeed
                        lastInteractionTime = System.currentTimeMillis()
                    },
                    onLoopToggle = {
                        isLooping = !isLooping
                        lastInteractionTime = System.currentTimeMillis()
                    },
                    onScaleModeToggle = {
                        scaleMode = if (scaleMode == PlayerScaleMode.FIT) PlayerScaleMode.FILL else PlayerScaleMode.FIT
                        lastInteractionTime = System.currentTimeMillis()
                    },
                    onFullscreenToggle = {
                        val next = !isFullscreen
                        isFullscreen = next
                        onFullscreenToggle?.invoke(next)
                        lastInteractionTime = System.currentTimeMillis()
                    }
                )
            }
        }
    }

    // Standard Embedded Editing Viewport
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("custom_media3_video_player"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = BorderStroke(1.dp, Color(0xFF334155))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(aspectRatio)
                .clip(RoundedCornerShape(16.dp))
        ) {
            playerSurface(false)
        }
    }

    // Fullscreen Immersive Theater Dialog
    if (isFullscreen) {
        Dialog(
            onDismissRequest = {
                isFullscreen = false
                onFullscreenToggle?.invoke(false)
            },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            BackHandler {
                isFullscreen = false
                onFullscreenToggle?.invoke(false)
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .testTag("custom_media3_fullscreen_dialog")
            ) {
                playerSurface(true)
            }
        }
    }
}

/**
 * Editorial Controls Overlay for Custom Media3 Video Player.
 */
@Composable
fun CustomPlayerControlsOverlay(
    title: String?,
    isPlaying: Boolean,
    playbackState: Int,
    currentPositionMs: Long,
    durationMs: Long,
    bufferedPositionMs: Long,
    isMuted: Boolean,
    playbackSpeed: Float,
    isLooping: Boolean,
    scaleMode: PlayerScaleMode,
    isFullscreen: Boolean,
    fps: Int,
    onPlayPauseToggle: () -> Unit,
    onSeekStarted: () -> Unit,
    onSeekChanged: (Long) -> Unit,
    onSeekFinished: (Long) -> Unit,
    onStepBackward: () -> Unit,
    onStepForward: () -> Unit,
    onRewind10s: () -> Unit,
    onForward10s: () -> Unit,
    onMuteToggle: () -> Unit,
    onSpeedChange: (Float) -> Unit,
    onLoopToggle: () -> Unit,
    onScaleModeToggle: () -> Unit,
    onFullscreenToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showSpeedMenu by remember { mutableStateOf(false) }

    val isEnded = playbackState == Player.STATE_ENDED
    val progressFraction = remember(currentPositionMs, durationMs) {
        if (durationMs > 0) (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f
    }
    val bufferedFraction = remember(bufferedPositionMs, durationMs) {
        if (durationMs > 0) (bufferedPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.75f),
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.88f)
                    )
                )
            )
            .testTag("custom_media3_controls_overlay")
    ) {
        // TOP CONTROL BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                if (isFullscreen) {
                    IconButton(
                        onClick = onFullscreenToggle,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("custom_player_exit_fullscreen_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Salir de pantalla completa",
                            tint = Color.White
                        )
                    }
                }

                Column {
                    Text(
                        text = title ?: "Media3 Video Editor Viewport",
                        color = Color.White,
                        fontSize = if (isFullscreen) 14.sp else 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "ExoPlayer Engine • ${fps} FPS • Seek Exacto",
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Top action buttons: Loop & Aspect Ratio
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = onLoopToggle,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("custom_player_loop_btn")
                ) {
                    Icon(
                        imageVector = if (isLooping) Icons.Default.RepeatOne else Icons.Default.Repeat,
                        contentDescription = "Repetición en bucle",
                        tint = if (isLooping) MaterialTheme.colorScheme.primary else Color(0xFF94A3B8),
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onScaleModeToggle,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("custom_player_scale_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.AspectRatio,
                        contentDescription = "Modo de escala",
                        tint = if (scaleMode == PlayerScaleMode.FILL) MaterialTheme.colorScheme.primary else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // CENTER HUD CONTROLS: REWIND 10s, STEP BACK -1F, PLAY/PAUSE, STEP FWD +1F, FWD 10s
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Quick rewind 10s
            IconButton(
                onClick = onRewind10s,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .testTag("custom_player_rewind_10s_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.FastRewind,
                    contentDescription = "Retroceder 10 segundos",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Step Backward -1 Frame (Editor Precision)
            IconButton(
                onClick = onStepBackward,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .border(0.5.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                    .testTag("custom_player_step_backward_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Retroceder 1 fotograma (-1F)",
                    tint = Color(0xFF38BDF8),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Center Primary Play/Pause Button
            val playScale by animateFloatAsState(
                targetValue = if (isPlaying) 1.0f else 1.08f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "customPlayerPlayScale"
            )

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .scale(playScale)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .border(2.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true, color = Color.White),
                        onClick = onPlayPauseToggle
                    )
                    .testTag("custom_player_center_play_btn"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when {
                        isEnded -> Icons.Default.Replay
                        isPlaying -> Icons.Default.Pause
                        else -> Icons.Default.PlayArrow
                    },
                    contentDescription = if (isPlaying) "Pausar reproducción" else "Iniciar reproducción",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(34.dp)
                )
            }

            // Step Forward +1 Frame (Editor Precision)
            IconButton(
                onClick = onStepForward,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .border(0.5.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                    .testTag("custom_player_step_forward_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Avanzar 1 fotograma (+1F)",
                    tint = Color(0xFF38BDF8),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Quick forward 10s
            IconButton(
                onClick = onForward10s,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .testTag("custom_player_forward_10s_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.FastForward,
                    contentDescription = "Avanzar 10 segundos",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // BOTTOM EDITORIAL BAR: HIGH-PRECISION SEEKBAR & TIMECODE
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // Interactive Scrubber & Buffer Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(26.dp),
                contentAlignment = Alignment.Center
            ) {
                // Buffer line in background
                LinearProgressIndicator(
                    progress = { bufferedFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = Color.White.copy(alpha = 0.35f),
                    trackColor = Color.White.copy(alpha = 0.15f),
                )

                // Seek Slider with smooth thumb
                Slider(
                    value = progressFraction,
                    onValueChange = { frac ->
                        val targetMs = (frac * durationMs).toLong()
                        onSeekStarted()
                        onSeekChanged(targetMs)
                    },
                    onValueChangeFinished = {
                        val targetMs = (progressFraction * durationMs).toLong()
                        onSeekFinished(targetMs)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_player_seek_slider"),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = Color.Transparent
                    )
                )
            }

            // Bottom Control Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Bottom Play/Pause & SMPTE Timecode
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = onPlayPauseToggle,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("custom_player_bottom_play_btn")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pausar" else "Reproducir",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Precise SMPTE timecode (HH:MM:SS:FF / HH:MM:SS:FF)
                    Text(
                        text = "${formatSmpteTimecode(currentPositionMs, fps)} / ${formatSmpteTimecode(durationMs, fps)}",
                        color = Color.White,
                        fontSize = 10.5.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.testTag("custom_player_timecode_text")
                    )
                }

                // Action Controls: Speed + Audio Mute + Fullscreen Toggle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Playback Speed Selector (0.25x - 2.0x)
                    Box {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Black.copy(alpha = 0.55f),
                            border = BorderStroke(0.5.dp, Color(0xFF475569)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { showSpeedMenu = true }
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                                .testTag("custom_player_speed_btn")
                        ) {
                            Text(
                                text = "${playbackSpeed}x",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        DropdownMenu(
                            expanded = showSpeedMenu,
                            onDismissRequest = { showSpeedMenu = false }
                        ) {
                            listOf(0.25f, 0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { speed ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            "${speed}x",
                                            fontWeight = if (playbackSpeed == speed) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        onSpeedChange(speed)
                                        showSpeedMenu = false
                                    }
                                )
                            }
                        }
                    }

                    // Audio Mute Toggle
                    IconButton(
                        onClick = onMuteToggle,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("custom_player_mute_btn")
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = if (isMuted) "Activar audio" else "Silenciar audio",
                            tint = if (isMuted) Color(0xFFEF4444) else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Fullscreen Toggle Button (Crucial editing control)
                    IconButton(
                        onClick = onFullscreenToggle,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("custom_player_fullscreen_btn")
                    ) {
                        Icon(
                            imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                            contentDescription = if (isFullscreen) "Salir de pantalla completa" else "Pantalla completa para edición",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}
