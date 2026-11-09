package com.example.ui.components.metadata

import android.widget.Toast
import androidx.compose.animation.*
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
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.Project
import com.example.data.metadata.VideoMetadataEntity
import com.example.ui.MainViewModel
import com.example.ui.components.settings.FreedVerifiableStatusBadge
import com.example.ui.components.settings.FreedAuditDetailsDialog
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * =========================================================================================
 * VMSB© 2026 - COMPONENTE UI DE EDICIÓN Y PERSISTENCIA DE METADATOS CON ROOM DATABASE
 * & BLINDAJE POST-CUÁNTICO CRYSTALS-DILITHIUM (NIST FIPS 204 ML-DSA-65 / Q-DAY IMMUNITY)
 * =========================================================================================
 *
 * Permite a los usuarios editar y persistir de forma reactiva en SQLite:
 * 1. Título, Descripción / Sinopsis, Etiquetas (Tags) y Categoría del video procesado.
 * 2. Datos de autoría, derechos de autor y resolución técnica.
 * 3. Firma criptográfica post-cuántica Dilithium inmune a computadoras cuánticas ("Q-Day")
 *    y ataques "Store Now, Decrypt Later" (SNDL).
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun VideoMetadataEditorDialog(
    viewModel: MainViewModel,
    project: Project?,
    existingMetadata: VideoMetadataEntity? = null,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Estados de edición de metadatos
    var title by remember(existingMetadata, project) {
        mutableStateOf(existingMetadata?.title ?: project?.title ?: "Nuevo Video Veo Studio")
    }
    var description by remember(existingMetadata, project) {
        mutableStateOf(existingMetadata?.description ?: project?.prompt ?: "")
    }
    var tagInput by remember { mutableStateOf("") }
    val tagsList = remember(existingMetadata, project) {
        mutableStateListOf<String>().apply {
            if (existingMetadata != null && existingMetadata.tags.isNotBlank()) {
                addAll(existingMetadata.getTagList())
            } else if (project != null && project.tagsText.isNotBlank()) {
                addAll(project.tagsText.split(",").map { it.trim() }.filter { it.isNotEmpty() })
            } else {
                addAll(listOf("VeoStudio", "4KCinematic", "GeminiVision", "PostQuantum"))
            }
        }
    }

    var selectedCategory by remember(existingMetadata) {
        mutableStateOf(existingMetadata?.category ?: "Cinematic Short")
    }
    var author by remember(existingMetadata) {
        mutableStateOf(existingMetadata?.author ?: "Veo Studio Creator")
    }
    var copyright by remember(existingMetadata) {
        mutableStateOf(existingMetadata?.copyrightNotice ?: "© 2026 Sovereign Creator • All Rights Reserved")
    }

    var isSaving by remember { mutableStateOf(false) }
    var isVerifyingQuantum by remember { mutableStateOf(false) }
    var verificationResultText by remember { mutableStateOf<String?>(null) }
    var isQDayDossierVisible by remember { mutableStateOf(false) }
    var showFreedAuditDialog by remember { mutableStateOf(false) }
    val freedAuditStatus by viewModel.freedAuditStatus.collectAsState()

    val categories = listOf(
        "Cinematic Short",
        "Social Reel (9:16)",
        "Documentary",
        "Music Video",
        "Commercial",
        "Experimental AI"
    )

    val quickSuggestedTags = listOf(
        "Veo3.1", "4KHDR", "Shorts", "Reels", "GeminiVision",
        "LatticeCrypto", "NIST_FIPS_204", "Dilithium65", "C2PA_Certified"
    )

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.94f)
                .testTag("video_metadata_editor_dialog"),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF070B14),
            border = BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                        .padding(bottom = 76.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // -------------------------------------------------------------
                    // ENCABEZADO MODAL CON SELLO POST-CUÁNTICO
                    // -------------------------------------------------------------
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFF059669), Color(0xFF0284C7))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EditNote,
                                    contentDescription = "Metadatos Room",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Metadatos de Video & Room DB",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Persistencia Local SQLite • Sello Dilithium Q-Day",
                                    fontSize = 11.sp,
                                    color = Color(0xFF34D399)
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismissRequest,
                            modifier = Modifier.testTag("metadata_close_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = Color(0xFF94A3B8)
                            )
                        }
                    }

                    // -------------------------------------------------------------
                    // BANNER DE INMUNIDAD AL DÍA CUÁNTICO (Q-DAY IMMUNITY)
                    // -------------------------------------------------------------
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF062016)),
                        border = BorderStroke(1.dp, Color(0xFF059669))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = Color(0xFF34D399),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Q-DAY PROTECTED • NIST FIPS 204",
                                        color = Color(0xFF34D399),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 0.5.sp
                                    )
                                }

                                TextButton(
                                    onClick = { isQDayDossierVisible = !isQDayDossierVisible },
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (isQDayDossierVisible) "Ocultar Info" else "¿Qué es Q-Day?",
                                        fontSize = 10.sp,
                                        color = Color(0xFF38BDF8)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Los metadatos se firman con reticulados CRYSTALS-Dilithium (ML-DSA-65). Inmunes al algoritmo cuántico de Shor y a ataques 'Store Now, Decrypt Later'.",
                                fontSize = 10.5.sp,
                                color = Color(0xFFD1FAE5)
                            )

                            AnimatedVisibility(visible = isQDayDossierVisible) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp)
                                        .background(Color(0xFF02170E), RoundedCornerShape(8.dp))
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = "• Amenaza Q-Day: Cuando un computador cuántico con qubits lógicos rompa RSA-2048 y curvas elípticas (ECC).",
                                        fontSize = 9.5.sp,
                                        color = Color(0xFFA7F3D0)
                                    )
                                    Text(
                                        text = "• Solución Dilithium: Basado en el problema matemático Module Learning with Errors (M-LWE), sin solución en tiempo polinómico cuántico.",
                                        fontSize = 9.5.sp,
                                        color = Color(0xFFA7F3D0)
                                    )
                                    Text(
                                        text = "• Coeficiente de Seguridad: 128+ bits de dureza cuántica real (NIST Nivel 3).",
                                        fontSize = 9.5.sp,
                                        color = Color(0xFFA7F3D0)
                                    )
                                }
                            }
                        }
                    }

                    // -------------------------------------------------------------
                    // BADGE VERIFICABLE FREED_vmsb_lattice_ZKP & EXPORTAR PDF
                    // -------------------------------------------------------------
                    FreedVerifiableStatusBadge(
                        auditStatus = freedAuditStatus,
                        onOpenDetails = { showFreedAuditDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // -------------------------------------------------------------
                    // CAMPO 1: TÍTULO DEL VIDEO
                    // -------------------------------------------------------------
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "TÍTULO DEL VIDEO (ROOM DB)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8)
                        )
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("metadata_input_title"),
                            leadingIcon = {
                                Icon(Icons.Default.Title, contentDescription = null, tint = Color(0xFF38BDF8))
                            },
                            trailingIcon = {
                                if (title.isNotEmpty()) {
                                    IconButton(onClick = { title = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Limpiar", tint = Color.Gray)
                                    }
                                }
                            },
                            placeholder = { Text("Ej. Amanecer Cinemático en 4K") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF0284C7),
                                unfocusedBorderColor = Color(0xFF1E293B),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color(0xFF0F172A),
                                unfocusedContainerColor = Color(0xFF0F172A)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    // -------------------------------------------------------------
                    // CAMPO 2: DESCRIPCIÓN / SINOPSIS DEL VIDEO
                    // -------------------------------------------------------------
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "DESCRIPCIÓN & SINOPSIS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF94A3B8)
                            )
                            Text(
                                text = "${description.length} caracteres",
                                fontSize = 10.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 90.dp, max = 130.dp)
                                .testTag("metadata_input_description"),
                            leadingIcon = {
                                Icon(Icons.AutoMirrored.Filled.Notes, contentDescription = null, tint = Color(0xFF38BDF8))
                            },
                            placeholder = { Text("Escribe una sinopsis, prompt original de Veo o notas editoriales...") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF0284C7),
                                unfocusedBorderColor = Color(0xFF1E293B),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color(0xFF0F172A),
                                unfocusedContainerColor = Color(0xFF0F172A)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    // -------------------------------------------------------------
                    // CAMPO 3: ETIQUETAS (TAGS) CON NUBE INTERACTIVA
                    // -------------------------------------------------------------
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "ETIQUETAS & HASHTAGS (${tagsList.size})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8)
                        )

                        // Fila de entrada de tag
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = tagInput,
                                onValueChange = { tagInput = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("metadata_input_tag"),
                                placeholder = { Text("Añadir tag (ej. #Shorts)") },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF0284C7),
                                    unfocusedBorderColor = Color(0xFF1E293B),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedContainerColor = Color(0xFF0F172A),
                                    unfocusedContainerColor = Color(0xFF0F172A)
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Button(
                                onClick = {
                                    val cleaned = tagInput.trim().removePrefix("#")
                                    if (cleaned.isNotBlank() && !tagsList.contains(cleaned)) {
                                        tagsList.add(cleaned)
                                        tagInput = ""
                                    }
                                },
                                enabled = tagInput.isNotBlank(),
                                modifier = Modifier
                                    .height(52.dp)
                                    .testTag("metadata_add_tag_btn"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF0284C7),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Añadir")
                            }
                        }

                        // Nube de tags activos
                        if (tagsList.isNotEmpty()) {
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                tagsList.forEach { tag ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFF1E293B),
                                        border = BorderStroke(1.dp, Color(0xFF334155)),
                                        modifier = Modifier.testTag("tag_item_$tag")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = "#$tag",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFF38BDF8)
                                            )
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Eliminar tag",
                                                tint = Color(0xFF94A3B8),
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .clickable { tagsList.remove(tag) }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Tags rápidos sugeridos
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            quickSuggestedTags.forEach { sug ->
                                val alreadyAdded = tagsList.contains(sug)
                                FilterChip(
                                    selected = alreadyAdded,
                                    onClick = {
                                        if (alreadyAdded) tagsList.remove(sug) else tagsList.add(sug)
                                    },
                                    label = { Text("+$sug", fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF0284C7).copy(alpha = 0.2f),
                                        selectedLabelColor = Color(0xFF38BDF8)
                                    )
                                )
                            }
                        }
                    }

                    // -------------------------------------------------------------
                    // CAMPO 4: CATEGORÍA DEL VIDEO
                    // -------------------------------------------------------------
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "CATEGORÍA DEL CONTENIDO",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            categories.forEach { cat ->
                                val isSelected = selectedCategory == cat
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedCategory = cat },
                                    label = { Text(cat, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF10B981).copy(alpha = 0.2f),
                                        selectedLabelColor = Color(0xFF34D399),
                                        containerColor = Color(0xFF0F172A),
                                        labelColor = Color(0xFF94A3B8)
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = isSelected,
                                        borderColor = if (isSelected) Color(0xFF10B981) else Color(0xFF1E293B)
                                    )
                                )
                            }
                        }
                    }

                    // -------------------------------------------------------------
                    // CAMPO 5: AUTOR & DERECHOS DE AUTOR
                    // -------------------------------------------------------------
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = author,
                            onValueChange = { author = it },
                            label = { Text("Autor / Creador", fontSize = 10.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("metadata_input_author"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF0284C7),
                                unfocusedBorderColor = Color(0xFF1E293B),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )

                        OutlinedTextField(
                            value = copyright,
                            onValueChange = { copyright = it },
                            label = { Text("Copyright / Licencia", fontSize = 10.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("metadata_input_copyright"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF0284C7),
                                unfocusedBorderColor = Color(0xFF1E293B),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    // -------------------------------------------------------------
                    // COFRE CUÁNTICO: SELLO DILITHIUM EXISTENTE (SI YA FUE GUARDADO)
                    // -------------------------------------------------------------
                    if (existingMetadata != null && existingMetadata.dilithiumSignature.isNotBlank()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0B132B)),
                            border = BorderStroke(1.dp, Color(0xFF1E293B))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "FIRMA POST-CUÁNTICA EN ROOM DB",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF38BDF8),
                                        letterSpacing = 0.5.sp
                                    )
                                    OutlinedButton(
                                        onClick = {
                                            isVerifyingQuantum = true
                                            coroutineScope.launch {
                                                val verdict = viewModel.verifyVideoMetadataDilithium(existingMetadata)
                                                verificationResultText = if (verdict.isValid) {
                                                    "Autenticidad Cuántica 100% Verificada (ML-DSA-65 Inalterado)"
                                                } else {
                                                    "Alerta: Metadatos alterados o firma discordante"
                                                }
                                                isVerifyingQuantum = false
                                            }
                                        },
                                        enabled = !isVerifyingQuantum,
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text("Verificar Firma", fontSize = 10.sp, color = Color(0xFF34D399))
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Firma: ${existingMetadata.dilithiumSignature.take(36)}...",
                                    fontSize = 9.5.sp,
                                    color = Color(0xFF94A3B8),
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "Vault Nonce: ${existingMetadata.quantumVaultNonce} • SHA-256: ${existingMetadata.metadataSha256Digest.take(16)}...",
                                    fontSize = 9.sp,
                                    color = Color(0xFF64748B),
                                    fontFamily = FontFamily.Monospace
                                )

                                if (verificationResultText != null) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = verificationResultText!!,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (verificationResultText!!.contains("100%")) Color(0xFF10B981) else Color(0xFFEF4444)
                                    )
                                }
                            }
                        }
                    }
                }

                // -------------------------------------------------------------
                // BARRA INFERIOR DE ACCIÓN: GUARDAR EN ROOM DATABASE
                // -------------------------------------------------------------
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(),
                    color = Color(0xFF070B14).copy(alpha = 0.96f),
                    border = BorderStroke(1.dp, Color(0xFF1E293B))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismissRequest,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("metadata_cancel_btn"),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFF334155))
                        ) {
                            Text("Cancelar", color = Color(0xFF94A3B8))
                        }

                        Button(
                            onClick = {
                                if (title.isBlank()) {
                                    Toast.makeText(context, "Por favor ingresa un título", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                isSaving = true
                                coroutineScope.launch {
                                    viewModel.saveVideoMetadata(
                                        id = existingMetadata?.id ?: 0L,
                                        projectId = project?.id ?: existingMetadata?.projectId ?: 0,
                                        videoUri = project?.videoUri ?: existingMetadata?.videoUri ?: "",
                                        title = title,
                                        description = description,
                                        tags = tagsList.joinToString(", "),
                                        category = selectedCategory,
                                        author = author,
                                        copyright = copyright,
                                        resolution = existingMetadata?.resolution ?: "1080p (FHD)",
                                        aspectRatio = project?.aspectRatio ?: existingMetadata?.aspectRatio ?: "16:9",
                                        durationSeconds = project?.durationSec ?: existingMetadata?.durationSeconds ?: 15
                                    )
                                    isSaving = false
                                    Toast.makeText(
                                        context,
                                        "Metadatos guardados en Room DB con firma Dilithium Q-Day",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    onDismissRequest()
                                }
                            },
                            enabled = !isSaving,
                            modifier = Modifier
                                .weight(2f)
                                .height(46.dp)
                                .testTag("metadata_save_room_btn"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF10B981),
                                contentColor = Color.White,
                                disabledContainerColor = Color(0xFF1E293B)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            if (isSaving) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Firmando & Guardando...", fontSize = 12.sp)
                            } else {
                                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Guardar en Room (Dilithium)", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFreedAuditDialog) {
        FreedAuditDetailsDialog(
            project = project,
            auditStatus = freedAuditStatus,
            viewModel = viewModel,
            onDismissRequest = { showFreedAuditDialog = false }
        )
    }
}
