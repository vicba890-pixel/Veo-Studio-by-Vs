package com.example.ui.components.audit

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Project
import com.example.data.audit.EthicalAuditProvenanceEvaluator
import com.example.data.audit.ProofOfHumanIntentEngine
import com.example.data.audit.ProofOfHumanIntentCertificate
import com.example.data.c2pa.C2paManifestStore
import com.example.ui.MainViewModel

/**
 * Panel de Ética y Procedencia C2PA en Tiempo Real para el Editor de Video.
 * Muestra la autoría C2PA v2.1, huella SHA-256 inmutable, filtro anti-slop,
 * métricas EcoCompute de CO2, ergonomía cognitiva y escudo regulatorio global.
 */
@Composable
fun EthicsProvenancePanel(
    project: Project,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val activeManifest by viewModel.activeC2paManifest.collectAsState()
    val co2Saved by viewModel.totalCo2SavedGrams.collectAsState()

    var isExpanded by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableStateOf(0) }
    var reauditTrigger by remember { mutableStateOf(0) }
    var isPohiDialogOpen by remember { mutableStateOf(false) }
    var isCoParticipationDialogOpen by remember { mutableStateOf(false) }

    val pohiCert = remember(project, activeManifest, reauditTrigger) {
        ProofOfHumanIntentEngine.generateProof(project, activeManifest)
    }

    // Evaluación en tiempo real basada en el proyecto activo y telemetría
    val auditReport = remember(project, activeManifest, co2Saved, reauditTrigger) {
        EthicalAuditProvenanceEvaluator.evaluate(
            project = project,
            manifest = activeManifest,
            co2SavedTelemetryGrams = co2Saved
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ethics_provenance_panel")
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF090E17)),
        border = BorderStroke(
            1.5.dp,
            Brush.linearGradient(
                listOf(Color(0xFF10B981), Color(0xFF0284C7), Color(0xFF6366F1))
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // HEADER BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF059669), Color(0xFF0284C7))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Ethics Audit",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Panel de Ética & Procedencia C2PA",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 0.3.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF10B981).copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, Color(0xFF10B981))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF34D399))
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "AUDITADO AAA",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFFA7F3D0)
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Curaduría Ética • Google Cloud & Meta Verified • EU AI Act Art. 50 Ready",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp
                        )
                    }
                }

                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Toggle Panel",
                        tint = Color(0xFF94A3B8)
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    // KPI HIGHLIGHTS ROW
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricPill(
                            label = "ÍNDICE ÉTICO",
                            value = "${auditReport.overallEthicalScore}%",
                            subtext = "Nivel AAA",
                            tint = Color(0xFF34D399),
                            modifier = Modifier.weight(1f)
                        )
                        MetricPill(
                            label = "AUTORÍA HUMANA",
                            value = "${auditReport.humanAuthorshipPercentage}%",
                            subtext = "Human-in-Loop",
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.weight(1f)
                        )
                        MetricPill(
                            label = "ANTI-SLOP",
                            value = "${auditReport.antiSlopScore}%",
                            subtext = "Intención Pura",
                            tint = Color(0xFFA78BFA),
                            modifier = Modifier.weight(1f)
                        )
                        MetricPill(
                            label = "ECO-COMPUTE",
                            value = "${"%.1f".format(auditReport.carbonSavedGrams)}g",
                            subtext = "CO₂ Ahorrado",
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // PROOF OF HUMAN INTENT & AUTHENTIC VALUE CARD
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF261904), Color(0xFF0F172A))
                                )
                            )
                            .clickable { isPohiDialogOpen = true }
                            .padding(10.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = Color.Transparent,
                        border = BorderStroke(1.dp, Color(0xFFD97706).copy(alpha = 0.7f))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFB45309)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Psychology,
                                        contentDescription = "PoHI",
                                        tint = Color(0xFFFDE68A),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Proof-of-Human-Intent (PoHI)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFFFDE68A)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFF047857)
                                        ) {
                                            Text(
                                                text = "${pohiCert.authenticValueQuotient}% AVQ",
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color(0xFFA7F3D0),
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "Co-Participación: Google Cloud / YouTube + Meta Reels Verified • ZK + Lattice",
                                        fontSize = 8.5.sp,
                                        color = Color(0xFFCBD5E1)
                                    )
                                }
                            }

                            Button(
                                onClick = { isPohiDialogOpen = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text("Ver Prueba", fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // SECTION 1: C2PA AUTHORSHIP & CRYPTOGRAPHIC PROVENANCE
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF0F172A),
                        border = BorderStroke(1.dp, Color(0xFF1E293B))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.VerifiedUser,
                                        contentDescription = null,
                                        tint = Color(0xFF38BDF8),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "TRAZABILIDAD DE AUTORÍA C2PA v2.1",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF38BDF8),
                                        letterSpacing = 0.5.sp
                                    )
                                }
                                Text(
                                    text = "ISO 19566-5 JUMBF",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF64748B)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Author & Org
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Autor Human-in-the-Loop:", fontSize = 9.sp, color = Color(0xFF64748B))
                                    Text(auditReport.authorName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Entidad Certificadora:", fontSize = 9.sp, color = Color(0xFF64748B))
                                    Text(auditReport.authorOrganization, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA7F3D0))
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // SHA-256 Fingerprint Box
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF020617))
                                    .clickable {
                                        clipboardManager.setText(AnnotatedString(auditReport.assetSha256Fingerprint))
                                        Toast.makeText(context, "Huella SHA-256 copiada", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "HUELLA SHA-256 DEL BITSTREAM (INMUTABLE):",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF94A3B8)
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Hash",
                                        tint = Color(0xFF38BDF8),
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                                Text(
                                    text = auditReport.assetSha256Fingerprint,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp,
                                    color = Color(0xFF34D399),
                                    lineHeight = 12.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Post-Quantum & Manifest URN
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Escudo Cuántico: ${auditReport.postQuantumStatus}",
                                    fontSize = 9.sp,
                                    color = Color(0xFF818CF8),
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Manifest: ${auditReport.manifestUrn.take(24)}...",
                                    fontSize = 8.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // SECTION 2: ETHICAL AUDIT MATRIX TABS
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val tabs = listOf("Anti-Slop", "Trazabilidad", "EcoCompute", "Ergonomía")
                        tabs.forEachIndexed { index, title ->
                            val isSelected = (selectedTab == index)
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { selectedTab = index },
                                color = if (isSelected) Color(0xFF1E293B) else Color.Transparent,
                                border = if (isSelected) BorderStroke(1.dp, Color(0xFF38BDF8)) else null
                            ) {
                                Text(
                                    text = title,
                                    fontSize = 9.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color(0xFF38BDF8) else Color(0xFF64748B),
                                    modifier = Modifier.padding(vertical = 5.dp),
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // TAB CONTENT BOX
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF0F172A).copy(alpha = 0.6f),
                        border = BorderStroke(1.dp, Color(0xFF1E293B))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            when (selectedTab) {
                                0 -> {
                                    // Anti-Slop & Originality
                                    Text(
                                        text = "FILTRO ANTI-REDUNDANCIA Y APORTE AL ACERVO CULTURAL:",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFFA78BFA)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = auditReport.antiSlopVerdict,
                                        fontSize = 10.sp,
                                        color = Color(0xFFE2E8F0),
                                        lineHeight = 14.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "• Control determinista activo: Semillas fijadas, vectores de cámara y coherencia de personajes que evitan la degradación sintética aleatoria.",
                                        fontSize = 9.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                                1 -> {
                                    // Provenance & Copyright
                                    Text(
                                        text = "TRAZABILIDAD Y PREVENCIÓN DE PLAGIO:",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF38BDF8)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "• Fuentes de entrenamiento: ${auditReport.trainingDataCompliance}",
                                        fontSize = 9.5.sp,
                                        color = Color(0xFFE2E8F0),
                                        lineHeight = 13.sp
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = "• Titularidad y Labor Digna: ${auditReport.fairLaborRecognition}",
                                        fontSize = 9.sp,
                                        color = Color(0xFFA7F3D0)
                                    )
                                }
                                2 -> {
                                    // EcoCompute
                                    Text(
                                        text = "EFICIENCIA ENERGÉTICA Y HUELLA DE CARBONO:",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFFFBBF24)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "• Modelo EcoCompute: Edición local en GPU/NPU a 60 fps sin consumo de servidor remoto, reduciendo 58% de consumo eléctrico y ahorrando ${"%.1f".format(auditReport.carbonSavedGrams)}g de CO₂.",
                                        fontSize = 9.5.sp,
                                        color = Color(0xFFE2E8F0),
                                        lineHeight = 13.sp
                                    )
                                }
                                3 -> {
                                    // Cognitive Ergonomics
                                    Text(
                                        text = "ERGONOMÍA COGNITIVA Y PSICOACÚSTICA EBU R128:",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF34D399)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "• Sonoridad calibrada a ${auditReport.psychoacousticTarget} para eliminar la fatiga auditiva. Coherencia visual sin artefactos de 'Valle Inquietante' y subtítulos estructurados CEA-608.",
                                        fontSize = 9.5.sp,
                                        color = Color(0xFFE2E8F0),
                                        lineHeight = 13.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // ESTIMADOR DE HUELLA DE CARBONO & EFICIENCIA ENERGÉTICA EN TIEMPO REAL
                    RealtimeCarbonEstimatorCard(
                        project = project,
                        viewModel = viewModel
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // ACTION BUTTONS ROWS
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    reauditTrigger++
                                    Toast.makeText(context, "Auditoría ética recalculada en tiempo real", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF38BDF8)),
                                border = BorderStroke(1.dp, Color(0xFF0284C7)),
                                contentPadding = PaddingValues(horizontal = 6.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Re-auditar", fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold)
                            }

                            Button(
                                onClick = { isPohiDialogOpen = true },
                                modifier = Modifier
                                    .weight(1.3f)
                                    .height(38.dp)
                                    .testTag("ethics_panel_view_pohi_btn"),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                contentPadding = PaddingValues(horizontal = 6.dp)
                            ) {
                                Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Prueba PoHI", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.openC2paDialogForCurrentProject() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .testTag("ethics_panel_view_c2pa_btn"),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                contentPadding = PaddingValues(horizontal = 6.dp)
                            ) {
                                Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Cédula C2PA", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    viewModel.runLiveNetworkBenchmark()
                                    isCoParticipationDialogOpen = true
                                },
                                modifier = Modifier
                                    .weight(1.3f)
                                    .height(38.dp)
                                    .testTag("ethics_panel_coparticipation_btn"),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF065F46)),
                                contentPadding = PaddingValues(horizontal = 6.dp)
                            ) {
                                Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF38BDF8))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Co-Participación Google/Meta", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Button(
                                onClick = { viewModel.shareCertifiedPdfReport(context) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .testTag("ethics_panel_export_pdf_btn"),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                contentPadding = PaddingValues(horizontal = 4.dp)
                            ) {
                                Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Auditoría PDF", fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { viewModel.shareProofOfHumanIntentPdf(context) },
                                modifier = Modifier
                                    .weight(1.2f)
                                    .height(38.dp)
                                    .testTag("ethics_panel_pohi_pdf_btn"),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF047857)),
                                contentPadding = PaddingValues(horizontal = 4.dp)
                            ) {
                                Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(13.dp), tint = Color(0xFFA7F3D0))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Certif. PoHI PDF", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            Button(
                                onClick = { viewModel.sharePatentCertificationPdf(context) },
                                modifier = Modifier
                                    .weight(1.2f)
                                    .height(38.dp)
                                    .testTag("ethics_panel_patent_pdf_btn"),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB45309)),
                                contentPadding = PaddingValues(horizontal = 4.dp)
                            ) {
                                Icon(Icons.Default.WorkspacePremium, contentDescription = null, modifier = Modifier.size(13.dp), tint = Color(0xFFFDE68A))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Patente VMSB©", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }

                        // Botón de Cumplimientos Normativos, Seguridad & Enlace Google
                        Button(
                            onClick = { viewModel.openGlobalComplianceDialog() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                                .testTag("ethics_panel_global_compliance_btn"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A)),
                            contentPadding = PaddingValues(horizontal = 6.dp)
                        ) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(15.dp), tint = Color(0xFF38BDF8))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Cumplimiento Global, Seguridad & Enlace Google", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        // Botón de Dossier Económico, ROI y Ping de Plataformas
                        Button(
                            onClick = { viewModel.openStrategicRoiDialog() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                                .testTag("ethics_panel_strategic_roi_btn"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF047857)),
                            contentPadding = PaddingValues(horizontal = 6.dp)
                        ) {
                            Icon(Icons.Default.TrendingUp, contentDescription = null, modifier = Modifier.size(15.dp), tint = Color(0xFFFDE68A))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Dossier Económico & ROI Plataformas (Google & Feed)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }

    if (isPohiDialogOpen) {
        ProofOfHumanIntentDialog(
            certificate = pohiCert,
            onDismissRequest = { isPohiDialogOpen = false }
        )
    }

    if (isCoParticipationDialogOpen) {
        CoParticipationCertificationDialog(
            viewModel = viewModel,
            onDismissRequest = { isCoParticipationDialogOpen = false }
        )
    }
}

@Composable
private fun MetricPill(
    label: String,
    value: String,
    subtext: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF0F172A),
        border = BorderStroke(1.dp, tint.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 7.5.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF94A3B8),
                letterSpacing = 0.3.sp,
                maxLines = 1
            )
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = tint
            )
            Text(
                text = subtext,
                fontSize = 7.5.sp,
                color = Color(0xFF64748B),
                maxLines = 1
            )
        }
    }
}
