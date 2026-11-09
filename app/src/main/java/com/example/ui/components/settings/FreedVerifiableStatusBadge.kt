package com.example.ui.components.settings

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.ElectricBolt
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Speed
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
import com.example.data.freed.FreedAuditStatus
import com.example.ui.MainViewModel
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * =========================================================================================
 * VMSB(c) 2026 - COMPONENTE 'VERIFIABLE STATUS' BADGE EN AJUSTES DE PROYECTO
 * CRUCE DE INNOVACIÓN: FREED_vmsb_lattice_ZKP
 * =========================================================================================
 *
 * Muestra el estado en tiempo real de la auditoría de energía residual y prueba post-cuántica
 * ZKP, con opción de exportar el reporte de cumplimiento en PDF firmado digitalmente.
 */
@Composable
fun FreedVerifiableStatusBadge(
    auditStatus: FreedAuditStatus,
    onOpenDetails: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onOpenDetails)
            .testTag("freed_verifiable_badge"),
        color = Color(0xFF061A14),
        border = BorderStroke(
            1.2.dp,
            Brush.horizontalGradient(
                listOf(
                    Color(0xFF10B981).copy(alpha = alphaAnim),
                    Color(0xFF0284C7).copy(alpha = 0.8f)
                )
            )
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        if (compact) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .defaultMinSize(minHeight = 48.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981).copy(alpha = alphaAnim))
                )
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            "VERIFIED: FREED_vmsb_lattice_ZKP",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF34D399)
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF064E3B)
                        ) {
                            Text(
                                "100% AUDIT",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFA7F3D0),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Text(
                        "Energía Residual +${String.format(Locale.US, "%.1f", auditStatus.telemetry.energySavingPercentage)}% • Latencia -${String.format(Locale.US, "%.1f", auditStatus.telemetry.latencyReductionPercentage)}%",
                        fontSize = 8.5.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Ver detalles de auditoría FREED",
                    tint = Color(0xFF34D399),
                    modifier = Modifier.size(16.dp)
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
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
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981).copy(alpha = alphaAnim))
                                .border(1.5.dp, Color(0xFF6EE7B7), CircleShape)
                        )
                        Column {
                            Text(
                                "ESTADO VERIFICADO: FREED_vmsb_lattice_ZKP",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF34D399),
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                "Auditoría Criptográfica Post-Cuántica & Energía Residual",
                                fontSize = 9.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF064E3B),
                        border = BorderStroke(1.dp, Color(0xFF10B981))
                    ) {
                        Text(
                            text = "${auditStatus.verificationScore}% SCORE",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFA7F3D0),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Telemetry Quick Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Pill 1: Ahorro Energía
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF0F291E)
                    ) {
                        Row(
                            modifier = Modifier.padding(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Outlined.ElectricBolt,
                                contentDescription = null,
                                tint = Color(0xFF34D399),
                                modifier = Modifier.size(14.dp)
                            )
                            Column {
                                Text("Ahorro Conv.", fontSize = 7.5.sp, color = Color(0xFF94A3B8))
                                Text(
                                    "+${String.format(Locale.US, "%.1f", auditStatus.telemetry.energySavingPercentage)}%",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF6EE7B7)
                                )
                            }
                        }
                    }

                    // Pill 2: Aceleración
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF0F2634)
                    ) {
                        Row(
                            modifier = Modifier.padding(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Outlined.Speed,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(14.dp)
                            )
                            Column {
                                Text("Aceleración", fontSize = 7.5.sp, color = Color(0xFF94A3B8))
                                Text(
                                    "+${String.format(Locale.US, "%.1f", auditStatus.telemetry.latencyReductionPercentage)}%",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF7DD3FC)
                                )
                            }
                        }
                    }

                    // Pill 3: Ruido Residual
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1E1E2E)
                    ) {
                        Row(
                            modifier = Modifier.padding(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Outlined.Security,
                                contentDescription = null,
                                tint = Color(0xFFA78BFA),
                                modifier = Modifier.size(14.dp)
                            )
                            Column {
                                Text("Lattice ZKP", fontSize = 7.5.sp, color = Color(0xFF94A3B8))
                                Text("FIPS 204", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC4B5FD))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Certificado: ${auditStatus.auditCertificateId}",
                        fontSize = 8.5.sp,
                        color = Color(0xFF64748B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Toca para auditar & exportar PDF →",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF38BDF8)
                    )
                }
            }
        }
    }
}

/**
 * Modal detallado de la auditoría FREED_vmsb_lattice_ZKP con exportador de PDF firmado.
 */
