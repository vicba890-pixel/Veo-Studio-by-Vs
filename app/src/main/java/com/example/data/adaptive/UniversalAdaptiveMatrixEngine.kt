package com.example.data.adaptive

import java.io.Serializable

/**
 * Categorías de Dispositivos Universales bajo el Estándar Adaptativo VMSB©.
 */
enum class UniversalTargetDevice(
    val id: String,
    val title: String,
    val subtitle: String,
    val primaryAspect: String,
    val supportedAspects: List<String>,
    val viewingDistance: String,
    val inputMethod: String,
    val maxResolution: String,
    val refreshRateHz: Int,
    val audioLufs: Float,
    val audioStandardName: String,
    val colorGamut: String,
    val preferredCodec: String,
    val containerFormat: String,
    val networkProtocol: String,
    val brandHex: Long
) : Serializable {

    MOBILE_HANDHELD(
        id = "mobile",
        title = "Smartphone Handheld",
        subtitle = "Ergonomía vertical a una mano, zonas táctiles con pulgar y bajo consumo",
        primaryAspect = "9:16",
        supportedAspects = listOf("9:16", "1:1", "16:9"),
        viewingDistance = "25 - 35 cm",
        inputMethod = "Táctil Directo (Pulgar / Multi-Touch)",
        maxResolution = "1080x1920 (FHD Vertical)",
        refreshRateHz = 60,
        audioLufs = -14.0f,
        audioStandardName = "Mobile Loudness (EBU R128 -14 LUFS / ITU-R BS.1770-4)",
        colorGamut = "sRGB / BT.709 Estándar",
        preferredCodec = "H.264 AVC High / H.265 Main",
        containerFormat = "MP4 FastStart (moov atom)",
        networkProtocol = "HTTPS Resumable Chunked Ingest",
        brandHex = 0xFF0284C7
    ),

    TABLET_PRO_STUDIO(
        id = "tablet",
        title = "Tablet Pro & Foldables",
        subtitle = "Lienzo de estudio bimanual, soporte de Stylus / Apple Pencil y multi-panel",
        primaryAspect = "16:10",
        supportedAspects = listOf("16:10", "4:3", "3:2", "16:9"),
        viewingDistance = "40 - 55 cm",
        inputMethod = "Lápiz Óptico 4096 Niveles / Bimanual / Trackpad",
        maxResolution = "2880x1800 (QHD+ Retina)",
        refreshRateHz = 120,
        audioLufs = -15.0f,
        audioStandardName = "Quad-Speaker Spatial Stereo (-15 LUFS)",
        colorGamut = "DCI-P3 Wide Color Gamut (10-bit)",
        preferredCodec = "AV1 / ProRes 422 Proxy / HEVC High",
        containerFormat = "MP4 / VMSB Project Package",
        networkProtocol = "Zero-Latency Local WebRTC & Cloud Sync",
        brandHex = 0xFF10B981
    ),

    SMART_TV_LIVING_ROOM(
        id = "smart_tv",
        title = "Smart TV (10-Foot Experience)",
        subtitle = "Cine en sala a 3 metros, control D-Pad, zonas seguras EBU R95 y Dolby Atmos",
        primaryAspect = "16:9",
        supportedAspects = listOf("16:9", "21:9 UltraWide Cinema"),
        viewingDistance = "2.5 - 4.0 metros",
        inputMethod = "Control Remoto D-Pad / Teclado HDMI-CEC / Voz",
        maxResolution = "3840x2160 (4K UHD) / 8K Cinema",
        refreshRateHz = 60,
        audioLufs = -23.0f,
        audioStandardName = "Broadcast Master Cinema (-23 LUFS ±0.5 / Dolby Atmos)",
        colorGamut = "Rec.2020 HDR10+ / Dolby Vision Profile 8.1",
        preferredCodec = "HEVC Main10 / AV1 Cinema 10-bit",
        containerFormat = "MPEG-TS / MP4 Multi-Audio Stream",
        networkProtocol = "Google Cast / Apple AirPlay 2 / DLNA UPnP",
        brandHex = 0xFF8B5CF6
    ),

    IOS_APPLE_ECOSYSTEM(
        id = "ios_apple",
        title = "iOS & Apple Ecosystem Pro",
        subtitle = "Apple ProRes 422 HQ, Display P3, QuickTime C2PA JUMBF y continuidad AirDrop",
        primaryAspect = "16:9",
        supportedAspects = listOf("16:9", "9:16", "4:5", "1:1"),
        viewingDistance = "Retina ProMotion (10 - 30 cm)",
        inputMethod = "Haptic Touch / ProMotion 120Hz Gestures",
        maxResolution = "3840x2160 (4K 60fps ProRes)",
        refreshRateHz = 120,
        audioLufs = -16.0f,
        audioStandardName = "Apple Sound Check / CoreAudio (-16 LUFS)",
        colorGamut = "Apple Display P3 (Wide Color Gamut)",
        preferredCodec = "Apple ProRes 422 HQ / HEVC 10-bit HDR",
        containerFormat = "QuickTime Movie (.mov) con átomo JUMBF C2PA",
        networkProtocol = "AirPlay 2 Direct Cast & AirDrop Transfer",
        brandHex = 0xFF38BDF8
    )
}

