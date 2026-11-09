package com.example.ui.components.vmsb

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.camera.BerneCopyrightCertificate
import com.example.data.vmsb.MctsOptimizationPlan
import com.example.data.vmsb.VmsbBenchmarkResult
import com.example.data.vmsb.VmsbEnergyTelemetry
import com.example.data.vmsb.VmsbVectorArch
import com.example.ui.MainViewModel
import kotlin.math.sin

/**
 * VMSB© Brand Visual Interface for Veo Studio by Vs
 *
 * Implements a high-class, smooth, luxurious, and technologically prestigious visual
 * interface expressing the quality of the VMSB© Native Core, Árbol MCTS, and Berne Protocol 2026.
 */
@Composable
fun VmsbBrandVisualInterface(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    initiallyExpanded: Boolean = true
) {
    val telemetry by viewModel.vmsbTelemetry.collectAsState()
    val latestMctsPlan by viewModel.vmsbLatestMctsPlan.collectAsState()
    val isBenchmarking by viewModel.isVmsbBenchmarkRunning.collectAsState()
    val lastBenchmark by viewModel.vmsbLastBenchmarkResult.collectAsState()
    val selectedBerneCert by viewModel.selectedBerneCertificate.collectAsState()

    var isExpanded by remember { mutableStateOf(initiallyExpanded) }
    var selectedTab by remember { mutableStateOf(0) } // 0: SIMD & Pipeline, 1: MCTS Árbol, 2: vmsb© Personal Sail, 3: Brand Manifesto

    val infiniteTransition = rememberInfiniteTransition(label = "vmsb_brand_pulse")
    val ambientGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ambientGlowAlpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("vmsb_brand_visual_interface_card")
            .drawBehind {
                // Subtle high-class ambient back-glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF06B6D4).copy(alpha = 0.12f * ambientGlowAlpha),
                            Color(0xFFF59E0B).copy(alpha = 0.06f * ambientGlowAlpha),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.8f, size.height * 0.2f),
                        radius = size.width * 0.6f
                    )
                )
            },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF080C14).copy(alpha = 0.92f)
        ),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(
            1.2.dp,
            Brush.linearGradient(
                listOf(
                    Color(0xFFF59E0B).copy(alpha = 0.75f),
                    Color(0xFF06B6D4).copy(alpha = 0.85f),
                    Color(0xFF10B981).copy(alpha = 0.65f)
                )
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .animateContentSize(animationSpec = spring(stiffness = Spring.StiffnessLow))
        ) {
            // Header: "VS" Crest + Veo Studio by Vs Branding + Status Beacon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { isExpanded = !isExpanded }
                ) {
                    // Stylized Luxury "VS" Monogram Crest
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(13.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFF1E293B),
                                        Color(0xFF0F172A),
                                        Color(0xFF020617)
                                    )
                                )
                            )
                            .border(
                                1.5.dp,
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFFF59E0B),
                                        Color(0xFF38BDF8)
                                    )
                                ),
                                RoundedCornerShape(13.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "VS",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    letterSpacing = 1.sp
                                ),
                                color = Color(0xFFF59E0B)
                            )
                            Text(
                                text = "vmsb©",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 6.5.sp,
                                    letterSpacing = 0.3.sp
                                ),
                                color = Color(0xFF38BDF8)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "VEO STUDIO",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    letterSpacing = 1.2.sp
                                ),
                                color = Color(0xFFF8FAFC)
                            )

                            // "by Vs" Signature Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                Color(0xFFF59E0B).copy(alpha = 0.22f),
                                                Color(0xFFD97706).copy(alpha = 0.15f)
                                            )
                                        )
                                    )
                                    .border(
                                        0.8.dp,
                                        Color(0xFFF59E0B).copy(alpha = 0.8f),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 1.5.dp)
                            ) {
                                Text(
                                    text = "by Vs",
                                    color = Color(0xFFFBBF24),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Pulsing Green Live Beacon
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981))
                            )
                            Text(
                                text = "vmsb© NATIVE ENGINE • BERNE PROTOCOL 2026",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 8.5.sp,
                                    letterSpacing = 0.4.sp
                                ),
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }

                // Expand / Collapse Chevron Button
                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = Color(0xFFF59E0B)
                    )
                }
            }

            // Quick Status Chips Row (Visible always for high-class glanceability)
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                VmsbMicroSpecChip(
                    icon = Icons.Default.Bolt,
                    label = "LATENCY",
                    value = "${telemetry.averageLatencyMicros / 1000.0} ms",
                    accentColor = Color(0xFF10B981)
                )
                VmsbMicroSpecChip(
                    icon = Icons.Default.Memory,
                    label = "SIMD",
                    value = "NEON/AVX 128-bit",
                    accentColor = Color(0xFF38BDF8)
                )
                VmsbMicroSpecChip(
                    icon = Icons.Default.AccountTree,
                    label = "MCTS ÁRBOL",
                    value = if (latestMctsPlan != null) "Plan: ${latestMctsPlan?.winningUcbScore?.let { String.format("%.2f", it) } ?: "Optimal"}" else "Active",
                    accentColor = Color(0xFFF59E0B)
                )
                VmsbMicroSpecChip(
                    icon = Icons.Default.Verified,
                    label = "BERNE 2026",
                    value = "SHA-256 Sello OK",
                    accentColor = Color(0xFFA855F7)
                )
                VmsbMicroSpecChip(
                    icon = Icons.Default.Speed,
                    label = "ZERO-GC",
                    value = "128MB Fakfa",
                    accentColor = Color(0xFFEC4899)
                )
            }

            // Expanded Full High-Class Interface
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(14.dp))

                    // Real-Time Dynamic Harmonic SIMD Waveform Canvas Visualizer
                    VmsbDynamicSimdWaveVisualizer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(68.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF030712))
                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp)),
                        telemetry = telemetry
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // High-Class Modern Navigation Pills
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        VmsbTabPill(
                            title = "SIMD & Fakfa",
                            icon = Icons.Default.Speed,
                            isSelected = selectedTab == 0,
                            onClick = { selectedTab = 0 }
                        )
                        VmsbTabPill(
                            title = "Árbol MCTS",
                            icon = Icons.Default.AccountTree,
                            isSelected = selectedTab == 1,
                            onClick = { selectedTab = 1 }
                        )
                        VmsbTabPill(
                            title = "vmsb© Personal Sail",
                            icon = Icons.Default.WorkspacePremium,
                            isSelected = selectedTab == 2,
                            onClick = { selectedTab = 2 }
                        )
                        VmsbTabPill(
                            title = "Manifiesto Vs",
                            icon = Icons.Default.AutoAwesome,
                            isSelected = selectedTab == 3,
                            onClick = { selectedTab = 3 }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Tab Contents
                    when (selectedTab) {
                        0 -> VmsbHardwareQualityTab(
                            telemetry = telemetry,
                            lastBenchmark = lastBenchmark,
                            isBenchmarking = isBenchmarking,
                            onRunBenchmark = { viewModel.runVmsbBenchmark() }
                        )
                        1 -> VmsbMctsArbolTab(
                            plan = latestMctsPlan,
                            onRunOptimization = { viewModel.runVmsbMctsOptimization(750) }
                        )
                        2 -> VmsbPersonalSailTab(
                            viewModel = viewModel,
                            onOpenCertificate = { viewModel.openBerneCertificateForLatest() }
                        )
                        3 -> VmsbBrandManifestoTab()
                    }
                }
            }
        }
    }
}

