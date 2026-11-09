package com.example.ui.components.export

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.Project
import com.example.data.export.ExportRenderConfig
import com.example.data.export.RenderedVideoResult
import com.example.data.export.SocialMediaExportManager
import com.example.data.export.SocialPlatform
import com.example.ui.MainViewModel
import kotlinx.coroutines.launch

/**
 * Componente principal de integración para exportar proyectos de video renderizados a redes sociales
 * a través de los Share Intents estándar de Android (Intent.ACTION_SEND).
 */
@Composable
fun SocialMediaExportIntegrationCard(
    project: Project,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    onOpenFullExportDialog: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedPlatform by remember { mutableStateOf(SocialPlatform.SYSTEM_CHOOSER) }
    var selectedResolution by remember { mutableStateOf(if (project.isUpscalingEnabled || project.resolution.contains("4K")) "4K UHD (3840x2160)" else "1080p FHD (1920x1080)") }
    var selectedAspect by remember { mutableStateOf(project.aspectRatio.ifBlank { "9:16" }) }
    var includeC2pa by remember { mutableStateOf(true) }
    var includeAiNotice by remember { mutableStateOf(true) }
    var includeAntiReaWatermark by remember { mutableStateOf(true) }
    var includeHashtags by remember { mutableStateOf(true) }

    var isRenderingAndSharing by remember { mutableStateOf(false) }
    var progressFraction by remember { mutableFloatStateOf(0f) }
    var progressStatusText by remember { mutableStateOf("") }
    var lastExportedResult by remember { mutableStateOf<RenderedVideoResult?>(null) }

    var showCaptionEditorModal by remember { mutableStateOf(false) }
    var customCaptionText by remember { mutableStateOf("") }

    val isTargetInstalled = remember(selectedPlatform, context) {
        SocialMediaExportManager.isPlatformInstalled(context, selectedPlatform)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("social_media_export_integration_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
        ),
        border = BorderStroke(
            1.2.dp,
            Brush.horizontalGradient(
                listOf(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    Color(0xFF38BDF8),
                    Color(0xFF10B981)
                )
            )
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Header con insignia de protocolo nativo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color(selectedPlatform.brandColorHex),
                                        MaterialTheme.colorScheme.primary
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(selectedPlatform.iconEmoji, fontSize = 18.sp)
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Exportar a Redes Sociales",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF10B981).copy(alpha = 0.18f))
                                    .border(0.8.dp, Color(0xFF10B981), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "ACTION_SEND",
                                    fontSize = 7.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF10B981),
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                        Text(
                            text = "Distribución nativa de video MP4 renderizado vía Android Share Intents",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (onOpenFullExportDialog != null) {
                    IconButton(
                        onClick = onOpenFullExportDialog,
                        modifier = Modifier.size(32.dp).testTag("open_full_export_dialog_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInFull,
                            contentDescription = "Expandir Exportación",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Selector Horizontal de Plataformas Sociales
            Text(
                text = "SELECCIONA PLATAFORMA DE DESTINO",
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SocialPlatform.values().forEach { platform ->
                    val isSelected = selectedPlatform == platform
                    val installed = remember(platform, context) {
                        SocialMediaExportManager.isPlatformInstalled(context, platform)
                    }

                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedPlatform = platform
                            if (platform.recommendedAspectRatio.contains("9:16")) {
                                selectedAspect = "9:16"
                            } else if (platform.recommendedAspectRatio.contains("1:1")) {
                                selectedAspect = "1:1"
                            } else if (platform.recommendedAspectRatio.contains("16:9")) {
                                selectedAspect = "16:9"
                            }
                        },
                        label = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(platform.iconEmoji, fontSize = 12.sp)
                                Text(
                                    text = platform.displayName.split(" ").firstOrNull() ?: platform.displayName,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                                )
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (installed) Color(0xFF10B981) else Color(0xFF38BDF8))
                                )
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(platform.brandColorHex).copy(alpha = 0.22f),
                            selectedLabelColor = MaterialTheme.colorScheme.onSurface
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) Color(platform.brandColorHex) else MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier.testTag("platform_chip_${platform.id}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tarjeta de estado de plataforma seleccionada
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                    .border(1.dp, Color(selectedPlatform.brandColorHex).copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(selectedPlatform.displayName, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isTargetInstalled) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFF38BDF8).copy(alpha = 0.15f))
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (isTargetInstalled) "APP INSTALADA" else "VÍA SHARESHEET",
                                    fontSize = 7.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isTargetInstalled) Color(0xFF10B981) else Color(0xFF0284C7),
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Text(
                            text = "Aspecto: ${selectedPlatform.recommendedAspectRatio}",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = selectedPlatform.tips,
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Opciones de configuración de Render
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Selector de Resolución
                Column(modifier = Modifier.weight(1f)) {
                    Text("RESOLUCIÓN", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(3.dp))
                    val resolutions = listOf("1080p FHD", "4K UHD")
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        resolutions.forEach { res ->
                            val isSel = selectedResolution.startsWith(res)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                    .clickable {
                                        selectedResolution = if (res == "4K UHD") "4K UHD (3840x2160)" else "1080p FHD (1920x1080)"
                                    }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = res,
                                    fontSize = 9.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // Selector de Relación de Aspecto
                Column(modifier = Modifier.weight(1f)) {
                    Text("ENCUADRE", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(3.dp))
                    val aspects = listOf("9:16", "16:9", "1:1")
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        aspects.forEach { asp ->
                            val isSel = selectedAspect == asp
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                    .clickable { selectedAspect = asp }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = asp,
                                    fontSize = 9.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Toggles de verificación C2PA y Transparencia
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Checkbox(
                        checked = includeC2pa,
                        onCheckedChange = { includeC2pa = it },
                        modifier = Modifier.size(24.dp).testTag("toggle_include_c2pa")
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("C2PA v2.1 Manifest", fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Checkbox(
                        checked = includeAntiReaWatermark,
                        onCheckedChange = { includeAntiReaWatermark = it },
                        modifier = Modifier.size(24.dp).testTag("toggle_include_anti_rea")
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("Anti-REA 2026", fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = { showCaptionEditorModal = true },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(28.dp).testTag("edit_caption_btn")
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(11.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copete Social", fontSize = 8.5.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Botones de Acción de Exportación y Compartir
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botón Primario: Render & Share Intent
                Button(
                    onClick = {
                        coroutineScope.launch {
                            isRenderingAndSharing = true
                            progressFraction = 0f
                            progressStatusText = "Iniciando renderizado..."
                            try {
                                val config = ExportRenderConfig(
                                    resolution = selectedResolution,
                                    aspectRatio = selectedAspect,
                                    fps = 60,
                                    customCaption = customCaptionText,
                                    includeC2paManifest = includeC2pa,
                                    includeSyntheticMediaNotice = includeAiNotice,
                                    includeAntiReaWatermark = includeAntiReaWatermark,
                                    includeHashtags = includeHashtags,
                                    targetPlatform = selectedPlatform
                                )

                                val result = SocialMediaExportManager.renderAndPrepareVideo(
                                    context = context,
                                    project = project,
                                    config = config,
                                    onProgress = { frac, text ->
                                        progressFraction = frac
                                        progressStatusText = text
                                    }
                                )
                                lastExportedResult = result

                                // Lanzar Intent estándar de Android
                                context.startActivity(result.shareIntent)
                                Toast.makeText(
                                    context,
                                    "Abriendo ${selectedPlatform.displayName} para compartir video...",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "Error al exportar video: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            } finally {
                                isRenderingAndSharing = false
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1.3f)
                        .height(44.dp)
                        .testTag("social_export_share_button"),
                    enabled = !isRenderingAndSharing,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(selectedPlatform.brandColorHex).copy(alpha = 0.9f)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (isRenderingAndSharing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Exportando...", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    } else {
                        Icon(Icons.Default.IosShare, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (selectedPlatform == SocialPlatform.SYSTEM_CHOOSER) "Exportar & Compartir" else "Compartir en ${selectedPlatform.displayName.split(" ").firstOrNull()}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Botón Secundario: Guardar en Galería del Dispositivo
                OutlinedButton(
                    onClick = {
                        coroutineScope.launch {
                            isRenderingAndSharing = true
                            progressStatusText = "Guardando en galería..."
                            try {
                                val config = ExportRenderConfig(
                                    resolution = selectedResolution,
                                    aspectRatio = selectedAspect,
                                    targetPlatform = selectedPlatform
                                )
                                val result = lastExportedResult ?: SocialMediaExportManager.renderAndPrepareVideo(
                                    context = context,
                                    project = project,
                                    config = config,
                                    onProgress = { frac, text ->
                                        progressFraction = frac
                                        progressStatusText = text
                                    }
                                )
                                val savedUri = SocialMediaExportManager.saveToDeviceGallery(context, result)
                                if (savedUri != null) {
                                    Toast.makeText(
                                        context,
                                        "Video guardado en Películas/VeoStudio exitosamente",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    Toast.makeText(context, "Guardado en caché de exportación", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            } finally {
                                isRenderingAndSharing = false
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(0.9f)
                        .height(44.dp)
                        .testTag("social_export_save_gallery_button"),
                    enabled = !isRenderingAndSharing,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Guardar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Barra de progreso interactiva
            AnimatedVisibility(visible = isRenderingAndSharing) {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Color(selectedPlatform.brandColorHex),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = progressStatusText,
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    // Modal de edición de copete social y hashtags
    if (showCaptionEditorModal) {
        Dialog(
            onDismissRequest = { showCaptionEditorModal = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .clip(RoundedCornerShape(16.dp)),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.RateReview, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text("Copete & Hashtags Sociales", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = { showCaptionEditorModal = false }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar", modifier = Modifier.size(18.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        "Personaliza el texto que acompañará el video al abrir la aplicación social receptora.",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = customCaptionText,
                        onValueChange = { customCaptionText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .testTag("custom_caption_input"),
                        placeholder = {
                            Text(
                                "Escribe aquí la descripción para TikTok, Instagram o YouTube...",
                                fontSize = 10.sp
                            )
                        },
                        textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Chips de ayuda rápida
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AssistChip(
                            onClick = {
                                customCaptionText = (project.title + "\n\n" + project.scriptText).trim()
                            },
                            label = { Text("Usar Guion", fontSize = 9.sp) },
                            leadingIcon = { Icon(Icons.Default.Description, null, modifier = Modifier.size(12.dp)) }
                        )

                        AssistChip(
                            onClick = {
                                val current = customCaptionText.trim()
                                val tags = selectedPlatform.defaultHashtags.joinToString(" ")
                                customCaptionText = if (current.isBlank()) tags else "$current\n\n$tags"
                            },
                            label = { Text("+ Hashtags", fontSize = 9.sp) },
                            leadingIcon = { Icon(Icons.Default.Tag, null, modifier = Modifier.size(12.dp)) }
                        )

                        AssistChip(
                            onClick = { customCaptionText = "" },
                            label = { Text("Limpiar", fontSize = 9.sp) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { showCaptionEditorModal = false },
                        modifier = Modifier.fillMaxWidth().height(40.dp)
                    ) {
                        Text("Guardar Copete", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
