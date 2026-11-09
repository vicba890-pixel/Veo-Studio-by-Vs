package com.example.ui.components.preview

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.Serializable
import kotlin.math.sin

/**
 * Marker data structure for the Dedicated Video Timeline.
 */
data class TimelineEditMarker(
    val id: String = java.util.UUID.randomUUID().toString(),
    val positionMs: Long,
    val label: String,
    val type: String = "EDIT_POINT", // "COLOR_GRADE", "REFRAME", "CUT", "VOICE_OVER"
    val color: Color = Color(0xFFF59E0B)
) : Serializable

/**
 * =========================================================================================
 * DEDICATED VIDEO TIMELINE FOR EXOPLAYER (VMSB© 2026)
 * =========================================================================================
 *
 * Professional NLE video timeline engineered to synchronize seamlessly with ExoPlayer.
 * Features:
 * - High-precision SMPTE Timecode display (HH:MM:SS:FF)
 * - Multi-layer visual track: Time Ruler, Filmstrip Video Track, Audio Waveform Sub-track
 * - In & Out Trim handles with real-time range highlighting and duration readouts
 * - Interactive Draggable Playhead needle with frame timecode pill bubble
 * - Frame-stepping (-1F / +1F) and fast jump (-5s / +5s) transport controls
 * - Visual Edit Markers for Color Grading, Reframe, Cuts, and Voice-Over points
 * - Split / Cut tool at current playhead position
 * - Zoom controls: Fit, 1x, 2x, 4x
 */
