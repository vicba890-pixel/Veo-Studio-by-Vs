package com.example.ui.components.preview

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorMatrix

/**
 * Representación de espacios de color y gamas cromáticas para masterización y edición profesional.
 */
enum class ColorGamutProfile(
    val id: String,
    val displayName: String,
    val gamutStandard: String,
    val bitDepth: String,
    val gammaCurve: String,
    val description: String,
    val badgeColor: Color
) {
    REC_709(
        id = "rec709",
        displayName = "Rec. 709 (sRGB)",
        gamutStandard = "ITU-R BT.709-6",
        bitDepth = "8-bit Standard",
        gammaCurve = "Gamma 2.4",
        description = "Estándar de difusión HD y redes sociales. Máxima compatibilidad web/móvil.",
        badgeColor = Color(0xFF3B82F6)
    ),
    DCI_P3(
        id = "dcip3",
        displayName = "DCI-P3 Cinema",
        gamutStandard = "SMPTE RP 431-2",
        bitDepth = "10-bit Wide Gamut",
        gammaCurve = "D65 Display P3",
        description = "Gama amplia cinematográfica con 25% más volumen cromático en rojos y verdes.",
        badgeColor = Color(0xFF10B981)
    ),
    BT_2020_HDR(
        id = "bt2020",
        displayName = "BT.2020 HDR Master",
        gamutStandard = "ITU-R BT.2020 (HDR10)",
        bitDepth = "10/12-bit Ultra HD",
        gammaCurve = "ST 2084 (PQ / HLG)",
        description = "Máxima fidelidad espectral para pantallas HDR OLED. Reproducción sin degradado.",
        badgeColor = Color(0xFFF59E0B)
    ),
    PRORES_LOG(
        id = "prores_log",
        displayName = "Apple ProRes Log",
        gamutStandard = "Wide Gamut Apple Log",
        bitDepth = "10-bit Logarithmic",
        gammaCurve = "Logarithmic Flat",
        description = "Curva plana que preserva hasta 14 pasos de rango dinámico para etalonaje manual.",
        badgeColor = Color(0xFFA855F7)
    ),
    FILM_ACES(
        id = "film_aces",
        displayName = "ACEScg Film Look",
        gamutStandard = "Academy Color Encoding",
        bitDepth = "16-bit Float AP1",
        gammaCurve = "Filmic S-Curve",
        description = "Emulación analógica de emulsión fílmica de 35mm con rolloff suave de altas luces.",
        badgeColor = Color(0xFFEC4899)
    ),
    CYBERPUNK_NEON(
        id = "cyberpunk",
        displayName = "Cyberpunk Vivid HDR",
        gamutStandard = "Extended Dynamic P3+",
        bitDepth = "10-bit Expanded",
        gammaCurve = "Punchy High-Contrast",
        description = "Saturación espectral optimizada para luces de neón, cianes eléctricos y magentas.",
        badgeColor = Color(0xFF06B6D4)
    )
}

/**
 * Configuración completa de etalonaje y calibración de color en tiempo real.
 */
