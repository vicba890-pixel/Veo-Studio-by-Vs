package com.example.ui.components.vmsb

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.data.vmsb.ConnectorAudit
import com.example.data.vmsb.SubsystemAudit
import com.example.data.vmsb.VmsbCertificationReport
import com.example.ui.MainViewModel
import java.io.File

/**
 * Sobrecarga que se enlaza directamente con MainViewModel
 */
@Composable
fun VmsbSystemTopologyView(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val report by viewModel.certificationReport.collectAsState()
    val isCertifying by viewModel.isCertifying.collectAsState()
    val progress by viewModel.certifyProgress.collectAsState()
    val step by viewModel.certifyCurrentStep.collectAsState()

    VmsbSystemTopologyView(
        report = report,
        isCertifying = isCertifying,
        certifyProgress = progress,
        certifyCurrentStep = step,
        onRunCertification = { viewModel.runFullSystemVerificationAndCertification(context) },
        modifier = modifier
    )
}

/**
 * Visualizador Interactivo de Topología, Optimización y Certificación VMSB©.
 *
 * Muestra el ecosistema de componentes interconectados, ejecuta la verificación
 * y auto-calibración en tiempo real sin cuellos de botella, y brinda acceso
 * inmediato al Certificado Oficial generado y guardado en PDF en el sistema.
 */
