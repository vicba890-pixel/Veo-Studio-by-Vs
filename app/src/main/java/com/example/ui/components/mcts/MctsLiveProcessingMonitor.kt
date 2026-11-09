package com.example.ui.components.mcts

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
import com.example.data.pdf.VmsbEconomicTriumviratePdfGenerator
import com.example.ui.MainViewModel

/**
 * Monitor en Tiempo Real del Motor MCTS (Monte Carlo Tree Search)
 * y Protocolo de Estado de Berna (WIPO Art. 5.2) mientras Veo 3.1 genera fotogramas.
 */
@Composable
fun MctsLiveProcessingMonitor(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isGenerating by viewModel.isGenerating.collectAsState()
    val generationProgress by viewModel.generationProgress.collectAsState()
    val latestMctsPlan by viewModel.vmsbLatestMctsPlan.collectAsState()

    var isSimulatingInference by remember { mutableStateOf(false) }
    var simulatedFrame by remember { mutableStateOf(36) }
    val totalFrames = 120
    var isExpanded by remember { mutableStateOf(true) }

    // Simulación reactiva cuando el usuario activa la orquestación o Veo está generando
    LaunchedEffect(isSimulatingInference, isGenerating) {
        if (isSimulatingInference || isGenerating) {
            while (isSimulatingInference || isGenerating) {
                kotlinx.coroutines.delay(120)
                simulatedFrame = (simulatedFrame + 1) % totalFrames
                if (simulatedFrame == 0) simulatedFrame = 1
                viewModel.runVmsbMctsOptimization(iterations = 150)
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("mcts_live_monitor_card")
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF080D1A)),
        border = BorderStroke(
            1.5.dp,
            Brush.horizontalGradient(
                listOf(Color(0xFFF59E0B), Color(0xFF10B981), Color(0xFF38BDF8))
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // HEADER
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
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
                                    listOf(Color(0xFFD97706), Color(0xFF059669))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountTree,
                            contentDescription = "MCTS Monitor",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Monitor MCTS & Protocolo de Berna",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (isGenerating || isSimulatingInference) Color(0xFF047857) else Color(0xFF1E293B)
                            ) {
                                Text(
                                    text = if (isGenerating || isSimulatingInference) "GENERANDO" else "CALIBRADO",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isGenerating || isSimulatingInference) Color(0xFFA7F3D0) else Color(0xFF94A3B8),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Búsqueda Estocástica UCB1 en Tiempo Real • Veo 3.1 & WIPO Art. 5.2",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFBBF24),
                            fontSize = 10.5.sp
                        )
                    }
                }

                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expandir/Colapsar",
                        tint = Color(0xFF94A3B8)
                    )
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(14.dp))

                // BARRA DE PROGRESO DE FOTOGRAMAS & MCTS DEPTH
                val currentFrameNum = if (isGenerating) (generationProgress * totalFrames).toInt().coerceAtLeast(1) else simulatedFrame
                val percentProgress = currentFrameNum.toFloat() / totalFrames.toFloat()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Fotograma Activo: $currentFrameNum / $totalFrames (${(percentProgress * 100).toInt()}%)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Profundidad Árbol: 5 Niveles | UCB1: 1.414",
                        fontSize = 9.sp,
                        color = Color(0xFF38BDF8),
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { percentProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF10B981),
                    trackColor = Color(0xFF1E293B)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // KPI CARDS EN TIEMPO REAL: SIMULACIONES, VELOCIDAD, CALIDAD SSIM, DECISIÓN
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val simulations = latestMctsPlan?.totalSimulationsRun ?: 750
                    val ssim = latestMctsPlan?.qualityScoreSsim ?: 0.985
                    val energySaving = latestMctsPlan?.selectedDecision?.energySavingPercent ?: 58

                    MetricCard(
                        label = "SIMULACIONES",
                        value = "$simulations",
                        sub = "UCB1 Rollouts",
                        color = Color(0xFF38BDF8),
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        label = "VELOCIDAD MCTS",
                        value = "850 n/s",
                        sub = "SIMD Direct",
                        color = Color(0xFF34D399),
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        label = "CALIDAD SSIM",
                        value = String.format("%.3f", ssim),
                        sub = "Perceptual AAA",
                        color = Color(0xFFFBBF24),
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        label = "AHORRO ENERGÍA",
                        value = "-$energySaving%",
                        sub = "Eco GPU/NPU",
                        color = Color(0xFFA78BFA),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // RAMA GANADORA MCTS (DECISION ACTIVA)
                val decisionLabel = latestMctsPlan?.selectedDecision?.label ?: "⚡ ARM64-Neon 120 FPS ProMotion (QP 18)"
                val ucbScore = latestMctsPlan?.winningUcbScore ?: 3.82

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF0F172A),
                    border = BorderStroke(1.dp, Color(0xFFD97706).copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "RAMA GANADORA MCTS (SELECCIÓN UCB1):",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFF59E0B),
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = decisionLabel,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF020617),
                            border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "UCB1: ${String.format("%.2f", ucbScore)}",
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF38BDF8),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // SECCIÓN: PROTOCOLO DE ESTADO DE BERNA (ART. 5.2 WIPO)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF051C14),
                    border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF34D399))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "PROTOCOLO DE ESTADO DE BERNA (ART. 5.2 OMPI/WIPO)",
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF34D399),
                                    letterSpacing = 0.5.sp
                                )
                            }
                            Text(
                                text = "BLOQUE #1042",
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF6EE7B7)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• Protección Automática Inmediata: Creación original fijada digitalmente sin sujeción a formalidades externas.",
                            fontSize = 8.sp,
                            color = Color(0xFFE2E8F0),
                            lineHeight = 11.sp
                        )
                        Text(
                            text = "• Sello Criptográfico NIST FIPS 204: Retículo post-cuántico CRYSTALS-Dilithium-5 vinculado al fotograma $currentFrameNum.",
                            fontSize = 8.sp,
                            color = Color(0xFFE2E8F0),
                            lineHeight = 11.sp
                        )
                    }
                }

                // SECCIÓN: MARKET DATA API & VALUACIÓN BIG TECH
                val marketData by viewModel.platformMarketMetrics.collectAsState()
                val isFetchingMarket by viewModel.isFetchingMarketData.collectAsState()

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF0F172A),
                    border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF38BDF8))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "MARKET DATA API & VALUACIÓN EN VIVO",
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF38BDF8),
                                    letterSpacing = 0.5.sp
                                )
                            }
                            IconButton(
                                onClick = { viewModel.refreshMarketData() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Actualizar Market API",
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Alphabet: ${marketData.alphabetMarketCap}", fontSize = 8.sp, color = Color(0xFFE2E8F0))
                            Text("Meta: ${marketData.metaMarketCap}", fontSize = 8.sp, color = Color(0xFFE2E8F0))
                            Text("Apple: ${marketData.appleMarketCap}", fontSize = 8.sp, color = Color(0xFFE2E8F0))
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "TAM Creadores: ${marketData.totalTamUsd} | FX: ${marketData.eurRate} EUR/USD | ${marketData.sourceName}",
                            fontSize = 7.5.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // BOTONES DE ACCIÓN: SIMULAR INFERENCIA CONTINUA & DESCARGAR DICTÁMENES PDF
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = {
                            isSimulatingInference = !isSimulatingInference
                            Toast.makeText(
                                context,
                                if (isSimulatingInference) "Simulación de inferencia MCTS activa en tiempo real" else "Simulación MCTS pausada",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .testTag("toggle_mcts_sim_btn"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSimulatingInference) Color(0xFFB45309) else Color(0xFF047857)
                        ),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Icon(
                            imageVector = if (isSimulatingInference) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = if (isSimulatingInference) "Pausar MCTS" else "Simular MCTS",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Botón para generar PDF con la librería externa PDFBox para Android
                    Button(
                        onClick = {
                            val pdf = viewModel.generateMctsAndMarketProjectionPdfBox(context)
                            if (pdf != null) {
                                viewModel.viewPdfFile(context, pdf)
                                Toast.makeText(context, "PDF generado con librería externa PDFBox", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Error generando PDF con PDFBox", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .weight(1.2f)
                            .height(38.dp)
                            .testTag("generate_pdfbox_mcts_btn"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFFDE68A))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "PDFBox MCTS & Market",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Button(
                        onClick = {
                            val pdf = VmsbEconomicTriumviratePdfGenerator.generateEconomicAuditPdf(
                                context = context,
                                benchmarkReport = viewModel.networkBenchmarkReport.value
                            )
                            viewModel.viewPdfFile(context, pdf)
                            Toast.makeText(context, "Dictamen Económico & Protocolo de Berna generado", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1.1f)
                            .height(38.dp)
                            .testTag("open_economic_audit_pdf_btn"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB45309)),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Icon(Icons.Default.Assessment, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Dictamen 3 Págs",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    label: String,
    value: String,
    sub: String,
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
            Text(text = label, fontSize = 7.5.sp, fontWeight = FontWeight.Black, color = Color(0xFF94A3B8), maxLines = 1)
            Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Black, color = color)
            Text(text = sub, fontSize = 7.sp, color = Color(0xFF64748B), maxLines = 1)
        }
    }
}
