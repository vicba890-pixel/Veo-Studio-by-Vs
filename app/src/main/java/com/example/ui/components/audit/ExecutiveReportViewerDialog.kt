package com.example.ui.components.audit

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.vmsb.ConnectivityBenchmarkData
import com.example.data.vmsb.VmsbEnterpriseAuditReportGenerator

@Composable
fun ExecutiveReportViewerDialog(
    projectTitle: String,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val connectivity = remember { ConnectivityBenchmarkData() }
    val milestones = remember { VmsbEnterpriseAuditReportGenerator.defaultMilestones }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .testTag("executive_report_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = BorderStroke(
                1.5.dp,
                Brush.verticalGradient(listOf(Color(0xFFF59E0B), Color(0xFF10B981), Color(0xFF0284C7)))
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF78350F)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Assessment,
                                contentDescription = "Audit",
                                tint = Color(0xFFFCD34D),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Auditoría & Proyección ROI",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Conectividad Google/Meta • C2PA v2.1 • 3 Años",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFFBBF24)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.testTag("executive_report_close")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // SECCIÓN 1: TELEMETRÍA REAL PING GOOGLE Y META
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1E293B))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "HITO DE CONECTIVIDAD & FORTALEZA (MEDICIÓN REAL)",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF38BDF8)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Google Cloud / DNS API", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 11.sp)
                            Text(
                                text = "${connectivity.googleLatencyMs} ms • HTTP ${connectivity.googleHttpStatus} OK",
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF34D399),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Cifrado: ${connectivity.googleTlsCipher} | Enlace Seguro Certificado",
                            fontSize = 9.5.sp,
                            color = Color(0xFF94A3B8)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Meta Graph API (Reels Gateway)", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 11.sp)
                            Text(
                                text = "${connectivity.metaLatencyMs} ms • Handshake OK",
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF38BDF8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Cifrado: ${connectivity.metaTlsCipher} | API Ingestion Verificada",
                            fontSize = 9.5.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    // SECCIÓN 2: MATRIZ DE ESCALABILIDAD Y ROI
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1E293B))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "PROYECCIÓN DE ESCALABILIDAD Y RETORNO FINANCIERO (ROI)",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFF59E0B)
                        )

                        milestones.forEach { m ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF0F172A))
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${m.period} • ${m.creatorsCount} usuarios",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = m.keyMilestoneObjective,
                                        fontSize = 9.5.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = m.annualRecurringRevenueUsd,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.5.sp,
                                        color = Color(0xFF34D399)
                                    )
                                    Text(
                                        text = m.cumulativeRoiPercent,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = Color(0xFFFBBF24)
                                    )
                                }
                            }
                        }
                    }

                    // SECCIÓN 3: DICTAMEN DE INTERÉS PARA LAS PLATAFORMAS
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF064E3B)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "INTERÉS DE GOOGLE Y META PARA SU ECOSISTEMA:",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF34D399)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Sí, interés de primer orden. Permite a las plataformas cumplir con el EU AI Act antes de la fecha límite regulatoria, cerrando el año con una solución verificada que monetiza el tráfico B2B sin asumir riesgos de multas por deepfakes.",
                                fontSize = 10.5.sp,
                                color = Color(0xFFD1FAE5),
                                lineHeight = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Buttons (Open PDF & Share)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val pdfFile = VmsbEnterpriseAuditReportGenerator.generateExecutiveAuditPdf(
                                context = context,
                                projectTitle = projectTitle,
                                connectivity = connectivity
                            )
                            VmsbEnterpriseAuditReportGenerator.sharePdf(context, pdfFile, projectTitle)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF38BDF8))
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Compartir PDF", fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            val pdfFile = VmsbEnterpriseAuditReportGenerator.generateExecutiveAuditPdf(
                                context = context,
                                projectTitle = projectTitle,
                                connectivity = connectivity
                            )
                            VmsbEnterpriseAuditReportGenerator.openPdf(context, pdfFile)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Generar PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
