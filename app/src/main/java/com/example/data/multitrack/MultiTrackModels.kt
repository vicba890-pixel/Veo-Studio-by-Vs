package com.example.data.multitrack

import java.io.Serializable
import java.util.UUID

/**
 * Enumeración de tipos de pistas soportadas en la arquitectura nativa multi-pista.
 */
enum class TrackType(val label: String, val badgeColorHex: Long) {
    VIDEO("Video / Base", 0xFF6366F1),
    TEXT("Text Overlay", 0xFFF59E0B),
    AUDIO("Audio Track", 0xFF10B981),
    EFFECT("FX & Filter", 0xFFA855F7)
}

/**
 * Posicionamiento en pantalla para superposiciones de texto (Text Overlays).
 */
enum class TextPosition(val label: String) {
    TOP("Top Header"),
    CENTER("Centered Title"),
    BOTTOM("Bottom Subtitle"),
    LOWER_THIRD("Lower-Third Broadcast")
}

/**
 * Estilos de animación para capas de texto.
 */
enum class TextAnimation(val label: String) {
    NONE("Static"),
    FADE_IN("Smooth Fade"),
    SLIDE_UP("Slide Up"),
    TYPEWRITER("Typewriter"),
    PULSE("Gentle Pulse")
}

/**
 * Carga útil y personalización de capa de texto.
 */
data class TextOverlayPayload(
    val text: String,
    val fontSizeSp: Float = 20f,
    val textColorArgb: Long = 0xFFFFFFFF,
    val backgroundColorArgb: Long = 0xCC0F172A,
    val position: TextPosition = TextPosition.BOTTOM,
    val isBold: Boolean = true,
    val hasBackground: Boolean = true,
    val animation: TextAnimation = TextAnimation.FADE_IN
) : Serializable

/**
 * Categorías de clips de audio.
 */
enum class AudioCategory(val label: String) {
    BGM("Background Music"),
    VOICEOVER("AI Voiceover"),
    SFX("Sound Effect"),
    AMBIENCE("Atmosphere Ambience")
}

/**
 * Carga útil de pista de audio con volumen, fundidos y muestras de forma de onda.
 */
data class AudioClipPayload(
    val audioTitle: String,
    val category: AudioCategory = AudioCategory.BGM,
    val volume: Float = 1.0f, // Rango 0.0f a 2.0f
    val fadeInMs: Long = 300L,
    val fadeOutMs: Long = 300L,
    val waveformSamples: List<Float> = emptyList() // Normalizado 0.0f .. 1.0f
) : Serializable

/**
 * Presets de efectos visuales y filtros aplicables a la línea de tiempo.
 */
enum class EffectType(val displayName: String, val iconName: String) {
    CINEMATIC_TEAL_ORANGE("Cinematic Teal & Orange", "palette"),
    VHS_RETRO_GLITCH("VHS 90s Glitch", "glitch"),
    FILM_GRAIN_NOIR("35mm Film Grain Noir", "grain"),
    VIGNETTE_FOCUS("Vignette & Focus Blur", "focus"),
    BLOOM_DREAMY("Dreamy Golden Bloom", "bloom"),
    NEON_CYBERPUNK("Cyberpunk Neon Glow", "neon"),
    SEPIA_VINTAGE("Sepia 1970s Warmth", "sepia")
}

/**
 * Carga útil de efecto visual en pista FX.
 */
data class EffectClipPayload(
    val effectType: EffectType = EffectType.CINEMATIC_TEAL_ORANGE,
    val intensity: Float = 0.8f, // 0.0f a 1.0f
    val blendModeName: String = "Overlay",
    val parameterTag: String = "cinema_v1"
) : Serializable

/**
 * Carga útil de clip de video local o importado en pista de video.
 */
data class VideoClipPayload(
    val uriString: String,
    val fileName: String,
    val durationMs: Long,
    val width: Int = 1920,
    val height: Int = 1080,
    val fps: Float = 30f,
    val mimeType: String? = "video/mp4",
    val format: String = "MP4",
    val isMasterTrack: Boolean = false,
    val bitrateKbps: Long = 0L,
    val aspectRatio: String = "16:9"
) : Serializable

