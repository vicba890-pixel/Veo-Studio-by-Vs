package com.example.data.colorgrading

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorMatrix
import java.io.Serializable

/**
 * =========================================================================================
 * VMSB© CINEMATIC COLOR GRADING & LUT PRESET ENGINE
 * =========================================================================================
 *
 * Motor de corrección de color cinemática de grado profesional.
 * Soporta presets de LUTs clásicos (Kodak, Fuji, Teal & Orange, Noir, etc.)
 * y ajustes manuales finos de Brillo, Contraste, Saturación y Temperatura de Color
 * con aceleración de matriz de color (ColorMatrix) para renderizado en tiempo real a 60 FPS.
 */

data class CinematicLutPreset(
    val id: String,
    val name: String,
    val subtitle: String,
    val category: String,
    val description: String,
    val iconEmoji: String,
    val gradientColors: List<Color>,
    val defaultBrightness: Float = 0.0f,
    val defaultContrast: Float = 1.0f,
    val defaultSaturation: Float = 1.0f,
    val defaultTemperature: Float = 0.0f,
    val shadowTint: Color = Color.Black,
    val highlightTint: Color = Color.White,
    val matrixAdjustment: FloatArray = FloatArray(20) { if (it % 6 == 0) 1f else 0f }
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CinematicLutPreset) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

data class ColorGradingState(
    val selectedLut: CinematicLutPreset = ColorGradingPresets.REC709_DIRECTOR,
    val brightness: Float = 0.0f,      // -1.0f .. 1.0f (0.0f = neutro)
    val contrast: Float = 1.0f,        // 0.5f .. 2.0f (1.0f = neutro)
    val saturation: Float = 1.0f,      // 0.0f .. 2.0f (1.0f = neutro, 0.0f = blanco y negro)
    val temperature: Float = 0.0f,     // -1.0f (frío) .. 1.0f (cálido)
    val lutIntensity: Float = 1.0f,    // 0.0f .. 1.0f
    val isSplitPreviewActive: Boolean = false, // Modo A/B (Antes / Después)
    val isEnabled: Boolean = true
) : Serializable

object ColorGradingPresets {

    val REC709_DIRECTOR = CinematicLutPreset(
        id = "rec709_director",
        name = "Rec.709 Director Neutral",
        subtitle = "Master Reference Monitor",
        category = "Referencia & Neutro",
        description = "Calibración estándar de transmisión broadcast. Rendición de color fidedigna y lineal sin desviación cromática.",
        iconEmoji = "🎬",
        gradientColors = listOf(Color(0xFF334155), Color(0xFF64748B), Color(0xFFCBD5E1)),
        defaultBrightness = 0.0f,
        defaultContrast = 1.0f,
        defaultSaturation = 1.0f,
        defaultTemperature = 0.0f
    )

    val KODAK_VISION3_5219 = CinematicLutPreset(
        id = "kodak_vision3",
        name = "Kodak Vision3 5219",
        subtitle = "35mm Hollywood Warm Gold",
        category = "Cine Clásico 35mm",
        description = "El stock analógico por excelencia del cine contemporáneo. Sombras profundas aterciopeladas, tonos de piel dorados y roll-off suave.",
        iconEmoji = "🎞️",
        gradientColors = listOf(Color(0xFF1E1A16), Color(0xFFB45309), Color(0xFFFDE68A)),
        defaultBrightness = 0.04f,
        defaultContrast = 1.15f,
        defaultSaturation = 1.12f,
        defaultTemperature = 0.35f,
        shadowTint = Color(0xFF1A140F),
        highlightTint = Color(0xFFFFF7ED)
    )

    val FUJI_ETERNA_250D = CinematicLutPreset(
        id = "fuji_eterna",
        name = "Fuji Eterna 250D",
        subtitle = "Pastel Soft Daylight",
        category = "Cine Clásico 35mm",
        description = "Atmósfera cinematográfica nostálgica con bajo contraste, sombras azuladas sutiles y tonos de piel en terciopelo pastel.",
        iconEmoji = "🌸",
        gradientColors = listOf(Color(0xFF132A2F), Color(0xFF0D9488), Color(0xFFCCFBF1)),
        defaultBrightness = 0.02f,
        defaultContrast = 0.92f,
        defaultSaturation = 0.88f,
        defaultTemperature = -0.15f,
        shadowTint = Color(0xFF0F172A),
        highlightTint = Color(0xFFF0FDFA)
    )

    val TEAL_AND_ORANGE_BLOCKBUSTER = CinematicLutPreset(
        id = "teal_and_orange",
        name = "Hollywood Teal & Orange",
        subtitle = "High-Impact Commercial Action",
        category = "Blockbuster Moderno",
        description = "Máxima separación cromática complementaria: cian frío en sombras y pieles ámbar cálidas y radiantes para gran impacto visual.",
        iconEmoji = "💥",
        gradientColors = listOf(Color(0xFF042F2E), Color(0xFF06B6D4), Color(0xFFF97316)),
        defaultBrightness = 0.0f,
        defaultContrast = 1.25f,
        defaultSaturation = 1.20f,
        defaultTemperature = 0.20f,
        shadowTint = Color(0xFF083344),
        highlightTint = Color(0xFFFFEDD5)
    )

    val MONOCHROME_NOIR = CinematicLutPreset(
        id = "monochrome_noir",
        name = "Silver Gelatin 35mm Noir",
        subtitle = "High Dynamic Range B&W",
        category = "Blanco & Negro de Autor",
        description = "Película pancromática con negros azabache profundos, platino en medios tonos y grano de plata hiper-definido.",
        iconEmoji = "⚫",
        gradientColors = listOf(Color(0xFF000000), Color(0xFF525252), Color(0xFFFFFFFF)),
        defaultBrightness = -0.02f,
        defaultContrast = 1.30f,
        defaultSaturation = 0.0f,
        defaultTemperature = 0.0f,
        shadowTint = Color(0xFF0A0A0A),
        highlightTint = Color(0xFFFAFAFA)
    )

