package com.example.data.camera

/**
 * Especificaciones y Modelos del Sistema de Cámara iOS Adaptativo y Universal VMSB©
 *
 * Emulación y adaptación de hardware y software de captura cinemática profesional estilo iOS:
 * ProRes 422 HQ, Apple Log, Grabación Cinemática con Rack Focus, Spatial Video estereoscópico,
 * Action Mode EIS y controles fotográficos manuales completos.
 */

enum class IosCameraMode(
    val label: String,
    val badge: String,
    val description: String,
    val maxFps: Int,
    val colorSpace: String
) {
    CINEMATIC(
        label = "CINEMÁTICO",
        badge = "4K HDR",
        description = "Apertura dinámica f/1.4 - f/16 con rack focus neuronal y mapa de profundidad",
        maxFps = 60,
        colorSpace = "Dolby Vision Profile 8.4"
    ),
    PRORES_LOG(
        label = "PRORES LOG",
        badge = "10-BIT HQ",
        description = "Apple Log nativo con curva tonal de alto rango dinámico para gradación de color",
        maxFps = 60,
        colorSpace = "Apple Log / Rec.2020"
    ),
    VIDEO_PRO(
        label = "VIDEO PRO",
        badge = "4K 60FPS",
        description = "Grabación de alta fidelidad con estabilización Action Mode y audio espacial",
        maxFps = 60,
        colorSpace = "Rec.709 / Rec.2020"
    ),
    SPATIAL_3D(
        label = "SPATIAL 3D",
        badge = "MV-HEVC",
        description = "Captura estereoscópica para Vision Pro / XR con calibración de disparidad IPD",
        maxFps = 30,
        colorSpace = "Spatial MV-HEVC SDR"
    ),
    SLOW_MO(
        label = "SLOW-MO",
        badge = "240 FPS",
        description = "Cámara ultra lenta a 240 fotogramas por segundo con interpolación de vectores",
        maxFps = 240,
        colorSpace = "Rec.709 High-Speed"
    ),
    TIME_LAPSE(
        label = "TIME-LAPSE",
        badge = "INTERVAL",
        description = "Intervalómetro continuo con bloqueo de exposición y compensación de movimiento",
        maxFps = 30,
        colorSpace = "DCI-P3"
    )
}

enum class IosLens(
    val label: String,
    val focalLengthMm: Int,
    val maxAperture: Float,
    val zoomFactor: Float,
    val sensorType: String
) {
    ULTRA_WIDE(".5", 13, 2.2f, 0.5f, "Ultra Gran Angular 120° FOV"),
    WIDE("1x", 24, 1.78f, 1.0f, "Principal Cuádruple Píxel 48MP"),
    TELE_2X("2x", 48, 1.78f, 2.0f, "Teleobjetivo Óptico 12MP"),
    TELE_5X("5x", 120, 2.8f, 5.0f, "Tetraprisma Periscópico 5x OIS")
}

data class IosProManualControls(
    val evBias: Float = 0.0f,                 // -2.0 a +2.0 EV
    val iso: Int = 100,                       // 50 a 6400 ISO
    val shutterSpeed: String = "1/60",        // 1/24 a 1/2000 seg
    val whiteBalanceKelvin: Int = 5500,       // 2800K a 7500K
    val cinematicAperture: Float = 2.8f,      // f/1.4 a f/16.0
    val isGrid3x3Enabled: Boolean = true,
    val isHorizonLevelerEnabled: Boolean = true,
    val isFocusPeakingEnabled: Boolean = false,
    val isHistogramEnabled: Boolean = true,
    val isTorchActive: Boolean = false,
    val isFrontCamera: Boolean = false,
    val aspectRatio: String = "16:9"          // 16:9, 9:16, 4:3, 1:1
)

data class CameraRecordedClip(
    val id: String,
    val title: String,
    val timestampMillis: Long,
    val durationSeconds: Double,
    val mode: IosCameraMode,
    val lens: IosLens,
    val resolution: String,
    val fps: Int,
    val totalVmsbFramesStreamed: Long,
    val berneLedgerHash: String,
    val latticeProofSignature: String,
    val zeroDataVerified: Boolean = true
)
