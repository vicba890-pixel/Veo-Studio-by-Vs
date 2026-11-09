package com.example.ui.components.diagnostic

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.diagnostic.PhaseTestResult
import com.example.data.diagnostic.StressPhaseType
import com.example.data.diagnostic.SystemStressAuditReport
import com.example.data.diagnostic.TestStatus
import com.example.ui.MainViewModel
import java.util.Locale

/**
 * =========================================================================================
 * VMSB© 2026 - DIÁLOGO MODAL: SUITE DE PRUEBAS DE SISTEMA, CONECTIVIDAD, SEGURIDAD,
 * STRESS EXTREMO Y FACTOR DE BALANCE DE CARGA (η)
 * =========================================================================================
 *
 * Interfaz interactiva de alta densidad y fidelidad visual que permite:
 * 1. Ejecutar las 5 fases de auditoría técnica en tiempo real con telemetría en vivo.
 * 2. Visualizar métricas críticas de hardware, latencias cloud y 6 vectores Anti-REA 2026.
 * 3. Emitir, visualizar y exportar el documento PDF certificado y verificado con sello SHA-256.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VmsbSystemStressAuditDialog(
    viewModel: MainViewModel,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current

    val report by viewModel.systemStressAuditReport.collectAsState()
    val isRunning by viewModel.isStressTestingRunning.collectAsState()
    val progress by viewModel.stressTestingProgress.collectAsState()
    val currentPhaseText by viewModel.currentStressPhase.collectAsState()

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.94f)
                .testTag("vmsb_system_stress_audit_dialog"),
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
                        .padding(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // -------------------------------------------------------------
                    // ENCABEZADO MODAL
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
                                            listOf(Color(0xFF0284C7), Color(0xFF10B981))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = "Stress Suite",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "VMSB© Stress & Balance Suite",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Sistema • Conectividad • Anti-REA • Stress • Balance",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismissRequest,
                            modifier = Modifier.testTag("stress_audit_close_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = Color(0xFF94A3B8)
                            )
                        }
                    }

                    // -------------------------------------------------------------
                    // BANNER DE ESTADO GLOBAL Y RESUMEN EJECUTIVO
                    // -------------------------------------------------------------
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        border = BorderStroke(1.dp, Color(0xFF1E293B))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "ESTADO DE CERTIFICACIÓN GLOBAL",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF38BDF8),
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = report?.overallStatus ?: "PENDIENTE DE EJECUCIÓN",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (report != null) Color(0xFF10B981) else Color(0xFFF59E0B)
                                    )
                                }

                                if (report != null) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF10B981).copy(alpha = 0.15f))
                                            .border(1.dp, Color(0xFF10B981), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = "${String.format(Locale.US, "%.1f", report!!.overallScorePercent)}% PASS A+",
                                            color = Color(0xFF10B981),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }

                            if (isRunning) {
                                Spacer(modifier = Modifier.height(12.dp))
                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = Color(0xFF38BDF8),
                                    trackColor = Color(0xFF1E293B)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = currentPhaseText,
                                    fontSize = 11.sp,
                                    color = Color(0xFF38BDF8),
                                    fontWeight = FontWeight.Medium
                                )
                            } else if (report != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Audit ID: ${report!!.auditId} • Sello SHA-256: ${report!!.sha256CertificateSeal.take(16)}...",
                                    fontSize = 10.sp,
                                    color = Color(0xFF64748B),
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    // -------------------------------------------------------------
                    // BOTÓN DE ACCIÓN: EJECUTAR SUITE COMPLETA (5 FASES)
                    // -------------------------------------------------------------
                    Button(
                        onClick = { viewModel.runFullSystemStressAndBalanceSuite() },
                        enabled = !isRunning,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("stress_audit_run_suite_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0284C7),
                            contentColor = Color.White,
                            disabledContainerColor = Color(0xFF1E293B)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isRunning) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Ejecutando Diagnóstico & Benchmarking...")
                        } else {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (report == null) "Ejecutar Suite Completa (5 Fases)" else "Re-ejecutar Pruebas & Calibración",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // -------------------------------------------------------------
                    // LISTADO DE LAS 5 FASES EVALUADAS
                    // -------------------------------------------------------------
                    Text(
                        text = "DIMENSIONES TÉCNICAS AUDITADAS (5/5)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        letterSpacing = 1.sp
                    )

                    val phasesToDisplay = report?.phases ?: getDefaultPendingPhases()

                    phasesToDisplay.forEach { phase ->
                        PhaseAuditCard(phase = phase)
                    }

                    // -------------------------------------------------------------
                    // MARCO JURÍDICO & CONFORMIDAD ENTERPRISE
                    // -------------------------------------------------------------
                    if (report != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0B132B)),
                            border = BorderStroke(1.dp, Color(0xFF1C2D5A))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Gavel,
                                        contentDescription = null,
                                        tint = Color(0xFF60A5FA),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Acreditación & Soberanía Tecnológica",
                                        color = Color(0xFF60A5FA),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = report!!.enterpriseComplianceSummary,
                                    fontSize = 11.sp,
                                    color = Color(0xFFCBD5E1)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Registro Berna: ${report!!.berneConventionRegistration}",
                                    fontSize = 10.sp,
                                    color = Color(0xFF94A3B8),
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }

                // -------------------------------------------------------------
                // BARRA FLOTANTE INFERIOR: GENERAR / ABRIR PDF CERTIFICADO
                // -------------------------------------------------------------
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(),
                    color = Color(0xFF070B14).copy(alpha = 0.95f),
                    border = BorderStroke(1.dp, Color(0xFF1E293B))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val file = viewModel.generateAndOpenSystemStressPdf(context)
                                if (file != null && file.exists()) {
                                    Toast.makeText(
                                        context,
                                        "Certificado PDF generado y disponible en el sistema",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("stress_audit_export_pdf_btn"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF10B981),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Ver PDF Certificado",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                val file = viewModel.exportSystemStressPdfDirect(context)
                                if (file != null && file.exists()) {
                                    viewModel.sharePdfFile(context, file)
                                } else {
                                    Toast.makeText(context, "Generando PDF para compartir...", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .height(46.dp)
                                .testTag("stress_audit_share_pdf_btn"),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF38BDF8)
                            ),
                            border = BorderStroke(1.dp, Color(0xFF0284C7)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Compartir",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PhaseAuditCard(phase: PhaseTestResult) {
    var isExpanded by remember { mutableStateOf(false) }

    val icon = when (phase.phase) {
        StressPhaseType.SYSTEM_PIPELINE -> Icons.Default.Memory
        StressPhaseType.CONNECTIVITY_RESILIENCE -> Icons.Default.Wifi
        StressPhaseType.SECURITY_ANTI_REA_2026 -> Icons.Default.Security
        StressPhaseType.STRESS_CONCURRENCY -> Icons.Default.Whatshot
        StressPhaseType.DISPATCHER_BALANCE -> Icons.Default.Balance
    }

    val statusColor = Color(phase.status.colorHex)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("phase_card_${phase.phase.code}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = BorderStroke(1.dp, Color(0xFF1E293B))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(statusColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = phase.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = phase.primaryMetric,
                            fontSize = 11.sp,
                            color = statusColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(statusColor.copy(alpha = 0.12f))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = phase.status.label,
                            color = statusColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = { isExpanded = !isExpanded },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Expandir detalles",
                            tint = Color(0xFF94A3B8)
                        )
                    }
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    HorizontalDivider(color = Color(0xFF1E293B))

                    // Métricas detalladas en cuadrícula
                    phase.metrics.forEach { metric ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = metric.label,
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                            Text(
                                text = "${metric.value} ${metric.unit}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    if (phase.optimizationsApplied.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Optimizaciones activadas:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF38BDF8)
                        )
                        phase.optimizationsApplied.forEach { opt ->
                            Text(
                                text = "• $opt",
                                fontSize = 10.sp,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getDefaultPendingPhases(): List<PhaseTestResult> {
    return listOf(
        PhaseTestResult(
            phase = StressPhaseType.SYSTEM_PIPELINE,
            title = "Integridad de Arquitectura & Pipeline SIMD",
            status = TestStatus.PENDING,
            scorePercent = 0f,
            executionTimeMs = 0L,
            primaryMetric = "Preparado para verificación de buffers y códecs",
            metrics = emptyList(),
            details = emptyList(),
            optimizationsApplied = emptyList()
        ),
        PhaseTestResult(
            phase = StressPhaseType.CONNECTIVITY_RESILIENCE,
            title = "Conectividad Cloud & Resiliencia",
            status = TestStatus.PENDING,
            scorePercent = 0f,
            executionTimeMs = 0L,
            primaryMetric = "Medición de latencia Vertex AI y conmutación offline",
            metrics = emptyList(),
            details = emptyList(),
            optimizationsApplied = emptyList()
        ),
        PhaseTestResult(
            phase = StressPhaseType.SECURITY_ANTI_REA_2026,
            title = "Seguridad Soberana Anti-REA 2026 (6 Vectores)",
            status = TestStatus.PENDING,
            scorePercent = 0f,
            executionTimeMs = 0L,
            primaryMetric = "Sonda anti-debugger, Frida sentinel y canarios RAM",
            metrics = emptyList(),
            details = emptyList(),
            optimizationsApplied = emptyList()
        ),
        PhaseTestResult(
            phase = StressPhaseType.STRESS_CONCURRENCY,
            title = "Stress Extremo 4K HDR & Concurrencia Multitrack",
            status = TestStatus.PENDING,
            scorePercent = 0f,
            executionTimeMs = 0L,
            primaryMetric = "5,000 iteraciones MCTS y 16 pistas en paralelo",
            metrics = emptyList(),
            details = emptyList(),
            optimizationsApplied = emptyList()
        ),
        PhaseTestResult(
            phase = StressPhaseType.DISPATCHER_BALANCE,
            title = "Balance de Carga & Equilibrio de Dispatchers",
            status = TestStatus.PENDING,
            scorePercent = 0f,
            executionTimeMs = 0L,
            primaryMetric = "Cálculo del factor de balance de carga (η)",
            metrics = emptyList(),
            details = emptyList(),
            optimizationsApplied = emptyList()
        )
    )
}
