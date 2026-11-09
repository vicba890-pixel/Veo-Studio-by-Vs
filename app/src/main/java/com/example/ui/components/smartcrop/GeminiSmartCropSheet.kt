package com.example.ui.components.smartcrop

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.Project
import com.example.data.smartcrop.SmartCropAspectRatio
import com.example.data.smartcrop.SmartCropSuggestion

/**
 * Diálogo / Hoja de configuración y detalles para el Smart-Cropping de Gemini AI.
 * Permite examinar el sujeto detectado, comparar los reencuadres 9:16 vs 4:5,
 * alternar el seguimiento cinemático en tiempo real y aplicar la relación de aspecto sugerida.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeminiSmartCropSheet(
    project: Project,
    suggestion: SmartCropSuggestion?,
    isAnalyzing: Boolean,
    isRealtimeTracking: Boolean,
    onDismissRequest: () -> Unit,
    onApplyAspectRatio: (SmartCropAspectRatio) -> Unit,
    onToggleRealtimeTracking: () -> Unit,
    onReanalyze: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f)
                .clip(RoundedCornerShape(24.dp))
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF38BDF8),
                            Color(0xFF818CF8),
                            Color(0xFF10B981)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                ),
            color = Color(0xFF030712)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF0284C7), Color(0xFF6366F1))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Gemini Smart-Crop",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "GEMINI SMART-CROP",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.White
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF0284C7).copy(alpha = 0.25f))
                                        .border(0.8.dp, Color(0xFF38BDF8), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = suggestion?.geminiModel ?: "gemini-3.5-flash",
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(0xFF38BDF8)
                                    )
                                }
                            }
                            Text(
                                text = "Detección de sujeto en tiempo real y reencuadre 9:16 / 4:5",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = Color(0xFFE2E8F0),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // SECCIÓN 1: SUJETO DETECTADO POR GEMINI
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1329)),
                            border = BorderStroke(1.dp, Color(0xFF1E293B))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CenterFocusWeak,
                                            contentDescription = null,
                                            tint = Color(0xFFFBBF24),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = "SUJETO PRINCIPAL IDENTIFICADO",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            color = Color(0xFFFBBF24)
                                        )
                                    }

                                    if (suggestion != null) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFF10B981).copy(alpha = 0.2f))
                                                .border(0.8.dp, Color(0xFF10B981), RoundedCornerShape(6.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "${(suggestion.confidence * 100).toInt()}% PRECISIÓN",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace,
                                                color = Color(0xFF34D399)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                if (isAnalyzing) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color(0xFF38BDF8), strokeWidth = 2.dp)
                                        Text("Gemini analizando el contenido semántico del video...", fontSize = 12.sp, color = Color(0xFFCBD5E1))
                                    }
                                } else if (suggestion != null) {
                                    Text(
                                        text = suggestion.detectedSubject.label,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = suggestion.detectedSubject.description,
                                        fontSize = 11.sp,
                                        color = Color(0xFF94A3B8),
                                        lineHeight = 15.sp
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        BadgeChip("Categoría: ${suggestion.detectedSubject.category}")
                                        BadgeChip("Movimiento: ${suggestion.detectedSubject.motionType}")
                                        BadgeChip("Latencia: ${suggestion.latencyMs}ms")
                                    }
                                }
                            }
                        }
                    }

                    // SECCIÓN 2: RECOMENDACIÓN DE REENCUADRE (9:16 vs 4:5)
                    item {
                        Text(
                            text = "OPCIONES DE REENCUADRE RECOMENDADAS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    // Tarjeta 9:16
                    item {
                        val isRec916 = suggestion?.suggestedAspectRatio == SmartCropAspectRatio.RATIO_9_16
                        val isCurrent916 = project.aspectRatio == "9:16"
                        AspectReframeCard(
                            aspect = SmartCropAspectRatio.RATIO_9_16,
                            isRecommended = isRec916,
                            isCurrentlyActive = isCurrent916,
                            reasoning = if (isRec916) suggestion.reasoning else (suggestion?.alternativeReasoning ?: "Formato vertical ultra-inmersivo para Shorts y Reels."),
                            onApply = { onApplyAspectRatio(SmartCropAspectRatio.RATIO_9_16) }
                        )
                    }

                    // Tarjeta 4:5
                    item {
                        val isRec45 = suggestion?.suggestedAspectRatio == SmartCropAspectRatio.RATIO_4_5
                        val isCurrent45 = project.aspectRatio == "4:5"
                        AspectReframeCard(
                            aspect = SmartCropAspectRatio.RATIO_4_5,
                            isRecommended = isRec45,
                            isCurrentlyActive = isCurrent45,
                            reasoning = if (isRec45) suggestion.reasoning else (suggestion?.alternativeReasoning ?: "Formato retrato balanceado para feeds de Instagram y Facebook."),
                            onApply = { onApplyAspectRatio(SmartCropAspectRatio.RATIO_4_5) }
                        )
                    }

                    // SECCIÓN 3: CONTROL DE SEGUIMIENTO EN TIEMPO REAL
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.TrackChanges,
                                        contentDescription = null,
                                        tint = if (isRealtimeTracking) Color(0xFF10B981) else Color(0xFF64748B),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "Seguimiento Cinemático en Tiempo Real",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Mantiene el sujeto en el centro del recorte a 60 FPS durante la reproducción",
                                            fontSize = 9.5.sp,
                                            color = Color(0xFF94A3B8)
                                        )
                                    }
                                }

                                Switch(
                                    checked = isRealtimeTracking,
                                    onCheckedChange = { onToggleRealtimeTracking() },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color(0xFF10B981),
                                        checkedTrackColor = Color(0xFF064E3B)
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Footer: Botón de Re-análisis
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onReanalyze,
                        enabled = !isAnalyzing,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF38BDF8)),
                        border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f))
                    ) {
                        if (isAnalyzing) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color(0xFF38BDF8), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "RE-ANALIZAR FOTOGRAMA",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Button(
                        onClick = onDismissRequest,
                        modifier = Modifier.weight(0.7f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1E293B),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "CERRAR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AspectReframeCard(
    aspect: SmartCropAspectRatio,
    isRecommended: Boolean,
    isCurrentlyActive: Boolean,
    reasoning: String,
    onApply: () -> Unit
) {
    val borderColor = if (isRecommended) Color(aspect.badgeColor) else Color(0xFF1E293B)
    val containerColor = if (isRecommended) Color(0xFF08152B) else Color(0xFF090D18)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(if (isRecommended) 1.5.dp else 1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(text = aspect.iconEmoji, fontSize = 20.sp)
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Aspecto ${aspect.ratioLabel}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            if (isRecommended) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(aspect.badgeColor).copy(alpha = 0.25f))
                                        .border(0.8.dp, Color(aspect.badgeColor), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "RECOMENDADO GEMINI",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(aspect.badgeColor)
                                    )
                                }
                            }
                            if (isCurrentlyActive) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF10B981).copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "ACTUAL",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(0xFF34D399)
                                    )
                                }
                            }
                        }
                        Text(
                            text = aspect.targetPlatformLabel,
                            fontSize = 10.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                Button(
                    onClick = onApply,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCurrentlyActive) Color(0xFF1E293B) else Color(aspect.badgeColor),
                        contentColor = if (isCurrentlyActive) Color(0xFF94A3B8) else Color(0xFF022C22)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = if (isCurrentlyActive) Icons.Default.Check else Icons.Default.Crop,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isCurrentlyActive) "APLICADO" else "APLICAR ${aspect.ratioLabel}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = reasoning,
                fontSize = 11.sp,
                color = Color(0xFFCBD5E1),
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
private fun BadgeChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF1E293B))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            color = Color(0xFF94A3B8)
        )
    }
}
