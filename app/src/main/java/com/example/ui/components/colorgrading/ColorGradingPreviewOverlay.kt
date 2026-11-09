package com.example.ui.components.colorgrading

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.colorgrading.ColorGradingPresets
import com.example.data.colorgrading.ColorGradingState

/**
 * Overlay de previsualización en tiempo real de corrección de color y LUTs cinemáticos
 * aplicado directamente sobre la superficie del reproductor Media3 / ExoPlayer.
 * Soporta modo normal y modo de comparación A/B dividida (Split Screen).
 */
@Composable
fun BoxScope.ColorGradingPreviewOverlay(
    state: ColorGradingState,
    modifier: Modifier = Modifier
) {
    if (!state.isEnabled) return

    val lut = state.selectedLut
    val isDefaultNeutral = lut.id == ColorGradingPresets.REC709_DIRECTOR.id &&
            state.brightness == 0f &&
            state.contrast == 1f &&
            state.saturation == 1f &&
            state.temperature == 0f

    if (isDefaultNeutral && !state.isSplitPreviewActive) return

    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        val w = size.width
        val h = size.height
        val splitX = if (state.isSplitPreviewActive) w * 0.5f else 0f

        clipRect(left = splitX, top = 0f, right = w, bottom = h) {
            // 1. CAPA DE BLANCO Y NEGRO / DESATURACIÓN (Monochrome Noir o Saturation reducida)
            if (lut.id == ColorGradingPresets.MONOCHROME_NOIR.id || state.saturation < 0.2f) {
                drawRect(
                    color = Color.Black.copy(alpha = (1f - (state.saturation * 0.5f)).coerceIn(0.5f, 0.92f)),
                    blendMode = BlendMode.Color
                )
            }

            // 2. TINTES DEL LUT CINEMÁTICO (Sombras & Luces)
            if (lut.id == ColorGradingPresets.KODAK_VISION3_5219.id) {
                // Calidez Kodak 35mm
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFEF3C7).copy(alpha = 0.12f * state.lutIntensity),
                            Color(0xFFB45309).copy(alpha = 0.15f * state.lutIntensity)
                        )
                    ),
                    blendMode = BlendMode.Overlay
                )
            } else if (lut.id == ColorGradingPresets.TEAL_AND_ORANGE_BLOCKBUSTER.id) {
                // Hollywood Teal & Orange
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0D9488).copy(alpha = 0.20f * state.lutIntensity), // Teal sombras
                            Color.Transparent,
                            Color(0xFFEA580C).copy(alpha = 0.22f * state.lutIntensity)  // Orange luces
                        )
                    ),
                    blendMode = BlendMode.Overlay
                )
            } else if (lut.id == ColorGradingPresets.FUJI_ETERNA_250D.id) {
                // Fuji Eterna pastel
                drawRect(
                    color = Color(0xFF0F766E).copy(alpha = 0.10f * state.lutIntensity),
                    blendMode = BlendMode.Softlight
                )
            } else if (lut.id == ColorGradingPresets.CYBERPUNK_NEON.id) {
                // Cyberpunk Magenta & Cyan
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF7C3AED).copy(alpha = 0.25f * state.lutIntensity),
                            Color(0xFF06B6D4).copy(alpha = 0.22f * state.lutIntensity)
                        )
                    ),
                    blendMode = BlendMode.Overlay
                )
            } else if (lut.id == ColorGradingPresets.BLEACH_BYPASS_WAR.id) {
                // Bleach Bypass contraste frío
                drawRect(
                    color = Color.Black.copy(alpha = 0.35f * state.lutIntensity),
                    blendMode = BlendMode.Color
                )
                drawRect(
                    color = Color(0xFF475569).copy(alpha = 0.20f * state.lutIntensity),
                    blendMode = BlendMode.Overlay
                )
            } else if (lut.id == ColorGradingPresets.VINTAGE_POLAROID_70S.id) {
                // Vintage Polaroid 1970s
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFEF3C7).copy(alpha = 0.18f * state.lutIntensity),
                            Color(0xFF78350F).copy(alpha = 0.22f * state.lutIntensity)
                        )
                    ),
                    blendMode = BlendMode.Overlay
                )
            }

            // 3. CAPA DE TEMPERATURA DE COLOR MANUAL (Cálido vs Frío)
            if (state.temperature > 0.05f) {
                // Tinte cálido ámbar
                drawRect(
                    color = Color(0xFFF59E0B).copy(alpha = (state.temperature * 0.22f).coerceIn(0f, 0.35f)),
                    blendMode = BlendMode.Overlay
                )
            } else if (state.temperature < -0.05f) {
                // Tinte frío cian
                drawRect(
                    color = Color(0xFF0284C7).copy(alpha = (-state.temperature * 0.22f).coerceIn(0f, 0.35f)),
                    blendMode = BlendMode.Overlay
                )
            }

            // 4. CAPA DE CONTRASTE Y BRILLO MANUAL
            if (state.contrast > 1.05f) {
                // Realzar contraste mediante paso suave
                val contrastAlpha = ((state.contrast - 1f) * 0.25f).coerceIn(0f, 0.35f)
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = contrastAlpha * 0.5f),
                            Color.Black.copy(alpha = contrastAlpha)
                        )
                    ),
                    blendMode = BlendMode.Overlay
                )
            }

            if (state.brightness > 0.05f) {
                drawRect(
                    color = Color.White.copy(alpha = (state.brightness * 0.35f).coerceIn(0f, 0.40f)),
                    blendMode = BlendMode.Screen
                )
            } else if (state.brightness < -0.05f) {
                drawRect(
                    color = Color.Black.copy(alpha = (-state.brightness * 0.50f).coerceIn(0f, 0.60f)),
                    blendMode = BlendMode.Darken
                )
            }
        }

        // LÍNEA DIVISORIA EN MODO SPLIT A/B
        if (state.isSplitPreviewActive) {
            drawLine(
                color = Color.White,
                start = Offset(splitX, 0f),
                end = Offset(splitX, h),
                strokeWidth = 2.5f
            )
            // Círculo central en el slider divisor
            drawCircle(
                color = Color(0xFFFBBF24),
                center = Offset(splitX, h * 0.5f),
                radius = 8.dp.toPx()
            )
        }
    }

    // ETIQUETAS A/B EN PANTALLA DIVIDIDA
    if (state.isSplitPreviewActive) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, start = 12.dp, end = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black.copy(alpha = 0.75f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "ORIGINAL REC.709",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF94A3B8)
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF0F172A).copy(alpha = 0.85f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "GRADED: ${lut.name.uppercase()}",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFFFBBF24)
                )
            }
        }
    }
}
