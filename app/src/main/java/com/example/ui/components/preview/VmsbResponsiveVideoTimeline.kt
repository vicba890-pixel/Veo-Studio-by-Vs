package com.example.ui.components.preview

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

import com.example.data.vert.VertEngine
import com.example.data.vert.VertEngineConfig
import com.example.data.vert.VertFrameTelemetry

/**
 * Modelo de datos para un fotograma o snapshot representativo de la tira de película VMSB.
 */
data class VmsbFrameSnapshot(
    val index: Int,
    val timestampMs: Long,
    val timecode: String,
    val bitmap: Bitmap? = null,
    val gopFrameType: String = "P-FRAME VMSB",
    val primaryColor: Color = Color(0xFF1E293B),
    val accentColor: Color = Color(0xFF38BDF8),
    val sceneLabel: String = "",
    val vertPanOffsetHint: Float = 0.5f
)

/**
 * Generador procedural de fotogramas VMSB para visualización inmediata de latencia cero,
 * con gradientes cinemáticos basados en el tiempo, tags de escena y estructura de GOP.
 */
fun generateProceduralVmsbSnapshots(
    totalDurationMs: Long,
    count: Int = 10,
    fps: Int = 30
): List<VmsbFrameSnapshot> {
    val duration = totalDurationMs.coerceAtLeast(1000L)
    val stepMs = duration / count.coerceAtLeast(1)
    val moodColors = listOf(
        Color(0xFF0F172A) to Color(0xFF38BDF8),
        Color(0xFF1E1B4B) to Color(0xFF818CF8),
        Color(0xFF172554) to Color(0xFF60A5FA),
        Color(0xFF14532D) to Color(0xFF34D399),
        Color(0xFF451A03) to Color(0xFFFBBF24),
        Color(0xFF4C0519) to Color(0xFFF43F5E),
        Color(0xFF2E1065) to Color(0xFFA855F7),
        Color(0xFF0C4A6E) to Color(0xFF06B6D4)
    )

    return (0 until count).map { i ->
        val ts = (i * stepMs).coerceIn(0L, duration)
        val frameIdx = ((ts / 1000.0) * fps).roundToInt()
        val totalSecs = (ts / 1000).toInt()
        val mins = totalSecs / 60
        val secs = totalSecs % 60
        val millis = ((ts % 1000) / 10).toInt()
        val timecode = String.format("%02d:%02d.%02d", mins, secs, millis)
        val colorPair = moodColors[i % moodColors.size]
        val gop = when {
            i == 0 || i % 4 == 0 -> "I-KEYFRAME"
            i % 2 == 0 -> "B-BIDIR"
            else -> "P-STREAM"
        }
        val scene = when (i) {
            0 -> "Apertura"
            count / 2 -> "Clímax"
            count - 1 -> "Cierre"
            else -> "Escena #${i + 1}"
        }
        val vertHint = (0.5f + (sin(i.toDouble() * 0.8) * 0.25f).toFloat()).coerceIn(0.2f, 0.8f)

        VmsbFrameSnapshot(
            index = frameIdx,
            timestampMs = ts,
            timecode = timecode,
            bitmap = null,
            gopFrameType = gop,
            primaryColor = colorPair.first,
            accentColor = colorPair.second,
            sceneLabel = scene,
            vertPanOffsetHint = vertHint
        )
    }
}

/**
 * Componente nativo de línea de tiempo de video en Jetpack Compose con arquitectura VMSB©:
 * - Totalmente responsivo para teléfonos compactos, plegables y tablets expandidas.
 * - Tira de película cinemática de 35mm con miniaturas/snapshots de fotogramas reales y procedurales.
 * - Interacciones táctiles de recorte (Trimming) con manijas de Trim In y Trim Out independientes.
 * - Máscaras visuales para cabezas y colas excluidas del corte con rayado de alta visibilidad.
 * - Cabezal de reproducción (Playhead) interactivo con aguja iluminada y visor de fotograma ampliado.
 * - Presets de recorte rápido (100%, 50% inicial, 50% final, Reset).
 * - Sincronización bidireccional inmediata con Media3 ExoPlayer y el motor de streaming VMSB.
 * - Integración VERT©: Visualización de anclaje de sujeto vertical en cada fotograma.
 */