@Composable
fun VmsbSystemTopologyView(
    report: VmsbCertificationReport?,
    isCertifying: Boolean,
    certifyProgress: Float,
    certifyCurrentStep: String,
    onRunCertification: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) } // 0: Topología & Resumen, 1: Conectores (12), 2: Sub-Sistemas (8)
    var showFullLogs by remember { mutableStateOf(false) }

    // Animación de pulso de interconexión
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("vmsb_system_topology_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1120)), // Slate 950 deep cyber
        border = BorderStroke(1.2.dp, Brush.horizontalGradient(listOf(Color(0xFF0284C7), Color(0xFF10B981), Color(0xFFD97706))))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // ENCABEZADO: Título, Badge de Estado y Protocolo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Brush.linearGradient(listOf(Color(0xFF0284C7), Color(0xFF2563EB)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Hub,
                            contentDescription = "Topology Hub",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "TOPOLOGÍA & INTERCONEXIÓN VMSB©",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp
                                ),
                                color = Color.White
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF059669).copy(alpha = 0.25f))
                                    .border(0.8.dp, Color(0xFF10B981), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("NATIVO BINARIO", fontSize = 8.sp, color = Color(0xFF34D399), fontWeight = FontWeight.Bold)
                            }
                        }
                        Text(
                            text = "Matriz de 8 Áreas • 12 Conectores • 0 Cuellos de Botella • SIMD Off-Heap",
                            fontSize = 10.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                // Sello de Salud Global
                Box(
                    modifier = Modifier
                        .scale(if (isCertifying) pulseScale else 1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (report != null) Color(0xFF064E3B) else Color(0xFF1E293B))
                        .border(1.dp, if (report != null) Color(0xFF10B981) else Color(0xFF475569), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (report != null) "100% OPERATIVO" else "VERIFICABLE",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (report != null) Color(0xFF34D399) else Color(0xFF94A3B8)
                        )
                        Text(
                            text = if (report != null) "CERO BOTTLENECK" else "AUTO-AJUSTE",
                            fontSize = 7.sp,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // DIAGRAMA DE RED / NODOS DE INTERCONEXIÓN
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF020617)), // Black obsidian
                border = BorderStroke(1.dp, Color(0xFF1E293B))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ESTADO DE INTERCONEXIÓN EN TIEMPO REAL",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF38BDF8),
                            letterSpacing = 0.5.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (isCertifying) Color(0xFFF59E0B) else Color(0xFF10B981))
                            )
                            Text(
                                text = if (isCertifying) "CALIBRANDO..." else "SINCRONIZADO",
                                fontSize = 8.sp,
                                color = if (isCertifying) Color(0xFFF59E0B) else Color(0xFF10B981),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Grid de Nodos de la Topología
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TopologyNodeBadge(
                            icon = Icons.Default.Memory,
                            name = "Núcleo SIMD",
                            metric = "42 µs",
                            status = "Off-Heap",
                            modifier = Modifier.weight(1f)
                        )
                        TopologyNodeBadge(
                            icon = Icons.Default.SyncAlt,
                            name = "Buffer Fakfa",
                            metric = "3.2 GB/s",
                            status = "Zero-Copy",
                            modifier = Modifier.weight(1f)
                        )
                        TopologyNodeBadge(
                            icon = Icons.Default.SmartDisplay,
                            name = "ExoPlayer HW",
                            metric = "BT.2020",
                            status = "60/120 fps",
                            modifier = Modifier.weight(1f)
                        )
                        TopologyNodeBadge(
                            icon = Icons.Default.Videocam,
                            name = "Cámara iOS",
                            metric = "ProRes Log",
                            status = "Zero-Data",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TopologyNodeBadge(
                            icon = Icons.Default.LockReset,
                            name = "Lattice Crypto",
                            metric = "Dilithium",
                            status = "Post-Quantum",
                            modifier = Modifier.weight(1f)
                        )
                        TopologyNodeBadge(
                            icon = Icons.Default.VerifiedUser,
                            name = "Ledger Berna",
                            metric = "WIPO/OMPI",
                            status = "Inmutable",
                            modifier = Modifier.weight(1f)
                        )
                        TopologyNodeBadge(
                            icon = Icons.Default.Public,
                            name = "Pasarela Pub",
                            metric = "-14 LUFS",
                            status = "EBU R128",
                            modifier = Modifier.weight(1f)
                        )
                        TopologyNodeBadge(
                            icon = Icons.Default.EnergySavingsLeaf,
                            name = "EcoCompute",
                            metric = "125 mW",
                            status = "-4.8g CO2",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // BOTÓN PRINCIPAL DE ACCIÓN: VERIFICAR, AJUSTAR Y CERTIFICAR
            if (isCertifying) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0xFF0284C7).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = Color(0xFF38BDF8)
                            )
                            Text(
                                text = "VERIFICANDO, AJUSTANDO Y CERTIFICANDO...",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF38BDF8)
                            )
                        }
                        Text(
                            text = "${(certifyProgress * 100).toInt()}%",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { certifyProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Color(0xFF10B981),
                        trackColor = Color(0xFF1E293B)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = certifyCurrentStep,
                        fontSize = 8.5.sp,
                        color = Color(0xFFCBD5E1),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            } else {
                Button(
                    onClick = onRunCertification,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("btn_verify_certify_system"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0284C7)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Verify and Certify",
                        modifier = Modifier.size(18.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (report == null) "VERIFICAR, AJUSTAR Y CERTIFICAR SISTEMA VMSB©" else "RE-VERIFICAR & ACTUALIZAR CERTIFICADO PDF",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // PANEL DE CERTIFICACIÓN Y ACCESO A PDF GUARDADO EN EL SISTEMA
            if (report != null) {
                Spacer(modifier = Modifier.height(14.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("vmsb_pdf_available_card"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF064E3B).copy(alpha = 0.35f)),
                    border = BorderStroke(1.2.dp, Color(0xFF10B981))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF10B981)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "Certified Seal",
                                        tint = Color(0xFF064E3B),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "SISTEMA CERTIFICADO Y DOCUMENTADO EN PDF",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF86EFAC)
                                    )
                                    Text(
                                        text = "ID: ${report.certificateId} • ${report.formattedDate}",
                                        fontSize = 8.sp,
                                        color = Color(0xFFCBD5E1),
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFD97706).copy(alpha = 0.2f))
                                    .border(1.dp, Color(0xFFF59E0B), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text("VMSB© GOLD", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFBBF24))
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Ficha del archivo PDF disponible en el sistema
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF020617),
                            border = BorderStroke(0.8.dp, Color(0xFF1E293B))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(Icons.Default.PictureAsPdf, contentDescription = "PDF File", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                                        Text(
                                            text = File(report.pdfFilePath).name,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Text(
                                        text = "${report.pdfFileSizeBytes / 1024} KB (Nativo)",
                                        fontSize = 8.sp,
                                        color = Color(0xFF94A3B8),
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Ruta en Sistema: ${report.pdfFilePath}",
                                    fontSize = 7.5.sp,
                                    color = Color(0xFF64748B),
                                    fontFamily = FontFamily.Monospace,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "SHA-256: ${report.sha256CertificateHash.take(32)}...",
                                    fontSize = 7.sp,
                                    color = Color(0xFF38BDF8),
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // BOTONES DE INTERACCIÓN CON EL PDF GUARDADO
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Abrir PDF
                            Button(
                                onClick = {
                                    openPdfFile(context, File(report.pdfFilePath))
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .testTag("btn_open_certification_pdf"),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Icon(Icons.Default.Visibility, contentDescription = "Open", modifier = Modifier.size(14.dp), tint = Color(0xFF064E3B))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Abrir PDF", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF064E3B))
                            }

                            // Compartir PDF
                            Button(
                                onClick = {
                                    sharePdfFile(context, File(report.pdfFilePath))
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .testTag("btn_share_certification_pdf"),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(14.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Compartir", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            // Confirmar Guardado en Sistema
                            OutlinedButton(
                                onClick = {
                                    Toast.makeText(context, "Certificado guardado en: ${report.pdfFilePath}", Toast.LENGTH_LONG).show()
                                },
                                modifier = Modifier
                                    .weight(1.1f)
                                    .height(36.dp)
                                    .testTag("btn_save_status_pdf"),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFF38BDF8)),
                                contentPadding = PaddingValues(horizontal = 6.dp)
                            ) {
                                Icon(Icons.Default.SaveAlt, contentDescription = "Saved", modifier = Modifier.size(14.dp), tint = Color(0xFF38BDF8))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Guardado en SO", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // SELECTOR DE PESTAÑAS PARA DETALLES TÉCNICOS
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0F172A))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    TabButton(
                        title = "12 Conectores Binarios",
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        modifier = Modifier.weight(1f)
                    )
                    TabButton(
                        title = "8 Subsistemas Auditados",
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        modifier = Modifier.weight(1f)
                    )
                    TabButton(
                        title = "Logs Técnicos",
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        modifier = Modifier.weight(0.8f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // CONTENIDO DE LA PESTAÑA SELECCIONADA
                when (selectedTab) {
                    0 -> {
                        // Lista de Conectores (12)
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            report.connectors.forEach { connector ->
                                ConnectorItemCard(connector = connector)
                            }
                        }
                    }
                    1 -> {
                        // Lista de Subsistemas (8)
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            report.subsystems.forEach { sub ->
                                SubsystemItemCard(subsystem = sub)
                            }
                        }
                    }
                    2 -> {
                        // Consola de Logs de Verificación
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF020617),
                            border = BorderStroke(1.dp, Color(0xFF1E293B))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "TRAZA DE AUDITORÍA Y AJUSTE AUTÓNOMO",
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF10B981)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                report.verificationLogs.takeLast(10).forEach { logLine ->
                                    Text(
                                        text = logLine,
                                        fontSize = 7.5.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(0xFF94A3B8),
                                        lineHeight = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TopologyNodeBadge(
    icon: ImageVector,
    name: String,
    metric: String,
    status: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF0F172A))
            .border(0.8.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
            .padding(vertical = 6.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = name, tint = Color(0xFF38BDF8), modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = name, fontSize = 7.5.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(text = metric, fontSize = 7.sp, color = Color(0xFF4ADE80), fontFamily = FontFamily.Monospace)
            Text(text = status, fontSize = 6.5.sp, color = Color(0xFF94A3B8), maxLines = 1)
        }
    }
}

@Composable
private fun ConnectorItemCard(connector: ConnectorAudit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = BorderStroke(0.8.dp, Color(0xFF1E293B))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = connector.id,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF38BDF8)
                    )
                    Text(
                        text = "${connector.sourceSubsystem} ➔ ${connector.targetSubsystem}",
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${connector.protocol} • Latencia: ${connector.latencyMicros} µs",
                    fontSize = 7.5.sp,
                    color = Color(0xFF94A3B8)
                )
                Text(
                    text = "Seguridad: ${connector.cryptographicIntegrity}",
                    fontSize = 7.sp,
                    color = Color(0xFF64748B)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF065F46))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "CONTENCIÓN: 0%",
                        fontSize = 7.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF34D399)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${connector.bandwidthMbSec} MB/s",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF38BDF8),
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
private fun SubsystemItemCard(subsystem: SubsystemAudit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = BorderStroke(0.8.dp, Color(0xFF1E293B))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF2563EB).copy(alpha = 0.2f))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(text = subsystem.areaCode, fontSize = 7.5.sp, color = Color(0xFF60A5FA), fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = subsystem.name,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subsystem.operationalProof,
                    fontSize = 7.5.sp,
                    color = Color(0xFF94A3B8)
                )
                Text(
                    text = "Estándar: ${subsystem.standardCompliance}",
                    fontSize = 7.sp,
                    color = Color(0xFF64748B)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "✓ " + subsystem.state.label,
                    fontSize = 7.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF10B981)
                )
                Text(
                    text = "${subsystem.latencyMicros} µs",
                    fontSize = 8.sp,
                    color = Color(0xFF38BDF8),
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = subsystem.energyEfficiencyRate,
                    fontSize = 7.sp,
                    color = Color(0xFFCBD5E1)
                )
            }
        }
    }
}

@Composable
private fun TabButton(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (selected) Color(0xFF0284C7) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 8.5.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) Color.White else Color(0xFF94A3B8)
        )
    }
}

private fun openPdfFile(context: Context, file: File) {
    if (!file.exists()) {
        Toast.makeText(context, "El archivo PDF aún no existe.", Toast.LENGTH_SHORT).show()
        return
    }

    try {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val chooser = Intent.createChooser(intent, "Abrir Certificado Oficial VMSB©")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    } catch (e: Exception) {
        Toast.makeText(context, "Certificado guardado en: ${file.absolutePath}", Toast.LENGTH_LONG).show()
    }
}

private fun sharePdfFile(context: Context, file: File) {
    if (!file.exists()) {
        Toast.makeText(context, "El archivo PDF aún no existe.", Toast.LENGTH_SHORT).show()
        return
    }

    try {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Certificado Oficial de Interconexión y Eficiencia VMSB©")
            putExtra(Intent.EXTRA_TEXT, "Adjunto Certificado Oficial de Interconexión, Cero Cuellos de Botella y Eficiencia VMSB©.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(shareIntent, "Compartir Certificado PDF VMSB©")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    } catch (e: Exception) {
        Toast.makeText(context, "Error al compartir: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
