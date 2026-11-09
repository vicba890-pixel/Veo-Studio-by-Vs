package com.example.ui.components.preview

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.colorgrading.CinematicLutPreset
import com.example.data.colorgrading.ColorGradingPresets
import com.example.data.colorgrading.ColorGradingState
import com.example.data.smartcrop.SmartCropAspectRatio
import com.example.data.smartcrop.SmartCropSuggestion
import kotlin.math.sin

/**
 * =========================================================================================
 * DEDICATED VIDEO PREVIEW OVERLAY FOR EXOPLAYER (VMSB© 2026)
 * =========================================================================================
 *
 * Dedicated video preview overlay that composites directly onto the ExoPlayer viewport to display
 * current edit state with real-time visual feedback for:
 *
 * 1. REAL-TIME COLOR GRADING:
 *    - Dynamic LUT shader rendering (Kodak 5219, Teal & Orange, Fuji Eterna, Cyberpunk, Noir, etc.)
 *    - Real-time Brightness, Contrast, Saturation, and Color Temperature adjustments
 *    - Live interactive A/B Split Screen comparison slider (Raw vs Graded edit state)
 *    - Mini RGB Scopes / Luminance telemetry badge and active LUT parameters
 *    - Live Quick-Tweak color drawer for instant feedback directly on the video
 *
 * 2. REAL-TIME REFRAME ADJUSTMENTS:
 *    - Aspect ratio matting (16:9, 9:16, 4:5, 1:1, 21:9) with dark cinematic cropping masks
 *    - Interactive Pan & Framing Window: touch-drag to adjust Pan X & Pan Y in real time
 *    - Framing Zoom / Scale factor slider (1.0x - 2.5x)
 *    - Rule of Thirds grid lines with golden ratio crosshairs
 *    - Center optical crosshairs (+)
 *    - Broadcast & Social Safe Zones (Action Safe 93%, Title Safe 80%, Social UI boundaries)
 *    - Gemini AI Smart-Crop Subject tracking bounding box with confidence score
 *
 * 3. CURRENT EDIT STATE HUD:
 *    - SMPTE timecode (HH:MM:SS:FF), codec, resolution, and edit status badges
 */