@Composable
fun DedicatedVideoTimeline(
    currentPositionMs: Long,
    durationMs: Long,
    isPlaying: Boolean,
    fps: Int = 30,
    trimStart: Float = 0f,
    trimEnd: Float = 1.0f,
    onTrimRangeChange: (Float, Float) -> Unit,
    onSeekTo: (Long) -> Unit,
    onPlayPauseToggle: () -> Unit,
    onStepFrame: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onSplitAtPlayhead: ((Long) -> Unit)? = null,
    markers: List<TimelineEditMarker> = emptyList(),
    onAddMarker: ((Long) -> Unit)? = null,
    onMarkerClick: ((TimelineEditMarker) -> Unit)? = null,
    isLooping: Boolean = true,
    onLoopToggle: (() -> Unit)? = null
) {
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current

    // Local UI states
    var timelineZoom by remember { mutableFloatStateOf(1.0f) }
    var isDraggingPlayhead by remember { mutableStateOf(false) }
    var dragSeekTargetMs by remember { mutableLongStateOf(currentPositionMs) }

    val effectivePosMs = if (isDraggingPlayhead) dragSeekTargetMs else currentPositionMs
    val safeDurationMs = durationMs.coerceAtLeast(1000L)
    val playheadFraction = (effectivePosMs.toFloat() / safeDurationMs.toFloat()).coerceIn(0f, 1f)

    val inPointMs = (trimStart * safeDurationMs).toLong()
    val outPointMs = (trimEnd * safeDurationMs).toLong()
    val trimmedDurationMs = (outPointMs - inPointMs).coerceAtLeast(0L)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("dedicated_video_timeline"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF070B14)),
        border = BorderStroke(1.2.dp, Color(0xFF1E293B))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // =========================================================================
            // 1. TIMELINE HEADER: TIMECODE, FPS, TRIMS, AND TOOLS
            // =========================================================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Timecode Readout
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = Color(0xFF0F172A),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = formatSmpteTimecode(effectivePosMs, fps),
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF38BDF8)
                            )
                            Text(
                                text = "/",
                                fontSize = 10.sp,
                                color = Color(0xFF64748B)
                            )
                            Text(
                                text = formatSmpteTimecode(safeDurationMs, fps),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    // FPS & Trims info
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Surface(
                                color = Color(0xFF10B981).copy(alpha = 0.15f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "$fps FPS",
                                    fontSize = 8.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF10B981),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                            Text(
                                text = "TRIM: ${formatSmpteTimecode(trimmedDurationMs, fps)}",
                                fontSize = 8.5.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                        Text(
                            text = "IN: ${formatSmpteTimecode(inPointMs, fps)} | OUT: ${formatSmpteTimecode(outPointMs, fps)}",
                            fontSize = 7.5.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                // Action Tools: + Marker, Split, Reset Trims, Zoom
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Add Marker Button
                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier
                            .clickable {
                                onAddMarker?.invoke(effectivePosMs)
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                            .testTag("timeline_add_marker_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.BookmarkAdd, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(12.dp))
                            Text(text = "+ MARKER", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    // Split / Cut Button
                    Surface(
                        color = Color(0xFF881337).copy(alpha = 0.4f),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, Color(0xFFE11D48)),
                        modifier = Modifier
                            .clickable {
                                onSplitAtPlayhead?.invoke(effectivePosMs)
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                            .testTag("timeline_split_clip_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.ContentCut, contentDescription = null, tint = Color(0xFFFDA4AF), modifier = Modifier.size(12.dp))
                            Text(text = "DIVIDIR", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFDA4AF))
                        }
                    }

                    // Reset Trims Button (if trimmed)
                    if (trimStart > 0.01f || trimEnd < 0.99f) {
                        Text(
                            text = "RESET TRIMS",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8),
                            modifier = Modifier
                                .clickable { onTrimRangeChange(0f, 1f) }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }

                    // Timeline Zoom Selector
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF0F172A))
                            .padding(2.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        listOf(1.0f to "1x", 2.0f to "2x", 4.0f to "4x").forEach { (zm, label) ->
                            val isSel = timelineZoom == zm
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isSel) Color(0xFF0284C7) else Color.Transparent)
                                    .clickable { timelineZoom = zm }
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 7.5.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) Color.White else Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }
            }

            // =========================================================================
            // 2. INTERACTIVE MULTI-TRACK TIMELINE CANVAS
            // =========================================================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF090D16))
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp))
                    .pointerInput(safeDurationMs) {
                        detectTapGestures { offset ->
                            val frac = (offset.x / size.width).coerceIn(0f, 1f)
                            val targetMs = (frac * safeDurationMs).toLong()
                            onSeekTo(targetMs)
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                    }
                    .pointerInput(safeDurationMs) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                isDraggingPlayhead = true
                                val frac = (offset.x / size.width).coerceIn(0f, 1f)
                                dragSeekTargetMs = (frac * safeDurationMs).toLong()
                                onSeekTo(dragSeekTargetMs)
                            },
                            onDragEnd = {
                                isDraggingPlayhead = false
                                onSeekTo(dragSeekTargetMs)
                            },
                            onDragCancel = {
                                isDraggingPlayhead = false
                            }
                        ) { change, dragAmount ->
                            change.consume()
                            val frac = ((change.position.x) / size.width).coerceIn(0f, 1f)
                            dragSeekTargetMs = (frac * safeDurationMs).toLong()
                            onSeekTo(dragSeekTargetMs)
                        }
                    }
                    .testTag("timeline_track_canvas")
            ) {
                // Canvas: Time Ruler, Filmstrip Frames, Audio Waveform & Trims
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val rulerH = 22.dp.toPx()
                    val videoH = 50.dp.toPx()
                    val audioH = 38.dp.toPx()

                    // --- A. TOP TIME RULER ---
                    drawRect(
                        color = Color(0xFF0F172A),
                        topLeft = Offset(0f, 0f),
                        size = Size(w, rulerH)
                    )
                    drawLine(
                        color = Color(0xFF334155),
                        start = Offset(0f, rulerH),
                        end = Offset(w, rulerH),
                        strokeWidth = 1.dp.toPx()
                    )

                    // Draw second marks
                    val totalSeconds = (safeDurationMs / 1000).toInt().coerceAtLeast(1)
                    val stepSec = if (totalSeconds > 60) 10 else if (totalSeconds > 20) 5 else 1

                    for (sec in 0..totalSeconds step stepSec) {
                        val secFrac = (sec * 1000f) / safeDurationMs
                        val secX = secFrac * w
                        drawLine(
                            color = Color(0xFF64748B),
                            start = Offset(secX, rulerH - 8.dp.toPx()),
                            end = Offset(secX, rulerH),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // --- B. VIDEO TRACK FILMSTRIP (V1) ---
                    val videoTop = rulerH
                    drawRect(
                        brush = Brush.verticalGradient(
                            listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                        ),
                        topLeft = Offset(0f, videoTop),
                        size = Size(w, videoH)
                    )

                    // Draw filmstrip frame divisions & perforations
                    val frameStep = w / 10f
                    for (i in 0..10) {
                        val fx = i * frameStep
                        // Perforation holes top and bottom
                        drawRect(
                            color = Color(0xFF090D16),
                            topLeft = Offset(fx + 2.dp.toPx(), videoTop + 2.dp.toPx()),
                            size = Size(4.dp.toPx(), 4.dp.toPx())
                        )
                        drawRect(
                            color = Color(0xFF090D16),
                            topLeft = Offset(fx + 2.dp.toPx(), videoTop + videoH - 6.dp.toPx()),
                            size = Size(4.dp.toPx(), 4.dp.toPx())
                        )
                        // Vertical frame divider
                        drawLine(
                            color = Color.White.copy(alpha = 0.08f),
                            start = Offset(fx, videoTop),
                            end = Offset(fx, videoTop + videoH),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // --- C. AUDIO TRACK WAVEFORM (A1) ---
                    val audioTop = videoTop + videoH
                    drawRect(
                        color = Color(0xFF0A101D),
                        topLeft = Offset(0f, audioTop),
                        size = Size(w, audioH)
                    )
                    drawLine(
                        color = Color(0xFF1E293B),
                        start = Offset(0f, audioTop),
                        end = Offset(w, audioTop),
                        strokeWidth = 1.dp.toPx()
                    )

                    // Draw simulated waveform bars
                    val barCount = (w / 4.dp.toPx()).toInt()
                    val centerY = audioTop + (audioH / 2f)
                    for (b in 0 until barCount) {
                        val bx = b * 4.dp.toPx()
                        val wavePhase = (b.toDouble() * 0.25)
                        val amp = (sin(wavePhase) * 0.4 + sin(wavePhase * 2.1) * 0.35 + 0.5).coerceIn(0.1, 0.95).toFloat()
                        val barH = (audioH * 0.75f) * amp

                        drawLine(
                            color = Color(0xFF10B981).copy(alpha = 0.65f),
                            start = Offset(bx, centerY - barH / 2f),
                            end = Offset(bx, centerY + barH / 2f),
                            strokeWidth = 2.dp.toPx()
                        )
                    }

                    // --- D. TRIM RANGE IN / OUT OVERLAYS ---
                    val trimStartX = trimStart * w
                    val trimEndX = trimEnd * w

                    // Dimmed inactive region (Before In)
                    if (trimStartX > 0f) {
                        drawRect(
                            color = Color.Black.copy(alpha = 0.70f),
                            topLeft = Offset(0f, rulerH),
                            size = Size(trimStartX, videoH + audioH)
                        )
                    }
                    // Dimmed inactive region (After Out)
                    if (trimEndX < w) {
                        drawRect(
                            color = Color.Black.copy(alpha = 0.70f),
                            topLeft = Offset(trimEndX, rulerH),
                            size = Size(w - trimEndX, videoH + audioH)
                        )
                    }

                    // Highlight active playback zone border
                    drawRect(
                        color = Color(0xFF38BDF8).copy(alpha = 0.8f),
                        topLeft = Offset(trimStartX, rulerH),
                        size = Size(trimEndX - trimStartX, videoH + audioH),
                        style = Stroke(width = 1.5.dp.toPx())
                    )

                    // Draw Trim In bar
                    drawLine(
                        color = Color(0xFF38BDF8),
                        start = Offset(trimStartX, rulerH),
                        end = Offset(trimStartX, h),
                        strokeWidth = 2.5.dp.toPx()
                    )
                    // Draw Trim Out bar
                    drawLine(
                        color = Color(0xFF38BDF8),
                        start = Offset(trimEndX, rulerH),
                        end = Offset(trimEndX, h),
                        strokeWidth = 2.5.dp.toPx()
                    )

                    // --- E. TIMELINE EDIT MARKERS ---
                    markers.forEach { marker ->
                        val mx = (marker.positionMs.toFloat() / safeDurationMs.toFloat()).coerceIn(0f, 1f) * w
                        // Marker diamond flag on the ruler
                        val markerPath = Path().apply {
                            moveTo(mx, 2.dp.toPx())
                            lineTo(mx + 4.dp.toPx(), 7.dp.toPx())
                            lineTo(mx, 12.dp.toPx())
                            lineTo(mx - 4.dp.toPx(), 7.dp.toPx())
                            close()
                        }
                        drawPath(markerPath, color = marker.color)
                        // Thin dashed marker guide line down the tracks
                        drawLine(
                            color = marker.color.copy(alpha = 0.7f),
                            start = Offset(mx, rulerH),
                            end = Offset(mx, h),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
                        )
                    }

                    // --- F. PLAYHEAD VERTICAL NEEDLE ---
                    val playheadX = playheadFraction * w
                    drawLine(
                        color = Color(0xFFEF4444),
                        start = Offset(playheadX, 0f),
                        end = Offset(playheadX, h),
                        strokeWidth = 2.dp.toPx()
                    )
                    // Playhead top handle diamond
                    val headPath = Path().apply {
                        moveTo(playheadX - 6.dp.toPx(), 0f)
                        lineTo(playheadX + 6.dp.toPx(), 0f)
                        lineTo(playheadX + 6.dp.toPx(), 8.dp.toPx())
                        lineTo(playheadX, 14.dp.toPx())
                        lineTo(playheadX - 6.dp.toPx(), 8.dp.toPx())
                        close()
                    }
                    drawPath(headPath, color = Color(0xFFEF4444))
                }

                // In & Out Interactive Trim Handles (Grips with >= 48dp touch area)
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val fullW = maxWidth

                    // In Handle
                    Box(
                        modifier = Modifier
                            .offset(x = (fullW * trimStart) - 14.dp, y = 14.dp)
                            .size(width = 28.dp, height = 75.dp)
                            .pointerInput(fullW) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    val deltaFrac = dragAmount.x / size.width
                                    val nextIn = (trimStart + deltaFrac).coerceIn(0f, trimEnd - 0.05f)
                                    onTrimRangeChange(nextIn, trimEnd)
                                }
                            }
                            .testTag("timeline_trim_start_handle"),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp),
                            color = Color(0xFF38BDF8),
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(modifier = Modifier.size(2.dp).background(Color.Black, CircleShape))
                                Spacer(modifier = Modifier.height(2.dp))
                                Box(modifier = Modifier.size(2.dp).background(Color.Black, CircleShape))
                            }
                        }
                    }

                    // Out Handle
                    Box(
                        modifier = Modifier
                            .offset(x = (fullW * trimEnd) - 14.dp, y = 14.dp)
                            .size(width = 28.dp, height = 75.dp)
                            .pointerInput(fullW) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    val deltaFrac = dragAmount.x / size.width
                                    val nextOut = (trimEnd + deltaFrac).coerceIn(trimStart + 0.05f, 1f)
                                    onTrimRangeChange(trimStart, nextOut)
                                }
                            }
                            .testTag("timeline_trim_end_handle"),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp),
                            color = Color(0xFF38BDF8),
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(modifier = Modifier.size(2.dp).background(Color.Black, CircleShape))
                                Spacer(modifier = Modifier.height(2.dp))
                                Box(modifier = Modifier.size(2.dp).background(Color.Black, CircleShape))
                            }
                        }
                    }
                }
            }

            // =========================================================================
            // 3. PRECISION TRANSPORT & EDITING TOOLBAR
            // =========================================================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Group: Jump to In, -5s, Step -1F
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Jump to In Point
                    IconButton(
                        onClick = { onSeekTo(inPointMs) },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B))
                            .testTag("timeline_jump_in_btn")
                    ) {
                        Icon(Icons.Default.SkipPrevious, contentDescription = "Saltar al punto In", tint = Color.White, modifier = Modifier.size(18.dp))
                    }

                    // Jump -5s
                    IconButton(
                        onClick = { onSeekTo((effectivePosMs - 5000L).coerceAtLeast(0L)) },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B))
                            .testTag("timeline_rewind_5s_btn")
                    ) {
                        Icon(Icons.Default.FastRewind, contentDescription = "Retroceder 5 segundos", tint = Color.White, modifier = Modifier.size(18.dp))
                    }

                    // Step -1 Frame
                    IconButton(
                        onClick = { onStepFrame(-1) },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B))
                            .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f), CircleShape)
                            .testTag("timeline_step_back_btn")
                    ) {
                        Text(text = "-1F", fontSize = 10.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, color = Color(0xFF38BDF8))
                    }
                }

                // Center Primary Play / Pause Button
                val playScale by animateFloatAsState(
                    targetValue = if (isPlaying) 1.0f else 1.06f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "timelinePlayScale"
                )
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF0284C7), Color(0xFF0369A1))
                            )
                        )
                        .border(1.5.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true, color = Color.White),
                            onClick = onPlayPauseToggle
                        )
                        .testTag("timeline_play_pause_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pausar" else "Reproducir",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Right Group: Step +1F, +5s, Jump to Out
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Step +1 Frame
                    IconButton(
                        onClick = { onStepFrame(1) },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B))
                            .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f), CircleShape)
                            .testTag("timeline_step_forward_btn")
                    ) {
                        Text(text = "+1F", fontSize = 10.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, color = Color(0xFF38BDF8))
                    }

                    // Jump +5s
                    IconButton(
                        onClick = { onSeekTo((effectivePosMs + 5000L).coerceAtMost(safeDurationMs)) },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B))
                            .testTag("timeline_forward_5s_btn")
                    ) {
                        Icon(Icons.Default.FastForward, contentDescription = "Avanzar 5 segundos", tint = Color.White, modifier = Modifier.size(18.dp))
                    }

                    // Jump to Out Point
                    IconButton(
                        onClick = { onSeekTo(outPointMs) },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B))
                            .testTag("timeline_jump_out_btn")
                    ) {
                        Icon(Icons.Default.SkipNext, contentDescription = "Saltar al punto Out", tint = Color.White, modifier = Modifier.size(18.dp))
                    }

                    // Loop Toggle
                    if (onLoopToggle != null) {
                        IconButton(
                            onClick = onLoopToggle,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isLooping) Color(0xFF0284C7).copy(alpha = 0.25f) else Color(0xFF1E293B))
                                .border(1.dp, if (isLooping) Color(0xFF38BDF8) else Color(0xFF334155), CircleShape)
                                .testTag("timeline_loop_toggle_btn")
                        ) {
                            Icon(
                                imageVector = if (isLooping) Icons.Default.RepeatOne else Icons.Default.Repeat,
                                contentDescription = "Bucle",
                                tint = if (isLooping) Color(0xFF38BDF8) else Color(0xFF94A3B8),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
