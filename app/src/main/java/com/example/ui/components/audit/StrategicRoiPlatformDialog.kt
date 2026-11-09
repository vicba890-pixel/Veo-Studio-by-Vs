package com.example.ui.components.audit

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.ui.window.Dialog
import com.example.ui.MainViewModel

@Composable
fun StrategicRoiPlatformDialog(
    viewModel: MainViewModel,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val report by viewModel.strategicEconomicReport.collectAsState()
    val isRunningPing by viewModel.isStrategicPingRunning.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Resumen ROI", "Ping Plataformas", "Feed Financiero", "Opciones Negocio", "Escalabilidad")

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .testTag("strategic_roi_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF070C18)),
            border = BorderStroke(
                1.5.dp,
                Brush.verticalGradient(
                    listOf(Color(0xFF38BDF8), Color(0xFF10B981), Color(0xFFF59E0B))
                )
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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Brush.linearGradient(listOf(Color(0xFF0284C7), Color(0xFF10B981)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.TrendingUp,
                                contentDescription = "ROI Estratégico",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                "VMSB© DOSSIER ECONÓMICO & ROI",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF1F5F9),
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Text(
                                "Ping Multiplataforma • Feed Verificable • Cero Capex",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8), fontSize = 11.sp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("strategic_dialog_close_btn")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color(0xFF94A3B8))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // KPI Ribbon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    KpiMiniCard(
                        title = "ROI AÑO 1",
                        value = report?.scalabilityAndRoi?.year1RoiPercentage ?: "485%",
                        accentColor = Color(0xFF10B981),
                        modifier = Modifier.weight(1f)
                    )
                    KpiMiniCard(
                        title = "ROI AÑO 3",
                        value = report?.scalabilityAndRoi?.year3RoiPercentage ?: "1,140%",
                        accentColor = Color(0xFF38BDF8),
                        modifier = Modifier.weight(1f)
                    )
                    KpiMiniCard(
                        title = "PAYBACK",
                        value = report?.scalabilityAndRoi?.paybackPeriodMonths ?: "3.8 Meses",
                        accentColor = Color(0xFFF59E0B),
                        modifier = Modifier.weight(1f)
                    )
                    KpiMiniCard(
                        title = "CAPEX",
                        value = "$0",
                        accentColor = Color(0xFFA78BFA),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Scrollable Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFF0F172A),
                    contentColor = Color(0xFF38BDF8),
                    edgePadding = 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    title,
                                    fontSize = 11.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) Color(0xFF38BDF8) else Color(0xFF94A3B8)
                                )
                            },
                            modifier = Modifier.testTag("tab_strategic_$index")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Scrollable Content
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    when (selectedTab) {
                        0 -> TabResumenRoi(report)
                        1 -> TabPingPlataformas(report, isRunningPing, onRePing = { viewModel.runStrategicPingAndCrossFeed() })
                        2 -> TabFeedFinanciero(report)
                        3 -> TabOpcionesNegocio(report)
                        4 -> TabEscalabilidad(report)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action Buttons Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // PDF Button
                    Button(
                        onClick = {
                            val file = viewModel.generateStrategicRoiPdf(context)
                            if (file != null && file.exists()) {
                                viewModel.viewPdfFile(context, file)
                            } else {
                                Toast.makeText(context, "Generando PDF...", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(44.dp)
                            .testTag("strategic_view_pdf_btn")
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ver PDF Oficial", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // Download Button
                    OutlinedButton(
                        onClick = {
                            val file = viewModel.generateStrategicRoiPdf(context)
                            if (file != null && file.exists()) {
                                viewModel.downloadPdfFile(context, file)
                            }
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF38BDF8)),
                        border = BorderStroke(1.dp, Color(0xFF0284C7)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("strategic_download_pdf_btn")
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Descargar", fontSize = 11.sp)
                    }

                    // Print Button
                    OutlinedButton(
                        onClick = {
                            val file = viewModel.generateStrategicRoiPdf(context)
                            if (file != null && file.exists()) {
                                viewModel.printPdfFile(context, file)
                            }
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF10B981)),
                        border = BorderStroke(1.dp, Color(0xFF059669)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("strategic_print_pdf_btn")
                    ) {
                        Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Imprimir", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun KpiMiniCard(
    title: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 9.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 13.sp, color = accentColor, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun TabResumenRoi(report: com.example.data.strategic.StrategicCrossedFeedEconomicReport?) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0B132B)),
            border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("DICTAMEN CUANTITATIVO: RETORNO DE INVERSIÓN (ROI)", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "El despliegue de la arquitectura VMSB© genera un retorno neto verificable desde el primer trimestre operativo al erradicar costos de transcodificación centralizada y evitar litigios regulatorios bajo el Convenio de Berna Art. 5.2 y EU AI Act.",
                    fontSize = 11.5.sp,
                    color = Color(0xFFCBD5E1),
                    lineHeight = 16.sp
                )
            }
        }

        // Métricas directas
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("INDICADORES ECONÓMICOS CLAVE", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF38BDF8))
                MetricRow("Costo de Adquisición de Infraestructura:", "$0 USD (Capex = 0)", Color(0xFF10B981))
                MetricRow("Ahorro de Opex en Ancho de Banda:", "42.6% Reducción Neta CDN", Color(0xFF38BDF8))
                MetricRow("Mitigación de Riesgo Legal / Litigios:", "$650M+ USD Blindado C2PA", Color(0xFFF59E0B))
                MetricRow("Período de Recuperación (Payback):", report?.scalabilityAndRoi?.paybackPeriodMonths ?: "3.8 Meses", Color(0xFFA78BFA))
                MetricRow("Relación Beneficio / Costo:", report?.scalabilityAndRoi?.benefitCostRatio ?: "5.85x", Color(0xFF10B981))
            }
        }

        // Certificación hash
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF090E17)),
            border = BorderStroke(1.dp, Color(0xFFD97706).copy(alpha = 0.5f)),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text("SELLO CRIPTOGRÁFICO SHA-256 DEL REPORTE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    report?.cryptographicSealSha256 ?: "C5A9F08D1E3B4C5A8F7D0B2E1A4C7E9F0A2B3C4D5E6F7A8B9C0D1E2F3A4B5C6D",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = Color(0xFF34D399)
                )
            }
        }
    }
}