data class ColorGradingConfig(
    val profile: ColorGamutProfile = ColorGamutProfile.REC_709,
    val exposureEv: Float = 0f,            // -2.0 a +2.0 EV
    val contrast: Float = 1.0f,            // 0.5 a 2.0
    val saturation: Float = 1.0f,          // 0.0 a 2.0
    val temperatureKelvin: Int = 6500,     // 3000K (cálido) a 9500K (frío)
    val tint: Float = 0f,                  // -50 (verde) a +50 (magenta)
    val gamma: Float = 1.0f,               // 0.7 a 1.5
    val shadowLift: Float = 0f,            // -0.3 a +0.3
    val highlightGain: Float = 1.0f        // 0.7 a 1.4
) {
    /**
     * Construye una matriz de color 4x5 acelerada por hardware (ColorMatrix)
     * que la GPU ejecuta nativamente en cada fotograma del reproductor.
     */
    fun toComposeColorMatrix(): ColorMatrix {
        // Multiplicador de exposición EV (2^EV)
        val expFactor = Math.pow(2.0, exposureEv.toDouble()).toFloat().coerceIn(0.2f, 4.0f)
        
        // Multiplicador de contraste
        val c = contrast.coerceIn(0.5f, 2.5f)
        val offset = 0.5f * (1f - c) * 255f

        // Balance de blancos / Temperatura (Kelvin relativo a 6500K neutral)
        val tempNorm = (temperatureKelvin - 6500f) / 3000f // -1.16 a +1.0
        val rTemp = (1f + tempNorm * 0.22f).coerceIn(0.7f, 1.4f)
        val bTemp = (1f - tempNorm * 0.22f).coerceIn(0.7f, 1.4f)

        // Tinte Verde / Magenta
        val gTint = (1f - (tint / 100f) * 0.20f).coerceIn(0.7f, 1.3f)
        val mTint = (1f + (tint / 100f) * 0.15f).coerceIn(0.7f, 1.3f)

        // Perfil predeterminado según Gamut
        val profileRBoost: Float
        val profileGBoost: Float
        val profileBBoost: Float
        val profileSatBoost: Float
        when (profile) {
            ColorGamutProfile.DCI_P3 -> {
                profileRBoost = 1.05f
                profileGBoost = 1.04f
                profileBBoost = 1.02f
                profileSatBoost = 1.08f
            }
            ColorGamutProfile.BT_2020_HDR -> {
                profileRBoost = 1.08f
                profileGBoost = 1.06f
                profileBBoost = 1.05f
                profileSatBoost = 1.15f
            }
            ColorGamutProfile.PRORES_LOG -> {
                // Curva logarítmica más suave y desaturada para preservación
                profileRBoost = 0.95f
                profileGBoost = 0.95f
                profileBBoost = 0.95f
                profileSatBoost = 0.82f
            }
            ColorGamutProfile.FILM_ACES -> {
                // Calidez fílmica analógica
                profileRBoost = 1.08f
                profileGBoost = 0.98f
                profileBBoost = 0.92f
                profileSatBoost = 1.05f
            }
            ColorGamutProfile.CYBERPUNK_NEON -> {
                profileRBoost = 1.15f
                profileGBoost = 0.95f
                profileBBoost = 1.22f
                profileSatBoost = 1.25f
            }
            ColorGamutProfile.REC_709 -> {
                profileRBoost = 1.0f
                profileGBoost = 1.0f
                profileBBoost = 1.0f
                profileSatBoost = 1.0f
            }
        }

        val finalSat = (saturation * profileSatBoost).coerceIn(0f, 2.5f)

        // Coeficientes estándar de luminancia Rec.709
        val lr = 0.2126f
        val lg = 0.7152f
        val lb = 0.0722f

        val invSat = 1f - finalSat
        val sR = invSat * lr
        val sG = invSat * lg
        val sB = invSat * lb

        val redGain = (c * expFactor * rTemp * mTint * profileRBoost * highlightGain).coerceIn(0.1f, 5f)
        val greenGain = (c * expFactor * gTint * profileGBoost * highlightGain).coerceIn(0.1f, 5f)
        val blueGain = (c * expFactor * bTemp * mTint * profileBBoost * highlightGain).coerceIn(0.1f, 5f)

        val liftOffset = (shadowLift * 50f)

        // Matriz 4x5 de saturación combinada con ganancia y compensación
        val values = floatArrayOf(
            (sR + finalSat) * redGain, sG * redGain, sB * redGain, 0f, offset + liftOffset,
            sR * greenGain, (sG + finalSat) * greenGain, sB * greenGain, 0f, offset + liftOffset,
            sR * blueGain, sG * blueGain, (sB + finalSat) * blueGain, 0f, offset + liftOffset,
            0f, 0f, 0f, 1f, 0f
        )

        return ColorMatrix(values)
    }

    companion object {
        val NEUTRAL_MASTER = ColorGradingConfig()
        
        val CINEMATIC_WARM = ColorGradingConfig(
            profile = ColorGamutProfile.FILM_ACES,
            exposureEv = 0.15f,
            contrast = 1.12f,
            saturation = 1.08f,
            temperatureKelvin = 5600,
            tint = 4f,
            shadowLift = 0.02f
        )

        val PRORES_DIRECT = ColorGradingConfig(
            profile = ColorGamutProfile.PRORES_LOG,
            exposureEv = 0f,
            contrast = 0.95f,
            saturation = 0.88f,
            temperatureKelvin = 6500,
            tint = 0f
        )

        val HDR_EXPANDED = ColorGradingConfig(
            profile = ColorGamutProfile.BT_2020_HDR,
            exposureEv = 0.25f,
            contrast = 1.18f,
            saturation = 1.15f,
            temperatureKelvin = 6500,
            tint = 0f,
            highlightGain = 1.1f
        )
    }
}

/**
 * Fuente de video de alta fidelidad para previsualización en el editor.
 */
data class HighFidelityVideoSource(
    val id: String,
    val title: String,
    val uriString: String,
    val resolutionLabel: String,
    val fps: Int,
    val gamut: ColorGamutProfile,
    val durationSeconds: Double,
    val isHdr: Boolean = false,
    val description: String = ""
) {
    companion object {
        val PRESETS = listOf(
            HighFidelityVideoSource(
                id = "hdr_color_calib",
                title = "4K Color Calibration Test",
                uriString = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                resolutionLabel = "3840x2160 UHD",
                fps = 60,
                gamut = ColorGamutProfile.BT_2020_HDR,
                durationSeconds = 596.0,
                isHdr = true,
                description = "Secuencia de referencia para comprobación de rango dinámico y saturación BT.2020."
            ),
            HighFidelityVideoSource(
                id = "tears_of_steel",
                title = "Tears of Steel (Cinematic 4K)",
                uriString = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
                resolutionLabel = "4K DCI Widescreen",
                fps = 24,
                gamut = ColorGamutProfile.DCI_P3,
                durationSeconds = 734.0,
                isHdr = false,
                description = "Master cinematográfico con amplia gradación tonal y texturas de alta frecuencia."
            ),
            HighFidelityVideoSource(
                id = "for_bigger_blazes",
                title = "High Motion & Chrominance Test",
                uriString = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                resolutionLabel = "1080p 60fps",
                fps = 60,
                gamut = ColorGamutProfile.REC_709,
                durationSeconds = 15.0,
                isHdr = false,
                description = "Prueba de alta velocidad de fotogramas (60fps) y estabilidad de codificador hardware."
            ),
            HighFidelityVideoSource(
                id = "elephants_dream",
                title = "3D Volumetric Grading Sample",
                uriString = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                resolutionLabel = "1080p HDR",
                fps = 30,
                gamut = ColorGamutProfile.CYBERPUNK_NEON,
                durationSeconds = 653.0,
                isHdr = true,
                description = "Contraste volumétrico para prueba de luces bajas y gradiente cian-ámbar."
            )
        )
    }
}
