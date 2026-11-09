package com.example.ui.components.voiceover

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.Project
import com.example.data.voiceover.VmsbVoiceActor
import com.example.data.voiceover.VmsbVoiceCatalog
import com.example.data.voiceover.VmsbVoiceOverConfig
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sin

/**
 * Diálogo modal del Voice Over Studio Nativo VMSB©.
 * Permite seleccionar voces cinemáticas profesionales, calibrar tono, velocidad,
 * volumen y estándar EBU R128, y renderizar la locución directamente a la línea de tiempo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VmsbVoiceOverStudioDialog(
    project: Project,
    config: VmsbVoiceOverConfig,
    onConfigChange: (VmsbVoiceOverConfig) -> Unit,
    onSynthesizeAndInsert: suspend (VmsbVoiceOverConfig) -> Unit,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isRendering by remember { mutableStateOf(false) }
    var scriptInput by remember { mutableStateOf(config.scriptText.ifBlank { project.scriptText.ifBlank { project.prompt } }) }

    // Animación de barras de ecualizador
    val infiniteTransition = rememberInfiniteTransition(label = "WaveformAnimation")
    val waveAnimPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WavePhase"
    )

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.90f)
                .clip(RoundedCornerShape(24.dp))
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF10B981), Color(0xFF06B6D4), Color(0xFF6366F1))
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
                // Header del Estudio
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
                                        listOf(Color(0xFF059669), Color(0xFF0D9488))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Voiceover Studio",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "VOICE OVER STUDIO VMSB©",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.White
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF065F46))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "DSP NATIVO",
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(0xFF34D399)
                                    )
                                }
                            }
                            Text(
                                text = "Doblaje Cinemático IA • EBU R128 • Teleprompter Sincronizado",
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

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // SECCIÓN 1: SELECTOR DE VOCES CINEMÁTICAS
                    item {
                        Text(
                            text = "VOCES Y TIMBRES DISPONIBLES",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF94A3B8)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            VmsbVoiceCatalog.ALL_ACTORS.forEach { actor ->
                                val isSelected = config.selectedActor.id == actor.id
                                VoiceActorCard(
                                    actor = actor,
                                    isSelected = isSelected,
                                    onClick = { onConfigChange(config.copy(selectedActor = actor, language = actor.language)) }
                                )
                            }
                        }
                    }

                    // SECCIÓN 2: TELEPROMPTER / GUION A NARRAR
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1329)),
                            border = BorderStroke(1.dp, Color(0xFF1E293B))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(Icons.Default.Notes, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                                        Text(
                                            text = "GUION DEL TELEPROMPTER",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            color = Color(0xFF38BDF8)
                                        )
                                    }

                                    TextButton(
                                        onClick = {
                                            val t = project.scriptText.ifBlank { project.prompt }
                                            scriptInput = t
                                            onConfigChange(config.copy(scriptText = t))
                                        },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text("Restaurar Guion", fontSize = 10.sp, color = Color(0xFF10B981))
                                    }
                                }

                                OutlinedTextField(
                                    value = scriptInput,
                                    onValueChange = {
                                        scriptInput = it
                                        onConfigChange(config.copy(scriptText = it))
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 85.dp, max = 130.dp),
                                    placeholder = { Text("Escribe o pega el texto a narrar...", fontSize = 11.sp, color = Color.Gray) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF10B981),
                                        unfocusedBorderColor = Color(0xFF334155),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color(0xFFE2E8F0)
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }
                    }

                    // SECCIÓN 3: FORMA DE ONDA & ECUALIZADOR DINÁMICO VMSB
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "VMSB DSP AUDIO ENGINE • EBU R128",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(0xFF10B981)
                                    )
                                    Text(
                                        text = config.ebuStandard.split(" ").firstOrNull() ?: "EBU R128",
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))

                                // Visualizador de espectro acústico en tiempo real
                                Canvas(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(38.dp)
                                ) {
                                    val barCount = 36
                                    val barWidth = size.width / (barCount * 1.5f)
                                    val spacing = barWidth * 0.5f

                                    for (i in 0 until barCount) {
                                        val phase = waveAnimPhase + (i * 0.25f)
                                        val heightFrac = (0.2f + (abs(sin(phase)) * 0.75f)).coerceIn(0.1f, 1f)
                                        val barHeight = size.height * heightFrac
                                        val x = i * (barWidth + spacing)
                                        val y = (size.height - barHeight) / 2f

                                        drawRoundRect(
                                            brush = Brush.verticalGradient(
                                                listOf(Color(0xFF34D399), Color(0xFF06B6D4))
                                            ),
                                            topLeft = Offset(x, y),
                                            size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // SECCIÓN 4: PARÁMETROS ACÚSTICOS (TONO, VELOCIDAD, VOLUMEN)
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF090D18)),
                            border = BorderStroke(1.dp, Color(0xFF1E293B))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "PARÁMETROS DE EMISIÓN VOCAL",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF94A3B8)
                                )

                                // Slider Tono (Pitch)
                                MicroSlider(
                                    title = "Tono (Pitch)",
                                    value = config.pitch,
                                    valueRange = 0.8f..1.25f,
                                    display = "${String.format("%.2f", config.pitch)}x",
                                    color = Color(0xFF10B981),
                                    onValueChange = { onConfigChange(config.copy(pitch = it)) }
                                )

                                // Slider Velocidad (Rate)
                                MicroSlider(
                                    title = "Velocidad (Rate)",
                                    value = config.speechRate,
                                    valueRange = 0.75f..1.4f,
                                    display = "${String.format("%.2f", config.speechRate)}x",
                                    color = Color(0xFF38BDF8),
                                    onValueChange = { onConfigChange(config.copy(speechRate = it)) }
                                )

                                // Slider Volumen
                                MicroSlider(
                                    title = "Ganancia Master",
                                    value = config.volume,
                                    valueRange = 0.0f..1.5f,
                                    display = "${(config.volume * 100).toInt()}%",
                                    color = Color(0xFFF59E0B),
                                    onValueChange = { onConfigChange(config.copy(volume = it)) }
                                )

                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Checkbox(
                                            checked = config.isDeEsserActive,
                                            onCheckedChange = { onConfigChange(config.copy(isDeEsserActive = it)) }
                                        )
                                        Text("DSP De-Esser", fontSize = 10.sp, color = Color(0xFFCBD5E1))
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Checkbox(
                                            checked = config.isNoiseGateActive,
                                            onCheckedChange = { onConfigChange(config.copy(isNoiseGateActive = it)) }
                                        )
                                        Text("Noise Gate Dinámico", fontSize = 10.sp, color = Color(0xFFCBD5E1))
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Footer Actions: Sintetizar & Renderizar a MultiTrack
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.weight(0.7f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF94A3B8))
                    ) {
                        Text("CANCELAR", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isRendering = true
                                val finalConfig = config.copy(scriptText = scriptInput)
                                onSynthesizeAndInsert(finalConfig)
                                isRendering = false
                                Toast.makeText(
                                    context,
                                    "¡Voz de ${config.selectedActor.name} sintetizada e insertada en la pista de audio!",
                                    Toast.LENGTH_LONG
                                ).show()
                                onDismissRequest()
                            }
                        },
                        enabled = !isRendering && scriptInput.isNotBlank(),
                        modifier = Modifier.weight(1.3f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF10B981),
                            contentColor = Color(0xFF022C22)
                        )
                    ) {
                        if (isRendering) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color(0xFF022C22), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.SpatialAudio, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "RENDERIZAR LOCUCIÓN",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VoiceActorCard(
    actor: VmsbVoiceActor,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(170.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("voice_actor_card_${actor.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF064E3B).copy(alpha = 0.5f) else Color(0xFF0F172A)
        ),
        border = BorderStroke(
            if (isSelected) 1.5.dp else 0.8.dp,
            if (isSelected) Color(actor.badgeColorHex) else Color(0xFF334155)
        )
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(actor.avatarEmoji, fontSize = 20.sp)
                Column {
                    Text(
                        text = actor.name,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isSelected) Color(actor.badgeColorHex) else Color.White,
                        maxLines = 1
                    )
                    Text(
                        text = actor.language,
                        fontSize = 9.sp,
                        color = Color(0xFF38BDF8),
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = actor.category,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE2E8F0),
                maxLines = 1
            )
            Text(
                text = actor.timbre,
                fontSize = 8.5.sp,
                color = Color(0xFF94A3B8),
                lineHeight = 11.sp,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun MicroSlider(
    title: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    display: String,
    color: Color,
    onValueChange: (Float) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 10.5.sp, color = Color(0xFFCBD5E1), modifier = Modifier.width(95.dp))
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(thumbColor = color, activeTrackColor = color),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = display,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = color,
            modifier = Modifier.width(45.dp)
        )
    }
}
