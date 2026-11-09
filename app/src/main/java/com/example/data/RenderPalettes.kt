package com.example.data

import androidx.compose.ui.graphics.Color

/**
 * High-end cinematic rendering palettes designed with smooth colors,
 * luxurious roll-offs, and sophisticated color grading curves.
 */
data class RenderPalette(
    val id: String,
    val name: String,
    val subtitle: String,
    val category: String,
    val description: String,
    val gradientColors: List<Color>,
    val highlightColor: Color,
    val shadowColor: Color,
    val accentGlowColor: Color,
    val temperatureKelvin: Int,
    val contrastCurve: String,
    val vignetteStrength: Float,
    val skinToneOptimized: Boolean,
    val grainIntensity: Float,
    val tags: List<String>
)

object RenderPaletteRegistry {
    val CHAMPAGNE_VELVET = RenderPalette(
        id = "champagne_velvet",
        name = "Champagne & Cashmere",
        subtitle = "Editorial Luxury Warmth",
        category = "Haute Couture & Commercial",
        description = "Tonos dorados aterciopelados y crema suave con sombras cálidas profundas. Curva de roll-off ultra-suave que preserva tonos de piel naturales con acabado editorial.",
        gradientColors = listOf(
            Color(0xFF1E1A16),
            Color(0xFF382F26),
            Color(0xFF8C7355),
            Color(0xFFD4AF37),
            Color(0xFFF3E5C8)
        ),
        highlightColor = Color(0xFFFFF8E7),
        shadowColor = Color(0xFF14110E),
        accentGlowColor = Color(0xFFE6C280),
        temperatureKelvin = 3200,
        contrastCurve = "Velvet S-Curve Soft",
        vignetteStrength = 0.22f,
        skinToneOptimized = true,
        grainIntensity = 0.08f,
        tags = listOf("Luxury", "Warmth", "Skin-Tone Safe", "3200K")
    )

    val NORDIC_OPAL = RenderPalette(
        id = "nordic_opal",
        name = "Nordic Opal & Mist",
        subtitle = "Minimalist Crisp Atmosphere",
        category = "Architecture & Clean Modern",
        description = "Gama fría satinada inspirada en la luz ártica y el diseño escandinavo. Blancos inmaculados, sombras pizarra azulada y micro-contraste de alta definición sin saturación agresiva.",
        gradientColors = listOf(
            Color(0xFF0F172A),
            Color(0xFF1E293B),
            Color(0xFF475569),
            Color(0xFF94A3B8),
            Color(0xFFE2E8F0)
        ),
        highlightColor = Color(0xFFF8FAFC),
        shadowColor = Color(0xFF0B1120),
        accentGlowColor = Color(0xFF38BDF8),
        temperatureKelvin = 5800,
        contrastCurve = "Linear Crisp High-Key",
        vignetteStrength = 0.15f,
        skinToneOptimized = true,
        grainIntensity = 0.04f,
        tags = listOf("Minimal", "Clean", "Architecture", "5800K")
    )

    val SUNSET_CASHMERE = RenderPalette(
        id = "sunset_cashmere",
        name = "Sunset Cashmere & Peach",
        subtitle = "Golden Hour Soft Radiance",
        category = "Cinematic Storytelling",
        description = "Resplandor crepuscular con degradados sutiles de melocotón satinado, ámbar dorado y cielo lavanda cremoso. Transiciones lumínicas orgánicas que transmiten nostalgia y confort.",
        gradientColors = listOf(
            Color(0xFF2E1065),
            Color(0xFF581C87),
            Color(0xFF9D174D),
            Color(0xFFF59E0B),
            Color(0xFFFDE68A)
        ),
        highlightColor = Color(0xFFFEF3C7),
        shadowColor = Color(0xFF1E0B38),
        accentGlowColor = Color(0xFFFBBF24),
        temperatureKelvin = 4200,
        contrastCurve = "Warm Highlight Roll-Off",
        vignetteStrength = 0.28f,
        skinToneOptimized = true,
        grainIntensity = 0.12f,
        tags = listOf("Golden Hour", "Organic", "Radiant", "4200K")
    )

    val EMERALD_NOIR = RenderPalette(
        id = "emerald_noir",
        name = "Emerald Noir & Silk",
        subtitle = "Deep Botanical Sophistication",
        category = "Fashion & High-Tech Prestige",
        description = "Verde jade lustroso equilibrado con negros turmalina y reflejos de titanio. Aporta un carácter distinguido, sobrio y profundamente contemporáneo a cualquier producción visual.",
        gradientColors = listOf(
            Color(0xFF022C22),
            Color(0xFF064E3B),
            Color(0xFF047857),
            Color(0xFF10B981),
            Color(0xFFA7F3D0)
        ),
        highlightColor = Color(0xFFECFDF5),
        shadowColor = Color(0xFF011A14),
        accentGlowColor = Color(0xFF34D399),
        temperatureKelvin = 5000,
        contrastCurve = "Deep Shadows Velvet Roll",
        vignetteStrength = 0.25f,
        skinToneOptimized = false,
        grainIntensity = 0.06f,
        tags = listOf("Prestige", "Botanical", "Deep Black", "5000K")
    )