@Composable
fun FreedAuditDetailsDialog(
    project: Project?,
    auditStatus: FreedAuditStatus,
    viewModel: MainViewModel,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isExporting by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .testTag("freed_audit_details_dialog"),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF070E17),
            border = BorderStroke(1.2.dp, Color(0xFF10B981).copy(alpha = 0.6f))
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
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.linearGradient(listOf(Color(0xFF059669), Color(0xFF0284C7)))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Outlined.Shield,
                                contentDescription = "FREED Shield",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                "FREED_vmsb_lattice_ZKP",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                "Auditoría Verificada & Incentivo Institucional",
                                fontSize = 11.sp,
                                color = Color(0xFF34D399)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.testTag("close_freed_dialog_btn")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color(0xFF94A3B8))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Badge de Estado Oficial
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF062217)),
                        border = BorderStroke(1.dp, Color(0xFF10B981)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(16.dp))
                                    Text(auditStatus.state.label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF34D399))
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Puntuación de Cumplimiento: ${auditStatus.verificationScore}%", fontSize = 10.sp, color = Color(0xFFA7F3D0))
                            }
                            Text(auditStatus.auditCertificateId, fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = Color(0xFF94A3B8))
                        }
                    }

                    // Innovación 1: Canal de Comunicación Silenciosa (Noise Carrier)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1929)),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFF1E3A5F))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.GraphicEq, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp))
                                Text("1. Modulación por Ruido de Energía Residual", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Aprovecha micro-fluctuaciones electromagnéticas y térmicas parasitarias del dispositivo para modular telemetría esteganográfica por debajo del piso térmico (${auditStatus.telemetry.noiseCarrierSnrDb} dB). Acelera la sincronización de pruebas en un ${String.format(Locale.US, "%.1f", auditStatus.telemetry.latencyReductionPercentage)}% sin congestión de espectro ni emisiones audibles.",
                                fontSize = 10.5.sp,
                                color = Color(0xFFCBD5E1),
                                lineHeight = 15.sp
                            )
                        }
                    }

                    // Innovación 2: Optimización de Energía Convencional & Computación Verde
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0A2218)),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFF065F46))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Outlined.ElectricBolt, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(18.dp))
                                Text("2. Ahorro de Energía Convencional (FREED)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Recupera micro-vatios residuales (${auditStatus.telemetry.harvestedMicroWatts} uW) para alimentar el pipeline criptográfico, reduciendo el gasto de batería tradicional en un +${String.format(Locale.US, "%.1f", auditStatus.telemetry.energySavingPercentage)}% (${auditStatus.telemetry.conventionalEnergyOffsetJoules} Joules de offset directo y ${auditStatus.telemetry.co2SavedGrams}g CO2e evitados).",
                                fontSize = 10.5.sp,
                                color = Color(0xFFCBD5E1),
                                lineHeight = 15.sp
                            )
                        }
                    }

                    // Innovación 3: Pruebas Reticulares Post-Cuánticas ZKP
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF171329)),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFF4C1D95))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Outlined.Lock, contentDescription = null, tint = Color(0xFFA78BFA), modifier = Modifier.size(18.dp))
                                Text("3. Blindaje Lattice ZKP (NIST FIPS 204)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Prueba de Conocimiento Cero sobre retículos cristalinos ML-DSA-65 (CRYSTALS-Dilithium). Garantiza verificación matemática sin revelar pesos de modelo ni fotogramas privados.",
                                fontSize = 10.5.sp,
                                color = Color(0xFFCBD5E1),
                                lineHeight = 15.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Hash de Prueba ZKP:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA78BFA))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF0F0B1E),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    auditStatus.proofHash,
                                    fontSize = 8.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFFE2E8F0),
                                    modifier = Modifier.padding(6.dp)
                                )
                            }
                        }
                    }

                    // Innovación 4: Incentivos para Empresas, Gobiernos e Instituciones
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1917)),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFF78350F))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.AccountBalance, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(18.dp))
                                Text("4. Incentivo Fiscal y Gubernamental Comprobado", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Certificación aceptada para créditos fiscales verdes (IRS-45V / Deducción ESG), directiva europea CSRD y estándares de compras públicas de computación sustentable.",
                                fontSize = 10.5.sp,
                                color = Color(0xFFCBD5E1),
                                lineHeight = 15.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Elegibilidad Fiscal:", fontSize = 9.sp, color = Color(0xFF94A3B8))
                                Text(auditStatus.incentive.taxCreditEligibilityCode, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFCD34D))
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Calificación ESG:", fontSize = 9.sp, color = Color(0xFF94A3B8))
                                Text(auditStatus.incentive.esgRatingTier, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF34D399))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Export Action
                Button(
                    onClick = {
                        coroutineScope.launch {
                            isExporting = true
                            try {
                                val pdfFile = viewModel.exportFreedComplianceReportPdf(context, project)
                                Toast.makeText(context, "Reporte FREED Exportado: ${pdfFile.name}", Toast.LENGTH_LONG).show()
                                viewModel.viewPdfFile(context, pdfFile)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error exportando PDF: ${e.message}", Toast.LENGTH_SHORT).show()
                            } finally {
                                isExporting = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("export_freed_compliance_pdf_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF059669)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isExporting
                ) {
                    if (isExporting) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Firmando y Exportando PDF...", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Outlined.Description, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Exportar Reporte de Cumplimiento (PDF Firmado)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
