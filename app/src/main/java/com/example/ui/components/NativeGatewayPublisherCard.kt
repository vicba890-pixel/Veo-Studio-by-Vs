package com.example.ui.components

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PlatformGatewaySpec
import com.example.data.PlatformRegistry
import com.example.data.Project
import com.example.ui.MainViewModel

@Composable
fun NativeGatewayPublisherCard(
    project: Project,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val selectedSpec by viewModel.selectedPlatformPreset.collectAsState()
    val activeSimulator by viewModel.activePlatformSimulator.collectAsState()
    val currentLoudness by viewModel.currentLoudnessLufs.collectAsState()
    val isNormalizingAudio by viewModel.isNormalizingAudio.collectAsState()
    val isPublishing by viewModel.isPublishingToGateway.collectAsState()
    val currentPublishingId by viewModel.currentGatewayPublishingId.collectAsState()
    val publishResults by viewModel.gatewayPublishResults.collectAsState()
    val securityAudit by viewModel.securityProtocolAudit.collectAsState()

    var showProtocolModal by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("native_gateway_publisher_card"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Pasarelas & Plataformas Nativas",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                "MERCADO & ESTÁNDARES",
                                fontSize = 7.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Text(
                        "Configura codecs, audio EBU R128 y protocolos para distribución directa",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                FilledTonalButton(
                    onClick = {
                        viewModel.generateSecurityAndC2paAudit(project)
                        showProtocolModal = true
                    },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp).testTag("view_c2pa_protocols_button")
                ) {
                    Icon(Icons.Default.Shield, contentDescription = "Security", modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Protocolos", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Platform Selector Horizontal Pills
            Text("Seleccionar Plataforma de Destino", fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PlatformRegistry.majorPlatforms.forEach { spec ->
                    val isSelected = selectedSpec.id == spec.id
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectPlatformPreset(spec) },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(spec.brandColorHex))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(spec.name, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            selectedLabelColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.testTag("platform_chip_${spec.id}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Platform Specification Card (Codecs, Aspect Ratio, Loudness, Safe-Zones)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedSpec.tagLine,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF10B981).copy(alpha = 0.12f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                "C2PA v2.1 FIRMADO",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Key Specs Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Ratio & Res
                        Column(modifier = Modifier.weight(1f)) {
                            Text("FORMATO / CÓDEC", fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                            Text(
                                "${selectedSpec.primaryAspectRatio} • ${selectedSpec.maxResolution}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(selectedSpec.videoCodec, fontSize = 8.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        // Audio Standard
                        Column(modifier = Modifier.weight(1f)) {
                            Text("NORMA DE AUDIO", fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                            Text(
                                "${currentLoudness} LUFS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (Math.abs(currentLoudness - selectedSpec.targetLufs) < 1.0f) Color(0xFF10B981) else Color(0xFFF59E0B)
                            )
                            Text(selectedSpec.audioLoudnessTarget, fontSize = 8.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(8.dp))

                    // Safe Zone & Protocol text
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(13.dp).padding(top = 1.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = selectedSpec.safeZoneDescription,
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Live Simulator Toggle Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Simulador de Interfaz Nativa:", fontSize = 9.5.sp, fontWeight = FontWeight.Medium)

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            val isSimulatingCurrent = activeSimulator == selectedSpec.id
                            OutlinedButton(
                                onClick = {
                                    viewModel.setPlatformSimulator(if (isSimulatingCurrent) "none" else selectedSpec.id)
                                },
                                modifier = Modifier.height(28.dp).testTag("toggle_simulator_button"),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = if (isSimulatingCurrent) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    if (isSimulatingCurrent) "Ocultar Mock" else "Ver en Frame",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Audio Normalizer Button
                            Button(
                                onClick = { viewModel.normalizeAudioToEbuR128(selectedSpec.targetLufs) },
                                modifier = Modifier.height(28.dp).testTag("normalize_audio_button"),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                enabled = !isNormalizingAudio
                            ) {
                                if (isNormalizingAudio) {
                                    CircularProgressIndicator(modifier = Modifier.size(10.dp), color = Color.White, strokeWidth = 1.dp)
                                } else {
                                    Icon(Icons.Default.GraphicEq, contentDescription = null, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("EBU R128", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons: Direct Platform Gateway vs Native Android Share Sheet
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Button 1: Ingestion Gateway
                Button(
                    onClick = { viewModel.publishToPlatformGateway(selectedSpec) },
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("publish_gateway_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    enabled = !isPublishing
                ) {
                    if (isPublishing && currentPublishingId == selectedSpec.id) {
                        CircularProgressIndicator(modifier = Modifier.size(14.dp), color = Color.White, strokeWidth = 1.5.dp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Transmitiendo...", fontSize = 11.sp)
                    } else {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pasarela Directa", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Button 2: Native Android Share Sheet
                OutlinedButton(
                    onClick = { viewModel.shareViaNativeAndroidSheet(context, project, selectedSpec) },
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("native_android_share_button")
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Compartir Nativo", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Results History from Gateways
            if (publishResults.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text("Envíos Realizados a Pasarelas", fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    publishResults.takeLast(3).reversed().forEach { res ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .border(1.dp, Color(0xFF10B981).copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(res.platformName, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        Text(res.transactionId, fontSize = 7.5.sp, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("${res.latencyMs}ms", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFF10B981).copy(alpha = 0.15f))
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text("-${res.co2OffsetGrams}g CO2", fontSize = 7.5.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Protocol & Compliance Modal
    if (showProtocolModal) {
        AlertDialog(
            onDismissRequest = { showProtocolModal = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Normas de Mercado & C2PA", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "Veo Studio cumple con las normativas internacionales de contenido sintético y distribución comercial.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Protocol 1: C2PA
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Verified, null, tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("C2PA Content Credentials v2.1", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                "Firma criptográfica inyectada en el archivo MP4. Demuestra procedencia y autoría sin exponer datos privados.",
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (securityAudit != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Hash: ${securityAudit?.sha256Fingerprint?.take(28)}...",
                                    fontSize = 7.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    // Protocol 2: Audio EBU R128
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.GraphicEq, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("EBU R128 & ITU-R BS.1770-4", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                "Control de sonoridad profesional: Normalización a ${selectedSpec.audioLoudnessTarget} y limitador True Peak a -1.0 dBTP para evitar distorsión.",
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Protocol 3: EU AI Act & COPPA
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Gavel, null, tint = Color(0xFFF59E0B), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("EU AI Act & Google Play Policy", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                "Etiquetado transparente de medios sintéticos generado por IA. Protección de privacidad infantil COPPA y no-recopilación sin consentimiento.",
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showProtocolModal = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}