    val CYBERPUNK_NEON = CinematicLutPreset(
        id = "cyberpunk_neon",
        name = "Cyberpunk Neo-Tokyo",
        subtitle = "Electric Magenta & Deep Cyan",
        category = "Estilizado & Sci-Fi",
        description = "Contraste nocturno futurista con realce de luces neón violeta, sombras cobalto y cromatismo eléctrico de ciencia ficción.",
        iconEmoji = "🌆",
        gradientColors = listOf(Color(0xFF1E1B4B), Color(0xFFEC4899), Color(0xFF06B6D4)),
        defaultBrightness = -0.04f,
        defaultContrast = 1.28f,
        defaultSaturation = 1.35f,
        defaultTemperature = -0.40f,
        shadowTint = Color(0xFF1E1B4B),
        highlightTint = Color(0xFFFDF4FF)
    )

    val BLEACH_BYPASS_WAR = CinematicLutPreset(
        id = "bleach_bypass",
        name = "Bleach Bypass Silver Retain",
        subtitle = "Gritty Desaturated Drama",
        category = "Drama & Acción Cruda",
        description = "Proceso químico de retención de plata: saturación reducida con microcontraste extremo para estética cruda y visceral.",
        iconEmoji = "⚔️",
        gradientColors = listOf(Color(0xFF1C1917), Color(0xFF78716C), Color(0xFFE7E5E4)),
        defaultBrightness = -0.03f,
        defaultContrast = 1.40f,
        defaultSaturation = 0.45f,
        defaultTemperature = -0.10f,
        shadowTint = Color(0xFF1C1917),
        highlightTint = Color(0xFFE7E5E4)
    )

    val VINTAGE_POLAROID_70S = CinematicLutPreset(
        id = "vintage_polaroid",
        name = "Vintage 1970s Instant Warmth",
        subtitle = "Retro Faded Matte Glow",
        category = "Retro & Nostalgia",
        description = "Negros levantados mates, tonos crema cálidos y tintado ámbar que emulan películas instantáneas de los años 70.",
        iconEmoji = "📸",
        gradientColors = listOf(Color(0xFF292524), Color(0xFFD97706), Color(0xFFFEF3C7)),
        defaultBrightness = 0.06f,
        defaultContrast = 0.88f,
        defaultSaturation = 0.95f,
        defaultTemperature = 0.45f,
        shadowTint = Color(0xFF292524),
        highlightTint = Color(0xFFFFFBEB)
    )

    val ALL_PRESETS: List<CinematicLutPreset> = listOf(
        REC709_DIRECTOR,
        KODAK_VISION3_5219,
        FUJI_ETERNA_250D,
        TEAL_AND_ORANGE_BLOCKBUSTER,
        MONOCHROME_NOIR,
        CYBERPUNK_NEON,
        BLEACH_BYPASS_WAR,
        VINTAGE_POLAROID_70S
    )
}

/**
 * Generador de ColorMatrix matemáticamente calibrado para Jetpack Compose y RenderNode.
 */
object ColorGradingEngine {

    fun buildColorMatrix(state: ColorGradingState): ColorMatrix {
        if (!state.isEnabled) {
            return ColorMatrix() // Matriz identidad
        }

        val cm = ColorMatrix()

        // 1. Aplicar saturación (0.0 = B&W, 1.0 = normal, 2.0 = ultra vívido)
        val effSat = state.saturation * (if (state.selectedLut.id == ColorGradingPresets.MONOCHROME_NOIR.id) 0f else 1f)
        cm.setToSaturation(effSat.coerceIn(0f, 2.5f))

        // 2. Aplicar contraste y brillo
        val c = state.contrast.coerceIn(0.5f, 2.0f)
        val b = state.brightness.coerceIn(-1.0f, 1.0f) * 255f
        val t = state.temperature.coerceIn(-1.0f, 1.0f)

        // Matriz de contraste y brillo:
        // R' = c * (R - 128) + 128 + b + warmShift
        val offset = (1f - c) * 128f + b
        val warmRed = (t * 22f).coerceIn(-30f, 30f)
        val coolBlue = (-t * 22f).coerceIn(-30f, 30f)

        val contrastBrightnessMatrix = ColorMatrix(
            floatArrayOf(
                c, 0f, 0f, 0f, offset + warmRed,
                0f, c, 0f, 0f, offset,
                0f, 0f, c, 0f, offset + coolBlue,
                0f, 0f, 0f, 1f, 0f
            )
        )

        val multiplied = concatColorMatrices(contrastBrightnessMatrix.values, cm.values)
        return ColorMatrix(multiplied)
    }

    /**
     * Multiplicación pura de matrices 4x5 para ColorMatrix según especificación SVG y Android.
     */
    fun concatColorMatrices(a: FloatArray, b: FloatArray): FloatArray {
        val result = FloatArray(20)
        for (row in 0 until 4) {
            for (col in 0 until 4) {
                var sum = 0f
                for (k in 0 until 4) {
                    sum += a[row * 5 + k] * b[k * 5 + col]
                }
                result[row * 5 + col] = sum
            }
            var offset = a[row * 5 + 4]
            for (k in 0 until 4) {
                offset += a[row * 5 + k] * b[k * 5 + 4]
            }
            result[row * 5 + 4] = offset
        }
        return result
    }
}
