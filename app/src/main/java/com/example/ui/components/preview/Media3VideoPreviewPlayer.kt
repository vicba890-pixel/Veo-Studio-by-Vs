package com.example.ui.components.preview

import android.content.Context
import android.net.Uri
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.vector.ImageVector
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
 * Formats a millisecond duration into standard MM:SS or HH:MM:SS timecode.
 */
fun formatMediaTime(timeMs: Long): String {
    if (timeMs <= 0) return "00:00"
    val totalSeconds = (timeMs / 1000).toInt()
    val seconds = totalSeconds % 60
    val minutes = (totalSeconds / 60) % 60
    val hours = totalSeconds / 3600

    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}

/**
 * Aspect Ratio display options for the player viewport.
 */
enum class PlayerScaleMode(val label: String, val resizeMode: Int) {
    FIT("Fit (16:9)", AspectRatioFrameLayout.RESIZE_MODE_FIT),
    FILL("Fill / Zoom", AspectRatioFrameLayout.RESIZE_MODE_ZOOM),
    FIXED_WIDTH("Fixed Width", AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH)
}

/**
 * High-quality Video Preview Component built with Media3 ExoPlayer.
 * Supports standard playback controls:
 * - Play / Pause (bottom bar + center HUD + surface tap)
 * - Seek (interactive scrubber slider, ±10s quick buttons, double-tap seek)
 * - Fullscreen toggle (modal immersive theater mode with safe insets)
 * - Additional standard controls: Mute/Volume, Playback speed, Loop, Buffering, Aspect scale
 */