/**
 * Clip elemental de línea de tiempo con tiempo de inicio, duración y payloads específicos.
 */
data class TimelineClip(
    val id: String = UUID.randomUUID().toString(),
    val trackId: String,
    val trackType: TrackType,
    val label: String,
    val startMs: Long,
    val durationMs: Long,
    val colorHex: Long,
    val textPayload: TextOverlayPayload? = null,
    val audioPayload: AudioClipPayload? = null,
    val effectPayload: EffectClipPayload? = null,
    val videoPayload: VideoClipPayload? = null,
    val isLocked: Boolean = false
) : Serializable {

    val endMs: Long get() = startMs + durationMs

    /**
     * Comprueba si el clip está activo en una marca de tiempo dada.
     */
    fun isActiveAt(timestampMs: Long): Boolean {
        return timestampMs in startMs until endMs
    }

    /**
     * Retorna el progreso normalizado (0f..1f) dentro del clip.
     */
    fun progressAt(timestampMs: Long): Float {
        if (durationMs <= 0L) return 0f
        return ((timestampMs - startMs).toFloat() / durationMs).coerceIn(0f, 1f)
    }
}

/**
 * Representa una pista / capa del editor con soporte para silenciado, ocultación y bloqueo.
 */
data class EditorTrack(
    val id: String = UUID.randomUUID().toString(),
    val type: TrackType,
    val name: String,
    val orderIndex: Int,
    val isMuted: Boolean = false,
    val isHidden: Boolean = false,
    val isLocked: Boolean = false,
    val volume: Float = 1.0f,
    val clips: List<TimelineClip> = emptyList()
) : Serializable

/**
 * Estado completo de composición multi-pista del proyecto de video.
 */
data class MultiTrackComposition(
    val tracks: List<EditorTrack> = emptyList(),
    val selectedClipId: String? = null,
    val selectedTrackId: String? = null,
    val isSnappingEnabled: Boolean = true
) : Serializable {

    /**
     * Retorna clips de texto activos en la posición actual del cabezal, excluyendo pistas ocultas.
     */
    fun getActiveTextClips(currentMs: Long): List<TimelineClip> {
        return tracks
            .filter { it.type == TrackType.TEXT && !it.isHidden }
            .flatMap { it.clips }
            .filter { it.isActiveAt(currentMs) }
    }

    /**
     * Retorna clips de audio activos en la posición actual del cabezal, excluyendo pistas silenciadas.
     */
    fun getActiveAudioClips(currentMs: Long): List<TimelineClip> {
        return tracks
            .filter { it.type == TrackType.AUDIO && !it.isMuted }
            .flatMap { it.clips }
            .filter { it.isActiveAt(currentMs) }
    }

    /**
     * Retorna clips de efectos activos en la posición actual del cabezal, excluyendo pistas ocultas.
     */
    fun getActiveEffectClips(currentMs: Long): List<TimelineClip> {
        return tracks
            .filter { it.type == TrackType.EFFECT && !it.isHidden }
            .flatMap { it.clips }
            .filter { it.isActiveAt(currentMs) }
    }

    /**
     * Retorna clips de video activos en la posición actual del cabezal, excluyendo pistas ocultas.
     */
    fun getActiveVideoClips(currentMs: Long): List<TimelineClip> {
        return tracks
            .filter { it.type == TrackType.VIDEO && !it.isHidden }
            .flatMap { it.clips }
            .filter { it.isActiveAt(currentMs) }
    }

    /**
     * Encuentra un clip por su identificador único.
     */
    fun findClip(clipId: String): TimelineClip? {
        return tracks.asSequence().flatMap { it.clips.asSequence() }.firstOrNull { it.id == clipId }
    }

    /**
     * Encuentra una pista por su identificador único.
     */
    fun findTrack(trackId: String): EditorTrack? {
        return tracks.firstOrNull { it.id == trackId }
    }
}
