package com.example.ui.components.media

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.media.CuratedStudioVideo
import com.example.data.media.LocalVideoMetadata
import com.example.data.media.VideoPlacementMode
import com.example.ui.MainViewModel

/**
 * Diálogo modal de servicio de selección e ingestión de video local para la línea de tiempo Veo Studio.
 * Diseñado conforme a la arquitectura de marca VMSB© y cumplimiento de políticas de Google Play (cero permisos requeridos).
 */
@Composable
fun VeoMediaPickerSheet(
    viewModel: MainViewModel,
    currentPositionMs: Long = 0L,
    onDismiss: () -> Unit
) {
    val selectedMetadata by viewModel.selectedLocalVideoMetadata.collectAsState()
    val isInspecting by viewModel.isInspectingVideo.collectAsState()
    val inspectionError by viewModel.videoInspectionError.collectAsState()
    val curatedVideos by viewModel.curatedStudioVideos.collectAsState()

    var placementMode by remember { mutableStateOf(VideoPlacementMode.SET_AS_MASTER) }

    // Launcher 1: Android Photo Picker estándar (cero permisos, recomendado por Google Play)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.handleSelectedVideoUri(uri)
        }
    }

    // Launcher 2: Explorador de contenido general para videos
    val fileBrowserLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.handleSelectedVideoUri(uri)
        }
    }

    LaunchedEffect(Unit) {
        if (curatedVideos.isEmpty()) {
            viewModel.openMediaPickerSheet()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .border(
                    BorderStroke(
                        1.2.dp,
                        Brush.linearGradient(
                            listOf(Color(0xFF38BDF8), Color(0xFF818CF8), Color(0xFF6366F1))
                        )
                    ),
                    RoundedCornerShape(24.dp)
                )
                .testTag("veo_media_picker_dialog"),
            color = Color(0xFF090D16),
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // ==========================================
                // CABECERA CON IDENTIDAD VMSB© BRAND
                // ==========================================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF38BDF8))
                            )
                            Text(
                                text = "VEO MEDIA PICKER SERVICE",
                                color = Color(0xFF38BDF8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF4338CA).copy(alpha = 0.4f))
                                    .border(0.5.dp, Color(0xFF818CF8), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "VMSB© 2026",
                                    color = Color(0xFFA5B4FC),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Importar Video a la Línea de Tiempo",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Decodificación nativa acelerada por hardware con sincronización de fotogramas",
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B))
                            .testTag("close_media_picker_dialog_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // ==========================================
                    // SELECCIÓN DE ORIGEN DE ALMACENAMIENTO
                    // ==========================================
                    item {
                        Text(
                            text = "SELECCIONAR DESDE ALMACENAMIENTO LOCAL",
                            color = Color(0xFF64748B),
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Opción 1: Photo Picker (Recomendado por Google Play)
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                                    .clickable {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                        )
                                    }
                                    .testTag("pick_video_system_gallery_btn"),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF131F37))
                            ) {
                                Column(
                                    modifier = Modifier
                                    .padding(14.dp)
                                    .fillMaxWidth()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFF0284C7).copy(alpha = 0.3f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VideoLibrary,
                                            contentDescription = "Galería",
                                            tint = Color(0xFF38BDF8),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "Galería / Videos",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Android Photo Picker seguro (cero permisos)",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 9.sp,
                                        lineHeight = 12.sp
                                    )
                                }
                            }

                            // Opción 2: Explorador de Archivos
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .border(1.dp, Color(0xFF818CF8).copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                                    .clickable {
                                        fileBrowserLauncher.launch("video/*")
                                    }
                                    .testTag("pick_video_file_browser_btn"),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF161938))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(14.dp)
                                        .fillMaxWidth()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFF6366F1).copy(alpha = 0.3f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.FolderOpen,
                                            contentDescription = "Archivos",
                                            tint = Color(0xFFA5B4FC),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "Explorador de Archivos",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Navegar MP4, MOV, MKV, WebM en el dispositivo",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 9.sp,
                                        lineHeight = 12.sp
                                    )
                                }
                            }
                        }
                    }

                    // ==========================================
                    // ESTADO DE INSPECCIÓN / ERROR
                    // ==========================================
                    if (isInspecting) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .border(1.dp, Color(0xFF38BDF8), RoundedCornerShape(14.dp)),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F243A))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(28.dp),
                                        color = Color(0xFF38BDF8),
                                        strokeWidth = 2.5.dp
                                    )
                                    Column {
                                        Text(
                                            text = "Inspeccionando archivo de video...",
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Extrayendo resolución, tasa de bits y códec con MediaMetadataRetriever",
                                            color = Color(0xFF94A3B8),
                                            fontSize = 9.5.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (inspectionError != null) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(14.dp)),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF29080A))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Error",
                                        tint = Color(0xFFF87171),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = inspectionError!!,
                                        color = Color(0xFFFECACA),
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    // ==========================================
                    // TARJETA DE VIDEO INSPECCIONADO Y METADATOS
                    // ==========================================
                    if (selectedMetadata != null) {
                        val meta = selectedMetadata!!
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(
                                        BorderStroke(1.2.dp, Color(0xFF10B981)),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .testTag("inspected_video_card"),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF06281E))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Compatible",
                                                tint = Color(0xFF34D399),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = "VIDEO CERTIFICADO PARA TIMELINE",
                                                color = Color(0xFF34D399),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                fontFamily = FontFamily.Monospace,
                                                letterSpacing = 0.5.sp
                                            )
                                        }

                                        Text(
                                            text = meta.format,
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFF047857))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = meta.name,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Grilla de Metadatos Técnicos
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        MetadataChip(
                                            title = "DURACIÓN",
                                            value = meta.formattedDuration,
                                            modifier = Modifier.weight(1f)
                                        )
                                        MetadataChip(
                                            title = "RESOLUCIÓN",
                                            value = "${meta.resolutionLabel} (${meta.aspectRatio})",
                                            modifier = Modifier.weight(1.3f)
                                        )
                                        MetadataChip(
                                            title = "TAMAÑO",
                                            value = meta.formattedSize,
                                            modifier = Modifier.weight(0.9f)
                                        )
                                        MetadataChip(
                                            title = "DIMENSIÓN",
                                            value = meta.dimensionsLabel,
                                            modifier = Modifier.weight(1.1f)
                                        )
                                    }
                                }
                            }
                        }

                        // ==========================================
                        // MODO DE COLOCACIÓN EN LA LÍNEA DE TIEMPO
                        // ==========================================
                        item {
                            Text(
                                text = "MODO DE INSERCIÓN EN LÍNEA DE TIEMPO",
                                color = Color(0xFF64748B),
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                VideoPlacementMode.values().forEach { mode ->
                                    val isSelected = placementMode == mode
                                    val borderColor = if (isSelected) Color(0xFF38BDF8) else Color(0xFF334155)
                                    val bgColor = if (isSelected) Color(0xFF0F2844) else Color(0xFF0F172A)

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(bgColor)
                                            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
                                            .clickable { placementMode = mode }
                                            .padding(horizontal = 12.dp, vertical = 10.dp)
                                            .testTag("placement_mode_${mode.name.lowercase()}"),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { placementMode = mode },
                                            colors = RadioButtonDefaults.colors(
                                                selectedColor = Color(0xFF38BDF8),
                                                unselectedColor = Color(0xFF64748B)
                                            )
                                        )
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = mode.title,
                                                color = if (isSelected) Color.White else Color(0xFFCBD5E1),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = if (mode == VideoPlacementMode.INSERT_AT_PLAYHEAD) {
                                                    "Inserta a partir de ${(currentPositionMs / 1000f)}s en la pista de video"
                                                } else {
                                                    mode.subtitle
                                                },
                                                color = Color(0xFF94A3B8),
                                                fontSize = 9.5.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ==========================================
                    // PRESETS CURADOS DE ESTUDIO (ALTA FIDELIDAD)
                    // ==========================================
                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "PRESETS CURADOS DE ESTUDIO (PRUEBA RÁPIDA)",
                                color = Color(0xFF64748B),
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "4K • 60fps • HDR",
                                color = Color(0xFF38BDF8),
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(curatedVideos) { video ->
                                val isSelected = selectedMetadata?.uri == video.uriString
                                Card(
                                    modifier = Modifier
                                        .width(200.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(
                                            1.dp,
                                            if (isSelected) Color(0xFF38BDF8) else Color(0xFF1E293B),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable {
                                            viewModel.selectCuratedVideo(video)
                                        }
                                        .testTag("curated_video_${video.id}"),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) Color(0xFF0F2B48) else Color(0xFF111827)
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(video.thumbnailEmoji, fontSize = 16.sp)
                                            Text(
                                                text = video.metadata.aspectRatio,
                                                color = Color(0xFF38BDF8),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace,
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(3.dp))
                                                    .background(Color(0xFF0284C7).copy(alpha = 0.25f))
                                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                            Text(
                                                text = video.metadata.resolutionLabel,
                                                color = Color(0xFF34D399),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = video.title,
                                            color = Color.White,
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Spacer(modifier = Modifier.height(3.dp))

                                        Text(
                                            text = video.description,
                                            color = Color(0xFF94A3B8),
                                            fontSize = 8.5.sp,
                                            lineHeight = 11.sp,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // BARRA INFERIOR DE ACCIONES
                // ==========================================
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("cancel_media_picker_btn"),
                        border = BorderStroke(1.dp, Color(0xFF475569)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF94A3B8)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Cancelar", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = {
                            val meta = selectedMetadata
                            if (meta != null) {
                                viewModel.loadVideoIntoTimeline(
                                    metadata = meta,
                                    mode = placementMode,
                                    currentPositionMs = currentPositionMs
                                )
                                onDismiss()
                            }
                        },
                        enabled = selectedMetadata != null && !isInspecting,
                        modifier = Modifier
                            .weight(2f)
                            .height(44.dp)
                            .testTag("load_video_into_timeline_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2563EB),
                            disabledContainerColor = Color(0xFF1E293B)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Cargar",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Cargar en Línea de Tiempo",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetadataChip(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF031E15))
            .padding(horizontal = 6.dp, vertical = 5.dp)
    ) {
        Text(
            text = title,
            fontSize = 7.5.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6EE7B7),
            fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
