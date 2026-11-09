package com.example.ui.components.adaptive

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
import com.example.data.Project
import com.example.data.adaptive.UniversalAdaptiveMatrixEngine
import com.example.data.adaptive.UniversalTargetDevice
import com.example.ui.MainViewModel

/**
 * Tarjeta de Control del Estándar Universal Adaptativo VMSB©.
 * Integra conectividad, ergonomía, calidad y accesibilidad para:
 * - Smartphones
 * - Tablets Pro & Plegables
 * - Smart TV (Experiencia de 10 Pies)
 * - iOS & Ecosistema Apple (ProRes 422 HQ / Display P3)
 */
@Composable
fun UniversalAdaptiveHubCard(
    project: Project,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedDevice by remember { mutableStateOf(UniversalTargetDevice.SMART_TV_LIVING_ROOM) }
    var isExpanded by remember { mutableStateOf(true) }
    val metrics = remember(selectedDevice) {
        UniversalAdaptiveMatrixEngine.getErgonomicMetrics(selectedDevice)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("universal_adaptive_hub_card")
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF090F1D)),
        border = BorderStroke(
            1.5.dp,
            Brush.horizontalGradient(
                listOf(Color(0xFF38BDF8), Color(0xFF8B5CF6), Color(0xFF10B981))
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // HEADER BAR
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
                                    listOf(Color(0xFF0284C7), Color(0xFF7C3AED))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Devices,
                            contentDescription = "Universal Standard",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Estándar Universal Adaptativo VMSB©",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF047857)
                            ) {
                                Text(
                                    text = "MULTI-SCREEN",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Smartphones • Tablets • Smart TV 10-Ft • iOS ProRes",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
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

                // SELECTOR DE DISPOSITIVO UNIVERSAL (4 TABS)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    UniversalTargetDevice.values().forEach { device ->
                        val isSelected = selectedDevice == device
                        val icon = when (device) {
                            UniversalTargetDevice.MOBILE_HANDHELD -> Icons.Default.Smartphone
                            UniversalTargetDevice.TABLET_PRO_STUDIO -> Icons.Default.Tablet
                            UniversalTargetDevice.SMART_TV_LIVING_ROOM -> Icons.Default.Tv
                            UniversalTargetDevice.IOS_APPLE_ECOSYSTEM -> Icons.Default.PhoneIphone
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(58.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { selectedDevice = device }
                                .testTag("adaptive_tab_${device.id}"),
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) Color(0xFF1E293B) else Color(0xFF0F172A),
                            border = BorderStroke(
                                if (isSelected) 1.5.dp else 1.dp,
                                if (isSelected) Color(device.brandHex) else Color(0xFF1E293B)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = device.title,
                                    tint = if (isSelected) Color(device.brandHex) else Color(0xFF64748B),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = when (device) {
                                        UniversalTargetDevice.MOBILE_HANDHELD -> "Móvil"
                                        UniversalTargetDevice.TABLET_PRO_STUDIO -> "Tablet"
                                        UniversalTargetDevice.SMART_TV_LIVING_ROOM -> "Smart TV"
                                        UniversalTargetDevice.IOS_APPLE_ECOSYSTEM -> "iOS Pro"
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // FICHA DE DETALLES DEL DISPOSITIVO SELECCIONADO
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF0B132B),
                    border = BorderStroke(1.dp, Color(selectedDevice.brandHex).copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = selectedDevice.title.uppercase(),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(selectedDevice.brandHex),
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = selectedDevice.subtitle,
                                    fontSize = 10.sp,
                                    color = Color(0xFFCBD5E1),
                                    lineHeight = 13.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // MATRIZ DE 4 PILARES: CONECTIVIDAD, CALIDAD, ERGONOMÍA, ACCESIBILIDAD
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // PILAR 1: CALIDAD Y VIDEO
                            DeviceFeatureCard(
                                title = "CALIDAD & CÓDEC",
                                items = listOf(
                                    "Resolución: ${selectedDevice.maxResolution}",
                                    "Tasa de Refresco: ${selectedDevice.refreshRateHz}Hz",
                                    "Códec: ${selectedDevice.preferredCodec}",
                                    "Espacio: ${selectedDevice.colorGamut}"
                                ),
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.weight(1f)
                            )

                            // PILAR 2: CONECTIVIDAD & PROTOCOLO
                            DeviceFeatureCard(
                                title = "CONECTIVIDAD",
                                items = listOf(
                                    "Protocolo: ${selectedDevice.networkProtocol}",
                                    "Contenedor: ${selectedDevice.containerFormat}",
                                    "Relación: ${selectedDevice.primaryAspect}",
                                    "Loudness: ${selectedDevice.audioLufs} LUFS"
                                ),
                                tint = Color(0xFF10B981),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // PILAR 3: ERGONOMÍA
                            DeviceFeatureCard(
                                title = "ERGONOMÍA",
                                items = listOf(
                                    "Distancia: ${selectedDevice.viewingDistance}",
                                    "Entrada: ${selectedDevice.inputMethod}",
                                    "Margen Seguro: ${metrics.safeMarginPercent}%",
                                    "Objetivo Táctil: ${metrics.minTouchTargetDp}dp"
                                ),
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.weight(1f)
                            )

                            // PILAR 4: ACCESIBILIDAD
                            DeviceFeatureCard(
                                title = "ACCESIBILIDAD & WCAG",
                                items = listOf(
                                    "Contraste: ${metrics.highContrastWcagRatio}",
                                    "Anillo de Foco: ${metrics.focusRingWidthDp}dp",
                                    "Audio: ${selectedDevice.audioStandardName}",
                                    "C2PA: Provenance Inyectado"
                                ),
                                tint = Color(0xFFEC4899),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // DESCRIPCIÓN DE ZONA SEGURA (SAFE ZONE)
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF020617),
                            border = BorderStroke(1.dp, Color(0xFF1E293B))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CropFree,
                                    contentDescription = null,
                                    tint = Color(selectedDevice.brandHex),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = metrics.safeZoneDescription,
                                    fontSize = 9.sp,
                                    color = Color(0xFFE2E8F0),
                                    lineHeight = 13.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // BOTONES DE ACCIÓN: APLICAR CALIBRACIÓN AL PROYECTO & PING DE TRANSMISIÓN
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val updated = project.copy(
                                        aspectRatio = selectedDevice.primaryAspect,
                                        resolution = selectedDevice.maxResolution.split(" ").first()
                                    )
                                    viewModel.updateProject(updated)
                                    Toast.makeText(
                                        context,
                                        "Proyecto calibrado para ${selectedDevice.title} (${selectedDevice.primaryAspect})",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .testTag("apply_adaptive_calibration_btn"),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(selectedDevice.brandHex)),
                                contentPadding = PaddingValues(horizontal = 6.dp)
                            ) {
                                Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Calibrar para ${selectedDevice.title.split(" ").first()}",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    viewModel.runLiveNetworkBenchmark()
                                    Toast.makeText(
                                        context,
                                        "Iniciando sondeo de enlace de red para ${selectedDevice.title}...",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                modifier = Modifier
                                    .weight(0.9f)
                                    .height(38.dp)
                                    .testTag("test_adaptive_broadcast_btn"),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFF38BDF8)),
                                contentPadding = PaddingValues(horizontal = 6.dp)
                            ) {
                                Icon(Icons.Default.WifiTethering, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF38BDF8))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Sondeo de Enlace",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF38BDF8)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DeviceFeatureCard(
    title: String,
    items: List<String>,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF0F172A),
        border = BorderStroke(1.dp, tint.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = title,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Black,
                color = tint,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            items.forEach { item ->
                Text(
                    text = "• $item",
                    fontSize = 7.5.sp,
                    color = Color(0xFFCBD5E1),
                    lineHeight = 11.sp,
                    maxLines = 1
                )
            }
        }
    }
}
