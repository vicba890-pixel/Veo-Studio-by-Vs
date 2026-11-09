package com.example.ui.components.omnichannel

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
import com.example.data.PlatformGatewaySpec
import com.example.data.PlatformRegistry
import com.example.data.GatewayPublishResult

@Composable
fun OmnichannelSafeZoneMaster(
    selectedPlatform: PlatformGatewaySpec,
    onSelectPlatform: (PlatformGatewaySpec) -> Unit,
    currentLufs: Float,
    isAudioNormalized: Boolean,
    onToggleAudioNormalize: () -> Unit,
    onBatchPublishAllGateways: () -> Unit,
    isPublishing: Boolean,
    publishResults: List<GatewayPublishResult>,
    modifier: Modifier = Modifier
) {
    val platforms = remember { PlatformRegistry.majorPlatforms }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("omnichannel_master_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1322)),
        border = BorderStroke(
            1.dp,
            Brush.horizontalGradient(
                listOf(Color(0xFFEC4899), Color(0xFF8B5CF6), Color(0xFF06B6D4))
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
                            .background(Color(0xFF831843)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Omnichannel",
                            tint = Color(0xFFF472B6),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Motor Omnicanal Inteligente",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "One-Take, Multi-Platform Master • Safe-Zones",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFF472B6)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF500724)
                ) {
                    Text(
                        text = "EBU R128 ACTIVO",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF472B6)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Platform Selector Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                platforms.forEach { p ->
                    val isSelected = p.id == selectedPlatform.id
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectPlatform(p) },
                        label = {
                            Text(
                                text = "${p.name} (${p.primaryAspectRatio})",
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(p.brandColorHex).copy(alpha = 0.8f),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFF1E293B),
                            labelColor = Color(0xFF94A3B8)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Safe Zone & Codec Specs Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
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
                        text = "ZONAS SEGURAS & ESPECIFICACIÓN: ${selectedPlatform.name.uppercase()}",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF38BDF8)
                    )
                    Text(
                        text = "Target: ${selectedPlatform.targetLufs} LUFS",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF34D399)
                    )
                }

                Text(
                    text = selectedPlatform.safeZoneDescription,
                    fontSize = 11.sp,
                    color = Color(0xFFE2E8F0),
                    lineHeight = 15.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "• Códec: ${selectedPlatform.videoCodec}",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                    Text(
                        text = "• Max: ${selectedPlatform.maxResolution} @ ${selectedPlatform.maxFps}fps",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                // Gateway Endpoint
                Text(
                    text = "Gateway: ${selectedPlatform.gatewayEndpoint}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.5.sp,
                    color = Color(0xFF64748B)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Audio Normalization Section (EBU R128)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF162032))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Normalización EBU R128 / ITU-R BS.1770",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Nivel Actual: ${String.format("%.1f", currentLufs)} LUFS (True Peak -1.0 dBTP)",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF34D399)
                    )
                }

                Button(
                    onClick = onToggleAudioNormalize,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAudioNormalized) Color(0xFF059669) else Color(0xFF0284C7)
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (isAudioNormalized) "Normalizado ✓" else "Normalizar Audio",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Batch Publish to All Gateways Button
            Button(
                onClick = onBatchPublishAllGateways,
                enabled = !isPublishing,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("batch_publish_gateways_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBE185D)),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (isPublishing) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Publicando simultáneamente...", fontSize = 12.sp)
                } else {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Publicación Simultánea en Todos los Gateways", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (publishResults.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "RECIBOS DE INGESTIÓN DIRECTA (GATEWAYS):",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF34D399)
                    )
                    publishResults.take(4).forEach { res ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF1E293B))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "• ${res.platformName}: ${res.status}",
                                fontSize = 10.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Tx: ${res.transactionId.take(8)}... (${res.latencyMs}ms)",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                color = Color(0xFF38BDF8)
                            )
                        }
                    }
                }
            }
        }
    }
}
