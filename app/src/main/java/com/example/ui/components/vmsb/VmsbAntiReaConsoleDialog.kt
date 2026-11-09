package com.example.ui.components.vmsb

import android.content.Context
import android.content.Intent
import androidx.compose.animation.*
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import com.example.data.vmsb.antirea.AntiReaSecurityLevel
import com.example.data.vmsb.antirea.AntiReaState
import com.example.data.vmsb.antirea.AntiReaThreatStatus
import com.example.data.vmsb.antirea.AntiReaThreatVector
import com.example.data.vmsb.antirea.ThreatVectorReport
import kotlinx.coroutines.launch

/**
 * Consola Interactiva de Seguridad y Defensa Activa:
 * VMSB© SISTEMA ANTI-REA-ING NATIVO 2026
 *
 * Interfaz M3 de grado militar con telemetría en tiempo real, matriz de amenazas,
 * simulación de vectores de ataque, zeroización de buffers off-heap y exportación de certificados.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VmsbAntiReaConsoleDialog(
    state: AntiReaState,
    onDismissRequest: () -> Unit,
    onRunDeepScan: () -> Unit,
    onSimulateThreat: (AntiReaThreatVector) -> Unit,
    onZeroizeBuffers: () -> Unit,
    onExportCertificate: suspend (Context) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isExporting by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) } // 0: Matriz de Amenazas, 1: Registro Forense, 2: Canarios & Enclave

    // Animación de pulso para el escudo activo
    val infiniteTransition = rememberInfiniteTransition(label = "ShieldPulse")
    val shieldGlow by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ShieldGlow"
    )

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF10B981).copy(alpha = shieldGlow),
                            Color(0xFF065F46),
                            Color(0xFF022C22)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                ),
            color = Color(0xFF030712) // Slate ultra oscuro
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // ==============================
                // HEADER TECNOLÓGICO MILITAR
                // ==============================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFF10B981).copy(alpha = 0.35f * shieldGlow),
                                            Color(0xFF064E3B).copy(alpha = 0.2f)
                                        )
                                    )
                                )
                                .border(1.dp, Color(0xFF10B981), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Anti-REA Shield",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "VMSB© ANTI-REA-ING 2026",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFFF8FAFC),
                                    letterSpacing = 1.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF065F46))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "NATIVO JNI/C++",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(0xFF34D399)
                                    )
                                }
                            }
                            Text(
                                text = "Anti-Reverse Engineering & Anti-Extraction Architecture",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = Color(0xFFE2E8F0),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ==============================
                // BANNER DE ESTADO Y ATTESTATION SCORE
                // ==============================
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1329)),
                    border = BorderStroke(1.dp, Color(state.securityLevel.badgeColor).copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Círculo de porcentaje de atestación
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF030712))
                                    .border(
                                        width = 3.dp,
                                        color = Color(state.securityLevel.badgeColor),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${state.systemAttestationScore}%",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(state.securityLevel.badgeColor)
                                    )
                                    Text(
                                        text = "SCORE",
                                        fontSize = 7.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = state.securityLevel.label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(state.securityLevel.badgeColor)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Sesión: ${state.activeSessionNonce}",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF64748B)
                                )
                                Text(
                                    text = "Keystream: ${state.ephemeralKeystreamId} • Zeroizado: ${state.canaryBytesWiped} B",
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF38BDF8)
                                )
                            }
                        }

                        // Botón de escaneo rápido
                        FilledTonalButton(
                            onClick = onRunDeepScan,
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color(0xFF10B981).copy(alpha = 0.2f),
                                contentColor = Color(0xFF34D399)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Escanear",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ESCANEAR",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ==============================
                // PESTAÑAS SELECTORAS
                // ==============================
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0F172A))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val tabs = listOf(
                        "MATRIZ DE VECTORES" to Icons.Default.Security,
                        "REGISTRO FORENSE" to Icons.Default.ListAlt,
                        "ENCLAVE & CANARIOS" to Icons.Default.Memory
                    )
                    tabs.forEachIndexed { index, (label, icon) ->
                        val isSelected = selectedTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Color(0xFF1E293B) else Color.Transparent)
                                .clickable { selectedTab = index }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) Color(0xFF34D399) else Color(0xFF64748B),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (isSelected) Color(0xFFF1F5F9) else Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ==============================
                // CONTENIDO DINÁMICO POR PESTAÑA
                // ==============================
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    when (selectedTab) {
                        0 -> ThreatVectorMatrixTab(
                            reports = state.threatVectorReports,
                            onSimulateThreat = onSimulateThreat
                        )
                        1 -> ForensicAuditLogTab(logs = state.recentAuditLog)
                        2 -> EnclaveAndCanariesTab(
                            state = state,
                            onZeroizeBuffers = onZeroizeBuffers
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ==============================
                // BARRA INFERIOR DE ACCIONES CRÍTICAS
                // ==============================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onZeroizeBuffers,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFEF4444)
                        ),
                        border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Zeroizar",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "PURGAR ENCLAVE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isExporting = true
                                onExportCertificate(context)
                                isExporting = false
                            }
                        },
                        modifier = Modifier.weight(1.3f),
                        enabled = !isExporting,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF10B981),
                            contentColor = Color(0xFF022C22)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        if (isExporting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color(0xFF022C22),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Exportar",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "COMPARTIR CERTIFICADO",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ThreatVectorMatrixTab(
    reports: List<ThreatVectorReport>,
    onSimulateThreat: (AntiReaThreatVector) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(reports) { report ->
            ThreatVectorItemCard(report = report, onSimulate = { onSimulateThreat(report.vector) })
        }
    }
}

@Composable
private fun ThreatVectorItemCard(
    report: ThreatVectorReport,
    onSimulate: () -> Unit
) {
    val statusColor = when (report.status) {
        AntiReaThreatStatus.SECURE -> Color(0xFF10B981)
        AntiReaThreatStatus.ANOMALY_DETECTED -> Color(0xFFEF4444)
        AntiReaThreatStatus.THREAT_BLOCKED -> Color(0xFF38BDF8)
        AntiReaThreatStatus.SIMULATION_VERIFIED -> Color(0xFFA855F7)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B132B)),
        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                    Text(
                        text = report.vector.displayName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF1F5F9)
                    )
                    Text(
                        text = "[${report.vector.code}]",
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF64748B)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .border(1.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${report.status.label} • ${report.latencyMicros}µs",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = report.details,
                fontSize = 11.sp,
                color = Color(0xFFCBD5E1),
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Mitigación: ${report.mitigationAction}",
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF94A3B8)
                )

                TextButton(
                    onClick = onSimulate,
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                    modifier = Modifier.height(24.dp)
                ) {
                    Text(
                        text = "SIMULAR TEST",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFFA855F7)
                    )
                }
            }
        }
    }
}

@Composable
private fun ForensicAuditLogTab(logs: List<String>) {
    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF030712)),
        border = BorderStroke(1.dp, Color(0xFF1E293B))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF10B981)))
                Text(
                    text = "REGISTRO DETERMINISTA DE AUDITORÍA (INMUTABLE)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF10B981)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (logs.isEmpty()) {
                    item {
                        Text(
                            text = "No hay eventos anómalos registrados. Todas las sondas nominales.",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF64748B)
                        )
                    }
                } else {
                    items(logs) { log ->
                        Text(
                            text = log,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = if (log.contains("Alerta") || log.contains("SIMULACIÓN")) Color(0xFFFBBF24) else Color(0xFF94A3B8),
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EnclaveAndCanariesTab(
    state: AntiReaState,
    onZeroizeBuffers: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0B132B)),
                border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "ENCLAVE DE MEMORIA DIRECTA ZERO-COPY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF38BDF8)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Los fotogramas VMSB y pesos neuronales se procesan en memoria nativa off-heap fuera del recolector de basura (Zero GC). Canarios criptográficos de 64 bytes protegen los límites contra herramientas de volcado de memoria (Memory Dumps / Heap Scrapers).",
                        fontSize = 11.sp,
                        color = Color(0xFFCBD5E1),
                        lineHeight = 16.sp
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "PARÁMETROS CRIPTOGRÁFICOS ACTIVOS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF94A3B8)
                    )
                    InfoRow("Patrón Canario Off-Heap:", "0x56 0x4D 0x53 0x42 0xDE 0xAD 0xBE 0xEF")
                    InfoRow("Keystream Polimórfico:", state.ephemeralKeystreamId)
                    InfoRow("Sesión Activa:", state.activeSessionNonce)
                    InfoRow("Bytes Purgados en Caliente:", "${state.canaryBytesWiped} Bytes")
                    InfoRow("Nivel de Arquitectura:", state.defenseTier)
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 10.sp, color = Color(0xFF64748B), fontFamily = FontFamily.Monospace)
        Text(text = value, fontSize = 10.sp, color = Color(0xFF38BDF8), fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
    }
}
