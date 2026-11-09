package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.EcoComputeMode
import com.example.ui.MainViewModel

@Composable
fun EcoEngineDashboard(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val activeMode by viewModel.ecoComputeMode.collectAsState()
    val currentLatency by viewModel.liveTelemetryLatencyMs.collectAsState()
    val totalCo2Saved by viewModel.totalCo2SavedGrams.collectAsState()

    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("eco_engine_dashboard_card"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            1.dp,
            if (activeMode == EcoComputeMode.GREEN_ECO) Color(0xFF10B981).copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
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
                            .background(
                                if (activeMode == EcoComputeMode.GREEN_ECO) Color(0xFF10B981).copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (activeMode == EcoComputeMode.GREEN_ECO) Icons.Default.Eco else Icons.Default.Bolt,
                            contentDescription = "Eco Engine",
                            tint = if (activeMode == EcoComputeMode.GREEN_ECO) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "GreenAI & Low-Latency Engine",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF10B981).copy(alpha = 0.15f))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "NET-ZERO",
                                    color = Color(0xFF10B981),
                                    fontSize = 7.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(
                            text = "Calidad cinematográfica óptima con mínimo impacto ambiental y latencia",
                            fontSize = 9.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
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

            Spacer(modifier = Modifier.height(12.dp))

            // Real-Time Telemetry Metrics Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Metric 1: Latency
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("LATENCIA", fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF10B981)))
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${currentLatency} ms",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (currentLatency < 100) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                            fontFamily = FontFamily.Monospace
                        )
                        Text("Sub-frame instantáneo", fontSize = 7.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // Metric 2: Energy Saving
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("ENERGÍA", fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                            Icon(Icons.Default.BatteryChargingFull, "Battery", tint = Color(0xFF10B981), modifier = Modifier.size(10.dp))
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "-${activeMode.energySavingPercent}%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981),
                            fontFamily = FontFamily.Monospace
                        )
                        Text("Consumo optimizado", fontSize = 7.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // Metric 3: CO2 Saved
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("HUELLA CO2", fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                            Icon(Icons.Default.Eco, "CO2", tint = Color(0xFF10B981), modifier = Modifier.size(10.dp))
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = String.format("%.1fg", totalCo2Saved),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981),
                            fontFamily = FontFamily.Monospace
                        )
                        Text("CO2e compensado", fontSize = 7.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Mode Selector Options
            Spacer(modifier = Modifier.height(12.dp))
            Text("Perfil de Procesamiento y Sostenibilidad", fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EcoComputeMode.values().forEach { mode ->
                    val isSelected = activeMode == mode
                    val borderColor by animateColorAsState(
                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                        label = "border"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
                                else MaterialTheme.colorScheme.surface
                            )
                            .border(if (isSelected) 1.5.dp else 1.dp, borderColor, RoundedCornerShape(10.dp))
                            .clickable { viewModel.setEcoComputeMode(mode) }
                            .padding(8.dp)
                            .testTag("eco_mode_${mode.name.lowercase()}")
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = when (mode) {
                                        EcoComputeMode.GREEN_ECO -> "🍃 Eco-Green"
                                        EcoComputeMode.ULTRA_LOW_LATENCY -> "⚡ Turbo Edge"
                                        EcoComputeMode.CINEMA_MASTER -> "🎬 Studio 4K"
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Active",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = mode.badge,
                                fontSize = 7.5.sp,
                                color = if (mode == EcoComputeMode.GREEN_ECO) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Expanded Details Section
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = activeMode.description,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 14.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Technical Architecture & Protocols
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Verified, "Protocol", tint = Color(0xFF10B981), modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Protocolo Green Computing: Data Center 24/7 Carbon-Free Match",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Memory, "NPU", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Pipeline de Inferencia: Neural Processing Unit (NPU) + Vulkan Compute",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, "Encrypted", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Seguridad y Privacidad: Cifrado en reposo AES-GCM-256 sin telemetría de usuario",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}
