package com.example.ui.components.audit

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.EcoComputeMode
import com.example.data.Project
import com.example.data.audit.PipelineCarbonMetrics
import com.example.data.audit.RealtimeCarbonFootprintEstimator
import com.example.ui.MainViewModel

/**
 * Tarjeta Interactiva del Estimador de Huella de Carbono y Eficiencia Energética
 * integrada dentro del Panel de Ética (Ethics Panel).
 * Calcula en tiempo real el gasto energético de la inferencia IA y el renderizado NLE local.
 */
@Composable
fun RealtimeCarbonEstimatorCard(
    project: Project,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val activeEcoMode by viewModel.ecoComputeMode.collectAsState()
    val telemetry by viewModel.vmsbTelemetry.collectAsState()

    var isDetailedViewOpen by remember { mutableStateOf(false) }

    // Estimación en tiempo real ante cualquier cambio en el proyecto, modo eco o telemetría
    val carbonMetrics: PipelineCarbonMetrics = remember(project, activeEcoMode, telemetry) {
        RealtimeCarbonFootprintEstimator.estimate(
            project = project,
            mode = activeEcoMode,
            telemetry = telemetry
        )
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("realtime_carbon_estimator_card")
            .animateContentSize(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF062117).copy(alpha = 0.85f),
        border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // HEADER DE SOSTENIBILIDAD
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF059669)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Eco,
                            contentDescription = "Carbon Footprint",
                            tint = Color(0xFFA7F3D0),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "HUELLA DE CARBONO & EFICIENCIA EN TIEMPO REAL",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFA7F3D0),
                                letterSpacing = 0.4.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF10B981).copy(alpha = 0.25f)
                            ) {
                                Text(
                                    text = "ISO 14064",
                                    fontSize = 7.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF6EE7B7),
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "Inferencia IA + Renderizado NLE Local • PUE ${carbonMetrics.datacenterPue}",
                            fontSize = 8.5.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                IconButton(
                    onClick = { isDetailedViewOpen = !isDetailedViewOpen },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isDetailedViewOpen) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand Carbon Details",
                        tint = Color(0xFFA7F3D0)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // TOP 3 CARBON KPIS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CarbonKpiBox(
                    title = "EMISIÓN NETA",
                    value = "${"%.1f".format(carbonMetrics.totalEmissionsGramsCo2e)}g",
                    subtext = "CO₂e / Video",
                    color = Color(0xFF34D399),
                    modifier = Modifier.weight(1f)
                )
                CarbonKpiBox(
                    title = "AHORRO NETO",
                    value = "-${"%.1f".format(carbonMetrics.netCarbonSavedGramsCo2e)}g",
                    subtext = "${"%.0f".format(carbonMetrics.carbonReductionPercentage)}% Reducción",
                    color = Color(0xFFFBBF24),
                    modifier = Modifier.weight(1f)
                )
                CarbonKpiBox(
                    title = "RATIO EFICIENCIA",
                    value = "${"%.1f".format(carbonMetrics.carbonReductionPercentage / 18.0)}x",
                    subtext = "vs Nube Tradicional",
                    color = Color(0xFF38BDF8),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // COMPARATIVE VISUAL PROGRESS BAR (VMSB vs Baseline Tradicional)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF03140E))
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "VMSB EcoCompute (${"%.1f".format(carbonMetrics.totalEmissionsGramsCo2e)}g CO₂e)",
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF34D399)
                    )
                    Text(
                        text = "Línea Base Industria (${"%.1f".format(carbonMetrics.traditionalBaselineEmissionsGramsCo2e)}g CO₂e)",
                        fontSize = 8.5.sp,
                        color = Color(0xFFEF4444)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF1E293B))
                ) {
                    val progressFraction = (carbonMetrics.totalEmissionsGramsCo2e / carbonMetrics.traditionalBaselineEmissionsGramsCo2e)
                        .toFloat().coerceIn(0.05f, 1.0f)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = progressFraction)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF10B981), Color(0xFF34D399))
                                )
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // SELECTOR INTERACTIVO DE MODO ECOCOMPUTE
            Text(
                text = "MODO DE COMPUTACIÓN & PERFIL DE ENERGÍA:",
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6EE7B7)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                EcoComputeMode.entries.forEach { mode ->
                    val isSelected = (activeEcoMode == mode)
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { viewModel.setEcoComputeMode(mode) },
                        color = if (isSelected) Color(0xFF047857) else Color(0xFF0F172A),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) Color(0xFF34D399) else Color(0xFF1E293B)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = mode.badge,
                                fontSize = 8.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                maxLines = 1
                            )
                            Text(
                                text = "-${mode.energySavingPercent}% kWh",
                                fontSize = 7.5.sp,
                                color = if (isSelected) Color(0xFFA7F3D0) else Color(0xFF64748B)
                            )
                        }
                    }
                }
            }

            // EXPANDABLE TECHNICAL BREAKDOWN DETAILS
            AnimatedVisibility(visible = isDetailedViewOpen) {
                Column(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF020E09))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "DESGLOSE POR FASE DEL PIPELINE (${project.resolution} @ ${carbonMetrics.fps} fps):",
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFBBF24)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Inferencia
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("1. Inferencia Difusión IA (Veo 3.1):", fontSize = 8.5.sp, color = Color(0xFFCBD5E1))
                        Text(
                            "${"%.1f".format(carbonMetrics.inferenceWattHours)} Wh (${"%.2f".format(carbonMetrics.inferenceCarbonGramsCo2e)}g CO₂e)",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Renderizado NLE
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("2. Render NLE Local (VMSB Core):", fontSize = 8.5.sp, color = Color(0xFFCBD5E1))
                        Text(
                            "${"%.2f".format(carbonMetrics.localRenderWattHours)} Wh (0.0g Servidor)",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF34D399)
                        )
                    }

                    // Energía Total
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("3. Energía Total Consumida:", fontSize = 8.5.sp, color = Color(0xFFCBD5E1))
                        Text(
                            "${"%.1f".format(carbonMetrics.totalPipelineEnergyJoules)} Joules (${"%.1f".format(carbonMetrics.totalPipelineWattHours)} Wh)",
                            fontSize = 8.5.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF38BDF8)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "• Fuente Energética: ${carbonMetrics.regionalEnergySource}",
                        fontSize = 8.sp,
                        color = Color(0xFFA7F3D0),
                        lineHeight = 11.sp
                    )
                    Text(
                        text = "• Cumplimiento: ${carbonMetrics.iso14064ComplianceStatus}",
                        fontSize = 8.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}

@Composable
private fun CarbonKpiBox(
    title: String,
    value: String,
    subtext: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF031A12),
        border = BorderStroke(1.dp, color.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 7.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF94A3B8),
                maxLines = 1
            )
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
            Text(
                text = subtext,
                fontSize = 7.sp,
                color = Color(0xFFA7F3D0),
                maxLines = 1
            )
        }
    }
}