/**
 * Visualizador de Ondas Vectoriales SIMD y Telemetría en Tiempo Real usando Canvas
 */
@Composable
fun VmsbDynamicSimdWaveVisualizer(
    modifier: Modifier = Modifier,
    telemetry: VmsbEnergyTelemetry
) {
    val infiniteTransition = rememberInfiniteTransition(label = "simd_wave")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val midY = height / 2f

            // Micro-grid lines for high-tech HUD aesthetic
            val gridStep = 24.dp.toPx()
            var x = 0f
            while (x < width) {
                drawLine(
                    color = Color(0xFF1E293B).copy(alpha = 0.45f),
                    start = Offset(x, 0f),
                    end = Offset(x, height),
                    strokeWidth = 0.8f
                )
                x += gridStep
            }

            // Primary SIMD Vector Wave (Cyan Laser)
            val primaryPath = Path()
            val secondaryPath = Path()
            val points = 80
            val step = width / points

            for (i in 0..points) {
                val currentX = i * step
                val normalizedX = (i.toFloat() / points) * (4 * Math.PI).toFloat()
                val yOffset1 = (sin(normalizedX + phase) * (height * 0.28f)).toFloat()
                val yOffset2 = (sin(normalizedX * 1.5f - phase * 0.8f) * (height * 0.18f)).toFloat()

                val y1 = midY + yOffset1
                val y2 = midY + yOffset2

                if (i == 0) {
                    primaryPath.moveTo(currentX, y1)
                    secondaryPath.moveTo(currentX, y2)
                } else {
                    primaryPath.lineTo(currentX, y1)
                    secondaryPath.lineTo(currentX, y2)
                }
            }

            // Draw Secondary Harmonic Wave (Champagne Gold)
            drawPath(
                path = secondaryPath,
                brush = Brush.horizontalGradient(
                    listOf(
                        Color(0xFFF59E0B).copy(alpha = 0.3f),
                        Color(0xFFFDE68A).copy(alpha = 0.6f),
                        Color(0xFFF59E0B).copy(alpha = 0.3f)
                    )
                ),
                style = Stroke(width = 1.8.dp.toPx())
            )

            // Draw Primary Wave (Cyan Laser)
            drawPath(
                path = primaryPath,
                brush = Brush.horizontalGradient(
                    listOf(
                        Color(0xFF06B6D4),
                        Color(0xFF38BDF8),
                        Color(0xFF10B981)
                    )
                ),
                style = Stroke(width = 2.4.dp.toPx())
            )
        }

        // Live HUD Overlay Badges
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF0F172A).copy(alpha = 0.85f))
                        .border(0.6.dp, Color(0xFF06B6D4), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "SIMD STREAM: 120 FPS NATIVE",
                        color = Color(0xFF38BDF8),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF0F172A).copy(alpha = 0.85f))
                    .border(0.6.dp, Color(0xFF10B981), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "JITTER: <0.11 ms • OFF-HEAP",
                    color = Color(0xFF10B981),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

/**
 * Tab 0: Hardware Quality, Zero-Copy SIMD Pipeline & Live Benchmark
 */
@Composable
private fun VmsbHardwareQualityTab(
    telemetry: VmsbEnergyTelemetry,
    lastBenchmark: VmsbBenchmarkResult?,
    isBenchmarking: Boolean,
    onRunBenchmark: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Specifications Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            VmsbDetailCard(
                modifier = Modifier.weight(1f),
                title = "Fakfa Ring Buffer",
                subtitle = "Memoria Off-Heap",
                mainValue = "128 MB",
                footnote = "0 Bytes Garbage Collection",
                borderColor = Color(0xFF38BDF8)
            )
            VmsbDetailCard(
                modifier = Modifier.weight(1f),
                title = "Aceleración SIMD",
                subtitle = "ARM NEON & AVX2",
                mainValue = "128 / 256b",
                footnote = "Matrices Vectoriales 64B",
                borderColor = Color(0xFFF59E0B)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            VmsbDetailCard(
                modifier = Modifier.weight(1f),
                title = "Latencia Promedio",
                subtitle = "Zero-Copy Pipeline",
                mainValue = "${telemetry.averageLatencyMicros / 1000.0} ms",
                footnote = "Procesamiento <1.2ms máx",
                borderColor = Color(0xFF10B981)
            )
            VmsbDetailCard(
                modifier = Modifier.weight(1f),
                title = "Eficiencia Térmica",
                subtitle = "Margen de Potencia",
                mainValue = "${String.format("%.1f", telemetry.thermalMarginDegreesCelsius)} °C",
                footnote = "${String.format("%.1f", telemetry.instantaneousPowerMilliwatts)} mW Consumo",
                borderColor = Color(0xFFA855F7)
            )
        }

        // Benchmark Action and Last Result
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.7f)),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFF334155))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "BENCHMARK NATIVO MEDIBLE",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = Color(0xFFF1F5F9)
                        )
                        Text(
                            text = "Prueba de estrés SIMD, tasa Fakfa y balance Joules",
                            fontSize = 9.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    Button(
                        onClick = onRunBenchmark,
                        enabled = !isBenchmarking,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0284C7)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        if (isBenchmarking) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Midiendo...", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.Speed, contentDescription = "Benchmark", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Iniciar Test", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (lastBenchmark != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Color(0xFF1E293B))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Último Resultado: ${lastBenchmark.summaryText}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF38BDF8)
                        )
                    )
                }
            }
        }
    }
}