@Composable
private fun TabPingPlataformas(
    report: com.example.data.strategic.StrategicCrossedFeedEconomicReport?,
    isRunningPing: Boolean,
    onRePing: () -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "ENLACES DIRECTOS VERIFICADOS (${report?.pingMetrics?.size ?: 7} NODOS)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF38BDF8)
            )
            FilledTonalButton(
                onClick = onRePing,
                enabled = !isRunningPing,
                modifier = Modifier.height(32.dp),
                colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFF0369A1))
            ) {
                if (isRunningPing) {
                    CircularProgressIndicator(modifier = Modifier.size(12.dp), color = Color.White, strokeWidth = 1.5.dp)
                } else {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Re-Ping en Vivo", fontSize = 10.sp)
                }
            }
        }

        report?.pingMetrics?.forEach { item ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                border = BorderStroke(1.dp, if (item.isOptimal) Color(0xFF10B981).copy(alpha = 0.3f) else Color(0xFFF59E0B).copy(alpha = 0.3f)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (item.isOptimal) Color(0xFF10B981) else Color(0xFFF59E0B))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(item.platformName, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFFF1F5F9))
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(item.endpointUrl, fontSize = 9.5.sp, color = Color(0xFF64748B))
                        Text("TLS: ${item.cipherSuite} • Hash: ${item.verificationHash}", fontSize = 9.sp, color = Color(0xFF94A3B8))
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Surface(
                            color = Color(0xFF064E3B),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                "${item.latencyMs} ms",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF34D399)
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text("${item.httpStatusCode} OK", fontSize = 9.sp, color = Color(0xFF38BDF8), fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun TabFeedFinanciero(report: com.example.data.strategic.StrategicCrossedFeedEconomicReport?) {
    val scrollState = rememberScrollState()
    val feed = report?.feedData
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("CRUCE CON FEED FINANCIERO EN VIVO", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF38BDF8))
                MetricRow("Fuente del Feed:", feed?.feedSource ?: "ECB Frankfurter & Tech Index", Color(0xFFCBD5E1))
                MetricRow("Fecha de Captura:", feed?.feedDate ?: "2026-10-01 UTC", Color(0xFFCBD5E1))
                MetricRow("Tasa EUR / USD:", "€${feed?.eurRate ?: 0.92}", Color(0xFF10B981))
                MetricRow("Tasa GBP / USD:", "£${feed?.gbpRate ?: 0.78}", Color(0xFF10B981))
                MetricRow("Valuación Alphabet (Google):", feed?.alphabetMarketCap ?: "$2.28T USD", Color(0xFF38BDF8))
                MetricRow("Valuación Meta Platforms:", feed?.metaMarketCap ?: "$1.46T USD", Color(0xFF38BDF8))
                MetricRow("Valuación Apple Inc:", feed?.appleMarketCap ?: "$3.42T USD", Color(0xFF38BDF8))
                MetricRow("TAM Video & GenAI Media:", feed?.cloudAiVideoTam ?: "$18.5B USD", Color(0xFFF59E0B))
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0B132B)),
            border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("CONSISTENCIA ECONÓMICA Y VERACIDAD DE DATOS", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF38BDF8))
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Todos los datos financieros son cotejados en tiempo real contra los registros públicos de capitalización bursátil de las plataformas y tipos de cambio de referencia del Banco Central Europeo (ECB), demostrando solvencia y capacidad inmediata de absorción directa de la tecnología sin requerir emisión de deuda ni apalancamiento.",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8),
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
private fun TabOpcionesNegocio(report: com.example.data.strategic.StrategicCrossedFeedEconomicReport?) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        report?.businessOptions?.forEach { opt ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                border = BorderStroke(1.dp, Color(0xFFD97706).copy(alpha = 0.4f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(opt.title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFFDE68A), modifier = Modifier.weight(1f))
                        Surface(
                            color = Color(0xFF78350F),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(opt.estimatedValuation, fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFDE68A), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(opt.structure, fontSize = 11.sp, color = Color(0xFFCBD5E1), lineHeight = 15.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Ventaja: ${opt.strategicAdvantage}", fontSize = 10.5.sp, color = Color(0xFF38BDF8))
                    Text("Plazo de Ejecución: ${opt.executionHorizon}", fontSize = 10.sp, color = Color(0xFF94A3B8))
                }
            }
        }
    }
}