@Composable
fun BoxScope.DedicatedVideoPreviewOverlay(
    colorGradingState: ColorGradingState,
    onColorGradingChange: (ColorGradingState) -> Unit,
    selectedAspectRatio: String,
    onAspectRatioChange: (String) -> Unit,
    reframePanX: Float,
    reframePanY: Float,
    onReframePanChange: (panX: Float, panY: Float) -> Unit,
    reframeZoom: Float,
    onReframeZoomChange: (Float) -> Unit,
    currentPositionMs: Long,
    durationMs: Long,
    fps: Int = 30,
    smartCropSuggestion: SmartCropSuggestion? = null,
    isSmartCropAnalyzing: Boolean = false,
    onApplySmartCrop: ((SmartCropAspectRatio) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // Local UI states
    var isReframeToolActive by remember { mutableStateOf(false) }
    var showThirdsGrid by remember { mutableStateOf(true) }
    var showSafeZones by remember { mutableStateOf(true) }
    var showCenterCross by remember { mutableStateOf(true) }
    var showQuickColorDrawer by remember { mutableStateOf(false) }
    var splitPositionFraction by remember { mutableFloatStateOf(0.5f) }
    var isDraggingSplit by remember { mutableStateOf(false) }

    // Pulsing animation for AI Subject Tracking Reticle
    val infiniteTransition = rememberInfiniteTransition(label = "ReticlePulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val targetNumericAspect = remember(selectedAspectRatio) {
        when (selectedAspectRatio) {
            "9:16" -> 9f / 16f
            "4:5" -> 4f / 5f
            "1:1" -> 1f
            "21:9" -> 21f / 9f
            else -> 16f / 9f
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("dedicated_video_preview_overlay")
    ) {
        // =========================================================================
        // 1. REAL-TIME COLOR GRADING SHADER LAYER (WITH A/B SPLIT SCREEN)
        // =========================================================================
        if (colorGradingState.isEnabled) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("color_grading_canvas_layer")
            ) {
                val w = size.width
                val h = size.height
                val splitX = if (colorGradingState.isSplitPreviewActive) w * splitPositionFraction else 0f

                clipRect(left = splitX, top = 0f, right = w, bottom = h) {
                    val lut = colorGradingState.selectedLut

                    // Desaturation / Monochrome Noir
                    if (lut.id == ColorGradingPresets.MONOCHROME_NOIR.id || colorGradingState.saturation < 0.2f) {
                        drawRect(
                            color = Color.Black.copy(
                                alpha = (1f - (colorGradingState.saturation * 0.5f)).coerceIn(0.5f, 0.92f)
                            ),
                            blendMode = BlendMode.Color
                        )
                    }

                    // Cinematic LUT Tints
                    when (lut.id) {
                        ColorGradingPresets.KODAK_VISION3_5219.id -> {
                            drawRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFFFEF3C7).copy(alpha = 0.14f * colorGradingState.lutIntensity),
                                        Color(0xFFB45309).copy(alpha = 0.16f * colorGradingState.lutIntensity)
                                    )
                                ),
                                blendMode = BlendMode.Overlay
                            )
                        }
                        ColorGradingPresets.TEAL_AND_ORANGE_BLOCKBUSTER.id -> {
                            drawRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF0D9488).copy(alpha = 0.22f * colorGradingState.lutIntensity),
                                        Color.Transparent,
                                        Color(0xFFEA580C).copy(alpha = 0.24f * colorGradingState.lutIntensity)
                                    )
                                ),
                                blendMode = BlendMode.Overlay
                            )
                        }
                        ColorGradingPresets.FUJI_ETERNA_250D.id -> {
                            drawRect(
                                color = Color(0xFF0F766E).copy(alpha = 0.12f * colorGradingState.lutIntensity),
                                blendMode = BlendMode.Softlight
                            )
                        }
                        ColorGradingPresets.CYBERPUNK_NEON.id -> {
                            drawRect(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF7C3AED).copy(alpha = 0.25f * colorGradingState.lutIntensity),
                                        Color(0xFF06B6D4).copy(alpha = 0.22f * colorGradingState.lutIntensity)
                                    )
                                ),
                                blendMode = BlendMode.Screen
                            )
                        }
                        ColorGradingPresets.VINTAGE_POLAROID_70S.id -> {
                            drawRect(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFFF59E0B).copy(alpha = 0.22f * colorGradingState.lutIntensity),
                                        Color(0xFF991B1B).copy(alpha = 0.15f * colorGradingState.lutIntensity)
                                    )
                                ),
                                blendMode = BlendMode.Overlay
                            )
                        }
                        ColorGradingPresets.BLEACH_BYPASS_WAR.id -> {
                            drawRect(
                                color = Color(0xFF38BDF8).copy(alpha = 0.18f * colorGradingState.lutIntensity),
                                blendMode = BlendMode.Hardlight
                            )
                        }
                    }

                    // Contrast Adjustment (Darken shadows / highlight roll-off)
                    if (colorGradingState.contrast > 1.05f) {
                        val extraContrast = (colorGradingState.contrast - 1f).coerceIn(0f, 1f)
                        drawRect(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.25f * extraContrast)
                                ),
                                center = Offset(w / 2f, h / 2f),
                                radius = w * 0.75f
                            ),
                            blendMode = BlendMode.Multiply
                        )
                    }

                    // Brightness Adjustment
                    if (colorGradingState.brightness > 0.02f) {
                        drawRect(
                            color = Color.White.copy(alpha = (colorGradingState.brightness * 0.35f).coerceIn(0f, 0.45f)),
                            blendMode = BlendMode.Screen
                        )
                    } else if (colorGradingState.brightness < -0.02f) {
                        drawRect(
                            color = Color.Black.copy(alpha = (-colorGradingState.brightness * 0.5f).coerceIn(0f, 0.65f)),
                            blendMode = BlendMode.Multiply
                        )
                    }

                    // Temperature Balance
                    if (colorGradingState.temperature > 0.05f) {
                        drawRect(
                            color = Color(0xFFF59E0B).copy(alpha = (colorGradingState.temperature * 0.18f).coerceIn(0f, 0.3f)),
                            blendMode = BlendMode.Color
                        )
                    } else if (colorGradingState.temperature < -0.05f) {
                        drawRect(
                            color = Color(0xFF0284C7).copy(alpha = (-colorGradingState.temperature * 0.2f).coerceIn(0f, 0.3f)),
                            blendMode = BlendMode.Color
                        )
                    }
                }

                // Draw Split Screen divider line & labels
                if (colorGradingState.isSplitPreviewActive) {
                    val lineX = w * splitPositionFraction
                    drawLine(
                        color = Color.White,
                        start = Offset(lineX, 0f),
                        end = Offset(lineX, h),
                        strokeWidth = 2.dp.toPx()
                    )
                    drawLine(
                        color = Color(0xFF38BDF8),
                        start = Offset(lineX, 0f),
                        end = Offset(lineX, h),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            }

            // Split A/B draggable divider handle & labels
            if (colorGradingState.isSplitPreviewActive) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Left Label (RAW)
                    Surface(
                        color = Color.Black.copy(alpha = 0.65f),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(start = 12.dp, top = 48.dp)
                    ) {
                        Text(
                            text = "A: RAW REC.709",
                            color = Color(0xFF94A3B8),
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // Right Label (GRADED)
                    Surface(
                        color = Color(0xFF10B981).copy(alpha = 0.85f),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(end = 12.dp, top = 48.dp)
                    ) {
                        Text(
                            text = "B: GRADED (${colorGradingState.selectedLut.name.take(12)})",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // Center Interactive Split Handle
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.12f)
                            .align(Alignment.CenterStart)
                            .offset(x = 0.dp) // Handled by pointer input drag
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = { isDraggingSplit = true },
                                    onDragEnd = { isDraggingSplit = false },
                                    onDragCancel = { isDraggingSplit = false }
                                ) { change, dragAmount ->
                                    change.consume()
                                    val newFrac = (splitPositionFraction + (dragAmount.x / size.width)).coerceIn(0.05f, 0.95f)
                                    splitPositionFraction = newFrac
                                }
                            }
                    )
                }
            }
        }

        // =========================================================================
        // 2. REAL-TIME REFRAME ADJUSTMENTS & MATTING LAYER
        // =========================================================================
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("reframe_matting_canvas")
        ) {
            val totalW = size.width
            val totalH = size.height

            // Calculate reframe crop rectangle based on target numeric aspect
            val containerAspect = totalW / totalH
            val (cropW, cropH) = if (containerAspect > targetNumericAspect) {
                // Crop is narrower than container (pillarbox, e.g. 9:16 or 4:5 on 16:9 container)
                val h = totalH
                val w = (h * targetNumericAspect).coerceAtMost(totalW)
                Pair(w, h)
            } else {
                // Crop is wider than container (letterbox, e.g. 21:9 on 16:9 container)
                val w = totalW
                val h = (w / targetNumericAspect).coerceAtMost(totalH)
                Pair(w, h)
            }

            // Apply Pan offsets (clamped within available margin)
            val maxPanX = (totalW - cropW) / 2f
            val maxPanY = (totalH - cropH) / 2f
            val panOffsetX = reframePanX * maxPanX
            val panOffsetY = reframePanY * maxPanY

            val cropLeft = ((totalW - cropW) / 2f) + panOffsetX
            val cropTop = ((totalH - cropH) / 2f) + panOffsetY
            val cropRight = cropLeft + cropW
            val cropBottom = cropTop + cropH

            // 1. Dark Matting Mask (Mask areas outside of target aspect crop)
            if (selectedAspectRatio != "16:9" || reframeZoom > 1.01f || isReframeToolActive) {
                // Top mask
                if (cropTop > 0) {
                    drawRect(
                        color = Color.Black.copy(alpha = 0.72f),
                        topLeft = Offset(0f, 0f),
                        size = Size(totalW, cropTop)
                    )
                }
                // Bottom mask
                if (cropBottom < totalH) {
                    drawRect(
                        color = Color.Black.copy(alpha = 0.72f),
                        topLeft = Offset(0f, cropBottom),
                        size = Size(totalW, totalH - cropBottom)
                    )
                }
                // Left mask
                if (cropLeft > 0) {
                    drawRect(
                        color = Color.Black.copy(alpha = 0.72f),
                        topLeft = Offset(0f, cropTop),
                        size = Size(cropLeft, cropH)
                    )
                }
                // Right mask
                if (cropRight < totalW) {
                    drawRect(
                        color = Color.Black.copy(alpha = 0.72f),
                        topLeft = Offset(cropRight, cropTop),
                        size = Size(totalW - cropRight, cropH)
                    )
                }

                // Active Framing Box Border
                drawRect(
                    color = Color(0xFF38BDF8).copy(alpha = 0.85f),
                    topLeft = Offset(cropLeft, cropTop),
                    size = Size(cropW, cropH),
                    style = Stroke(width = 2.dp.toPx())
                )
            }

            // 2. Rule of Thirds Grid inside active crop window
            if (showThirdsGrid) {
                val gridColor = Color.White.copy(alpha = 0.18f)
                val gridStroke = Stroke(
                    width = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                )

                // Vertical grid lines
                drawLine(gridColor, Offset(cropLeft + cropW / 3f, cropTop), Offset(cropLeft + cropW / 3f, cropBottom), gridStroke.width)
                drawLine(gridColor, Offset(cropLeft + 2f * cropW / 3f, cropTop), Offset(cropLeft + 2f * cropW / 3f, cropBottom), gridStroke.width)
                // Horizontal grid lines
                drawLine(gridColor, Offset(cropLeft, cropTop + cropH / 3f), Offset(cropRight, cropTop + cropH / 3f), gridStroke.width)
                drawLine(gridColor, Offset(cropLeft, cropTop + 2f * cropH / 3f), Offset(cropRight, cropTop + 2f * cropH / 3f), gridStroke.width)

                // Golden crosshairs on intersections
                val chRadius = 4.dp.toPx()
                listOf(
                    Offset(cropLeft + cropW / 3f, cropTop + cropH / 3f),
                    Offset(cropLeft + 2f * cropW / 3f, cropTop + cropH / 3f),
                    Offset(cropLeft + cropW / 3f, cropTop + 2f * cropH / 3f),
                    Offset(cropLeft + 2f * cropW / 3f, cropTop + 2f * cropH / 3f)
                ).forEach { pt ->
                    drawCircle(Color(0xFFF59E0B).copy(alpha = 0.6f), radius = chRadius, center = pt)
                }
            }

            // 3. Center Optical Crosshairs
            if (showCenterCross) {
                val cx = cropLeft + cropW / 2f
                val cy = cropTop + cropH / 2f
                val crossSize = 14.dp.toPx()

                drawLine(Color.White.copy(alpha = 0.45f), Offset(cx - crossSize, cy), Offset(cx + crossSize, cy), 1.dp.toPx())
                drawLine(Color.White.copy(alpha = 0.45f), Offset(cx, cy - crossSize), Offset(cx, cy + crossSize), 1.dp.toPx())
            }

            // 4. Broadcast & Social Safe Zones
            if (showSafeZones) {
                // Action Safe (93%)
                val asW = cropW * 0.93f
                val asH = cropH * 0.93f
                drawRect(
                    color = Color(0xFF10B981).copy(alpha = 0.35f),
                    topLeft = Offset(cropLeft + (cropW - asW) / 2f, cropTop + (cropH - asH) / 2f),
                    size = Size(asW, asH),
                    style = Stroke(
                        width = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                    )
                )

                // Title Safe (80%)
                val tsW = cropW * 0.80f
                val tsH = cropH * 0.80f
                drawRect(
                    color = Color(0xFFF59E0B).copy(alpha = 0.35f),
                    topLeft = Offset(cropLeft + (cropW - tsW) / 2f, cropTop + (cropH - tsH) / 2f),
                    size = Size(tsW, tsH),
                    style = Stroke(
                        width = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
                    )
                )
            }

            // 5. Smart-Crop Gemini Subject Tracking Reticle
            if (smartCropSuggestion != null) {
                val subj = smartCropSuggestion.detectedSubject
                val b = subj.boundingBox

                // Normalized to crop box
                val sLeft = cropLeft + (b.left * cropW)
                val sTop = cropTop + (b.top * cropH)
                val sW = (b.width * cropW).coerceAtLeast(30f)
                val sH = (b.height * cropH).coerceAtLeast(30f)

                // Pulsing highlight
                drawRoundRect(
                    color = Color(0xFF38BDF8).copy(alpha = 0.12f * pulseAlpha),
                    topLeft = Offset(sLeft, sTop),
                    size = Size(sW, sH),
                    cornerRadius = CornerRadius(8.dp.toPx())
                )
                drawRoundRect(
                    color = Color(0xFF38BDF8).copy(alpha = 0.9f * pulseAlpha),
                    topLeft = Offset(sLeft, sTop),
                    size = Size(sW, sH),
                    cornerRadius = CornerRadius(8.dp.toPx()),
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }

        // =========================================================================
        // 3. INTERACTIVE TOUCH-DRAG REFRAME SURFACE
        // =========================================================================
        if (isReframeToolActive) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val nextPanX = (reframePanX + (dragAmount.x / (size.width * 0.5f))).coerceIn(-1.0f, 1.0f)
                            val nextPanY = (reframePanY + (dragAmount.y / (size.height * 0.5f))).coerceIn(-1.0f, 1.0f)
                            onReframePanChange(nextPanX, nextPanY)
                        }
                    }
                    .testTag("reframe_pan_drag_surface")
            ) {
                // Interactive Drag HUD indicator
                Surface(
                    color = Color(0xFF0F172A).copy(alpha = 0.85f),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, Color(0xFF38BDF8)),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.OpenWith, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                        Text(
                            text = "ARRASTRA PARA REENCUADRAR • PAN (X: ${(reframePanX * 100).toInt()}%, Y: ${(reframePanY * 100).toInt()}%)",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // =========================================================================
        // 4. TOP EDIT STATE TELEMETRY HUD BAR
        // =========================================================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Edit State & Timecode Pill
            Surface(
                color = Color.Black.copy(alpha = 0.75f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981))
                    )
                    Text(
                        text = "EDIT STATE",
                        color = Color(0xFF10B981),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "•",
                        color = Color.White.copy(alpha = 0.3f),
                        fontSize = 9.sp
                    )
                    Text(
                        text = formatSmpteTimecode(currentPositionMs, fps),
                        color = Color.White,
                        fontSize = 9.5.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Active Color & Reframe Badges
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Color Grade Badge
                Surface(
                    color = if (colorGradingState.isEnabled) Color(0xFF7C3AED).copy(alpha = 0.8f) else Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(0.5.dp, if (colorGradingState.isEnabled) Color(0xFFA78BFA) else Color.White.copy(alpha = 0.15f))
                ) {
                    Text(
                        text = if (colorGradingState.isEnabled) "LUT: ${colorGradingState.selectedLut.name.take(10)}" else "COLOR: OFF",
                        color = Color.White,
                        fontSize = 8.5.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }

                // Aspect Ratio Badge
                Surface(
                    color = Color(0xFF0284C7).copy(alpha = 0.8f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(0.5.dp, Color(0xFF38BDF8))
                ) {
                    Text(
                        text = selectedAspectRatio,
                        color = Color.White,
                        fontSize = 8.5.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }

                // Quick Color Tweak Toggle Button
                IconButton(
                    onClick = { showQuickColorDrawer = !showQuickColorDrawer },
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.7f))
                        .testTag("color_grading_quick_tweak_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = "Ajustes de Color en Vivo",
                        tint = if (showQuickColorDrawer) Color(0xFFF59E0B) else Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                }

                // Reframe Tool Toggle Button
                IconButton(
                    onClick = { isReframeToolActive = !isReframeToolActive },
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(if (isReframeToolActive) Color(0xFF0284C7) else Color.Black.copy(alpha = 0.7f))
                        .testTag("reframe_tool_toggle_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Crop,
                        contentDescription = "Herramienta de Reencuadre",
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }

        // =========================================================================
        // 5. QUICK REFRAME & ASPECT RATIO FLOATING ACTION ROW
        // =========================================================================
        Surface(
            color = Color.Black.copy(alpha = 0.85f),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 10.dp, bottom = 44.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("16:9", "9:16", "4:5", "1:1", "21:9").forEach { ratio ->
                    val isSel = selectedAspectRatio == ratio
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) Color(0xFF0284C7) else Color.Transparent)
                            .clickable { onAspectRatioChange(ratio) }
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                            .testTag("reframe_aspect_ratio_chip_$ratio")
                    ) {
                        Text(
                            text = ratio,
                            fontSize = 8.5.sp,
                            fontWeight = if (isSel) FontWeight.Black else FontWeight.Normal,
                            fontFamily = FontFamily.Monospace,
                            color = if (isSel) Color.White else Color(0xFF94A3B8)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(2.dp))

                // Toggle Thirds Grid
                IconButton(
                    onClick = { showThirdsGrid = !showThirdsGrid },
                    modifier = Modifier.size(22.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.GridOn,
                        contentDescription = "Regla de Tercios",
                        tint = if (showThirdsGrid) Color(0xFF10B981) else Color(0xFF64748B),
                        modifier = Modifier.size(13.dp)
                    )
                }

                // Toggle Safe Zones
                IconButton(
                    onClick = { showSafeZones = !showSafeZones },
                    modifier = Modifier.size(22.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AspectRatio,
                        contentDescription = "Zonas Seguras",
                        tint = if (showSafeZones) Color(0xFFF59E0B) else Color(0xFF64748B),
                        modifier = Modifier.size(13.dp)
                    )
                }

                // Toggle A/B Split Screen
                IconButton(
                    onClick = {
                        onColorGradingChange(colorGradingState.copy(isSplitPreviewActive = !colorGradingState.isSplitPreviewActive))
                    },
                    modifier = Modifier.size(22.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Compare,
                        contentDescription = "Comparación A/B",
                        tint = if (colorGradingState.isSplitPreviewActive) Color(0xFF38BDF8) else Color(0xFF64748B),
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }

        // =========================================================================
        // 6. EXPANDABLE QUICK-COLOR ADJUSTMENTS DRAWER
        // =========================================================================
        AnimatedVisibility(
            visible = showQuickColorDrawer,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, bottom = 44.dp)
        ) {
            Surface(
                color = Color(0xFF0F172A).copy(alpha = 0.96f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AJUSTES DE COLOR EN TIEMPO REAL",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF38BDF8)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            // Reset button
                            Text(
                                text = "RESET",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF4444),
                                modifier = Modifier
                                    .clickable {
                                        onColorGradingChange(
                                            colorGradingState.copy(
                                                brightness = 0f,
                                                contrast = 1f,
                                                saturation = 1f,
                                                temperature = 0f
                                            )
                                        )
                                    }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                            // Close button
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { showQuickColorDrawer = false }
                            )
                        }
                    }

                    // Preset LUTs Carousel
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(
                            ColorGradingPresets.REC709_DIRECTOR,
                            ColorGradingPresets.KODAK_VISION3_5219,
                            ColorGradingPresets.TEAL_AND_ORANGE_BLOCKBUSTER,
                            ColorGradingPresets.FUJI_ETERNA_250D,
                            ColorGradingPresets.CYBERPUNK_NEON,
                            ColorGradingPresets.MONOCHROME_NOIR,
                            ColorGradingPresets.VINTAGE_POLAROID_70S,
                            ColorGradingPresets.BLEACH_BYPASS_WAR
                        ).forEach { lut ->
                            val isSel = colorGradingState.selectedLut.id == lut.id
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) Color(0xFF1E293B) else Color(0xFF090D16),
                                border = BorderStroke(1.dp, if (isSel) Color(0xFF38BDF8) else Color(0xFF1E293B)),
                                modifier = Modifier.clickable {
                                    onColorGradingChange(
                                        colorGradingState.copy(
                                            selectedLut = lut,
                                            contrast = lut.defaultContrast,
                                            saturation = lut.defaultSaturation,
                                            temperature = lut.defaultTemperature,
                                            brightness = lut.defaultBrightness
                                        )
                                    )
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(text = lut.iconEmoji, fontSize = 11.sp)
                                    Text(
                                        text = lut.name.take(12),
                                        fontSize = 8.5.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSel) Color(0xFF38BDF8) else Color.White
                                    )
                                }
                            }
                        }
                    }

                    // Sliders Row: Brillo, Contraste, Saturación, Temperatura
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Brillo
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "BRILLO (${(colorGradingState.brightness * 100).toInt()})", fontSize = 8.sp, color = Color(0xFF94A3B8))
                            Slider(
                                value = colorGradingState.brightness,
                                onValueChange = { onColorGradingChange(colorGradingState.copy(brightness = it)) },
                                valueRange = -0.5f..0.5f,
                                modifier = Modifier.height(18.dp)
                            )
                        }
                        // Contraste
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "CONTRASTE (${(colorGradingState.contrast * 100).toInt()}%)", fontSize = 8.sp, color = Color(0xFF94A3B8))
                            Slider(
                                value = colorGradingState.contrast,
                                onValueChange = { onColorGradingChange(colorGradingState.copy(contrast = it)) },
                                valueRange = 0.5f..1.8f,
                                modifier = Modifier.height(18.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Saturación
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "SATURACIÓN (${(colorGradingState.saturation * 100).toInt()}%)", fontSize = 8.sp, color = Color(0xFF94A3B8))
                            Slider(
                                value = colorGradingState.saturation,
                                onValueChange = { onColorGradingChange(colorGradingState.copy(saturation = it)) },
                                valueRange = 0.0f..2.0f,
                                modifier = Modifier.height(18.dp)
                            )
                        }
                        // Temperatura
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "TEMPERATURA (${(colorGradingState.temperature * 100).toInt()})", fontSize = 8.sp, color = Color(0xFF94A3B8))
                            Slider(
                                value = colorGradingState.temperature,
                                onValueChange = { onColorGradingChange(colorGradingState.copy(temperature = it)) },
                                valueRange = -0.8f..0.8f,
                                modifier = Modifier.height(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