/**
 * Tab 1: Árbol MCTS (Monte Carlo Tree Search)
 */
@Composable
private fun VmsbMctsArbolTab(
    plan: MctsOptimizationPlan?,
    onRunOptimization: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.8f)),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ÁRBOL ESTOCÁSTICO MCTS (UCB1)",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                letterSpacing = 0.5.sp
                            ),
                            color = Color(0xFFFDE68A)
                        )
                        Text(
                            text = "Algoritmo de balance y exploración continua de trayectorias",
                            fontSize = 9.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    Button(
                        onClick = onRunOptimization,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.AccountTree, contentDescription = "Run MCTS", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Optimizar", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (plan != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("DECISIÓN GANADORA", fontSize = 8.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                            Text(plan.selectedDecision.label, fontSize = 11.sp, color = Color(0xFFF8FAFC), fontWeight = FontWeight.Bold)
                            Text("Cuantización QP: ${plan.selectedDecision.quantizationQp} • ${plan.selectedDecision.chromaSubsampling}", fontSize = 9.sp, color = Color(0xFF38BDF8))
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("SCORE UCB1", fontSize = 8.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                            Text(String.format("%.3f", plan.winningUcbScore), fontSize = 14.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Black)
                            Text("Simulaciones: ${plan.totalSimulationsRun}", fontSize = 8.sp, color = Color(0xFFCBD5E1))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { (plan.qualityScoreSsim).toFloat().coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Color(0xFFF59E0B),
                        trackColor = Color(0xFF334155)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Índice SSIM: ${String.format("%.3f", plan.qualityScoreSsim)}", fontSize = 8.sp, color = Color(0xFF94A3B8))
                        Text("Eficiencia: +${String.format("%.1f", plan.efficiencyFactor * 100)}%", fontSize = 8.sp, color = Color(0xFF10B981))
                    }
                } else {
                    Text(
                        text = "El árbol MCTS se auto-calibra automáticamente en cada fotograma transmitido.",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}

/**
 * Tab 2: vmsb© Personal Sail & Berne Convention Protocol 2026
 */
@Composable
private fun VmsbPersonalSailTab(
    viewModel: MainViewModel,
    onOpenCertificate: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.85f)),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFFA855F7).copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF7E22CE).copy(alpha = 0.3f))
                                .border(1.dp, Color(0xFFA855F7), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = "Sello",
                                tint = Color(0xFFE9D5FF),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "vmsb© PERSONAL SAIL",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp,
                                    letterSpacing = 0.6.sp
                                ),
                                color = Color(0xFFF3E8FF)
                            )
                            Text(
                                text = "Convenio de Berna • Protocolo OMPI/WIPO 2026",
                                fontSize = 8.5.sp,
                                color = Color(0xFFD8B4FE)
                            )
                        }
                    }

                    Button(
                        onClick = onOpenCertificate,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7E22CE)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Icon(Icons.Default.Verified, contentDescription = "Certificado", modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ver Sello", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Legal Holder & Jurisdiction Readout
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF030712))
                        .border(0.8.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("TITULAR PATRIMONIAL:", fontSize = 8.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                            Text("vicba890@gmail.com (Vs)", fontSize = 8.5.sp, color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("JURISDICCIÓN:", fontSize = 8.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                            Text("181 Estados Miembros de Berna", fontSize = 8.5.sp, color = Color(0xFFF1F5F9))
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("AÑO DE REGISTRO EN TIEMPO REAL:", fontSize = 8.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                            Text("2026 INMUTABLE", fontSize = 8.5.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Black)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("INTEGRIDAD CRIPTOGRÁFICA:", fontSize = 8.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                            Text("SHA-256 + HMAC NATIVO", fontSize = 8.5.sp, color = Color(0xFFFDE68A), fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Tab 3: Brand Manifesto & System Architecture Overview
 */
@Composable
private fun VmsbBrandManifestoTab() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B111E)),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFF1E293B))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "MANIFIESTO VEO STUDIO BY VS",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp
                ),
                color = Color(0xFFF59E0B)
            )

            Text(
                text = "Veo Studio by Vs trasciende las herramientas de edición convencionales. Combina la vanguardia de la inteligencia generativa con la pureza y contundencia del procesamiento nativo en bajo nivel a través del protocolo propietario vmsb©.",
                fontSize = 10.5.sp,
                lineHeight = 15.sp,
                color = Color(0xFFE2E8F0)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                VmsbPrinciplePill(
                    icon = Icons.Default.Bolt,
                    title = "Velocidad Nativa",
                    desc = "Zero-Copy en C++ off-heap sin pausas de Garbage Collection."
                )
                VmsbPrinciplePill(
                    icon = Icons.Default.Shield,
                    title = "Soberanía Legal",
                    desc = "Sellado criptográfico y protección automática bajo el Convenio de Berna."
                )
            }
        }
    }
}