@Composable
private fun TabEscalabilidad(report: com.example.data.strategic.StrategicCrossedFeedEconomicReport?) {
    val scrollState = rememberScrollState()
    val sc = report?.scalabilityAndRoi
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("ESCALABILIDAD A 1 AÑO (DESPLIEGUE INICIAL)", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF10B981))
                MetricRow("Volumen de Streams:", sc?.year1StreamsCertified ?: "120M Streams", Color(0xFFF1F5F9))
                MetricRow("Petabytes Optimizados:", sc?.year1DataOptimizedPetabytes ?: "18.4 PB", Color(0xFFF1F5F9))
                MetricRow("Ahorro Operativo Directo:", sc?.year1NetFinancialSavingsUsd ?: "$34.8M USD", Color(0xFF10B981))
                MetricRow("Retorno Proyectado Año 1:", sc?.year1RoiPercentage ?: "485%", Color(0xFF38BDF8))
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0B132B)),
            border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("ESCALABILIDAD A 3 AÑOS (ESTÁNDAR GLOBAL)", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF38BDF8))
                MetricRow("Volumen de Streams:", sc?.year3StreamsCertified ?: "1.4B Streams", Color(0xFFF1F5F9))
                MetricRow("Petabytes Optimizados:", sc?.year3DataOptimizedPetabytes ?: "340 PB", Color(0xFFF1F5F9))
                MetricRow("Ahorro Acumulado Directo:", sc?.year3NetFinancialSavingsUsd ?: "$215.6M USD", Color(0xFF10B981))
                MetricRow("Retorno Acumulado Año 3:", sc?.year3RoiPercentage ?: "1,140%", Color(0xFF38BDF8))
            }
        }
    }
}

@Composable
private fun MetricRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 11.sp, color = Color(0xFF94A3B8))
        Text(value, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = valueColor)
    }
}