@OptIn(UnstableApi::class)
@Composable
fun Media3VideoPreviewPlayer(
    videoUri: String,
    modifier: Modifier = Modifier,
    title: String? = null,
    aspectRatio: Float = 16f / 9f,
    autoPlay: Boolean = true,
    isLoopingInitial: Boolean = true,
    initialMuted: Boolean = false,
    showControlsInitially: Boolean = true,
    controlsTimeoutMs: Long = 3500L,
    onPositionChanged: ((currentMs: Long, durationMs: Long) -> Unit)? = null,
    onPlaybackEnded: (() -> Unit)? = null,
    onFullscreenToggle: ((Boolean) -> Unit)? = null,
    isFullscreenInitially: Boolean = false,
    overlayContent: (@Composable BoxScope.() -> Unit)? = null
) {
    val context = LocalContext.current

    // Internal Fullscreen state
    var isFullscreen by remember { mutableStateOf(isFullscreenInitially) }

    // Playback state
    var isPlaying by remember { mutableStateOf(false) }
    var playbackState by remember { mutableIntStateOf(Player.STATE_IDLE) }
    var currentPositionMs by remember { mutableLongStateOf(0L) }
    var durationMs by remember { mutableLongStateOf(0L) }
    var bufferedPositionMs by remember { mutableLongStateOf(0L) }
    var isMuted by remember { mutableStateOf(initialMuted) }
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }
    var isLooping by remember { mutableStateOf(isLoopingInitial) }
    var scaleMode by remember { mutableStateOf(PlayerScaleMode.FIT) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var videoWidth by remember { mutableIntStateOf(0) }
    var videoHeight by remember { mutableIntStateOf(0) }

    // Controls visibility state
    var areControlsVisible by remember { mutableStateOf(showControlsInitially) }
    var lastInteractionTimestamp by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var isScrubbing by remember { mutableStateOf(false) }
    var scrubTargetMs by remember { mutableLongStateOf(0L) }

    // Double-tap seek visual feedback
    var showDoubleTapSeekLeft by remember { mutableStateOf(false) }
    var showDoubleTapSeekRight by remember { mutableStateOf(false) }

    // ExoPlayer initialization
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
            .setSeekParameters(SeekParameters.CLOSEST_SYNC)
            .build().apply {
                playWhenReady = autoPlay
                repeatMode = if (isLoopingInitial) Player.REPEAT_MODE_ALL else Player.REPEAT_MODE_OFF
            }
    }

    // Attach ExoPlayer listener
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
                if (playing) {
                    lastInteractionTimestamp = System.currentTimeMillis()
                }
            }

            override fun onPlaybackStateChanged(state: Int) {
                playbackState = state
                if (state == Player.STATE_READY) {
                    durationMs = exoPlayer.duration.coerceAtLeast(0L)
                    errorMessage = null
                } else if (state == Player.STATE_ENDED) {
                    onPlaybackEnded?.invoke()
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

    // Load media item on URI change
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

    // Keep speed, volume, and repeat mode synced
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
            delay(150)
        }
    }

    // Auto-hide controls timer
    LaunchedEffect(areControlsVisible, isPlaying, isScrubbing, lastInteractionTimestamp) {
        if (areControlsVisible && isPlaying && !isScrubbing) {
            delay(controlsTimeoutMs)
            areControlsVisible = false
        }
    }

    // Reusable player content
    val playerSurface: @Composable (isFullScreenView: Boolean) -> Unit = { isFullScreenView ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            // 1. AndroidView with Media3 PlayerView
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = false // Use custom Jetpack Compose controls
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
                    .testTag("media3_player_view")
            )

            // 2. Tap detection overlay for play/pause and controls toggle
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                areControlsVisible = !areControlsVisible
                                lastInteractionTimestamp = System.currentTimeMillis()
                            },
                            onDoubleTap = { offset ->
                                lastInteractionTimestamp = System.currentTimeMillis()
                                if (offset.x < size.width / 2) {
                                    // Rewind 10 seconds
                                    val target = (exoPlayer.currentPosition - 10000L).coerceAtLeast(0L)
                                    exoPlayer.seekTo(target)
                                    currentPositionMs = target
                                    showDoubleTapSeekLeft = true
                                } else {
                                    // Fast forward 10 seconds
                                    val target = (exoPlayer.currentPosition + 10000L).coerceAtMost(durationMs)
                                    exoPlayer.seekTo(target)
                                    currentPositionMs = target
                                    showDoubleTapSeekRight = true
                                }
                            }
                        )
                    }
            )

            // Double tap feedback animations
            if (showDoubleTapSeekLeft) {
                LaunchedEffect(Unit) {
                    delay(700)
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
                            contentDescription = "Rewind 10s",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = "-10s",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            if (showDoubleTapSeekRight) {
                LaunchedEffect(Unit) {
                    delay(700)
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
                            contentDescription = "Forward 10s",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = "+10s",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
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
                        .testTag("media3_buffering_indicator")
                )
            }

            // 4. Error Message Display
            if (errorMessage != null) {
                Surface(
                    color = Color.Black.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
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
                            contentDescription = "Error",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage ?: "Unknown error",
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
                            Text("Retry", fontSize = 12.sp)
                        }
                    }
                }
            }

            // 5. Custom External Overlay Content
            overlayContent?.invoke(this)

            // 6. Comprehensive Media3 Controls Overlay (Fade In / Out)
            AnimatedVisibility(
                visible = areControlsVisible || !isPlaying || isScrubbing,
                enter = fadeIn(animationSpec = tween(200)),
                exit = fadeOut(animationSpec = tween(200)),
                modifier = Modifier.fillMaxSize()
            ) {
                Media3PlayerControlsOverlay(
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
                    videoWidth = videoWidth,
                    videoHeight = videoHeight,
                    onPlayPauseToggle = {
                        lastInteractionTimestamp = System.currentTimeMillis()
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
                        lastInteractionTimestamp = System.currentTimeMillis()
                    },
                    onSeekChanged = { newMs ->
                        scrubTargetMs = newMs
                        lastInteractionTimestamp = System.currentTimeMillis()
                    },
                    onSeekFinished = { targetMs ->
                        isScrubbing = false
                        exoPlayer.seekTo(targetMs)
                        currentPositionMs = targetMs
                        lastInteractionTimestamp = System.currentTimeMillis()
                    },
                    onRewind10s = {
                        lastInteractionTimestamp = System.currentTimeMillis()
                        val target = (exoPlayer.currentPosition - 10000L).coerceAtLeast(0L)
                        exoPlayer.seekTo(target)
                        currentPositionMs = target
                    },
                    onForward10s = {
                        lastInteractionTimestamp = System.currentTimeMillis()
                        val target = (exoPlayer.currentPosition + 10000L).coerceAtMost(durationMs)
                        exoPlayer.seekTo(target)
                        currentPositionMs = target
                    },
                    onMuteToggle = {
                        isMuted = !isMuted
                        lastInteractionTimestamp = System.currentTimeMillis()
                    },
                    onSpeedChange = { newSpeed ->
                        playbackSpeed = newSpeed
                        lastInteractionTimestamp = System.currentTimeMillis()
                    },
                    onLoopToggle = {
                        isLooping = !isLooping
                        lastInteractionTimestamp = System.currentTimeMillis()
                    },
                    onScaleModeToggle = {
                        scaleMode = if (scaleMode == PlayerScaleMode.FIT) PlayerScaleMode.FILL else PlayerScaleMode.FIT
                        lastInteractionTimestamp = System.currentTimeMillis()
                    },
                    onFullscreenToggle = {
                        val target = !isFullscreen
                        isFullscreen = target
                        onFullscreenToggle?.invoke(target)
                        lastInteractionTimestamp = System.currentTimeMillis()
                    }
                )
            }
        }
    }

    // Standard Embedded View
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("media3_video_preview_player"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(aspectRatio)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                playerSurface(false)
            }
        }
    }

    // Fullscreen Immersive Modal Dialog
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
                    .testTag("media3_fullscreen_dialog")
            ) {
                playerSurface(true)
            }
        }
    }
}

/**
 * Dedicated Fullscreen Video Player Dialog powered by Media3 ExoPlayer.
 * Provides an immersive, edge-to-edge playback experience with complete standard controls.
 */
