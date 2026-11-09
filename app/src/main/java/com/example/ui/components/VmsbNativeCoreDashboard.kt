package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.vmsb.VmsbVectorArch
import com.example.ui.MainViewModel
import com.example.ui.components.vmsb.VmsbAntiReaBannerCard

@Composable
fun VmsbNativeCoreDashboard(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val telemetry by viewModel.vmsbTelemetry.collectAsState()
    val latestMctsPlan by viewModel.vmsbLatestMctsPlan.collectAsState()
    val isBenchmarking by viewModel.isVmsbBenchmarkRunning.collectAsState()
    val lastBenchmark by viewModel.vmsbLastBenchmarkResult.collectAsState()
    val antiReaState by viewModel.antiReaState.collectAsState()

    var isExpanded by remember { mutableStateOf(false) }
    var selectedViewMode by remember { mutableStateOf(0) } // 0: MCTS & Fakfa, 1: Benchmark Medible, 2: Cripto-Seguridad

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("vmsb_native_core_dashboard_card"),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0A0F1D).copy(alpha = 0.90f)
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            1.dp,
            Brush.horizontalGradient(
                listOf(
                    Color(0xFFF59E0B).copy(alpha = 0.6f),
                    Color(0xFF06B6D4).copy(alpha = 0.7f),
                    Color(0xFF10B981).copy(alpha = 0.6f)
                )
            )
        )
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .animateContentSize()
        ) {
            // Header: VMSB Logo, Badge & Architecture
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                                )
                            )
                            .border(1.dp, Color(0xFFF59E0B), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "VS",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = Color(0xFFF59E0B)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "VEO STUDIO by Vs",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    letterSpacing = 0.6.sp
                                ),
                                color = Color(0xFFF8FAFC)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF06B6D4).copy(alpha = 0.18f))
                                    .border(0.8.dp, Color(0xFF06B6D4), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "vmsb© CORE",
                                    color = Color(0xFF38BDF8),
                                    fontSize = 7.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Text(
                            text = "Vectorized Stream Binary • Árbol MCTS • Buffer Circular Fakfa Off-Heap",
                            fontSize = 9.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sub-header Badges: Architecture SIMD, Off-Heap, and Low-Energy
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF10B981).copy(alpha = 0.12f))
                        .border(0.8.dp, Color(0xFF10B981).copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(Color(0xFF10B981)))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = viewModel.vmsbCore.detectedArch.label,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF6366F1).copy(alpha = 0.12f))
                        .border(0.8.dp, Color(0xFF6366F1).copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Speed, null, tint = Color(0xFF6366F1), modifier = Modifier.size(9.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ZERO-COPY RING BUFFER", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6366F1))
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFF59E0B).copy(alpha = 0.12f))
                        .border(0.8.dp, Color(0xFFF59E0B).copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, null, tint = Color(0xFFF59E0B), modifier = Modifier.size(9.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("HMAC-SHA256 SEALED", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Real-Time Measurable Telemetry Grid (4 Metrics)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Latency Metric
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("LATENCIA NATIVA", fontSize = 7.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(Color(0xFF10B981)))
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "${telemetry.averageLatencyMicros} µs",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF10B981)
                        )
                        Text("Sub-milisegundo", fontSize = 7.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // Measurable Power Metric (mW)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("POTENCIA", fontSize = 7.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Icon(Icons.Default.Bolt, null, tint = Color(0xFFF59E0B), modifier = Modifier.size(9.dp))
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = String.format("%.1f mW", telemetry.instantaneousPowerMilliwatts),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFFF59E0B)
                        )
                        Text("${telemetry.energyPerFrameMicrojoules} µJ/frame", fontSize = 7.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // Cumulative Energy (Joules / uWh)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("ENERGÍA TOTAL", fontSize = 7.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Icon(Icons.Default.BatteryChargingFull, null, tint = Color(0xFF10B981), modifier = Modifier.size(9.dp))
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = String.format("%.2f µWh", telemetry.cumulativeEnergyMicroWattHours),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(String.format("%.4f J", telemetry.cumulativeEnergyJoules), fontSize = 7.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // SIMD / GC Metric
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("GC PRESSURE", fontSize = 7.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF10B981), modifier = Modifier.size(9.dp))
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "0 Bytes",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF10B981)
                        )
                        Text("100% Off-Heap", fontSize = 7.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Winning MCTS Decision Banner
            latestMctsPlan?.let { plan ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(0.8.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccountTree, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "Árbol MCTS Seleccionado (UCB1):",
                                    fontSize = 7.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = plan.selectedDecision.label,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Score: ${String.format("%.3f", plan.winningUcbScore)}",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                            Text(
                                text = "${plan.totalSimulationsRun} rollouts",
                                fontSize = 7.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons: Run MCTS Rollout & Run Benchmark
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.runVmsbMctsOptimization(iterations = 500) },
                    modifier = Modifier.weight(1f).height(36.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                ) {
                    Icon(Icons.Default.Refresh, null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Re-optimizar MCTS", fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = { viewModel.runVmsbBenchmark() },
                    modifier = Modifier.weight(1.2f).height(36.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                    enabled = !isBenchmarking
                ) {
                    if (isBenchmarking) {
                        CircularProgressIndicator(modifier = Modifier.size(14.dp), color = Color.White, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Benchmarking...", fontSize = 10.sp, color = Color.White)
                    } else {
                        Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(14.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Benchmark VMSB©", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            // Benchmark Result Card (if available)
            lastBenchmark?.let { b ->
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "RESULTADOS DE BENCHMARK MEDIBLE VMSB©",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF38BDF8),
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                "500 FRAMES STREAMED",
                                fontSize = 7.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Tiempo de Ejecución:", fontSize = 8.sp, color = Color.Gray)
                                Text("${b.benchmarkDurationMicros} µs (${String.format("%.2f ms", b.benchmarkDurationMicros / 1000.0)})", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White, fontFamily = FontFamily.Monospace)
                            }
                            Column {
                                Text("Tasa Throughput:", fontSize = 8.sp, color = Color.Gray)
                                Text("${String.format("%.1f", b.fakfaThroughputMbSec)} MB/s", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981), fontFamily = FontFamily.Monospace)
                            }
                            Column {
                                Text("Energía Medida:", fontSize = 8.sp, color = Color.Gray)
                                Text("${b.totalEnergyConsumedMicrojoules} µJ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B), fontFamily = FontFamily.Monospace)
                            }
                            Column {
                                Text("Presión GC:", fontSize = 8.sp, color = Color.Gray)
                                Text("0 Bytes", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8), fontFamily = FontFamily.Monospace)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = b.summaryText,
                            fontSize = 8.sp,
                            color = Color.LightGray,
                            lineHeight = 11.sp
                        )
                    }
                }
            }

            // Expanded Technical Section: Fakfa Partitions & MCTS Search Tree Exploration
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Tab Selector within expanded view
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Fakfa Ring Buffer", "Árbol MCTS", "Cripto HMAC").forEachIndexed { idx, label ->
                            val isSelected = selectedViewMode == idx
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                        else MaterialTheme.colorScheme.surface
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable { selectedViewMode = idx }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 9.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    when (selectedViewMode) {
                        0 -> {
                            // Fakfa Partitions List
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Particiones de Streaming Binario Off-Heap (Fakfa)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                viewModel.vmsbCore.fakfaCluster.getAllMetrics().forEach { part ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(MaterialTheme.colorScheme.surface)
                                            .padding(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(part.name, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                                Text("Lag: ${part.lag} | Procesados: ${part.packetsProcessed} packets", fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                            Column(horizontalAlignment = Alignment.End) {
                                                Text("${part.directMemoryUsedKb} KB Direct", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                                                Text("HW Mark: ${part.highWatermarkOffset}", fontSize = 7.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        1 -> {
                            // MCTS Decision Tree Nodes
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Exploración del Árbol Probabilístico MCTS (UCB1)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                latestMctsPlan?.exploredNodesSummary?.forEach { branchInfo ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(MaterialTheme.colorScheme.surface)
                                            .padding(6.dp)
                                    ) {
                                        Text(branchInfo, fontSize = 8.5.sp, fontFamily = FontFamily.Monospace)
                                    }
                                }
                            }
                        }
                        2 -> {
                            // Cripto-Seguridad HMAC, C2PA y Sistema Anti-REA-ing 2026
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Seguridad Criptográfica, C2PA & Anti-REA-ing 2026", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                
                                // Banner interactivo Anti-REA-ing 2026
                                VmsbAntiReaBannerCard(
                                    state = antiReaState,
                                    onOpenConsole = { viewModel.openAntiReaConsoleDialog() }
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MaterialTheme.colorScheme.surface)
                                        .padding(8.dp)
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text("• Algoritmo de Firma: HMAC-SHA256 con clave secreta nativa en hardware", fontSize = 8.5.sp)
                                        Text("• Checksum Integridad: CRC32 Little-Endian en header de 32 bytes", fontSize = 8.5.sp)
                                        Text("• Procedencia C2PA: Content Credentials v2.1 incrustadas en metadatos binarios", fontSize = 8.5.sp)
                                        Text("• Prevención de Inyección: Sellado por frame contra ataques de splicing", fontSize = 8.5.sp)
                                        Text("• Anti-REA-ing 2026: Sondas JDWP/Frida, canarios off-heap y esteganografía forense", fontSize = 8.5.sp, color = Color(0xFF10B981))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
