package com.example.data

import java.io.Serializable

enum class EcoComputeMode(
    val title: String,
    val badge: String,
    val description: String,
    val latencyMs: Int,
    val energySavingPercent: Int,
    val co2SavedGrams: Double
) {
    GREEN_ECO(
        title = "Eco-Green Neutral",
        badge = "🍃 100% ENERGÍA LIMPIA",
        description = "Enrutamiento inteligente a regiones cloud carbono-neutrales con regulación de frecuencia NPU/GPU que reduce el consumo de batería un 58%.",
        latencyMs = 135,
        energySavingPercent = 58,
        co2SavedGrams = 48.5
    ),
    ULTRA_LOW_LATENCY(
        title = "Turbo Edge Ultra-Baja Latencia",
        badge = "⚡ SUB-120MS EDGE",
        description = "Aceleración de hardware neural en el dispositivo (NPU/GPU Vulkan), preview reactivo inmediato y edición fluida con latencia imperceptible.",
        latencyMs = 82,
        energySavingPercent = 35,
        co2SavedGrams = 24.0
    ),
    CINEMA_MASTER(
        title = "Cinema Master Pro 4K HDR",
        badge = "🎬 10-BIT STUDIO HDR",
        description = "Máxima fidelidad cinematográfica en 4K UHD 60fps, espacio de color Rec.2020 HDR10 y máxima profundidad de razonamiento multimodal Veo 3.1.",
        latencyMs = 295,
        energySavingPercent = 12,
        co2SavedGrams = 6.5
    )
}

data class PlatformGatewaySpec(
    val id: String,
    val name: String,
    val brandColorHex: Long,
    val tagLine: String,
    val primaryAspectRatio: String,
    val secondaryAspectRatios: List<String>,
    val maxResolution: String,
    val maxFps: Int,
    val targetBitrateMbps: Int,
    val audioLoudnessTarget: String,
    val targetLufs: Float,
    val videoCodec: String,
    val containerFormat: String,
    val safeZoneDescription: String,
    val gatewayName: String,
    val gatewayEndpoint: String,
    val complianceProtocols: List<String>,
    val isC2paSigned: Boolean = true
) : Serializable

data class SecurityProtocolAudit(
    val c2paSignature: String,
    val sha256Fingerprint: String,
    val aesStorageEncrypted: Boolean = true,
    val tlsCipherSuite: String = "TLS_AES_256_GCM_SHA384",
    val ebuR128Status: String = "Normalizado a target ±0.2 LUFS",
    val truePeakDbtp: Float = -1.0f,
    val euAiActTransparencyTag: String = "Contenido Sintético Verificado: Veo 3.1 & Gemini Pro",
    val zeroKnowledgeProofId: String
) : Serializable