@Composable
fun Media3FullscreenVideoPlayerDialog(
    videoUri: String,
    title: String? = null,
    onDismissRequest: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        BackHandler {
            onDismissRequest()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .testTag("media3_fullscreen_standalone_dialog")
        ) {
            Media3VideoPreviewPlayer(
                videoUri = videoUri,
                title = title,
                modifier = Modifier.fillMaxSize(),
                isFullscreenInitially = true,
                onFullscreenToggle = { isFull ->
                    if (!isFull) {
                        onDismissRequest()
                    }
                }
            )
        }
    }
}

/**
 * High-fidelity Jetpack Compose Controls Overlay for Media3 ExoPlayer.
 */
@Composable
fun Media3PlayerControlsOverlay(
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
    videoWidth: Int,
    videoHeight: Int,
    onPlayPauseToggle: () -> Unit,
    onSeekStarted: () -> Unit,
    onSeekChanged: (Long) -> Unit,
    onSeekFinished: (Long) -> Unit,
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
                        Color.Black.copy(alpha = 0.85f)
                    )
                )
            )
            .testTag("media3_player_controls_overlay")
    ) {
        // TOP BAR
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
                            .testTag("media3_exit_fullscreen_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Exit Fullscreen",
                            tint = Color.White
                        )
                    }
                }

                Column {
                    Text(
                        text = title ?: "Media3 Video Preview",
                        color = Color.White,
                        fontSize = if (isFullscreen) 14.sp else 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (videoWidth > 0 && videoHeight > 0) {
                        Text(
                            text = "${videoWidth}x${videoHeight} • Media3 ExoPlayer",
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // Top action icons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Loop toggle
                IconButton(
                    onClick = onLoopToggle,
                    modifier = Modifier.size(36.dp).testTag("media3_loop_toggle_btn")
                ) {
                    Icon(
                        imageVector = if (isLooping) Icons.Default.RepeatOne else Icons.Default.Repeat,
                        contentDescription = "Loop playback",
                        tint = if (isLooping) MaterialTheme.colorScheme.primary else Color(0xFF94A3B8),
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Scale mode toggle (Fit vs Zoom)
                IconButton(
                    onClick = onScaleModeToggle,
                    modifier = Modifier.size(36.dp).testTag("media3_scale_mode_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.AspectRatio,
                        contentDescription = "Scale Mode",
                        tint = if (scaleMode == PlayerScaleMode.FILL) MaterialTheme.colorScheme.primary else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // CENTER PLAY / PAUSE / REPLAY HUD BUTTONS
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            // Quick rewind 10s button
            IconButton(
                onClick = onRewind10s,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .testTag("media3_rewind_10s_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.FastRewind,
                    contentDescription = "Rewind 10 seconds",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Big Center Play/Pause button with ripple
            val playScale by animateFloatAsState(
                targetValue = if (isPlaying) 1.0f else 1.08f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "centerPlayScale"
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
                    .testTag("media3_center_play_btn"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when {
                        isEnded -> Icons.Default.Replay
                        isPlaying -> Icons.Default.Pause
                        else -> Icons.Default.PlayArrow
                    },
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(34.dp)
                )
            }

            // Quick forward 10s button
            IconButton(
                onClick = onForward10s,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .testTag("media3_forward_10s_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.FastForward,
                    contentDescription = "Forward 10 seconds",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // BOTTOM BAR: SCRUBBER + TIMECODE + CONTROLS
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // Interactive Scrubber with Buffer Bar
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

                // High-precision Seek Slider
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
                        .testTag("media3_seek_slider"),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = Color.Transparent
                    )
                )
            }

            // Controls & Timecode Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Timecode display & bottom Play button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = onPlayPauseToggle,
                        modifier = Modifier.size(36.dp).testTag("media3_play_pause_btn")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // SMPTE / Duration display
                    Text(
                        text = "${formatMediaTime(currentPositionMs)} / ${formatMediaTime(durationMs)}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.testTag("media3_timecode_display")
                    )
                }

                // Action Controls: Speed + Volume + Fullscreen
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Playback Speed dropdown menu
                    Box {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Black.copy(alpha = 0.55f),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFF475569)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { showSpeedMenu = true }
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                                .testTag("media3_speed_btn")
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
                            listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { speed ->
                                DropdownMenuItem(
                                    text = { Text("${speed}x", fontWeight = if (playbackSpeed == speed) FontWeight.Bold else FontWeight.Normal) },
                                    onClick = {
                                        onSpeedChange(speed)
                                        showSpeedMenu = false
                                    }
                                )
                            }
                        }
                    }

                    // Audio Mute toggle
                    IconButton(
                        onClick = onMuteToggle,
                        modifier = Modifier.size(36.dp).testTag("media3_mute_toggle_btn")
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = if (isMuted) "Unmute" else "Mute",
                            tint = if (isMuted) Color(0xFFEF4444) else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Fullscreen Toggle
                    IconButton(
                        onClick = onFullscreenToggle,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("media3_fullscreen_toggle_btn")
                    ) {
                        Icon(
                            imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                            contentDescription = if (isFullscreen) "Exit Fullscreen" else "Enter Fullscreen",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}