    val OBSIDIAN_IRIS = RenderPalette(
        id = "obsidian_iris",
        name = "Obsidian Iris & Cyber",
        subtitle = "Modern Digital Elegance",
        category = "Next-Gen AI & Tech",
        description = "Grafito obsidiana combinado con una sutil luminiscencia violeta iris y reflejos de platino suave. Crea un ambiente inmersivo de estudio futurista con contraste de grado OLED.",
        gradientColors = listOf(
            Color(0xFF090A0F),
            Color(0xFF1E1B4B),
            Color(0xFF4338CA),
            Color(0xFF818CF8),
            Color(0xFFE0E7FF)
        ),
        highlightColor = Color(0xFFEEF2FF),
        shadowColor = Color(0xFF05060A),
        accentGlowColor = Color(0xFFA5B4FC),
        temperatureKelvin = 6500,
        contrastCurve = "OLED Infinite Black",
        vignetteStrength = 0.20f,
        skinToneOptimized = true,
        grainIntensity = 0.05f,
        tags = listOf("Cyber", "Futuristic", "OLED Pure", "6500K")
    )

    val TITANIUM_MONOCHROME = RenderPalette(
        id = "titanium_monochrome",
        name = "Titanium Monochrome Master",
        subtitle = "Classic 35mm Silver Halide",
        category = "Master Photography & Cinema",
        description = "Escala de grises con gradación de plata coloidal, profundidad tonal infinita y micro-textura suave inspirada en emulsión Kodak Tri-X 400. Elegancia atemporal insuperable.",
        gradientColors = listOf(
            Color(0xFF0A0A0A),
            Color(0xFF1F1F1F),
            Color(0xFF4B4B4B),
            Color(0xFF9E9E9E),
            Color(0xFFE5E5E5)
        ),
        highlightColor = Color(0xFFFFFFFF),
        shadowColor = Color(0xFF000000),
        accentGlowColor = Color(0xFFD4D4D4),
        temperatureKelvin = 5400,
        contrastCurve = "Zone System Ansel Fine",
        vignetteStrength = 0.32f,
        skinToneOptimized = true,
        grainIntensity = 0.16f,
        tags = listOf("Monochrome", "35mm Analog", "Silver Halide", "Fine Art")
    )

    val allPalettes: List<RenderPalette> = listOf(
        CHAMPAGNE_VELVET,
        NORDIC_OPAL,
        SUNSET_CASHMERE,
        EMERALD_NOIR,
        OBSIDIAN_IRIS,
        TITANIUM_MONOCHROME
    )

    fun getById(id: String?): RenderPalette {
        return allPalettes.find { it.id.equals(id, ignoreCase = true) } ?: CHAMPAGNE_VELVET
    }

    fun getByMood(mood: String?): RenderPalette {
        return when {
            mood?.contains("Vintage", ignoreCase = true) == true -> CHAMPAGNE_VELVET
            mood?.contains("Golden", ignoreCase = true) == true -> SUNSET_CASHMERE
            mood?.contains("Daylight", ignoreCase = true) == true || mood?.contains("Natural", ignoreCase = true) == true -> NORDIC_OPAL
            mood?.contains("Cyber", ignoreCase = true) == true -> OBSIDIAN_IRIS
            mood?.contains("Mono", ignoreCase = true) == true -> TITANIUM_MONOCHROME
            mood?.contains("Emerald", ignoreCase = true) == true -> EMERALD_NOIR
            else -> CHAMPAGNE_VELVET
        }
    }
}

/**
 * Dynamic rendering & execution dynamics profile to ensure
 * buttery smooth 60/120fps scrubbing, thermal safety, and zero stutter.
 */
data class DynamicOptimizationProfile(
    val framePacingTargetFps: Int = 60,
    val isSmoothLerpScrubbingEnabled: Boolean = true,
    val isAdaptiveThermalThrottlingEnabled: Boolean = true,
    val isVolumetricAuraEnabled: Boolean = true,
    val isSkinToneSafeColorLockEnabled: Boolean = true,
    val hdrRollOffStrength: Float = 0.85f,
    val currentThermalEfficiencyScore: Int = 98 // 0 - 100
)
