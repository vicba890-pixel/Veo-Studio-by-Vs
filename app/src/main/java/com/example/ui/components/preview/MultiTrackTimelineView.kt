package com.example.ui.components.preview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.multitrack.*
import com.example.data.multitrack.command.*
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Vista de línea de tiempo multi-pista nativa sincronizada con el motor de scrubbing.
 * Permite gestionar capas simultáneas de video, texto superpuesto, pistas de audio y efectos.
 * Integrada con el Patrón Command para deshacer y rehacer granularmente cualquier acción.
 */
@Composable
fun MultiTrackTimelineView(
    composition: MultiTrackComposition,
    currentPositionMs: Long,
    durationMs: Long,
    zoomScale: Float = 1.0f,
    windowStartNorm: Float = 0f,
    canUndo: Boolean = false,
    canRedo: Boolean = false,
    undoDescription: String? = null,
    redoDescription: String? = null,
    undoHistoryList: List<String> = emptyList(),
    redoHistoryList: List<String> = emptyList(),
    onUndo: (() -> Unit)? = null,
    onRedo: (() -> Unit)? = null,
    onExecuteCommand: ((MultiTrackCommand) -> Unit)? = null,
    onSeekTo: (Long) -> Unit,
    onCompositionChange: (MultiTrackComposition) -> Unit,
    onImportVideoClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val effectiveDuration = durationMs.coerceAtLeast(1000L)
    val windowSpan = (1f / zoomScale.coerceAtLeast(1f)).coerceIn(0.1f, 1f)
    val effectiveStartNorm = windowStartNorm.coerceIn(0f, (1f - windowSpan).coerceAtLeast(0f))
    val effectiveEndNorm = (effectiveStartNorm + windowSpan).coerceAtMost(1f)

    var showInspectorDialog by remember { mutableStateOf(false) }
    var selectedClipForInspector by remember { mutableStateOf<TimelineClip?>(null) }
    var showHistoryDialog by remember { mutableStateOf(false) }
    var isExpandedView by remember { mutableStateOf(true) }

    val dispatchCommand: (MultiTrackCommand) -> Unit = { cmd ->
        if (onExecuteCommand != null) {
            onExecuteCommand(cmd)
        } else {
            onCompositionChange(cmd.execute(composition))
        }
    }

    val selectedClip = remember(composition.selectedClipId, composition.tracks) {
        composition.selectedClipId?.let { composition.findClip(it) }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0B0F19))
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
            .padding(10.dp)
            .testTag("multi_track_timeline_root")
    ) {

        // ==========================================
        // 1. BARRA SUPERIOR DE ACCIONES Y CAPAS
        // ==========================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF38BDF8).copy(alpha = 0.2f))
                        .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                        .testTag("multi_track_badge")
                ) {
                    Text(
                        text = "MULTI-TRACK (${composition.tracks.size} LANES)",
                        color = Color(0xFF38BDF8),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Botón magnético de snap
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (composition.isSnappingEnabled) Color(0xFF10B981).copy(alpha = 0.2f)
                            else Color(0xFF334155)
                        )
                        .clickable {
                            onCompositionChange(composition.copy(isSnappingEnabled = !composition.isSnappingEnabled))
                        }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                        .testTag("multi_track_snap_toggle_btn")
                ) {
                    Text(
                        text = if (composition.isSnappingEnabled) "🧲 SNAP ON" else "🧲 SNAP OFF",
                        color = if (composition.isSnappingEnabled) Color(0xFF6EE7B7) else Color(0xFF94A3B8),
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Botón Deshacer (Undo) Command Pattern
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (canUndo) Color(0xFF2563EB).copy(alpha = 0.25f) else Color(0xFF1E293B).copy(alpha = 0.35f))
                        .border(
                            1.dp,
                            if (canUndo) Color(0xFF38BDF8).copy(alpha = 0.6f) else Color(0xFF334155).copy(alpha = 0.3f),
                            RoundedCornerShape(4.dp)
                        )
                        .clickable(enabled = canUndo) {
                            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                            onUndo?.invoke()
                        }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                        .testTag("multi_track_undo_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "↶ UNDO",
                            color = if (canUndo) Color(0xFF38BDF8) else Color(0xFF64748B),
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        if (canUndo && !undoDescription.isNullOrBlank()) {
                            Text(
                                text = "(${undoDescription.take(8)})",
                                color = Color(0xFF93C5FD),
                                fontSize = 7.5.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Botón Rehacer (Redo) Command Pattern
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (canRedo) Color(0xFF0D9488).copy(alpha = 0.25f) else Color(0xFF1E293B).copy(alpha = 0.35f))
                        .border(
                            1.dp,
                            if (canRedo) Color(0xFF2DD4BF).copy(alpha = 0.6f) else Color(0xFF334155).copy(alpha = 0.3f),
                            RoundedCornerShape(4.dp)
                        )
                        .clickable(enabled = canRedo) {
                            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                            onRedo?.invoke()
                        }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                        .testTag("multi_track_redo_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "↷ REDO",
                            color = if (canRedo) Color(0xFF2DD4BF) else Color(0xFF64748B),
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        if (canRedo && !redoDescription.isNullOrBlank()) {
                            Text(
                                text = "(${redoDescription.take(8)})",
                                color = Color(0xFF99F6E4),
                                fontSize = 7.5.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Botón Historial de Comandos
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF1E293B).copy(alpha = 0.5f))
                        .clickable { showHistoryDialog = true }
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                        .testTag("multi_track_history_toggle_btn")
                ) {
                    Text(
                        text = "🕒 ${undoHistoryList.size}",
                        color = if (undoHistoryList.isNotEmpty()) Color(0xFFF1F5F9) else Color(0xFF94A3B8),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Acciones rápidas de creación de capas
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // + TEXT OVERLAY
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFF59E0B).copy(alpha = 0.2f))
                        .border(0.5.dp, Color(0xFFF59E0B), RoundedCornerShape(4.dp))
                        .clickable {
                            val textTrack = composition.tracks.firstOrNull { it.type == TrackType.TEXT }
                            if (textTrack != null) {
                                val newClip = TimelineClip(
                                    trackId = textTrack.id,
                                    trackType = TrackType.TEXT,
                                    label = "Text Title",
                                    startMs = currentPositionMs.coerceAtMost(effectiveDuration - 1000L),
                                    durationMs = 2500L.coerceAtMost(effectiveDuration - currentPositionMs),
                                    colorHex = 0xFFF59E0B,
                                    textPayload = TextOverlayPayload(
                                        text = "New Overlay Title",
                                        fontSizeSp = 20f,
                                        position = TextPosition.BOTTOM
                                    )
                                )
                                dispatchCommand(MultiTrackCommandFactory.createAddClipCommand(textTrack.id, newClip))
                            }
                        }
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                        .testTag("add_text_track_btn")
                ) {
                    Text(
                        text = "+ TEXT",
                        color = Color(0xFFFCD34D),
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // + AUDIO TRACK
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF10B981).copy(alpha = 0.2f))
                        .border(0.5.dp, Color(0xFF10B981), RoundedCornerShape(4.dp))
                        .clickable {
                            val audioTrack = composition.tracks.firstOrNull { it.type == TrackType.AUDIO }
                            if (audioTrack != null) {
                                val newClip = TimelineClip(
                                    trackId = audioTrack.id,
                                    trackType = TrackType.AUDIO,
                                    label = "Audio SFX",
                                    startMs = currentPositionMs.coerceAtMost(effectiveDuration - 800L),
                                    durationMs = 2000L.coerceAtMost(effectiveDuration - currentPositionMs),
                                    colorHex = 0xFF10B981,
                                    audioPayload = AudioClipPayload(
                                        audioTitle = "SFX Impact",
                                        category = AudioCategory.SFX,
                                        volume = 1.0f
                                    )
                                )
                                dispatchCommand(MultiTrackCommandFactory.createAddClipCommand(audioTrack.id, newClip))
                            }
                        }
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                        .testTag("add_audio_track_btn")
                ) {
                    Text(
                        text = "+ AUDIO",
                        color = Color(0xFF6EE7B7),
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // + FX TRACK
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFA855F7).copy(alpha = 0.2f))
                        .border(0.5.dp, Color(0xFFA855F7), RoundedCornerShape(4.dp))
                        .clickable {
                            val fxTrack = composition.tracks.firstOrNull { it.type == TrackType.EFFECT }
                            if (fxTrack != null) {
                                val newClip = TimelineClip(
                                    trackId = fxTrack.id,
                                    trackType = TrackType.EFFECT,
                                    label = "FX Preset",
                                    startMs = currentPositionMs.coerceAtMost(effectiveDuration - 1000L),
                                    durationMs = 3000L.coerceAtMost(effectiveDuration - currentPositionMs),
                                    colorHex = 0xFFA855F7,
                                    effectPayload = EffectClipPayload(
                                        effectType = EffectType.VHS_RETRO_GLITCH,
                                        intensity = 0.75f
                                    )
                                )
                                dispatchCommand(MultiTrackCommandFactory.createAddClipCommand(fxTrack.id, newClip))
                            }
                        }
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                        .testTag("add_fx_track_btn")
                ) {
                    Text(
                        text = "+ FX",
                        color = Color(0xFFD8B4FE),
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // + VIDEO TRACK (Media Picker Service)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF3B82F6).copy(alpha = 0.25f))
                        .border(0.5.dp, Color(0xFF38BDF8), RoundedCornerShape(4.dp))
                        .clickable {
                            onImportVideoClick?.invoke()
                        }
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                        .testTag("timeline_import_video_btn")
                ) {
                    Text(
                        text = "+ VIDEO",
                        color = Color(0xFF93C5FD),
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // ==========================================
        // 2. BARRA DE EDICIÓN DEL CLIP SELECCIONADO
        // ==========================================
        if (selectedClip != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF1E293B).copy(alpha = 0.6f))
                    .border(0.5.dp, Color(0xFF38BDF8).copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("selected_clip_toolbar"),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(selectedClip.colorHex))
                    )
                    Text(
                        text = "${selectedClip.label} (${selectedClip.startMs}ms - ${selectedClip.endMs}ms)",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botón Dividir en Cabezal
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFF334155))
                            .clickable {
                                val cmd = MultiTrackCommandFactory.createSplitClipCommand(
                                    composition,
                                    selectedClip.id,
                                    currentPositionMs
                                )
                                if (cmd != null) {
                                    dispatchCommand(cmd)
                                } else {
                                    onCompositionChange(
                                        MultiTrackStateManager.splitClipAt(composition, selectedClip.id, currentPositionMs)
                                    )
                                }
                            }
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                            .testTag("split_clip_btn")
                    ) {
                        Text("✂ SPLIT", color = Color(0xFF38BDF8), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }

                    // Botón Duplicar
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFF334155))
                            .clickable {
                                val cmd = MultiTrackCommandFactory.createDuplicateClipCommand(
                                    composition,
                                    selectedClip.id,
                                    offsetMs = 300L,
                                    maxDurationMs = effectiveDuration
                                )
                                if (cmd != null) {
                                    dispatchCommand(cmd)
                                } else {
                                    onCompositionChange(
                                        MultiTrackStateManager.duplicateClip(
                                            composition,
                                            selectedClip.id,
                                            offsetMs = 300L,
                                            maxDurationMs = effectiveDuration
                                        )
                                    )
                                }
                            }
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                            .testTag("duplicate_clip_btn")
                    ) {
                        Text("⧉ DUP", color = Color(0xFFF1F5F9), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }

                    // Botón Editar Inspector
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFF2563EB))
                            .clickable {
                                selectedClipForInspector = selectedClip
                                showInspectorDialog = true
                            }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                            .testTag("inspect_clip_btn")
                    ) {
                        Text("✎ EDIT", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }

                    // Botón Eliminar
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFFEF4444).copy(alpha = 0.2f))
                            .clickable {
                                val cmd = MultiTrackCommandFactory.createRemoveClipCommand(composition, selectedClip.id)
                                if (cmd != null) {
                                    dispatchCommand(cmd)
                                } else {
                                    onCompositionChange(MultiTrackStateManager.removeClip(composition, selectedClip.id))
                                }
                            }
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                            .testTag("delete_clip_btn")
                    ) {
                        Text("✕ DEL", color = Color(0xFFFCA5A5), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        // ==========================================
        // 3. MATRIZ DE PISTAS MULTI-CAPA
        // ==========================================
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF070A10))
                .border(0.5.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp))
                .testTag("multi_track_matrix_container")
        ) {
            val totalWidthPx = constraints.maxWidth.toFloat()
            val headerWidthDp = 72.dp
            val headerWidthPx = with(androidx.compose.ui.platform.LocalDensity.current) { headerWidthDp.toPx() }
            val timelineWidthPx = (totalWidthPx - headerWidthPx).coerceAtLeast(10f)

            Column(modifier = Modifier.fillMaxWidth()) {
                composition.tracks.forEach { track ->
                    TrackLaneRow(
                        track = track,
                        selectedClipId = composition.selectedClipId,
                        durationMs = effectiveDuration,
                        windowStartNorm = effectiveStartNorm,
                        windowEndNorm = effectiveEndNorm,
                        headerWidthDp = headerWidthDp,
                        onTrackToggleMute = {
                            val cmd = MultiTrackCommandFactory.createToggleMuteCommand(composition, track.id)
                            if (cmd != null) dispatchCommand(cmd)
                            else onCompositionChange(MultiTrackStateManager.toggleTrackMute(composition, track.id))
                        },
                        onTrackToggleVisibility = {
                            val cmd = MultiTrackCommandFactory.createToggleVisibilityCommand(composition, track.id)
                            if (cmd != null) dispatchCommand(cmd)
                            else onCompositionChange(MultiTrackStateManager.toggleTrackVisibility(composition, track.id))
                        },
                        onClipSelected = { clip ->
                            onCompositionChange(
                                composition.copy(
                                    selectedClipId = if (composition.selectedClipId == clip.id) null else clip.id,
                                    selectedTrackId = track.id
                                )
                            )
                        }
                    )
                }
            }

            // Superposición de interacción táctil y aguja de reproducción sincronizada
            val progressNorm = (currentPositionMs.toFloat() / effectiveDuration).coerceIn(0f, 1f)
            val isPlayheadInView = progressNorm in effectiveStartNorm..effectiveEndNorm
            val playheadRelNorm = if (effectiveEndNorm > effectiveStartNorm) {
                ((progressNorm - effectiveStartNorm) / (effectiveEndNorm - effectiveStartNorm)).coerceIn(0f, 1f)
            } else 0f
            val playheadX = headerWidthPx + playheadRelNorm * timelineWidthPx

            // Canvas de aguja sincronizada que atraviesa todas las pistas
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(effectiveDuration, effectiveStartNorm, effectiveEndNorm, composition.isSnappingEnabled) {
                        detectTapGestures { offset ->
                            if (offset.x >= headerWidthPx) {
                                val relNorm = ((offset.x - headerWidthPx) / timelineWidthPx).coerceIn(0f, 1f)
                                val rawTargetMs = (effectiveStartNorm + relNorm * (effectiveEndNorm - effectiveStartNorm)) * effectiveDuration
                                val snapped = MultiTrackStateManager.calculateSnappedTimestamp(
                                    rawTargetMs.toLong(),
                                    composition
                                )
                                onSeekTo(snapped.coerceIn(0L, effectiveDuration))
                            }
                        }
                    }
                    .pointerInput(effectiveDuration, effectiveStartNorm, effectiveEndNorm, composition.isSnappingEnabled) {
                        detectDragGestures { change, _ ->
                            change.consume()
                            val curX = change.position.x
                            if (curX >= headerWidthPx) {
                                val relNorm = ((curX - headerWidthPx) / timelineWidthPx).coerceIn(0f, 1f)
                                val rawTargetMs = (effectiveStartNorm + relNorm * (effectiveEndNorm - effectiveStartNorm)) * effectiveDuration
                                val snapped = MultiTrackStateManager.calculateSnappedTimestamp(
                                    rawTargetMs.toLong(),
                                    composition
                                )
                                onSeekTo(snapped.coerceIn(0L, effectiveDuration))
                            }
                        }
                    }
                    .testTag("multi_track_playhead_overlay")
            ) {
                if (isPlayheadInView) {
                    // Línea vertical que corta todas las pistas
                    drawLine(
                        color = Color(0xFFEF4444),
                        start = Offset(playheadX, 0f),
                        end = Offset(playheadX, size.height),
                        strokeWidth = 2.dp.toPx()
                    )
                    // Cabezal superior en forma de triángulo / puntero
                    drawCircle(
                        color = Color(0xFFEF4444),
                        radius = 4.dp.toPx(),
                        center = Offset(playheadX, 4.dp.toPx())
                    )
                }
            }
        }
    }

    // ==========================================
    // 4. DIÁLOGO INSPECTOR DE CLIP SELECCIONADO
    // ==========================================
    if (showInspectorDialog && selectedClipForInspector != null) {
        val currentInspectClip = composition.findClip(selectedClipForInspector!!.id) ?: selectedClipForInspector!!
        ClipInspectorDialog(
            clip = currentInspectClip,
            durationMs = effectiveDuration,
            onDismiss = { showInspectorDialog = false },
            onUpdateClip = { updated ->
                val cmd = MultiTrackCommandFactory.createUpdateClipCommand(composition, updated)
                if (cmd != null) {
                    dispatchCommand(cmd)
                } else {
                    onCompositionChange(MultiTrackStateManager.updateClip(composition, updated))
                }
            },
            onDeleteClip = {
                val cmd = MultiTrackCommandFactory.createRemoveClipCommand(composition, currentInspectClip.id)
                if (cmd != null) {
                    dispatchCommand(cmd)
                } else {
                    onCompositionChange(MultiTrackStateManager.removeClip(composition, currentInspectClip.id))
                }
                showInspectorDialog = false
            }
        )
    }

    // ==========================================
    // 5. DIÁLOGO HISTORIAL DE COMANDOS (UNDO/REDO)
    // ==========================================
    if (showHistoryDialog) {
        Dialog(onDismissRequest = { showHistoryDialog = false }) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                    .testTag("multi_track_history_dialog")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("🕒", fontSize = 16.sp)
                            Text(
                                text = "Command Stack History",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        IconButton(
                            onClick = { showHistoryDialog = false },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "REDO STACK (${redoHistoryList.size} pending):",
                        color = Color(0xFF2DD4BF),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    if (redoHistoryList.isEmpty()) {
                        Text(
                            text = "• Empty (No undone actions)",
                            color = Color(0xFF64748B),
                            fontSize = 10.sp,
                            modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                        )
                    } else {
                        redoHistoryList.take(6).forEach { title ->
                            Text(
                                text = "↷ $title",
                                color = Color(0xFF99F6E4),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "UNDO STACK (${undoHistoryList.size} applied):",
                        color = Color(0xFF38BDF8),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    if (undoHistoryList.isEmpty()) {
                        Text(
                            text = "• Empty (Initial state)",
                            color = Color(0xFF64748B),
                            fontSize = 10.sp,
                            modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                        )
                    } else {
                        undoHistoryList.take(10).forEach { title ->
                            Text(
                                text = "↶ $title",
                                color = Color(0xFFBAE6FD),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { showHistoryDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("Dismiss", color = Color.White, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Fila individual correspondiente a una pista / carril dentro de la línea de tiempo.
 */
@Composable
private fun TrackLaneRow(
    track: EditorTrack,
    selectedClipId: String?,
    durationMs: Long,
    windowStartNorm: Float,
    windowEndNorm: Float,
    headerWidthDp: androidx.compose.ui.unit.Dp,
    onTrackToggleMute: () -> Unit,
    onTrackToggleVisibility: () -> Unit,
    onClipSelected: (TimelineClip) -> Unit
) {
    val trackBadgeColor = Color(track.type.badgeColorHex)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(38.dp)
            .border(0.5.dp, Color(0xFF1E293B).copy(alpha = 0.6f))
            .testTag("track_lane_${track.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Encabezado de la pista (lado izquierdo)
        Row(
            modifier = Modifier
                .width(headerWidthDp)
                .fillMaxHeight()
                .background(Color(0xFF0F172A))
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.name.substringBefore(" •"),
                    color = trackBadgeColor,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = if (track.isMuted) "MUTED" else if (track.isHidden) "HIDDEN" else "${track.clips.size} clips",
                    color = if (track.isMuted || track.isHidden) Color(0xFFEF4444) else Color(0xFF64748B),
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Botón de alternar silencio / visibilidad
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF1E293B))
                    .clickable {
                        if (track.type == TrackType.AUDIO) onTrackToggleMute()
                        else onTrackToggleVisibility()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (track.type == TrackType.AUDIO) {
                        if (track.isMuted) "🔇" else "🔊"
                    } else {
                        if (track.isHidden) "🙈" else "👁"
                    },
                    fontSize = 8.sp
                )
            }
        }

        // Carril horizontal donde se ubican los clips
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(Color(0xFF070B12))
                .padding(vertical = 3.dp, horizontal = 2.dp)
        ) {
            val laneWidthPx = constraints.maxWidth.toFloat()
            val windowSpan = (windowEndNorm - windowStartNorm).coerceAtLeast(0.01f)

            track.clips.forEach { clip ->
                val clipStartNorm = (clip.startMs.toFloat() / durationMs).coerceIn(0f, 1f)
                val clipEndNorm = (clip.endMs.toFloat() / durationMs).coerceIn(0f, 1f)

                // Renderizar únicamente clips que caigan en la ventana visible
                if (clipEndNorm >= windowStartNorm && clipStartNorm <= windowEndNorm) {
                    val relStartNorm = ((clipStartNorm - windowStartNorm) / windowSpan).coerceIn(0f, 1f)
                    val relEndNorm = ((clipEndNorm - windowStartNorm) / windowSpan).coerceIn(0f, 1f)
                    val clipLeftPx = relStartNorm * laneWidthPx
                    val clipWidthPx = ((relEndNorm - relStartNorm) * laneWidthPx).coerceAtLeast(14f)
                    val isSelected = clip.id == selectedClipId

                    Box(
                        modifier = Modifier
                            .offset(
                                x = with(androidx.compose.ui.platform.LocalDensity.current) { clipLeftPx.toDp() }
                            )
                            .width(
                                with(androidx.compose.ui.platform.LocalDensity.current) { clipWidthPx.toDp() }
                            )
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(clip.colorHex).copy(alpha = if (track.isHidden || track.isMuted) 0.35f else 0.85f))
                            .border(
                                width = if (isSelected) 1.5.dp else 0.5.dp,
                                color = if (isSelected) Color(0xFF38BDF8) else Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clickable { onClipSelected(clip) }
                            .padding(horizontal = 4.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = clip.label,
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

/**
 * Diálogo inspector de propiedades para afinar detalles de texto, audio y efectos.
 */
@Composable
private fun ClipInspectorDialog(
    clip: TimelineClip,
    durationMs: Long,
    onDismiss: () -> Unit,
    onUpdateClip: (TimelineClip) -> Unit,
    onDeleteClip: () -> Unit
) {
    var labelState by remember { mutableStateOf(clip.label) }
    var startMsState by remember { mutableLongStateOf(clip.startMs) }
    var durationMsState by remember { mutableLongStateOf(clip.durationMs) }

    // Estados específicos de Text Payload
    var textContent by remember { mutableStateOf(clip.textPayload?.text ?: "") }
    var fontSizeSp by remember { mutableFloatStateOf(clip.textPayload?.fontSizeSp ?: 18f) }
    var textPosition by remember { mutableStateOf(clip.textPayload?.position ?: TextPosition.BOTTOM) }

    // Estados específicos de Audio Payload
    var audioVolume by remember { mutableFloatStateOf(clip.audioPayload?.volume ?: 1.0f) }
    var audioCategory by remember { mutableStateOf(clip.audioPayload?.category ?: AudioCategory.BGM) }

    // Estados específicos de Effect Payload
    var effectType by remember { mutableStateOf(clip.effectPayload?.effectType ?: EffectType.CINEMATIC_TEAL_ORANGE) }
    var effectIntensity by remember { mutableFloatStateOf(clip.effectPayload?.intensity ?: 0.8f) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
                .testTag("clip_inspector_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                // Título
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "INSPECTOR • ${clip.trackType.label.uppercase()}",
                        color = Color(clip.colorHex),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Nombre del clip
                OutlinedTextField(
                    value = labelState,
                    onValueChange = { labelState = it },
                    label = { Text("Clip Label", fontSize = 10.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("inspector_clip_label_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Duración y posición temporal
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Start: ${startMsState}ms", color = Color(0xFF94A3B8), fontSize = 9.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Button(
                                onClick = { startMsState = (startMsState - 200L).coerceAtLeast(0L) },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("-0.2s", fontSize = 9.sp)
                            }
                            Button(
                                onClick = { startMsState = (startMsState + 200L).coerceAtMost(durationMs - durationMsState) },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("+0.2s", fontSize = 9.sp)
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Duration: ${durationMsState}ms", color = Color(0xFF94A3B8), fontSize = 9.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Button(
                                onClick = { durationMsState = (durationMsState - 200L).coerceAtLeast(300L) },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("-0.2s", fontSize = 9.sp)
                            }
                            Button(
                                onClick = { durationMsState = (durationMsState + 200L).coerceAtMost(durationMs - startMsState) },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("+0.2s", fontSize = 9.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Contenido según tipo de pista
                when (clip.trackType) {
                    TrackType.TEXT -> {
                        OutlinedTextField(
                            value = textContent,
                            onValueChange = { textContent = it },
                            label = { Text("Overlay Text", fontSize = 10.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("inspector_text_content_input"),
                            maxLines = 2
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Position: ${textPosition.label}",
                            color = Color(0xFF94A3B8),
                            fontSize = 9.sp
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            TextPosition.values().forEach { pos ->
                                FilterChip(
                                    selected = textPosition == pos,
                                    onClick = { textPosition = pos },
                                    label = { Text(pos.name.take(4), fontSize = 8.sp) }
                                )
                            }
                        }
                    }

                    TrackType.AUDIO -> {
                        Text(
                            text = "Volume: ${(audioVolume * 100).roundToInt()}%",
                            color = Color(0xFF94A3B8),
                            fontSize = 9.sp
                        )
                        Slider(
                            value = audioVolume,
                            onValueChange = { audioVolume = it },
                            valueRange = 0f..2.0f,
                            modifier = Modifier.testTag("inspector_audio_volume_slider")
                        )
                    }

                    TrackType.EFFECT -> {
                        Text(
                            text = "Filter Preset: ${effectType.displayName}",
                            color = Color(0xFF94A3B8),
                            fontSize = 9.sp
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf(
                                EffectType.CINEMATIC_TEAL_ORANGE,
                                EffectType.VHS_RETRO_GLITCH,
                                EffectType.FILM_GRAIN_NOIR,
                                EffectType.BLOOM_DREAMY
                            ).forEach { eff ->
                                FilterChip(
                                    selected = effectType == eff,
                                    onClick = { effectType = eff },
                                    label = { Text(eff.name.take(5), fontSize = 8.sp) }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Intensity: ${(effectIntensity * 100).roundToInt()}%",
                            color = Color(0xFF94A3B8),
                            fontSize = 9.sp
                        )
                        Slider(
                            value = effectIntensity,
                            onValueChange = { effectIntensity = it },
                            valueRange = 0f..1.0f,
                            modifier = Modifier.testTag("inspector_effect_intensity_slider")
                        )
                    }

                    TrackType.VIDEO -> {
                        val videoPayload = clip.videoPayload
                        if (videoPayload != null) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF0F2038))
                                    .padding(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "VIDEO SOURCE DETAILS",
                                        color = Color(0xFF38BDF8),
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = videoPayload.format,
                                        color = Color.White,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(Color(0xFF0284C7))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = videoPayload.fileName,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Resolution: ${videoPayload.width}x${videoPayload.height} (${videoPayload.aspectRatio}) • ${videoPayload.fps}fps",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 8.5.sp
                                )
                            }
                        } else {
                            Text(
                                text = "Base video track is master source locked.",
                                color = Color(0xFF64748B),
                                fontSize = 9.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Botones de acción (Guardar / Eliminar)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = onDeleteClip,
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFEF4444))
                    ) {
                        Text("Delete Clip", fontSize = 10.sp)
                    }

                    Button(
                        onClick = {
                            val updated = clip.copy(
                                label = labelState,
                                startMs = startMsState,
                                durationMs = durationMsState,
                                textPayload = clip.textPayload?.copy(
                                    text = textContent,
                                    fontSizeSp = fontSizeSp,
                                    position = textPosition
                                ),
                                audioPayload = clip.audioPayload?.copy(
                                    volume = audioVolume,
                                    category = audioCategory
                                ),
                                effectPayload = clip.effectPayload?.copy(
                                    effectType = effectType,
                                    intensity = effectIntensity
                                )
                            )
                            onUpdateClip(updated)
                            onDismiss()
                        },
                        modifier = Modifier.testTag("save_inspector_changes_btn")
                    ) {
                        Text("Save Changes", fontSize = 10.sp)
                    }
                }
            }
        }
    }
}