@Composable
fun VmsbResponsiveVideoTimeline(
    currentPositionMs: Long,
    durationMs: Long,
    trimStart: Float,
    trimEnd: Float,
    onTrimRangeChange: (start: Float, end: Float) -> Unit,
    onSeekTo: (Long) -> Unit,
    videoUri: String = "",
    fps: Int = 30,
    isPlaying: Boolean = false,
    vertConfig: VertEngineConfig? = null,
    modifier: Modifier = Modifier,
    minTrimSpanFraction: Float = 0.05f,
    showQuickTrimPresets: Boolean = true,
    showTelemetryBadge: Boolean = true
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val haptic = LocalHapticFeedback.current
    val effectiveDuration = durationMs.coerceAtLeast(1000L)

    // Snapshots de fotogramas cargados desde video o generados con el motor procedural VMSB
    var frameSnapshots by remember(effectiveDuration, fps) {
        mutableStateOf(generateProceduralVmsbSnapshots(effectiveDuration, count = 12, fps = fps))
    }

    // Extracción asíncrona de miniaturas reales con MediaMetadataRetriever si hay un URI de video accesible
    LaunchedEffect(videoUri, effectiveDuration) {
        if (videoUri.isNotBlank()) {
            withContext(Dispatchers.IO) {
                try {
                    val retriever = MediaMetadataRetriever()
                    val uri = Uri.parse(videoUri)
                    if (videoUri.startsWith("http://") || videoUri.startsWith("https://")) {
                        retriever.setDataSource(videoUri, HashMap())
                    } else {
                        retriever.setDataSource(context, uri)
                    }

                    val count = 10
                    val stepUs = (effectiveDuration * 1000L) / count.coerceAtLeast(1)
                    val loaded = mutableListOf<VmsbFrameSnapshot>()

                    for (i in 0 until count) {
                        val timeUs = (i * stepUs).coerceIn(0L, effectiveDuration * 1000L)
                        val bmp = try {
                            retriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                        } catch (e: Exception) {
                            null
                        }

                        val tsMs = timeUs / 1000L
                        val frameIdx = ((tsMs / 1000.0) * fps).roundToInt()
                        val totalSecs = (tsMs / 1000).toInt()
                        val mins = totalSecs / 60
                        val secs = totalSecs % 60
                        val millis = ((tsMs % 1000) / 10).toInt()
                        val timecode = String.format("%02d:%02d.%02d", mins, secs, millis)

                        loaded.add(
                            VmsbFrameSnapshot(
                                index = frameIdx,
                                timestampMs = tsMs,
                                timecode = timecode,
                                bitmap = bmp,
                                gopFrameType = if (i % 3 == 0) "I-FRAME" else "P-VMSB",
                                primaryColor = Color(0xFF0F172A),
                                accentColor = Color(0xFF38BDF8),
                                sceneLabel = "F#$frameIdx"
                            )
                        )
                    }

                    retriever.release()
                    if (loaded.any { it.bitmap != null }) {
                        withContext(Dispatchers.Main) {
                            frameSnapshots = loaded
                        }
                    }
                } catch (e: Exception) {
                    // Si falla la extracción directa por red o permisos, se mantienen los snapshots cinemáticos procedurales VMSB
                    e.printStackTrace()
                }
            }
        }
    }

    // Estados de interacción para recorte (Trimming) y arrastre del cabezal
    var isDraggingStartHandle by remember { mutableStateOf(false) }
    var isDraggingEndHandle by remember { mutableStateOf(false) }
    var isDraggingPlayhead by remember { mutableStateOf(false) }

    // Tiempo actual normalizado (0.0f a 1.0f)
    val currentNorm = (currentPositionMs.toFloat() / effectiveDuration.toFloat()).coerceIn(0f, 1f)

    // Duración activa recortada
    val trimmedDurationMs = remember(trimStart, trimEnd, effectiveDuration) {
        ((trimEnd - trimStart).coerceAtLeast(0f) * effectiveDuration).toLong()
    }

    // Fotograma más cercano a la posición actual del cabezal para vista previa ampliada
    val activeSnapshot = remember(currentNorm, frameSnapshots) {
        val targetIdx = (currentNorm * (frameSnapshots.size - 1)).roundToInt().coerceIn(0, frameSnapshots.lastIndex)
        frameSnapshots.getOrNull(targetIdx)
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF070B14))
            .border(1.2.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
            .padding(12.dp)
            .testTag("vmsb_responsive_video_timeline_root")
    ) {
        val totalWidthDp = maxWidth
        val isCompactLayout = totalWidthDp < 540.dp
        val isTabletExpanded = totalWidthDp >= 800.dp

        Column(modifier = Modifier.fillMaxWidth()) {

            // ==========================================
            // 1. BARRA SUPERIOR: TELEMETRÍA Y TIME-CODES
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Badge VMSB Native Engine
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF38BDF8).copy(alpha = 0.15f))
                            .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (isPlaying) Color(0xFF10B981) else Color(0xFF38BDF8))
                            )
                            Text(
                                text = "VMSB© NATIVE TIMELINE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF38BDF8),
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    if (showTelemetryBadge && !isCompactLayout) {
                        Text(
                            text = "${fps} FPS • ${frameSnapshots.size} SNAPSHOTS",
                            fontSize = 9.sp,
                            color = Color(0xFF94A3B8),
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    if (vertConfig != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF0284C7).copy(alpha = 0.25f))
                                .border(0.8.dp, Color(0xFF38BDF8), RoundedCornerShape(4.dp))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "VERT© ${vertConfig.mode.name.take(4)}",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF38BDF8),
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Desglose de Recorte: In Point - Playhead - Out Point
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Trim In Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF0F172A))
                            .border(0.5.dp, Color(0xFFF59E0B).copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "IN: ${formatTimecode( (trimStart * effectiveDuration).toLong() )}",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFBBF24),
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // Playhead Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF0F172A))
                            .border(0.5.dp, Color(0xFFEF4444).copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "POS: ${formatTimecode(currentPositionMs)}",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF87171),
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // Trim Out Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF0F172A))
                            .border(0.5.dp, Color(0xFFF59E0B).copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "OUT: ${formatTimecode( (trimEnd * effectiveDuration).toLong() )}",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFBBF24),
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // ==========================================
            // 2. TIRA DE PELÍCULA RESPONSIVA & LIENZO DE RECORTE
            // ==========================================
            val trackHeightDp = if (isTabletExpanded) 96.dp else if (isCompactLayout) 74.dp else 84.dp

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(trackHeightDp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF030712))
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp))
                    .testTag("vmsb_timeline_track_canvas_container")
            ) {
                // A. Tira de miniaturas de fotogramas (Row responsiva con snapshots distribuidos)
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 2.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    val displaySnapshots = remember(frameSnapshots, isCompactLayout) {
                        if (isCompactLayout) {
                            frameSnapshots.filterIndexed { index, _ -> index % 2 == 0 }
                        } else {
                            frameSnapshots
                        }
                    }

                    displaySnapshots.forEachIndexed { idx, snap ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(4.dp))
                                .background(snap.primaryColor)
                                .border(0.5.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (snap.bitmap != null) {
                                androidx.compose.foundation.Image(
                                    bitmap = snap.bitmap.asImageBitmap(),
                                    contentDescription = "Snapshot ${snap.timecode}",
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                // Dibujo vectorial estilizado del fotograma
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    snap.primaryColor,
                                                    snap.accentColor.copy(alpha = 0.35f),
                                                    Color.Black.copy(alpha = 0.85f)
                                                )
                                            )
                                        )
                                ) {
                                    // Sello de fotograma
                                    Text(
                                        text = snap.sceneLabel,
                                        fontSize = 7.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontFamily = FontFamily.Monospace,
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .padding(2.dp),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            // Timecode inferior sobreimpreso en la miniatura
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .background(Color.Black.copy(alpha = 0.7f))
                                    .padding(vertical = 1.dp)
                            ) {
                                Text(
                                    text = snap.timecode,
                                    fontSize = 7.sp,
                                    color = Color.White.copy(alpha = 0.85f),
                                    textAlign = TextAlign.Center,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            // Marca VERT: Línea vertical sutil de seguimiento de sujeto
                            if (vertConfig != null && vertConfig.showGuideOverlay) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .fillMaxHeight()
                                        .fillMaxWidth()
                                ) {
                                    val anchorBias = (snap.vertPanOffsetHint * 2f) - 1f
                                    Box(
                                        modifier = Modifier
                                            .align(BiasAlignment(anchorBias, -1f))
                                            .width(1.5.dp)
                                            .fillMaxHeight(0.6f)
                                            .background(Color(0xFF38BDF8).copy(alpha = 0.65f))
                                    )
                                }
                            }
                        }
                    }
                }

                // B. Superficie de Canvas interactiva para perforaciones de 35mm, máscaras de recorte y Playhead
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(effectiveDuration, trimStart, trimEnd) {
                            detectTapGestures { offset ->
                                val norm = (offset.x / size.width).coerceIn(0f, 1f)
                                val seekTarget = (norm * effectiveDuration).toLong()
                                onSeekTo(seekTarget)
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                        }
                ) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height

                    // 1. Perforaciones cinematográficas superiores e inferiores (estilo película 35mm)
                    val holeWidth = 5.dp.toPx()
                    val holeHeight = 3.dp.toPx()
                    val holeGap = 10.dp.toPx()
                    val totalHoles = (canvasWidth / (holeWidth + holeGap)).toInt() + 1
                    for (i in 0 until totalHoles) {
                        val hx = i * (holeWidth + holeGap) + 3.dp.toPx()
                        drawRoundRect(
                            color = Color(0xFF1E293B).copy(alpha = 0.8f),
                            topLeft = Offset(hx, 1.5.dp.toPx()),
                            size = Size(holeWidth, holeHeight),
                            cornerRadius = CornerRadius(1.dp.toPx(), 1.dp.toPx())
                        )
                        drawRoundRect(
                            color = Color(0xFF1E293B).copy(alpha = 0.8f),
                            topLeft = Offset(hx, canvasHeight - holeHeight - 1.5.dp.toPx()),
                            size = Size(holeWidth, holeHeight),
                            cornerRadius = CornerRadius(1.dp.toPx(), 1.dp.toPx())
                        )
                    }

                    val trimStartX = (trimStart * canvasWidth).coerceIn(0f, canvasWidth)
                    val trimEndX = (trimEnd * canvasWidth).coerceIn(0f, canvasWidth)

                    // 2. Máscara de corte Head (0f hasta trimStart)
                    if (trimStartX > 0f) {
                        drawRect(
                            color = Color.Black.copy(alpha = 0.72f),
                            topLeft = Offset(0f, 0f),
                            size = Size(trimStartX, canvasHeight)
                        )
                        // Rayado diagonal de advertencia de material cortado
                        var hatchX = 0f
                        while (hatchX < trimStartX) {
                            drawLine(
                                color = Color.White.copy(alpha = 0.08f),
                                start = Offset(hatchX, canvasHeight),
                                end = Offset(hatchX + 16.dp.toPx(), 0f),
                                strokeWidth = 1.2.dp.toPx()
                            )
                            hatchX += 14.dp.toPx()
                        }
                    }

                    // 3. Máscara de corte Tail (trimEnd hasta canvasWidth)
                    if (trimEndX < canvasWidth) {
                        drawRect(
                            color = Color.Black.copy(alpha = 0.72f),
                            topLeft = Offset(trimEndX, 0f),
                            size = Size(canvasWidth - trimEndX, canvasHeight)
                        )
                        var hatchX = trimEndX
                        while (hatchX < canvasWidth) {
                            drawLine(
                                color = Color.White.copy(alpha = 0.08f),
                                start = Offset(hatchX, canvasHeight),
                                end = Offset(hatchX + 16.dp.toPx(), 0f),
                                strokeWidth = 1.2.dp.toPx()
                            )
                            hatchX += 14.dp.toPx()
                        }
                    }

                    // 4. Borde del rango activo seleccionado (Trim Range Active Border)
                    val activeWidth = (trimEndX - trimStartX).coerceAtLeast(0f)
                    if (activeWidth > 0f) {
                        // Borde superior iluminado ámbar / oro cinematográfico
                        drawLine(
                            color = Color(0xFFF59E0B),
                            start = Offset(trimStartX, 0f),
                            end = Offset(trimEndX, 0f),
                            strokeWidth = 3.dp.toPx()
                        )
                        // Borde inferior iluminado
                        drawLine(
                            color = Color(0xFFF59E0B),
                            start = Offset(trimStartX, canvasHeight),
                            end = Offset(trimEndX, canvasHeight),
                            strokeWidth = 3.dp.toPx()
                        )
                    }

                    // 5. Aguja del cabezal de reproducción (Playhead)
                    val playheadX = (currentNorm * canvasWidth).coerceIn(0f, canvasWidth)

                    // Halo rojo brillante
                    drawLine(
                        color = Color(0xFFEF4444).copy(alpha = 0.35f),
                        start = Offset(playheadX, 0f),
                        end = Offset(playheadX, canvasHeight),
                        strokeWidth = 6.dp.toPx()
                    )
                    // Aguja central nítida
                    drawLine(
                        color = Color(0xFFEF4444),
                        start = Offset(playheadX, 0f),
                        end = Offset(playheadX, canvasHeight),
                        strokeWidth = 2.dp.toPx()
                    )

                    // Diamante superior del cabezal
                    val playheadDiamondSize = 6.dp.toPx()
                    val diamondPath = Path().apply {
                        moveTo(playheadX, 0f)
                        lineTo(playheadX + playheadDiamondSize, playheadDiamondSize * 1.4f)
                        lineTo(playheadX, playheadDiamondSize * 2f)
                        lineTo(playheadX - playheadDiamondSize, playheadDiamondSize * 1.4f)
                        close()
                    }
                    drawPath(diamondPath, color = Color(0xFFEF4444))
                    drawPath(diamondPath, color = Color.White, style = Stroke(1.dp.toPx()))
                }

                // C. Manija de Recorte Inicial (Trim IN Handle) con detector de gestos táctiles de arrastre
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val availableWidthPx = constraints.maxWidth.toFloat()

                    // Trim Start Handle
                    val startHandleOffsetDp = with(density) {
                        (trimStart * availableWidthPx).toDp() - 14.dp
                    }
                    Box(
                        modifier = Modifier
                            .offset(x = startHandleOffsetDp.coerceAtLeast(0.dp))
                            .width(28.dp)
                            .fillMaxHeight()
                            .pointerInput(effectiveDuration, trimEnd) {
                                detectHorizontalDragGestures(
                                    onDragStart = {
                                        isDraggingStartHandle = true
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    },
                                    onDragEnd = {
                                        isDraggingStartHandle = false
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    },
                                    onDragCancel = {
                                        isDraggingStartHandle = false
                                    },
                                    onHorizontalDrag = { change, dragAmount ->
                                        change.consume()
                                        val deltaNorm = dragAmount / availableWidthPx
                                        val newStart = (trimStart + deltaNorm).coerceIn(0f, (trimEnd - minTrimSpanFraction).coerceAtLeast(0f))
                                        onTrimRangeChange(newStart, trimEnd)
                                        // También reposicionar cabezal al punto de recorte para retroalimentación visual en vivo
                                        onSeekTo((newStart * effectiveDuration).toLong())
                                    }
                                )
                            }
                            .testTag("vmsb_timeline_trim_start_handle"),
                        contentAlignment = Alignment.Center
                    ) {
                        // Manija visual con textura de agarre táctil
                        Surface(
                            shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp, topEnd = 3.dp, bottomEnd = 3.dp),
                            color = if (isDraggingStartHandle) Color(0xFFFBBF24) else Color(0xFFF59E0B),
                            shadowElevation = 4.dp,
                            modifier = Modifier
                                .width(16.dp)
                                .fillMaxHeight()
                                .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp, topEnd = 3.dp, bottomEnd = 3.dp))
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                // Líneas de textura táctil (grip bars)
                                repeat(3) {
                                    Box(
                                        modifier = Modifier
                                            .width(7.dp)
                                            .height(2.dp)
                                            .background(Color(0xFF78350F), RoundedCornerShape(1.dp))
                                            .padding(vertical = 1.dp)
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                }
                            }
                        }
                    }

                    // Trim End Handle
                    val endHandleOffsetDp = with(density) {
                        (trimEnd * availableWidthPx).toDp() - 14.dp
                    }
                    Box(
                        modifier = Modifier
                            .offset(x = endHandleOffsetDp.coerceAtMost(totalWidthDp - 28.dp))
                            .width(28.dp)
                            .fillMaxHeight()
                            .pointerInput(effectiveDuration, trimStart) {
                                detectHorizontalDragGestures(
                                    onDragStart = {
                                        isDraggingEndHandle = true
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    },
                                    onDragEnd = {
                                        isDraggingEndHandle = false
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    },
                                    onDragCancel = {
                                        isDraggingEndHandle = false
                                    },
                                    onHorizontalDrag = { change, dragAmount ->
                                        change.consume()
                                        val deltaNorm = dragAmount / availableWidthPx
                                        val newEnd = (trimEnd + deltaNorm).coerceIn((trimStart + minTrimSpanFraction).coerceAtMost(1f), 1f)
                                        onTrimRangeChange(trimStart, newEnd)
                                        onSeekTo((newEnd * effectiveDuration).toLong())
                                    }
                                )
                            }
                            .testTag("vmsb_timeline_trim_end_handle"),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp, topStart = 3.dp, bottomStart = 3.dp),
                            color = if (isDraggingEndHandle) Color(0xFFFBBF24) else Color(0xFFF59E0B),
                            shadowElevation = 4.dp,
                            modifier = Modifier
                                .width(16.dp)
                                .fillMaxHeight()
                                .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp, topStart = 3.dp, bottomStart = 3.dp))
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                repeat(3) {
                                    Box(
                                        modifier = Modifier
                                            .width(7.dp)
                                            .height(2.dp)
                                            .background(Color(0xFF78350F), RoundedCornerShape(1.dp))
                                            .padding(vertical = 1.dp)
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ==========================================
            // 3. BARRA INFERIOR: DURACIÓN ACTIVA Y PRESETS DE RECORTE
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Métricas del corte
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(
                        imageVector = Icons.Default.ContentCut,
                        contentDescription = "Trim Tool",
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "CORTE ACTIVO: ${formatTimecode(trimmedDurationMs)} / ${formatTimecode(effectiveDuration)}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = FontFamily.Monospace
                    )
                    val cutPercentage = (1f - (trimEnd - trimStart).coerceIn(0f, 1f)) * 100f
                    if (cutPercentage > 1f) {
                        Text(
                            text = "(-${cutPercentage.toInt()}%)",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981),
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Botones de presets de recorte rápido
                if (showQuickTrimPresets) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Preset 1: Restablecer (100% completo)
                        TimelinePresetButton(
                            label = "100%",
                            isActive = trimStart == 0f && trimEnd == 1f,
                            onClick = {
                                onTrimRangeChange(0f, 1f)
                                onSeekTo(0L)
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            },
                            testTag = "vmsb_trim_preset_full"
                        )

                        // Preset 2: 1ra Mitad (0 - 50%)
                        TimelinePresetButton(
                            label = "1RA MITAD",
                            isActive = trimStart == 0f && trimEnd == 0.5f,
                            onClick = {
                                onTrimRangeChange(0f, 0.5f)
                                onSeekTo(0L)
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            },
                            testTag = "vmsb_trim_preset_first_half"
                        )

                        // Preset 3: 2da Mitad (50 - 100%)
                        TimelinePresetButton(
                            label = "2DA MITAD",
                            isActive = trimStart == 0.5f && trimEnd == 1f,
                            onClick = {
                                onTrimRangeChange(0.5f, 1f)
                                onSeekTo((0.5f * effectiveDuration).toLong())
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            },
                            testTag = "vmsb_trim_preset_second_half"
                        )
                    }
                }
            }
        }
    }
}

/**
 * Botón chip estilizado para los presets de recorte rápido.
 */
@Composable
private fun TimelinePresetButton(
    label: String,
    isActive: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isActive) Color(0xFFF59E0B) else Color(0xFF1E293B))
            .clickable(onClick = onClick)
            .padding(horizontal = 7.dp, vertical = 3.dp)
            .testTag(testTag)
    ) {
        Text(
            text = label,
            fontSize = 8.5.sp,
            fontWeight = if (isActive) FontWeight.Black else FontWeight.Bold,
            color = if (isActive) Color(0xFF0F172A) else Color(0xFF94A3B8),
            fontFamily = FontFamily.Monospace
        )
    }
}

/**
 * Formatea milisegundos en código de tiempo estándar MM:SS.ms
 */
private fun formatTimecode(timeMs: Long): String {
    if (timeMs <= 0L) return "00:00.00"
    val totalSeconds = (timeMs / 1000).toInt()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val millis = ((timeMs % 1000) / 10).toInt()
    return String.format("%02d:%02d.%02d", minutes, seconds, millis)
}
