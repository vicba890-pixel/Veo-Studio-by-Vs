package com.example.ui.components.preview

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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.example.data.colorgrading.ColorGradingState
import com.example.data.smartcrop.SmartCropAspectRatio
import com.example.data.smartcrop.SmartCropSuggestion
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

/**
 * =========================================================================================
 * DEDICATED EXOPLAYER PREVIEW SUITE WITH EDIT STATE OVERLAY & VIDEO TIMELINE (VMSB© 2026)
 * =========================================================================================
 *
 * All-in-one professional video editing suite combining:
 * 1. ExoPlayer hardware-accelerated playback with SeekParameters.EXACT.
 * 2. Dedicated Video Preview Overlay with real-time feedback for Color Grading (LUTs, brightness,
 *    contrast, saturation, temperature, A/B split-screen) and Reframe Adjustments (aspect ratios,
 *    pan offset X/Y, zoom, rule of thirds, safe zones, and Gemini AI smart-crop reticles).
 * 3. Dedicated Video Timeline synchronized frame-by-frame with ExoPlayer playback, featuring
 *    SMPTE timecodes, visual filmstrip track, audio waveform sub-track, in/out trim handles,
 *    edit markers, and transport controls (-1F / +1F, -5s / +5s, play/pause).
 */
@OptIn(UnstableApi::class)
@Composable
fun DedicatedExoPlayerPreviewSuite(
    videoUri: String,
    aspectRatio: String,
    onAspectRatioChange: (String) -> Unit,
    colorGradingState: ColorGradingState,
    onColorGradingChange: (ColorGradingState) -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    reframePanX: Float = 0f,
    reframePanY: Float = 0f,
    onReframePanChange: (panX: Float, panY: Float) -> Unit = { _, _ -> },
    reframeZoom: Float = 1.0f,
    onReframeZoomChange: (Float) -> Unit = {},
    trimStart: Float = 0f,
    trimEnd: Float = 1.0f,
    onTrimRangeChange: (Float, Float) -> Unit = { _, _ -> },
    markers: List<TimelineEditMarker> = emptyList(),
    onAddMarker: ((Long) -> Unit)? = null,
    onSplitAtPlayhead: ((Long) -> Unit)? = null,
    smartCropSuggestion: SmartCropSuggestion? = null,
    isSmartCropAnalyzing: Boolean = false,
    onApplySmartCrop: ((SmartCropAspectRatio) -> Unit)? = null,
    fps: Int = 30,
    autoPlay: Boolean = false,
    onPositionChanged: ((currentMs: Long, durationMs: Long) -> Unit)? = null
) {
    val context = LocalContext.current
    val frameDurationMs = remember(fps) { (1000.0 / fps.coerceIn(12, 120)).toLong().coerceAtLeast(16L) }

    // Playback state
    var isPlaying by remember { mutableStateOf(false) }
    var playbackState by remember { mutableIntStateOf(Player.STATE_IDLE) }
    var currentPositionMs by remember { mutableLongStateOf(0L) }
    var durationMs by remember { mutableLongStateOf(0L) }
    var isLooping by remember { mutableStateOf(true) }
    var isFullscreen by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Target numeric aspect for the player viewport container
    val containerAspect = remember(aspectRatio) {
        when (aspectRatio) {
            "9:16" -> 9f / 16f
            "4:5" -> 4f / 5f
            "1:1" -> 1f
            "21:9" -> 21f / 9f
            else -> 16f / 9f
        }
    }

    // Initialize ExoPlayer with SeekParameters.EXACT for editing precision
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
                repeatMode = Player.REPEAT_MODE_ALL
            }
    }

    // Attach ExoPlayer state listener
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
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
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    // Load video item when videoUri changes
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

    // High frequency position tracker while playing
    LaunchedEffect(isPlaying) {
        while (isActive) {
            if (isPlaying) {
                currentPositionMs = exoPlayer.currentPosition
                durationMs = exoPlayer.duration.coerceAtLeast(0L)
                onPositionChanged?.invoke(currentPositionMs, durationMs)

                // Enforce In/Out trim looping bounds
                if (durationMs > 0) {
                    val inMs = (trimStart * durationMs).toLong()
                    val outMs = (trimEnd * durationMs).toLong()
                    if (currentPositionMs >= outMs) {
                        if (isLooping) {
                            exoPlayer.seekTo(inMs)
                            currentPositionMs = inMs
                        } else {
                            exoPlayer.pause()
                        }
                    }
                }
            }
            delay(100)
        }
    }

    fun seekToPosition(targetMs: Long) {
        val bounded = targetMs.coerceIn(0L, durationMs.coerceAtLeast(0L))
        exoPlayer.seekTo(bounded)
        currentPositionMs = bounded
        onPositionChanged?.invoke(bounded, durationMs)
    }

    fun stepFrames(deltaFrames: Int) {
        val target = currentPositionMs + (deltaFrames * frameDurationMs)
        seekToPosition(target)
    }

    // Reusable Video Viewport Composable with Dedicated Preview Overlay
    val videoViewportContent: @Composable (isFull: Boolean) -> Unit = { isFull ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(containerAspect)
                .clip(RoundedCornerShape(if (isFull) 0.dp else 16.dp))
                .background(Color.Black)
                .border(
                    width = if (isFull) 0.dp else 1.2.dp,
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(if (isFull) 0.dp else 16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            // 1. AndroidView wrapping Media3 PlayerView
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = false
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        setShutterBackgroundColor(android.graphics.Color.TRANSPARENT)
                    }
                },
                update = { playerView ->
                    playerView.player = exoPlayer
                },
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("dedicated_exoplayer_surface")
            )

            // 2. Gesture tap for Play/Pause toggle
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                if (exoPlayer.isPlaying) {
                                    exoPlayer.pause()
                                } else {
                                    if (exoPlayer.playbackState == Player.STATE_ENDED) {
                                        exoPlayer.seekTo(0L)
                                    }
                                    exoPlayer.play()
                                }
                            }
                        )
                    }
            )

            // 3. Buffering Indicator
            if (playbackState == Player.STATE_BUFFERING) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 3.dp,
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("dedicated_exoplayer_buffering")
                )
            }

            // 4. Dedicated Video Preview Overlay (Color Grading + Reframe Feedback)
            DedicatedVideoPreviewOverlay(
                colorGradingState = colorGradingState,
                onColorGradingChange = onColorGradingChange,
                selectedAspectRatio = aspectRatio,
                onAspectRatioChange = onAspectRatioChange,
                reframePanX = reframePanX,
                reframePanY = reframePanY,
                onReframePanChange = onReframePanChange,
                reframeZoom = reframeZoom,
                onReframeZoomChange = onReframeZoomChange,
                currentPositionMs = currentPositionMs,
                durationMs = durationMs,
                fps = fps,
                smartCropSuggestion = smartCropSuggestion,
                isSmartCropAnalyzing = isSmartCropAnalyzing,
                onApplySmartCrop = onApplySmartCrop
            )

            // 5. Fullscreen Toggle Button at Top-Right
            IconButton(
                onClick = { isFullscreen = !isFullscreen },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.65f))
                    .testTag("dedicated_preview_fullscreen_toggle_btn")
            ) {
                Icon(
                    imageVector = if (isFull) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                    contentDescription = if (isFull) "Salir de pantalla completa" else "Pantalla completa",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }

    // =========================================================================
    // MAIN LAYOUT: VIEWPORT + DEDICATED VIDEO TIMELINE
    // =========================================================================
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("dedicated_video_preview_suite"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. Video Viewport with Dedicated Preview Overlay
        videoViewportContent(false)

        // 2. Dedicated Video TimeLine
        DedicatedVideoTimeline(
            currentPositionMs = currentPositionMs,
            durationMs = durationMs,
            isPlaying = isPlaying,
            fps = fps,
            trimStart = trimStart,
            trimEnd = trimEnd,
            onTrimRangeChange = onTrimRangeChange,
            onSeekTo = { targetMs -> seekToPosition(targetMs) },
            onPlayPauseToggle = {
                if (exoPlayer.isPlaying) {
                    exoPlayer.pause()
                } else {
                    if (exoPlayer.playbackState == Player.STATE_ENDED) {
                        exoPlayer.seekTo(0L)
                    }
                    exoPlayer.play()
                }
            },
            onStepFrame = { delta -> stepFrames(delta) },
            onSplitAtPlayhead = onSplitAtPlayhead,
            markers = markers,
            onAddMarker = onAddMarker,
            isLooping = isLooping,
            onLoopToggle = {
                val next = !isLooping
                isLooping = next
                exoPlayer.repeatMode = if (next) Player.REPEAT_MODE_ALL else Player.REPEAT_MODE_OFF
            },
            modifier = Modifier.fillMaxWidth()
        )
    }

    // =========================================================================
    // FULLSCREEN THEATER DIALOG
    // =========================================================================
    if (isFullscreen) {
        Dialog(
            onDismissRequest = { isFullscreen = false },
            properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
        ) {
            BackHandler { isFullscreen = false }
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                color = Color.Black
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Fullscreen Viewport
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        videoViewportContent(true)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Fullscreen Timeline
                    DedicatedVideoTimeline(
                        currentPositionMs = currentPositionMs,
                        durationMs = durationMs,
                        isPlaying = isPlaying,
                        fps = fps,
                        trimStart = trimStart,
                        trimEnd = trimEnd,
                        onTrimRangeChange = onTrimRangeChange,
                        onSeekTo = { targetMs -> seekToPosition(targetMs) },
                        onPlayPauseToggle = {
                            if (exoPlayer.isPlaying) {
                                exoPlayer.pause()
                            } else {
                                exoPlayer.play()
                            }
                        },
                        onStepFrame = { delta -> stepFrames(delta) },
                        onSplitAtPlayhead = onSplitAtPlayhead,
                        markers = markers,
                        onAddMarker = onAddMarker,
                        isLooping = isLooping,
                        onLoopToggle = {
                            val next = !isLooping
                            isLooping = next
                            exoPlayer.repeatMode = if (next) Player.REPEAT_MODE_ALL else Player.REPEAT_MODE_OFF
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
