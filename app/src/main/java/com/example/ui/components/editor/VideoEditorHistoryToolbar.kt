package com.example.ui.components.editor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel

/**
 * =========================================================================================
 * VMSB(c) 2026 - BARRA DE HERRAMIENTAS DE HISTORIAL UNDO / REDO Y AUTO-SAVE ROOM
 * =========================================================================================
 *
 * Provee controles táctiles de al menos 48dp de Deshacer (Undo) y Rehacer (Redo)
 * respaldados por Room SQLite, visualización del paso activo en la pila, descripción
 * de la última edición revertible e indicador de auto-guardado en segundo plano.
 */
@Composable
fun VideoEditorHistoryToolbar(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val canUndo by viewModel.canUndo.collectAsState()
    val canRedo by viewModel.canRedo.collectAsState()
    val currentIndex by viewModel.currentHistoryIndex.collectAsState()
    val maxIndex by viewModel.maxHistoryIndex.collectAsState()
    val lastActionDesc by viewModel.lastHistoryActionDescription.collectAsState()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("video_editor_history_toolbar"),
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF0F172A).copy(alpha = 0.95f),
        border = BorderStroke(1.dp, Color(0xFF1E293B))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Sección Izquierda: Botones Deshacer y Rehacer con accesibilidad y testTags
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Botón Deshacer (Undo)
                IconButton(
                    onClick = { viewModel.undo() },
                    enabled = canUndo,
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("editor_undo_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Undo,
                        contentDescription = "Deshacer última edición en video",
                        tint = if (canUndo) Color(0xFF38BDF8) else Color(0xFF475569),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Botón Rehacer (Redo)
                IconButton(
                    onClick = { viewModel.redo() },
                    enabled = canRedo,
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("editor_redo_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Redo,
                        contentDescription = "Rehacer edición en video",
                        tint = if (canRedo) Color(0xFF38BDF8) else Color(0xFF475569),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Indicador de Pila de Historial Room (ej. Step 3/5)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF1E293B),
                    border = BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = if (maxIndex >= 0) "${currentIndex + 1}/${maxIndex + 1}" else "0/0",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                }

                // Descripción breve del último cambio revertible
                if (lastActionDesc.isNotBlank()) {
                    Text(
                        text = lastActionDesc,
                        fontSize = 9.5.sp,
                        color = Color(0xFF94A3B8),
                        maxLines = 1,
                        modifier = Modifier
                            .widthIn(max = 130.dp)
                            .padding(start = 4.dp)
                    )
                }
            }

            // Sección Derecha: Indicador de Auto-Save SQLite Room
            EditorAutoSaveIndicator(
                viewModel = viewModel,
                modifier = Modifier.wrapContentWidth()
            )
        }
    }
}
