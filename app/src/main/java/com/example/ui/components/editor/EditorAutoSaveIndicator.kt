package com.example.ui.components.editor

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.export.VeoProjectExporter
import com.example.ui.AutoSaveState
import com.example.ui.AutoSaveStatus
import com.example.ui.MainViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * =========================================================================================
 * VMSB(c) 2026 - COMPONENTE INDICADOR DE AUTO-GUARDADO ROOM & EXPORTADOR LOCAL
 * =========================================================================================
 *
 * Muestra el estado del auto-guardado en tiempo real en la base de datos local SQLite (Room)
 * y provee accesos rápidos para:
 * 1. Exportar el estado de edición como archivo de proyecto (.veoproj / JSON).
 * 2. Renderizar y guardar el video final en el almacenamiento local del dispositivo.
 * 3. Forzar auto-guardado inmediato.
 */
@Composable
fun EditorAutoSaveIndicator(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val autoSaveStatus by viewModel.autoSaveStatus.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }

    val formattedTime = remember(autoSaveStatus.lastSavedTimestamp) {
        SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(autoSaveStatus.lastSavedTimestamp))
    }

    val chipColor by animateColorAsState(
        targetValue = when (autoSaveStatus.state) {
            AutoSaveState.SAVING -> Color(0xFF0284C7)
            AutoSaveState.SAVED -> Color(0xFF059669)
            AutoSaveState.ERROR -> Color(0xFFDC2626)
            AutoSaveState.IDLE -> Color(0xFF475569)
        },
        label = "chipBg"
    )

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { showDialog = true }
            .testTag("editor_autosave_badge"),
        color = Color(0xFF0F172A),
        border = BorderStroke(1.dp, chipColor.copy(alpha = 0.8f)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            when (autoSaveStatus.state) {
                AutoSaveState.SAVING -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(12.dp),
                        strokeWidth = 1.5.dp,
                        color = Color(0xFF38BDF8)
                    )
                    Text(
                        "Auto-guardando...",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF38BDF8)
                    )
                }
                AutoSaveState.SAVED -> {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF34D399))
                    )
                    Text(
                        "Guardado en Room ($formattedTime)",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFA7F3D0)
                    )
                }
                AutoSaveState.ERROR -> {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Error",
                        tint = Color(0xFFF87171),
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        "Error guardando",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF87171)
                    )
                }
                AutoSaveState.IDLE -> {
                    Icon(
                        imageVector = Icons.Default.CloudQueue,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        "Room SQLite",
                        fontSize = 10.5.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = chipColor.copy(alpha = 0.25f)
            ) {
                Text(
                    text = "v${autoSaveStatus.saveVersion}",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                )
            }
        }
    }

    if (showDialog) {
        EditorAutoSaveAndExportDialog(
            viewModel = viewModel,
            autoSaveStatus = autoSaveStatus,
            onDismissRequest = { showDialog = false }
        )
    }
}

/**
 * Diálogo modal para inspección del auto-guardado en Room Database y acciones de
 * exportación de archivo de proyecto (.veoproj) o renderizado final a almacenamiento local.
 */