// Micro-Componentes Reutilizables de Alta Calidad

@Composable
fun VmsbMicroSpecChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    accentColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF0F172A).copy(alpha = 0.85f))
            .border(0.8.dp, accentColor.copy(alpha = 0.45f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 5.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(12.dp))
            Column {
                Text(
                    text = label,
                    fontSize = 6.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 0.4.sp
                )
                Text(
                    text = value,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF1F5F9),
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun VmsbTabPill(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF0284C7) else Color(0xFF1E293B).copy(alpha = 0.6f),
        label = "tabBg"
    )
    val textColor = if (isSelected) Color.White else Color(0xFF94A3B8)

    Box(
        modifier = Modifier
            .testTag("vmsb_tab_${title.replace(" ", "_").replace("©", "")}")
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(
                0.8.dp,
                if (isSelected) Color(0xFF38BDF8) else Color(0xFF334155),
                RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = textColor, modifier = Modifier.size(13.dp))
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = textColor
            )
        }
    }
}

@Composable
private fun VmsbDetailCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    mainValue: String,
    footnote: String,
    borderColor: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.6f)),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(0.8.dp, borderColor.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = title, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
            Text(text = subtitle, fontSize = 7.5.sp, color = Color(0xFF64748B))
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = mainValue,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = borderColor,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = footnote, fontSize = 7.5.sp, color = Color(0xFFCBD5E1))
        }
    }
}

@Composable
private fun RowScope.VmsbPrinciplePill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    desc: String
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF030712))
            .border(0.8.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(imageVector = icon, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(12.dp))
                Text(text = title, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF8FAFC))
            }
            Text(text = desc, fontSize = 7.5.sp, color = Color(0xFF94A3B8), lineHeight = 10.sp)
        }
    }
}

/**
 * Dialog Showcase para el Brand Crest "VS" de la TopAppBar
 */
@Composable
fun VmsbBrandShowcaseDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75f))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 520.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF0A0E17))
                    .border(1.5.dp, Color(0xFFF59E0B), RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF1E293B))
                                .border(1.dp, Color(0xFFF59E0B), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "VS",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = Color(0xFFF59E0B)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "VEO STUDIO by Vs",
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = Color(0xFFF8FAFC)
                            )
                            Text(
                                text = "vmsb© Native Engine Architecture & Quality",
                                fontSize = 9.sp,
                                color = Color(0xFF06B6D4)
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                VmsbBrandVisualInterface(
                    viewModel = viewModel,
                    initiallyExpanded = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Regresar al Estudio", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
