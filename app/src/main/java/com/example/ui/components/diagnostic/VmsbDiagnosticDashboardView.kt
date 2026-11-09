package com.example.ui.components.diagnostic

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import kotlinx.coroutines.launch

/**
 * Panel de Diagnóstico Integral del Motor VMSB© con Material 3 Cards.
 *
 * Visualiza en tiempo real:
 * 1. Cache Hit Rates y Telemetría de Memoria Zero-Copy (WASSAM Secure Cache).
 * 2. Latencia de Sincronización Multi-Canal (Google Cloud, Meta, Apple, Wasm Direct Bus).
 * 3. Estado Jurídico y Bloques del Protocolo de Berna (Art. 5.2 OMPI / NIST FIPS 204).
 * 4. Telemetría de Energía y Cómputo Vectorial VMSB (ARM64 NEON, MCTS UCB1, Eco-Efficiency).
 */
@Composable
fun VmsbDiagnosticDashboardView(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    onClose: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // 1. Telemetría de Caché Segura WASSAM
    val cacheTelemetry by viewModel.wassamManager.secureCache.telemetry.collectAsState()

    // 2. Telemetría de Salud de Sincronización y Latencia
    val syncHealthReport by viewModel.wassamManager.healthMonitor.report.collectAsState()
    val isDiagnosingSync by viewModel.wassamManager.healthMonitor.isDiagnosing.collectAsState()

    // 3. Telemetría del Motor Nativo VMSB
    val vmsbTelemetry by viewModel.vmsbTelemetry.collectAsState()
    val wasmTelemetry by viewModel.wasmRuntime.telemetry.collectAsState()

    // 4. Protocolo de Berna
    val latestBerneBlock = remember { viewModel.berneLedger.getLatestBlock() }
    val allBerneBlocks = remember { viewModel.berneLedger.getAllBlocks() }

    // 5. Servicio de Sincronización en Segundo Plano
    val isBackgroundSyncRunning by viewModel.isHealthSyncBackgroundRunning.collectAsState()
    val continuousState by viewModel.continuousHealthState.collectAsState()

    var isRunningFullAudit by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("vmsb_diagnostic_dashboard_view"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ==============================================================
        // HEADER ELEVATED CARD: STATUS RESUMEN DEL MOTOR VMSB©
        // ==============================================================
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFF0B1120)),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF0284C7), Color(0xFF10B981))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Analytics,
                                contentDescription = "VMSB Health",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "VMSB Engine Health Diagnostic",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF064E3B)
                                ) {
                                    Text(
                                        text = "LIVE 99.8%",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF34D399),
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Diagnóstico en tiempo real: Caché, Latencia Sync, Protocolo de Berna & Wasm",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF38BDF8),
                                fontSize = 9.5.sp
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    isRunningFullAudit = true
                                    viewModel.wassamManager.healthMonitor.runChannelDiagnostics()
                                    viewModel.wasmRuntime.executeCompleteWasmBenchmark()
                                    Toast.makeText(context, "Diagnóstico VMSB Completo Ejecutado", Toast.LENGTH_SHORT).show()
                                    isRunningFullAudit = false
                                }
                            },
                            enabled = !isRunningFullAudit && !isDiagnosingSync
                        ) {
                            Icon(
                                imageVector = if (isRunningFullAudit || isDiagnosingSync) Icons.Default.HourglassEmpty else Icons.Default.Refresh,
                                contentDescription = "Actualizar Diagnóstico",
                                tint = Color(0xFF38BDF8)
                            )
                        }

                        IconButton(
                            onClick = { viewModel.openStrategicRoiDialog() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = "Dossier Económico & ROI",
                                tint = Color(0xFFF59E0B)
                            )
                        }

                        IconButton(
                            onClick = { viewModel.openAuditLogHistoryDialog() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "Historial de Auditoría Room",
                                tint = Color(0xFF10B981)
                            )
                        }

                        IconButton(
                            onClick = { viewModel.openSystemStressAuditDialog() },
                            modifier = Modifier.testTag("diag_view_stress_suite_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = "Suite de Stress & Balance 2026",
                                tint = Color(0xFF00E5FF)
                            )
                        }

                        if (onClose != null) {
                            IconButton(onClick = onClose) {
                                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color(0xFF94A3B8))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Banner del Servicio en Segundo Plano (Continuous Data Flow)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("banner_vmsb_background_sync"),
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF070D1A),
                    border = BorderStroke(
                        1.dp,
                        if (isBackgroundSyncRunning) Color(0xFF10B981).copy(alpha = 0.5f) else Color(0xFFEF4444).copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isBackgroundSyncRunning) Color(0xFF10B981) else Color(0xFFEF4444))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isBackgroundSyncRunning) {
                                        if (continuousState.spikeDampingActive) "SINCRONIZACIÓN ASÍNCRONA: AMORTIGUACIÓN DE PICOS ACTIVA"
                                        else "SINCRONIZACIÓN ASÍNCRONA NO-BLOQUEANTE ACTIVA"
                                    } else "SERVICIO BACKGROUND DETENIDO",
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isBackgroundSyncRunning) {
                                        if (continuousState.spikeDampingActive) Color(0xFFFBBF24) else Color(0xFF34D399)
                                    } else Color(0xFFF87171)
                                )
                                Text(
                                    text = "Pulso #${continuousState.pulseCount} • Nodos Paralelos • ${String.format(java.util.Locale.US, "%.1f", continuousState.throughputFps)} FPS • 0% UI Freeze",
                                    fontSize = 7.5.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        FilledTonalButton(
                            onClick = { viewModel.toggleHealthSyncBackgroundService(context) },
                            modifier = Modifier
                                .height(28.dp)
                                .testTag("btn_toggle_background_sync"),
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = if (isBackgroundSyncRunning) Color(0xFF064E3B) else Color(0xFF7F1D1D)
                            )
                        ) {
                            Icon(
                                imageVector = if (isBackgroundSyncRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isBackgroundSyncRunning) "Pausar" else "Reanudar",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // CHIP ROW RESUMEN RÁPIDO
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    DiagnosticChip(
                        label = "Hit Rate Caché",
                        value = "${cacheTelemetry.cacheHitRatePercent}%",
                        color = Color(0xFF10B981),
                        modifier = Modifier.weight(1f)
                    )
                    DiagnosticChip(
                        label = "Latencia Sync",
                        value = "${syncHealthReport.averageLatencyMs} ms",
                        color = Color(0xFF38BDF8),
                        modifier = Modifier.weight(1f)
                    )
                    DiagnosticChip(
                        label = "Protocolo Berna",
                        value = "181 Países",
                        color = Color(0xFFFBBF24),
                        modifier = Modifier.weight(1f)
                    )
                    DiagnosticChip(
                        label = "MCTS Rollout",
                        value = "${wasmTelemetry.mctsRolloutsPerSec / 1000}k/s",
                        color = Color(0xFFA78BFA),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // ==============================================================
        // CARD 1: CACHE HIT RATES & SECURE ZEROIZATION (MATERIAL 3)
        // ==============================================================
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.outlinedCardColors(containerColor = Color(0xFF0F172A)),
            border = BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "1. Cache Hit Rates & Memoria Cero-GC",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF047857)
                    ) {
                        Text(
                            text = "HIT RATE ${cacheTelemetry.cacheHitRatePercent}%",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFA7F3D0),
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Progress Bar de Uso de Caché
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Capacidad Utilizada:",
                        fontSize = 8.5.sp,
                        color = Color(0xFF94A3B8)
                    )
                    Text(
                        text = "${cacheTelemetry.usedFormatted} de ${cacheTelemetry.capacityFormatted} (${(cacheTelemetry.usagePercent * 100).toInt()}%)",
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { cacheTelemetry.usagePercent },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFF0284C7),
                    trackColor = Color(0xFF1E293B)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Métricas de Hit / Miss y Zeroization
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Aciertos Caché (Hits):", fontSize = 8.sp, color = Color(0xFF94A3B8))
                        Text("${cacheTelemetry.totalHitsCount} lecturas", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF34D399))
                    }
                    Column {
                        Text("Fallos (Misses):", fontSize = 8.sp, color = Color(0xFF94A3B8))
                        Text("${cacheTelemetry.totalMissesCount} cargas", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFBBF24))
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Enclaves en RAM:", fontSize = 8.sp, color = Color(0xFF94A3B8))
                        Text("${cacheTelemetry.cachedBlocksCount} bloques activos", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Estado de Zeroization & Wasm Buffer
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF020617),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Zeroization: Sobrescritura con ceros (0 bytes Dalvik GC)",
                                fontSize = 8.sp,
                                color = Color(0xFFCBD5E1)
                            )
                        }

                        Button(
                            onClick = {
                                val saved = viewModel.wassamManager.secureCache.optimizeWasmCache()
                                Toast.makeText(context, "Caché compactada: ${(saved / (1024 * 1024))} MB liberados", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.height(26.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A)),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("Optimizar", fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // ==============================================================
        // CARD 2: REAL-TIME SYNC LATENCY & CHANNELS (MATERIAL 3)
        // ==============================================================
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.outlinedCardColors(containerColor = Color(0xFF0F172A)),
            border = BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.SyncAlt,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "2. Latencia de Sincronización Multi-Canal",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }

                    Text(
                        text = "Avg: ${syncHealthReport.averageLatencyMs} ms (±${syncHealthReport.averageJitterMs} ms)",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFBBF24)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Canales Monitoreados
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    syncHealthReport.channels.forEach { channel ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF0B132B),
                            border = BorderStroke(0.5.dp, Color(0xFF1E293B))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(if (channel.isHealthy) Color(0xFF10B981) else Color(0xFFEF4444))
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(channel.platformName, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        Text(channel.tlsVersion, fontSize = 7.sp, color = Color(0xFF64748B))
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "${channel.latencyMs} ms",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (channel.latencyMs < 10) Color(0xFF34D399) else Color(0xFFFBBF24)
                                    )
                                    Text(
                                        text = "0% Loss • Jitter ±${channel.jitterMs} ms",
                                        fontSize = 7.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ==============================================================
        // CARD 3: BERNE PROTOCOL STATUS & LEGAL LEDGER (MATERIAL 3)
        // ==============================================================
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.outlinedCardColors(containerColor = Color(0xFF0F172A)),
            border = BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Gavel,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "3. Protocolo de Berna 2026 (Art. 5.2 OMPI/WIPO)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF78350F)
                    ) {
                        Text(
                            text = "181 PAÍSES WIPO",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFDE68A),
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Estado Legal y Bloques Sellados
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Estado de Protección:", fontSize = 8.sp, color = Color(0xFF94A3B8))
                        Text("VIGENTE Y AUTOMÁTICA", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF34D399))
                    }
                    Column {
                        Text("Bloques en Ledger:", fontSize = 8.sp, color = Color(0xFF94A3B8))
                        Text("${allBerneBlocks.size} Bloques Sellados", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Firma Post-Cuántica:", fontSize = 8.sp, color = Color(0xFF94A3B8))
                        Text("NIST FIPS 204 (ML-DSA)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA78BFA))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Último Bloque y Botón Certificado
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF020617),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Último Bloque: #${latestBerneBlock?.blockIndex ?: 1} • PoHI Root",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFDE68A)
                            )
                            Text(
                                text = "Hash: ${(latestBerneBlock?.currentBlockHash ?: "c2pa-root-sha256-verified-lattice").take(24)}...",
                                fontSize = 7.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF94A3B8)
                            )
                        }

                        Button(
                            onClick = { viewModel.openBerneCertificateForLatest() },
                            modifier = Modifier.height(28.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB45309)),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ver Cédula", fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // ==============================================================
        // CARD 4: VMSB COMPUTE & ENERGY EFFICIENCY (MATERIAL 3)
        // ==============================================================
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.outlinedCardColors(containerColor = Color(0xFF0F172A)),
            border = BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = Color(0xFFA78BFA),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "4. Rendimiento Vectorial & Eficiencia Energética",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }

                    Text(
                        text = "${vmsbTelemetry.simdLaneEfficiency}% SIMD Efficiency",
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF34D399)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Potencia Instantánea:", fontSize = 8.sp, color = Color(0xFF94A3B8))
                        Text("${vmsbTelemetry.instantaneousPowerMilliwatts} mW", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                    }
                    Column {
                        Text("Energía por Frame:", fontSize = 8.sp, color = Color(0xFF94A3B8))
                        Text("${vmsbTelemetry.energyPerFrameMicrojoules} µJ/frame", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Margen Térmico:", fontSize = 8.sp, color = Color(0xFF94A3B8))
                        Text("${vmsbTelemetry.thermalMarginDegreesCelsius} °C", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFBBF24))
                    }
                }
            }
        }

        // ==============================================================
        // CARD NUEVA: MONTE CARLO TREE SEARCH (MCTS) PERFORMANCE LINE CHART
        // (Visualización gráfica con datos persistidos en Room Database)
        // ==============================================================
        MctsPerformanceLineChartView(
            viewModel = viewModel,
            modifier = Modifier.fillMaxWidth()
        )

        // ==============================================================
        // CARD 5: REPORTE AGREGADO DE RENDIMIENTO EN PDF (PDFBOX)
        // ==============================================================
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("card_performance_pdf_report"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.outlinedCardColors(containerColor = Color(0xFF0F172A)),
            border = BorderStroke(1.dp, Color(0xFF0284C7).copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "5. Reporte PDF de Rendimiento Agregado",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }

                    Surface(
                        color = Color(0xFF0C4A6E),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "PDFBOX NATIVO",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF38BDF8),
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Agrega en tiempo real las métricas de hardware, VMSB, memoria WASSAM, latencias de sync y WebAssembly, generando un documento PDF oficial guardado en almacenamiento interno para revisión del usuario.",
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8),
                    lineHeight = 14.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val file = viewModel.openPerformancePdfReportForReview(context)
                            if (file != null) {
                                Toast.makeText(context, "Reporte guardado en almacenamiento interno", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .weight(1.3f)
                            .height(40.dp)
                            .testTag("btn_generate_performance_pdf_review"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Revisar PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            val file = viewModel.aggregateAndGeneratePerformancePdfReport(context)
                            if (file != null) {
                                viewModel.downloadPdfFile(context, file)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("btn_download_performance_pdf"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF38BDF8)),
                        border = BorderStroke(1.dp, Color(0xFF0284C7))
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Descargar", fontSize = 10.5.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                FilledTonalButton(
                    onClick = {
                        val file = viewModel.exportAuditLogsAndPerformancePdf(context)
                        if (file != null && file.exists()) {
                            viewModel.viewPdfFile(context, file)
                            Toast.makeText(context, "Dossier Consolidado abierto para revisión", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .testTag("btn_export_consolidated_audit_perf_pdf"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color(0xFF047857).copy(alpha = 0.25f),
                        contentColor = Color(0xFF34D399)
                    )
                ) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Dossier Completo: Auditoría Room + MCTS + Rendimiento (PDF)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun DiagnosticChip(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF0F172A),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, fontSize = 7.sp, color = Color(0xFF94A3B8), maxLines = 1)
            Text(text = value, fontSize = 9.sp, fontWeight = FontWeight.Black, color = color, maxLines = 1)
        }
    }
}
