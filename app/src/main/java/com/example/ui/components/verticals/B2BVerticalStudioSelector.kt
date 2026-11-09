package com.example.ui.components.verticals

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.verticals.*

@Composable
fun B2BVerticalStudioSelector(
    onApplyTemplate: (VerticalTemplatePreset) -> Unit,
    modifier: Modifier = Modifier
) {
    val presets = remember { B2BVerticalRegistry.presets }
    var selectedPresetId by remember { mutableStateOf(presets.first().id) }
    val currentPreset = remember(selectedPresetId) { B2BVerticalRegistry.getPresetById(selectedPresetId) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("b2b_vertical_studio_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = BorderStroke(
            1.dp,
            Brush.horizontalGradient(
                listOf(Color(0xFF8B5CF6), Color(0xFFEC4899), Color(0xFFF59E0B))
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
                            .background(Color(0xFF6D28D9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "B2B", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Soluciones Verticales B2B",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Plantillas especializadas de alto retorno (ROI)",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFC4B5FD)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF312E81)
                ) {
                    Text(
                        text = "ALTO VALOR",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFA78BFA)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Selector Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presets.forEach { preset ->
                    val isSelected = preset.id == selectedPresetId
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedPresetId = preset.id },
                        label = {
                            Text(
                                text = "${preset.category.iconEmoji} ${preset.category.title}",
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF6D28D9),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFF1E293B),
                            labelColor = Color(0xFF94A3B8)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Current Preset Details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E293B))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = currentPreset.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFBBF24)
                    )
                    Text(
                        text = "${currentPreset.durationSec}s • Aspecto ${currentPreset.targetAspectRatio}",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                // Subtitle
                Text(
                    text = currentPreset.category.subtitle,
                    fontSize = 11.sp,
                    color = Color(0xFFE2E8F0)
                )

                // ROI / ARR Tag
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF0F172A)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = currentPreset.category.typicalArrRoi,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF34D399)
                        )
                    }
                }

                // Storyboard Structure preview
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "ESTRUCTURA DE STORYBOARD AUTOMATIZADO:",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF818CF8)
                    )
                    currentPreset.storyboardStructure.forEach { scene ->
                        Text(
                            text = "• $scene",
                            fontSize = 10.5.sp,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                }

                // Overlays
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "CAPAS DE TEXTO DINÁMICO & CALLOUTS:",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFF472B6)
                    )
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        currentPreset.onScreenOverlays.forEach { overlay ->
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF334155)
                            ) {
                                Text(
                                    text = overlay,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                    fontSize = 9.5.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                // Voice Languages
                Text(
                    text = "🌐 Idiomas de Narración: ${currentPreset.audioVoiceLanguages.joinToString(", ")}",
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8)
                )

                // Apply button
                Button(
                    onClick = { onApplyTemplate(currentPreset) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("apply_vertical_template_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Cargar Plantilla en el Proyecto",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
