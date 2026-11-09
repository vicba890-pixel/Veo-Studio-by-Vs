package com.example.ui.components.transition

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.transition.TransitionType
import com.example.data.transition.VideoTransitionConfig

/**
 * =========================================================================================
 * VMSB(c) 2026 - SELECTOR DE TRANSICIONES DE VIDEO CINEMATOGRÁFICAS
 * =========================================================================================
 *
 * Permite a los editores seleccionar transiciones (Disolvencia, Fundido a Negro, Barrido,
 * Zoom dinámico, Glitch VFX, Desenfoque de movimiento) y calibrar su duración en ms.
 */
@Composable
fun VideoTransitionSelector(
    config: VideoTransitionConfig,
    onConfigChange: (VideoTransitionConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("video_transition_selector_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = config.transitionType.accentColor.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, config.transitionType.accentColor)
                    ) {
                        Text(
                            text = config.transitionType.iconEmoji,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                        )
                    }
                    Column {
                        Text(
                            "Transiciones de Video",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${config.transitionType.displayName} (${config.durationMs}ms)",
                            fontSize = 10.5.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = config.transitionType.category,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Chips horizontales con todos los tipos de transición
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TransitionType.entries.forEach { type ->
                    val isSelected = config.transitionType == type
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            onConfigChange(
                                config.copy(
                                    transitionType = type,
                                    durationMs = if (type == TransitionType.NONE) 0L else config.durationMs.coerceAtLeast(300L)
                                )
                            )
                        },
                        leadingIcon = {
                            Text(type.iconEmoji, fontSize = 12.sp)
                        },
                        label = {
                            Text(
                                text = type.displayName,
                                fontSize = 10.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = type.accentColor.copy(alpha = 0.25f),
                            selectedLabelColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.testTag("transition_chip_${type.id}")
                    )
                }
            }

            // Explicación de la transición seleccionada
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
            ) {
                Text(
                    text = config.transitionType.description,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(8.dp),
                    lineHeight = 13.sp
                )
            }

            // Slider de duración de la transición si no es NONE
            AnimatedVisibility(visible = config.transitionType != TransitionType.NONE) {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Duración de la Transición:", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(
                            "${config.durationMs} ms",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Slider(
                        value = config.durationMs.toFloat(),
                        onValueChange = { newMs ->
                            onConfigChange(config.copy(durationMs = newMs.toLong()))
                        },
                        valueRange = 250f..2000f,
                        steps = 7,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("transition_duration_slider")
                    )
                }
            }
        }
    }
}
