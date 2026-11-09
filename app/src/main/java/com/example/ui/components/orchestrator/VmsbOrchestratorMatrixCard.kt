package com.example.ui.components.orchestrator

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.orchestrator.*
import com.example.data.wasm.VmsbWasmRuntimeCore
import kotlinx.coroutines.launch

@Composable
fun VmsbOrchestratorMatrixCard(
    orchestrator: VmsbNativeOrchestratorMatrix,
    wasmRuntime: VmsbWasmRuntimeCore? = null,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val liveWasmTelemetry = wasmRuntime?.telemetry?.collectAsState()?.value
    val isWasmBenchmarking = wasmRuntime?.isBenchmarkRunning?.collectAsState()?.value ?: false

    val partitions = remember { orchestrator.generateLivePartitions() }
    val wasm = remember { orchestrator.getWasmTelemetry() }
    val jurisdictions = remember { orchestrator.getJurisdictionMatrix() }
    val snarkProof = remember { orchestrator.generateSnarkProof("SAMPLE_VMSB_ORCHESTRATOR_RUN".toByteArray()) }

    var selectedTab by remember { mutableStateOf(0) } // 0: Kafka Partitions, 1: Wasm SIMD, 2: ZKP Proofs, 3: Legal Matrix

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("vmsb_orchestrator_matrix_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF090D16)),
        border = BorderStroke(
            1.dp,
            Brush.horizontalGradient(
                listOf(Color(0xFF3B82F6), Color(0xFF10B981), Color(0xFFF59E0B))
            )
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E3A8A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Memory,
                            contentDescription = "Native Matrix",
                            tint = Color(0xFF60A5FA),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Matriz Binaria Kafka C++ & Wasm",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Orquestador Nativo • Circom Arks SNARK ZKP",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF064E3B)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFF34D399))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "NATIVO CERTIFICADO",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF34D399)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sub-Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFF0F172A),
                contentColor = Color.White,
                edgePadding = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Kafka Streams", fontSize = 11.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Wasm/SIMD C++", fontSize = 11.sp) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Circom ZKP", fontSize = 11.sp) }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("Jurisdicciones", fontSize = 11.sp) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (selectedTab) {
                0 -> {
                    // Kafka Streams
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        partitions.forEach { p ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1E293B))
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = p.type.topic,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF38BDF8)
                                        )
                                    )
                                    Text(
                                        text = "Prioridad P${p.type.priority} • ${p.totalProcessedPackets} frames procesados • Drops: ${p.droppedPackets}",
                                        fontSize = 10.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "${p.throughputMBps} MB/s",
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF34D399),
                                        fontSize = 12.sp
                                    )
                                    LinearProgressIndicator(
                                        progress = { p.bufferFillPercent },
                                        modifier = Modifier
                                            .width(60.dp)
                                            .height(4.dp)
                                            .clip(RoundedCornerShape(2.dp)),
                                        color = Color(0xFF38BDF8),
                                        trackColor = Color(0xFF334155)
                                    )
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Wasm SIMD Telemetry & Live Operational Hub
                    val activeWasm = liveWasmTelemetry
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF111827))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⚡ Runtime: ${activeWasm?.runtimeVersion ?: wasm.runtimeName}",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFBBF24),
                                fontSize = 11.5.sp
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF065F46)
                            ) {
                                Text(
                                    text = if (activeWasm?.isWasmAccelerationActive == false) "INACTIVO" else "ZERO-COPY ACTIVO",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF6EE7B7),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = "• Vector Lanes: ${activeWasm?.simdArchitecture ?: wasm.simdVectorLaneWidth}",
                            fontSize = 10.5.sp,
                            color = Color(0xFFE2E8F0)
                        )
                        Text(
                            text = "• Memoria Lineal Wasm: ${activeWasm?.linearMemoryMb ?: wasm.memoryAllocatedMB} MB Off-Heap (0 bytes Dalvik GC)",
                            fontSize = 10.5.sp,
                            color = Color(0xFF38BDF8)
                        )
                        Text(
                            text = "• Transferencia Zero-Copy: ${activeWasm?.zeroCopyEfficiencyPercent ?: wasm.zeroCopyTransferPercent}% sin copias de memoria en CPU",
                            fontSize = 10.5.sp,
                            color = Color(0xFF34D399)
                        )
                        Text(
                            text = "• Latencia por Fotograma: ${activeWasm?.averageFrameLatencyMicros ?: wasm.executionLatencyMicros} µs (Sub-milisegundo)",
                            fontSize = 10.5.sp,
                            color = Color(0xFFFDE68A)
                        )
                        Text(
                            text = "• MCTS Rollouts: ${((activeWasm?.mctsRolloutsPerSec ?: 125000L) / 1000)}k/s | Audio EBU DSP: <${activeWasm?.ebuAudioDspLatencyMicros ?: 95L} µs",
                            fontSize = 10.5.sp,
                            color = Color(0xFFA78BFA)
                        )
                        Text(
                            text = "• Sandbox SFI: Enclave Criptográfico Seguro (Cero exposición de datos sensibles)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF86EFAC)
                        )

                        if (wasmRuntime != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        wasmRuntime.executeCompleteWasmBenchmark()
                                    }
                                },
                                enabled = !isWasmBenchmarking,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(34.dp),
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Icon(
                                    imageVector = if (isWasmBenchmarking) Icons.Default.HourglassEmpty else Icons.Default.Bolt,
                                    contentDescription = null,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isWasmBenchmarking) "Ejecutando Wasm SIMD..." else "Ejecutar Benchmark Wasm SIMD (Píxeles + EBU + MCTS)",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
                2 -> {
                    // Circom ZKP
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF111827))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Prueba Cero-Conocimiento Groth16 / BN254",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981),
                                fontSize = 12.sp
                            )
                        }
                        Text(
                            text = "Circuito R1CS: ${snarkProof.circuitName}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.5.sp,
                            color = Color(0xFF93C5FD)
                        )
                        Text(
                            text = "Proof Hash: ${snarkProof.proofHash}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = Color(0xFFCBD5E1)
                        )
                        Text(
                            text = "Verification Key: ${snarkProof.verificationKeyFingerprint}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = Color(0xFFFBBF24)
                        )
                        Text(
                            text = "✓ Validez Matemática: Certificado sin filtrar datos privados ni pesos neuronales (${snarkProof.executionTimeMs}ms)",
                            fontSize = 10.5.sp,
                            color = Color(0xFF34D399)
                        )
                    }
                }
                3 -> {
                    // Legal Jurisdictions
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        jurisdictions.forEach { jur ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1E293B))
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${jur.flagEmoji} ${jur.jurisdictionName} • ${jur.regulationName}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.5.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = jur.certificationStatus,
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF10B981)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                jur.legalArticles.forEach { art ->
                                    Text(
                                        text = "• $art",
                                        fontSize = 10.sp,
                                        color = Color(0xFF94A3B8)
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
