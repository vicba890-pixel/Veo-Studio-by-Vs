package com.example.ui.components.deterministic

import androidx.compose.animation.*
import androidx.compose.foundation.*
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
import com.example.data.deterministic.*

@Composable
fun DeterministicDirectingConsole(
    continuityEngine: DeterministicCharacterContinuityEngine,
    basePrompt: String,
    onApplyContinuityToStoryboard: (ContinuityReport) -> Unit,
    modifier: Modifier = Modifier
) {
    var anchor by remember { mutableStateOf(continuityEngine.getActiveAnchor()) }
    var selectedTrajectory by remember { mutableStateOf(CameraTrajectoryType.DOLLY_PUSH_IN) }
    var continuityReport by remember { mutableStateOf(continuityEngine.generateDeterministicStoryline(basePrompt)) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("deterministic_directing_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D131F)),
        border = BorderStroke(
            1.dp,
            Brush.horizontalGradient(
                listOf(Color(0xFF06B6D4), Color(0xFF3B82F6), Color(0xFF6366F1))
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
                            .background(Color(0xFF0369A1)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LockReset,
                            contentDescription = "Deterministic Lock",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Control Determinista & Anti-Drift",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Anclaje de Identidad, Trayectorias y Semillas",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF7DD3FC)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (anchor.isSeedLocked) Color(0xFF064E3B) else Color(0xFF7F1D1D)
                ) {
                    Text(
                        text = if (anchor.isSeedLocked) "CERO DERIVA" else "MODO LIBRE",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (anchor.isSeedLocked) Color(0xFF34D399) else Color(0xFFF87171)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Seed Lock Switch Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1E293B))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Bloqueo de Semilla (Seed Lock)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp,
                        color = Color.White
                    )
                    Text(
                        text = "Semilla Activa: 0x${anchor.lockedSeed.toString(16).uppercase()}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = Color(0xFF38BDF8)
                    )
                }

                Switch(
                    checked = anchor.isSeedLocked,
                    onCheckedChange = {
                        val updated = continuityEngine.toggleSeedLock()
                        anchor = updated
                        continuityReport = continuityEngine.generateDeterministicStoryline(basePrompt)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF0284C7)
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Character Latent Anchors
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF131D2E))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "ANCLAJE DE IDENTIDAD DEL SUJETO:",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF67E8F9)
                )
                Text(
                    text = "• Vector Facial Latente: ${anchor.latentFacialEmbeddingHash} (512-dim)",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = Color(0xFFCBD5E1)
                )
                Text(
                    text = "• Vestuario Anclado: ${anchor.attireDescription}",
                    fontSize = 10.5.sp,
                    color = Color(0xFFE2E8F0)
                )
                Text(
                    text = "• Iluminación de Referencia: ${anchor.lightingEnvironment}",
                    fontSize = 10.5.sp,
                    color = Color(0xFFFBBF24)
                )
                Text(
                    text = "✓ Consistencia Biométrica Garantizada: ${(anchor.identityPreservationScore * 100).toInt()}%",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF34D399)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Camera Trajectory Selector
            Text(
                text = "TRAYECTORIA DE CÁMARA DIRIGIDA:",
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF93C5FD)
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CameraTrajectoryType.values().forEach { traj ->
                    val isSelected = traj == selectedTrajectory
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedTrajectory = traj },
                        label = {
                            Text(
                                text = "${traj.title} (${traj.focalPresetMm}mm)",
                                fontSize = 10.5.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF0284C7),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFF1E293B),
                            labelColor = Color(0xFF94A3B8)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Storyboard Node Graph Preview
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1E293B))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "GRAFO DE CONTINUIDAD (3 ESCENAS)",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF38BDF8)
                    )
                    Text(
                        text = "Score Fidelidad: ${String.format("%.1f", continuityReport.averageFidelityScorePercent)}%",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF34D399)
                    )
                }

                continuityReport.nodes.forEach { node ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF0F172A))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF0284C7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${node.sceneNumber}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${node.title} (${node.cameraTrajectory.title})",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            Text(
                                text = "Semilla: 0x${node.lockedSeed.toString(16)} • Fidelidad: ${node.continuityFidelityPercent}%",
                                fontSize = 9.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }

                Button(
                    onClick = { onApplyContinuityToStoryboard(continuityReport) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("apply_continuity_storyboard_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Timeline, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Aplicar Continuidad al Storyboard", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