data class GatewayPublishResult(
    val platformId: String,
    val platformName: String,
    val status: String, // "Publicado", "En Tránsito", "Verificado"
    val ingestUrl: String,
    val transactionId: String,
    val latencyMs: Long,
    val co2OffsetGrams: Double,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable

object PlatformRegistry {
    val majorPlatforms: List<PlatformGatewaySpec> = listOf(
        PlatformGatewaySpec(
            id = "youtube",
            name = "YouTube & Shorts",
            brandColorHex = 0xFFFF0000,
            tagLine = "UHD 4K 60fps Broadcast & YouTube Shorts 9:16",
            primaryAspectRatio = "16:9",
            secondaryAspectRatios = listOf("9:16", "4:3"),
            maxResolution = "3840x2160 (4K UHD)",
            maxFps = 60,
            targetBitrateMbps = 45,
            audioLoudnessTarget = "-14.0 LUFS (EBU R128 Standard)",
            targetLufs = -14.0f,
            videoCodec = "AV1 / VP9 / H.264 High Profile L5.2",
            containerFormat = "MP4 FastStart (moov atom al inicio)",
            safeZoneDescription = "Margen de 10% para títulos y safe-zone en esquina inferior derecha para botones Shorts.",
            gatewayName = "YouTube Data API v3 Resumable Gateway",
            gatewayEndpoint = "https://www.googleapis.com/upload/youtube/v3/videos",
            complianceProtocols = listOf(
                "C2PA Content Credentials v2.1",
                "EBU R128 Audio Loudness Normalization (-14 LUFS)",
                "YouTube Synthetic Media Disclosure Certified",
                "CEA-608 & WebVTT Captions Synchronized"
            )
        ),
        PlatformGatewaySpec(
            id = "tiktok",
            name = "TikTok Native",
            brandColorHex = 0xFF00F2FE,
            tagLine = "Formato vertical 9:16 de alta retención viral",
            primaryAspectRatio = "9:16",
            secondaryAspectRatios = listOf("1:1"),
            maxResolution = "1080x1920 (FHD Vertical)",
            maxFps = 60,
            targetBitrateMbps = 20,
            audioLoudnessTarget = "-16.0 LUFS (TikTok Sound Engine)",
            targetLufs = -16.0f,
            videoCodec = "H.264 AVC Main / HEVC H.265",
            containerFormat = "MP4 Optimizado para Móvil",
            safeZoneDescription = "Zona segura reservada: 120dp inferior para descripción y 65dp lateral derecho para botones de like/comentarios.",
            gatewayName = "TikTok Content Posting Open API Gateway",
            gatewayEndpoint = "https://open-api.tiktok.com/share/video/upload/",
            complianceProtocols = listOf(
                "C2PA AI Provenance Watermark",
                "ITU-R BS.1770-4 Mobile Audio Limit (-1.0 dBTP)",
                "TikTok Commercial Music Rights Cleared",
                "COPPA Child Safety Filter Pass"
            )
        ),
        PlatformGatewaySpec(
            id = "instagram",
            name = "Instagram Reels & Feed",
            brandColorHex = 0xFFE1306C,
            tagLine = "Reels verticales 9:16 y Feed 1:1 con perfil sRGB",
            primaryAspectRatio = "9:16",
            secondaryAspectRatios = listOf("1:1", "4:5"),
            maxResolution = "1080x1920 (Reels) / 1080x1080",
            maxFps = 60,
            targetBitrateMbps = 25,
            audioLoudnessTarget = "-14.0 LUFS (Meta Standard)",
            targetLufs = -14.0f,
            videoCodec = "H.264 High Profile (Espacio sRGB / BT.709)",
            containerFormat = "MP4 Streaming Progresivo",
            safeZoneDescription = "Safe zone 9:16 libre de elementos clave en márgenes superior (80dp) e inferior (110dp).",
            gatewayName = "Meta Graph API (Instagram Reels Gateway)",
            gatewayEndpoint = "https://graph.facebook.com/v19.0/{ig_user_id}/media",
            complianceProtocols = listOf(
                "C2PA Metadata Standard Injection",
                "Meta Synthetic Media Flagging Protocol",
                "EBU R128 True Peak Compliance (-1.0 dBTP)",
                "Instagram Music Library Sync Ready"
            )
        ),
        PlatformGatewaySpec(
            id = "linkedin",
            name = "LinkedIn Business Video",
            brandColorHex = 0xFF0A66C2,
            tagLine = "Video corporativo 1:1 y 16:9 con subtítulos obligatorios",
            primaryAspectRatio = "1:1",
            secondaryAspectRatios = listOf("16:9", "9:16"),
            maxResolution = "1920x1080 / 1080x1080",
            maxFps = 30,
            targetBitrateMbps = 15,
            audioLoudnessTarget = "-16.0 LUFS (Optimizado para reproducción en silencio)",
            targetLufs = -16.0f,
            videoCodec = "H.264 Baseline/Main L4.1 + Sidecar WebVTT",
            containerFormat = "MP4 Enterprise Corporate Safe",
            safeZoneDescription = "Subtítulos incrustados en tercio inferior con alto contraste para reproducción sin sonido (85% de usuarios).",
            gatewayName = "LinkedIn UGC Marketing API Gateway",
            gatewayEndpoint = "https://api.linkedin.com/v2/assets?action=registerUpload",
            complianceProtocols = listOf(
                "GDPR & CCPA Corporate Data Privacy Compliance",
                "EBU R128 Noise Gate & EQ Profile",
                "W3C WebVTT Subtitle Standard",
                "C2PA Enterprise Provenance Seal"
            )
        ),
        PlatformGatewaySpec(
            id = "x_threads",
            name = "X (Twitter) & Threads",
            brandColorHex = 0xFF1DA1F2,
            tagLine = "Transmisión instantánea con buffer ultrarrápido",
            primaryAspectRatio = "16:9",
            secondaryAspectRatios = listOf("9:16", "1:1"),
            maxResolution = "1080p a 60fps",
            maxFps = 60,
            targetBitrateMbps = 18,
            audioLoudnessTarget = "-14.0 LUFS (Fast Buffer Audio)",
            targetLufs = -14.0f,
            videoCodec = "H.264 / AV1 Low-Latency Stream",
            containerFormat = "MP4 Fast-Chunking",
            safeZoneDescription = "Margen de 5% horizontal para visualización limpia en timeline de microblogging.",
            gatewayName = "X Media Upload API Chunked Gateway",
            gatewayEndpoint = "https://upload.twitter.com/1.1/media/upload.json",
            complianceProtocols = listOf(
                "C2PA Synthetic Media Declaration",
                "TLS 1.3 End-to-End Encrypted Ingestion",
                "True-Peak -1.0 dBTP Protection"
            )
        ),
        PlatformGatewaySpec(
            id = "smart_tv",
            name = "Smart TV (10-Foot Cinema)",
            brandColorHex = 0xFF8B5CF6,
            tagLine = "Experiencia de sala 4K HDR10+ a 3 metros con zonas seguras EBU R95",
            primaryAspectRatio = "16:9",
            secondaryAspectRatios = listOf("21:9", "16:10"),
            maxResolution = "3840x2160 (4K UHD) / 8K Cinema",
            maxFps = 60,
            targetBitrateMbps = 85,
            audioLoudnessTarget = "-23.0 LUFS (EBU R128 Broadcast / Dolby Atmos)",
            targetLufs = -23.0f,
            videoCodec = "HEVC Main10 / AV1 Cinema Profile (Rec.2020 10-bit)",
            containerFormat = "MP4 / TS Multi-Audio Stream",
            safeZoneDescription = "Zonas seguras de transmisión: 10% margen para títulos (Title Safe EBU R95) y 5% perimetral para acción (Action Safe). Interfaz D-Pad 10-ft.",
            gatewayName = "Living Room Broadcast & Cast Gateway (Android TV / Tizen / webOS)",
            gatewayEndpoint = "https://cast.google.com/publishers/",
            complianceProtocols = listOf(
                "EBU R128 Broadcast Loudness Target (-23 LUFS ±0.5)",
                "EBU R95 Safe Areas for 16:9 Television",
                "Dolby Vision Profile 8.1 / HDR10 Dynamic Metadata",
                "C2PA JUMBF Manifest Embedded (ATSC 3.0 / DVB-I Ready)"
            )
        ),
        PlatformGatewaySpec(
            id = "tablet_pro",
            name = "Tablet Pro (Studio Canvas)",
            brandColorHex = 0xFF10B981,
            tagLine = "Espacio de trabajo expandido 16:10 / 4:3 con lápiz óptico y bimanual",
            primaryAspectRatio = "16:10",
            secondaryAspectRatios = listOf("4:3", "3:2", "16:9"),
            maxResolution = "2880x1800 (QHD+ Retina Studio)",
            maxFps = 120,
            targetBitrateMbps = 55,
            audioLoudnessTarget = "-15.0 LUFS (Spatial Stereo Quad-Speakers)",
            targetLufs = -15.0f,
            videoCodec = "AV1 / ProRes 422 Proxy / H.265 High Tier",
            containerFormat = "MP4 FastStart + Timeline Project Package",
            safeZoneDescription = "Márgenes flotantes optimizados para agarre con dos manos (thumb-safe zones) y precisión sub-milimétrica para stylus / Apple Pencil / S-Pen.",
            gatewayName = "Tablet Multi-Window Studio Direct Pipeline",
            gatewayEndpoint = "https://cloud.veostudio.pro/api/v2/tablet/sync",
            complianceProtocols = listOf(
                "Material 3 Canonical List-Detail Layout Standard",
                "Stylus Hover & Pressure Curve Level 4096",
                "Zero Latency Off-Screen Canvas Mirroring",
                "C2PA Signed Vector Manifest"
            )
        ),
        PlatformGatewaySpec(
            id = "ios_apple",
            name = "iOS & Apple Ecosystem",
            brandColorHex = 0xFF38BDF8,
            tagLine = "ProRes 422 HQ, Display P3 Wide Gamut, AirPlay 2 y QuickTime C2PA",
            primaryAspectRatio = "16:9",
            secondaryAspectRatios = listOf("9:16", "4:5", "1:1"),
            maxResolution = "3840x2160 (4K 60fps ProRes)",
            maxFps = 60,
            targetBitrateMbps = 120,
            audioLoudnessTarget = "-16.0 LUFS (Apple Sound Check / CoreAudio)",
            targetLufs = -16.0f,
            videoCodec = "Apple ProRes 422 HQ / HEVC 10-bit Dolby Vision (Profile 8.4)",
            containerFormat = "QuickTime Movie (.mov) con átomos JUMBF / MP4",
            safeZoneDescription = "Dynamic Island safe area (54pt superior) y Home Indicator (34pt inferior). Espacio Display P3 nativo y 120Hz ProMotion.",
            gatewayName = "Apple AirPlay 2 & ProRes Transcode Gateway",
            gatewayEndpoint = "https://airplay.apple.com/v1/stream",
            complianceProtocols = listOf(
                "Apple ProRes Certified Specification",
                "Display P3 Wide Color Gamut (Rec.709 backward compatible)",
                "Apple CoreMedia & QuickTime C2PA Metadata Box",
                "AirPlay 2 Direct Cast Protocol"
            )
        )
    )

    fun getById(id: String): PlatformGatewaySpec {
        return majorPlatforms.firstOrNull { it.id == id } ?: majorPlatforms.first()
    }
}