/**
 * Ficha de Métricas de Ergonomía y Accesibilidad Adaptativa VMSB©.
 */
data class UniversalErgonomicMetrics(
    val target: UniversalTargetDevice,
    val safeMarginPercent: Float, // Ej: 10% para TV, 5% para Tablet
    val minTouchTargetDp: Int, // Ej: 48dp o 64dp para TV
    val focusRingWidthDp: Int,
    val highContrastWcagRatio: String, // "AAA 7.0:1"
    val safeZoneDescription: String,
    val ergonomicsHighlights: List<String>,
    val accessibilityFeatures: List<String>
) : Serializable

/**
 * Motor de Especificación, Calibración y Matriz Adaptativa Universal VMSB©.
 */
object UniversalAdaptiveMatrixEngine {

    fun getErgonomicMetrics(device: UniversalTargetDevice): UniversalErgonomicMetrics {
        return when (device) {
            UniversalTargetDevice.MOBILE_HANDHELD -> UniversalErgonomicMetrics(
                target = device,
                safeMarginPercent = 8.0f,
                minTouchTargetDp = 48,
                focusRingWidthDp = 2,
                highContrastWcagRatio = "WCAG 2.2 AAA (7.2:1)",
                safeZoneDescription = "Margen de 80dp inferior para barras de navegación gestual y margen superior para cámara frontal punch-hole.",
                ergonomicsHighlights = listOf(
                    "Zona ergonómica de alcance con pulgar (Thumb-Zone)",
                    "Modo de uso vertical con una sola mano optimizado",
                    "Ahorro de batería dinámico (GPU/NPU scaling -58%)"
                ),
                accessibilityFeatures = listOf(
                    "Escalado tipográfico dinámico hasta 200%",
                    "Vibración háptica confirmatoria en botones clave",
                    "TalkBack / VoiceOver contentDescription integrado"
                )
            )

            UniversalTargetDevice.TABLET_PRO_STUDIO -> UniversalErgonomicMetrics(
                target = device,
                safeMarginPercent = 5.0f,
                minTouchTargetDp = 48,
                focusRingWidthDp = 3,
                highContrastWcagRatio = "WCAG 2.2 AAA (8.5:1)",
                safeZoneDescription = "Márgenes perimetrales reducidos al 5% para maximizar el área de dibujo y edición multitarea en pantalla dividida.",
                ergonomicsHighlights = listOf(
                    "Distribución canónica List-Detail Material 3",
                    "Inspector lateral persistente sin ocultar el timeline",
                    "Rechazo de palma inteligente y soporte Stylus Hover"
                ),
                accessibilityFeatures = listOf(
                    "Soporte nativo de puntero y cursor de ratón/trackpad",
                    "Atajos de teclado físicos USB-C / Bluetooth (Ctrl+Z, Space, etc.)",
                    "Soporte para modo alto contraste y Daltonismo DCI-P3"
                )
            )

            UniversalTargetDevice.SMART_TV_LIVING_ROOM -> UniversalErgonomicMetrics(
                target = device,
                safeMarginPercent = 10.0f,
                minTouchTargetDp = 64,
                focusRingWidthDp = 4,
                highContrastWcagRatio = "WCAG 2.2 AAA (12.0:1 Ultra-Alto)",
                safeZoneDescription = "Zonas de seguridad oficiales EBU R95: 10% perimetral para títulos (Title Safe) y 5% para acción (Action Safe), protegiendo contra sobre-escaneo (Overscan).",
                ergonomicsHighlights = listOf(
                    "Interfaz de 10 pies (10-Foot UI) legible desde 3 metros",
                    "Navegación omnidireccional fluida D-Pad (Up, Down, Left, Right, OK)",
                    "Anillos de foco de alto resplandor (Glowing Focus Borders)"
                ),
                accessibilityFeatures = listOf(
                    "Normalización de audio cinematográfico EBU R128 (-23 LUFS)",
                    "Subtítulos para sordos Closed Captions CEA-608 de alto contraste",
                    "Feedback auditivo espacial en navegación D-Pad"
                )
            )

            UniversalTargetDevice.IOS_APPLE_ECOSYSTEM -> UniversalErgonomicMetrics(
                target = device,
                safeMarginPercent = 6.0f,
                minTouchTargetDp = 48,
                focusRingWidthDp = 2,
                highContrastWcagRatio = "WCAG 2.2 AAA (7.8:1)",
                safeZoneDescription = "Reserva de Dynamic Island (54pt) y Home Indicator inferior (34pt) con área de visualización limpia en 16:9 y 9:16.",
                ergonomicsHighlights = listOf(
                    "Inyección de átomo C2PA JUMBF compatible con QuickTime Movie",
                    "Espacio de color nativo Apple Display P3 (Wide Color)",
                    "Continuidad de timeline compatible con Final Cut Pro XML"
                ),
                accessibilityFeatures = listOf(
                    "Soporte nativo de Apple VoiceOver & Rotor",
                    "Filtro Dynamic Type adaptativo al sistema",
                    "Respuesta táctil háptica Taptic Engine táctica"
                )
            )
        }
    }
}