@Composable
fun EditorAutoSaveAndExportDialog(
    viewModel: MainViewModel,
    autoSaveStatus: AutoSaveStatus,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isExportingProject by remember { mutableStateOf(false) }
    var isRenderingVideo by remember { mutableStateOf(false) }
    var renderProgressText by remember { mutableStateOf("") }
    var renderProgressVal by remember { mutableFloatStateOf(0f) }

    var lastProjectExport by remember { mutableStateOf<VeoProjectExporter.ProjectExportResult?>(null) }
    var lastVideoRender by remember { mutableStateOf<VeoProjectExporter.LocalVideoRenderResult?>(null) }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("editor_autosave_dialog"),
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF059669)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Storage, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text(
                                "Auto-Guardado Room SQLite",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                "Persistencia local continua tras cada edición",
                                fontSize = 10.5.sp,
                                color = Color(0xFF34D399)
                            )
                        }
                    }

                    IconButton(onClick = onDismissRequest) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color(0xFF94A3B8))
                    }
                }

                // Info card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Estado:", fontSize = 11.sp, color = Color(0xFF94A3B8))
                            Text(
                                text = if (autoSaveStatus.state == AutoSaveState.SAVED) "Sincronizado y Seguro" else "Procesando cambios",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF34D399)
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Último guardado:", fontSize = 11.sp, color = Color(0xFF94A3B8))
                            Text(
                                text = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date(autoSaveStatus.lastSavedTimestamp)),
                                fontSize = 11.sp,
                                color = Color.White,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Revisión de Edición:", fontSize = 11.sp, color = Color(0xFF94A3B8))
                            Text("Versión #${autoSaveStatus.saveVersion}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Motivo:", fontSize = 11.sp, color = Color(0xFF94A3B8))
                            Text(autoSaveStatus.lastSavedReason, fontSize = 10.5.sp, color = Color(0xFFCBD5E1))
                        }
                    }
                }

                // Action 1: Force Save Now
                OutlinedButton(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.flushAutoSaveNow("Guardado manual forzado por usuario")
                            Toast.makeText(context, "Progreso guardado inmediatamente en Room SQLite", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("manual_autosave_btn"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF34D399))
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Forzar Guardado en Room Ahora", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }

                HorizontalDivider(color = Color(0xFF334155))

                // Action 2: Export Edit State as Project File (.veoproj)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        "EXPORTAR ARCHIVO DE PROYECTO (.veoproj)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF38BDF8)
                    )
                    Text(
                        "Empaqueta metadatos, recortes, color grading, reencuadre y marcadores en un archivo de proyecto JSON local.",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isExportingProject = true
                                try {
                                    val result = viewModel.exportCurrentEditStateAsProjectFile(context)
                                    lastProjectExport = result
                                    Toast.makeText(context, "Proyecto exportado: ${result.file.name}", Toast.LENGTH_LONG).show()
                                    VeoProjectExporter.shareProjectFile(context, result, "Proyecto Veo Studio")
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error exportando proyecto: ${e.message}", Toast.LENGTH_SHORT).show()
                                } finally {
                                    isExportingProject = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("export_project_file_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        enabled = !isExportingProject
                    ) {
                        if (isExportingProject) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Generando archivo .veoproj...", fontSize = 11.5.sp)
                        } else {
                            Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Exportar Estado como Archivo de Proyecto (.veoproj)", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (lastProjectExport != null) {
                        Text(
                            text = "Archivo guardado en: ${lastProjectExport?.file?.name} (${lastProjectExport?.fileSizeBytes} bytes)",
                            fontSize = 9.sp,
                            color = Color(0xFF38BDF8),
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFF334155))

                // Action 3: Render Final Video to Local Storage
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        "RENDERIZAR VIDEO A ALMACENAMIENTO LOCAL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF59E0B)
                    )
                    Text(
                        "Procesa los fotogramas, aplica el recorte, relación de aspecto y color grading, y guarda el MP4 en Movies/VeoStudio.",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isRenderingVideo = true
                                try {
                                    val result = viewModel.renderFinalVideoToLocalStorage(context) { prog, text ->
                                        renderProgressVal = prog
                                        renderProgressText = text
                                    }
                                    lastVideoRender = result
                                    Toast.makeText(context, "Video guardado localmente: ${result.file.name}", Toast.LENGTH_LONG).show()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error renderizando video: ${e.message}", Toast.LENGTH_SHORT).show()
                                } finally {
                                    isRenderingVideo = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("render_local_video_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                        enabled = !isRenderingVideo
                    ) {
                        if (isRenderingVideo) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(renderProgressText.ifBlank { "Renderizando video..." }, fontSize = 11.5.sp)
                        } else {
                            Icon(Icons.Default.MovieCreation, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Renderizar Video a Almacenamiento Local (MP4)", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (lastVideoRender != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF292524))
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("Ruta: ${lastVideoRender?.destinationPath}", fontSize = 9.sp, color = Color(0xFFFCD34D), fontFamily = FontFamily.Monospace)
                                Text("Tamaño: ${lastVideoRender?.fileSizeBytes} bytes • Formato: ${lastVideoRender?.resolution} (${lastVideoRender?.aspectRatio})", fontSize = 9.sp, color = Color(0xFFD6D3D1))
                                Spacer(modifier = Modifier.height(6.dp))
                                Button(
                                    onClick = { VeoProjectExporter.viewRenderedVideo(context, lastVideoRender!!) },
                                    modifier = Modifier.fillMaxWidth().height(32.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Reproducir Video Guardado", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
