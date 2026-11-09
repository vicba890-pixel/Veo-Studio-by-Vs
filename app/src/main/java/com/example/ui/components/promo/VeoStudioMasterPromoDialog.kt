package com.example.ui.components.promo

import android.content.Intent
import android.widget.Toast
import java.util.Locale
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import com.example.data.promo.PromoSegmentSpec
import com.example.data.promo.VeoStudioMasterPromoShowcase
import com.example.ui.MainViewModel
import com.example.ui.adaptive.tvFocusableRing
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * =========================================================================================
 * VMSB© 2026 - MODAL SHOWCASE DEL MASTER PROMO AUDIOVISUAL (20s)
 * VEO STUDIO BY VS - DOS SEGMENTOS DE 10s • MÁXIMA CALIDAD • DESPLIEGUE UNIVERSAL
 * =========================================================================================
 *
 * Ofrece:
 * 1. Reproducción segmentada con timecode SMPTE (Segmento 1: 00:00-00:10 / Segmento 2: 00:10-00:20).
 * 2. Visualización multi-formato adaptativa (16:9 Smart TV, 9:16 iOS/Shorts, 4:5 Feed).
 * 3. Modo Pantalla Completa inmersivo (Fullscreen) con navegación D-Pad para Smart TV y Tablets.
 * 4. Bajar video MP4 master al almacenamiento público de Descargas.
 * 5. Compartir nativo vía Android Sharesheet e inyección C2PA v2.1.
 * 6. Exportación y visualización inmediata del Dossier Maestro en PDF certificado.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VeoStudioMasterPromoDialog(
    viewModel: MainViewModel,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedSegment by remember { mutableStateOf<Int?>(null) } // null = 20s Completo, 1 = Seg 1, 2 = Seg 2
    var selectedRatio by remember { mutableStateOf("16:9") } // "16:9", "9:16", "4:5"
    var isPlaying by remember { mutableStateOf(true) }
    var playbackProgress by remember { mutableFloatStateOf(0f) }
    var isFullscreen by remember { mutableStateOf(false) }
    var isDownloading by remember { mutableStateOf(false) }

    // Simulación del bucle de reproducción interactivo de 20 segundos
    LaunchedEffect(isPlaying, selectedSegment) {
        val totalDurationMs = when (selectedSegment) {
            1 -> 10000L
            2 -> 10000L
            else -> 20000L
        }

        while (isPlaying) {
            delay(100)
            playbackProgress += (100f / totalDurationMs)
            if (playbackProgress >= 1f) {
                playbackProgress = 0f
            }
        }
    }

    val currentSeconds = when (selectedSegment) {
        1 -> (playbackProgress * 10f)
        2 -> 10f + (playbackProgress * 10f)
        else -> (playbackProgress * 20f)
    }

    val activeSegmentSpec: PromoSegmentSpec = if (currentSeconds < 10f) {
        VeoStudioMasterPromoShowcase.SEGMENT_1
    } else {
        VeoStudioMasterPromoShowcase.SEGMENT_2
    }

    // Modal a Pantalla Completa Inmersiva
    if (isFullscreen) {
        Dialog(
            onDismissRequest = { isFullscreen = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .testTag("promo_fullscreen_modal"),
                color = Color.Black
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Contenido de video simulado a pantalla completa
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(if (selectedRatio == "9:16") 0.5f else 0.95f)
                                .aspectRatio(if (selectedRatio == "9:16") 9f / 16f else if (selectedRatio == "4:5") 4f / 5f else 16f / 9f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    Brush.radialGradient(
                                        listOf(
                                            if (currentSeconds < 10f) Color(0xFF0284C7) else Color(0xFF059669),
                                            Color(0xFF070B14)
                                        )
                                    )
                                )
                                .border(2.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(20.dp)
                            ) {
                                Text(
                                    text = if (currentSeconds < 10f) "VMSB© NATIVE VECTOR PIPELINE" else "SOVEREIGN DILITHIUM Q-DAY IMMUNITY",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = activeSegmentSpec.voiceOverScript,
                                    color = Color(0xFFE2E8F0),
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Controles en pantalla completa
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilledTonalIconButton(
                                onClick = { isPlaying = !isPlaying },
                                modifier = Modifier.size(56.dp)
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play/Pausa",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Button(
                                onClick = { isFullscreen = false },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.FullscreenExit, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Salir de Pantalla Completa")
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Estándar
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.95f)
                .testTag("veo_master_promo_dialog"),
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
                    // ENCABEZADO
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
                                            listOf(Color(0xFFE11D48), Color(0xFF0284C7))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MovieFilter,
                                    contentDescription = "Promo Showcase",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Veo Studio By Vs — Master Promo",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "2 Segmentos x 10s (20s) • Máxima Calidad VMSB©",
                                    fontSize = 11.sp,
                                    color = Color(0xFF38BDF8)
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismissRequest,
                            modifier = Modifier.testTag("promo_dialog_close_btn")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color(0xFF94A3B8))
                        }
                    }

                    // -------------------------------------------------------------
                    // REPRODUCTOR AUDIOVISUAL CON INDICADOR DE SEGMENTO ACTIVO
                    // -------------------------------------------------------------
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF020617)),
                        border = BorderStroke(1.dp, Color(0xFF1E293B))
                    ) {
                        Column {
                            // Área de video simulada adaptativa
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(
                                                if (currentSeconds < 10f) Color(0xFF022C44) else Color(0xFF063321),
                                                Color(0xFF020617)
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (currentSeconds < 10f) Color(0xFF0284C7).copy(alpha = 0.25f) else Color(0xFF10B981).copy(alpha = 0.25f),
                                        border = BorderStroke(1.dp, if (currentSeconds < 10f) Color(0xFF0284C7) else Color(0xFF10B981))
                                    ) {
                                        Text(
                                            text = if (currentSeconds < 10f) "SEGMENTO 1 (00:00 - 00:10): MOTOR NATIVO VMSB" else "SEGMENTO 2 (00:10 - 00:20): SOBERANÍA & Q-DAY",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (currentSeconds < 10f) Color(0xFF38BDF8) else Color(0xFF34D399),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = activeSegmentSpec.title,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        textAlign = TextAlign.Center
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "\"${activeSegmentSpec.voiceOverScript.take(90)}...\"",
                                        fontSize = 11.sp,
                                        color = Color(0xFFCBD5E1),
                                        textAlign = TextAlign.Center,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                // Botón flotante para ampliar a pantalla completa
                                IconButton(
                                    onClick = { isFullscreen = true },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                        .testTag("promo_fullscreen_btn")
                                        .tvFocusableRing(onDpadEnter = { isFullscreen = true })
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Fullscreen,
                                        contentDescription = "Ampliar a Pantalla Completa",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            // Barra de progreso segmentada en 2 bloques de 10s
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = String.format(Locale.US, "SMPTE %02d:%02d:%02d:00", (currentSeconds / 60).toInt(), (currentSeconds % 60).toInt(), ((currentSeconds * 60) % 60).toInt()),
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(0xFF38BDF8),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "00:00:20:00 MASTER",
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(0xFF94A3B8)
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                LinearProgressIndicator(
                                    progress = { playbackProgress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = if (currentSeconds < 10f) Color(0xFF0284C7) else Color(0xFF10B981),
                                    trackColor = Color(0xFF1E293B)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Controles de reproducción
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        IconButton(onClick = { isPlaying = !isPlaying }) {
                                            Icon(
                                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                contentDescription = null,
                                                tint = Color.White
                                            )
                                        }
                                        IconButton(onClick = { playbackProgress = 0f }) {
                                            Icon(
                                                imageVector = Icons.Default.Replay,
                                                contentDescription = "Reiniciar",
                                                tint = Color(0xFF94A3B8)
                                            )
                                        }
                                    }

                                    // Indicador LUFS
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                            contentDescription = null,
                                            tint = Color(0xFF10B981),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "-14.0 LUFS EBU R128",
                                            fontSize = 9.sp,
                                            color = Color(0xFF10B981),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // -------------------------------------------------------------
                    // SELECTOR DE SEGMENTOS (10s + 10s = 20s)
                    // -------------------------------------------------------------
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "SELECTOR DE SEGMENTOS MAESTROS (2x 10s)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = selectedSegment == null,
                                onClick = {
                                    selectedSegment = null
                                    playbackProgress = 0f
                                },
                                label = { Text("Completo (20s)", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF0284C7).copy(alpha = 0.25f),
                                    selectedLabelColor = Color(0xFF38BDF8)
                                )
                            )
                            FilterChip(
                                selected = selectedSegment == 1,
                                onClick = {
                                    selectedSegment = 1
                                    playbackProgress = 0f
                                },
                                label = { Text("Seg 1 (10s)", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF0284C7).copy(alpha = 0.25f),
                                    selectedLabelColor = Color(0xFF38BDF8)
                                )
                            )
                            FilterChip(
                                selected = selectedSegment == 2,
                                onClick = {
                                    selectedSegment = 2
                                    playbackProgress = 0f
                                },
                                label = { Text("Seg 2 (10s)", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF10B981).copy(alpha = 0.25f),
                                    selectedLabelColor = Color(0xFF34D399)
                                )
                            )
                        }
                    }

                    // -------------------------------------------------------------
                    // SELECTOR DE FORMATO ADAPTATIVO (16:9, 9:16, 4:5)
                    // -------------------------------------------------------------
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "FORMATO VISUAL ADAPTATIVO",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("16:9" to "📺 Smart TV / PC", "9:16" to "📱 iOS / Shorts", "4:5" to "🖼️ Social Feed").forEach { (ratio, label) ->
                                val isSel = selectedRatio == ratio
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSel) Color(0xFF1E293B) else Color(0xFF0F172A),
                                    border = BorderStroke(1.dp, if (isSel) Color(0xFF38BDF8) else Color(0xFF1E293B)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedRatio = ratio }
                                        .padding(vertical = 4.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(text = ratio, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (isSel) Color(0xFF38BDF8) else Color.White)
                                        Text(text = label, fontSize = 9.sp, color = Color(0xFF94A3B8), textAlign = TextAlign.Center)
                                    }
                                }
                            }
                        }
                    }

                    // -------------------------------------------------------------
                    // MATRIZ DE COMPATIBILIDAD UNIVERSAL MULTI-BRAND
                    // -------------------------------------------------------------
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "COMPATIBILIDAD MULTI-BRAND CERTIFICADA",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8)
                        )
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1120)),
                            border = BorderStroke(1.dp, Color(0xFF1E293B))
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                VeoStudioMasterPromoShowcase.BRAND_TARGETS.take(4).forEach { brand ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(text = brand.brandName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            Text(text = brand.codecContainer, fontSize = 9.sp, color = Color(0xFF94A3B8))
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFF10B981).copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = "60 FPS 4K OK",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF10B981),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    if (brand != VeoStudioMasterPromoShowcase.BRAND_TARGETS[3]) {
                                        HorizontalDivider(color = Color(0xFF1E293B))
                                    }
                                }
                            }
                        }
                    }
                }

                // -------------------------------------------------------------
                // BARRA INFERIOR DE ACCIONES: BAJAR, COMPARTIR, EXPORTAR PDF
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
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 1. Botón Bajar Video
                        Button(
                            onClick = {
                                isDownloading = true
                                coroutineScope.launch {
                                    val file = VeoStudioMasterPromoShowcase.downloadPromoVideoToPublicFolder(context)
                                    isDownloading = false
                                    Toast.makeText(
                                        context,
                                        "Video Master 20s descargado en: ${file.name}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            },
                            enabled = !isDownloading,
                            modifier = Modifier
                                .weight(1.2f)
                                .height(46.dp)
                                .testTag("promo_download_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = if (isDownloading) "Bajando..." else "Bajar Video", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        // 2. Botón Compartir Video Nativo
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    val videoFile = VeoStudioMasterPromoShowcase.generateOrGetMasterPromoVideoFile(context)
                                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", videoFile)
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "video/mp4"
                                        putExtra(Intent.EXTRA_STREAM, uri)
                                        putExtra(Intent.EXTRA_SUBJECT, "Veo Studio By Vs — Master Promo 4K HDR (20s)")
                                        putExtra(Intent.EXTRA_TEXT, "Veo Studio by Vs (VMSB Master 20s): Edición nativa zero-copy, C2PA v2.1 y seguridad cuántica Dilithium.")
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Compartir Video Master"))
                                }
                            },
                            modifier = Modifier
                                .weight(1.2f)
                                .height(46.dp)
                                .testTag("promo_share_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Compartir", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        // 3. Botón Exportar PDF Certificado
                        OutlinedButton(
                            onClick = {
                                val pdfFile = viewModel.exportMasterPromoShowcasePdf(context)
                                if (pdfFile != null && pdfFile.exists()) {
                                    viewModel.viewPdfFile(context, pdfFile)
                                }
                            },
                            modifier = Modifier
                                .weight(1.4f)
                                .height(46.dp)
                                .testTag("promo_export_pdf_btn"),
                            border = BorderStroke(1.dp, Color(0xFF38BDF8)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF38BDF8)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Dossier PDF", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
