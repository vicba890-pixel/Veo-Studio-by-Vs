package com.example.data.multitrack

import java.util.UUID
import kotlin.math.abs
import kotlin.math.sin

/**
 * Gestor de lógica pura y transiciones inmutables de estado para la arquitectura multi-pista.
 */
object MultiTrackStateManager {

    /**
     * Genera una composición multi-pista inicial completa y equilibrada para cualquier proyecto.
     */
    fun createDefaultComposition(
        projectDurationMs: Long,
        projectTitle: String = "Cinematic Master"
    ): MultiTrackComposition {
        val duration = projectDurationMs.coerceAtLeast(5000L)

        // 1. Pista Base de Video (V1)
        val videoTrackId = "track_v1_video"
        val videoClip = TimelineClip(
            id = "clip_v1_main",
            trackId = videoTrackId,
            trackType = TrackType.VIDEO,
            label = "Master Source • $projectTitle",
            startMs = 0L,
            durationMs = duration,
            colorHex = 0xFF4338CA,
            isLocked = true
        )
        val videoTrack = EditorTrack(
            id = videoTrackId,
            type = TrackType.VIDEO,
            name = "V1 • Master Video",
            orderIndex = 0,
            clips = listOf(videoClip)
        )

        // 2. Pista de Superposiciones de Texto (T1)
        val textTrackId = "track_t1_text"
        val introTitleClip = TimelineClip(
            id = "clip_t1_intro",
            trackId = textTrackId,
            trackType = TrackType.TEXT,
            label = "Intro Title",
            startMs = 0L,
            durationMs = (duration * 0.28f).toLong().coerceIn(1500L, 4000L),
            colorHex = 0xFFF59E0B,
            textPayload = TextOverlayPayload(
                text = projectTitle.ifBlank { "DIRECTOR'S CUT" }.uppercase(),
                fontSizeSp = 22f,
                textColorArgb = 0xFFF8FAFC,
                backgroundColorArgb = 0xE60F172A,
                position = TextPosition.TOP,
                isBold = true,
                hasBackground = true,
                animation = TextAnimation.FADE_IN
            )
        )

        val midSubtitleStart = (duration * 0.35f).toLong()
        val midSubtitleDuration = (duration * 0.32f).toLong().coerceIn(1500L, 5000L)
        val subtitleClip = TimelineClip(
            id = "clip_t1_subtitle",
            trackId = textTrackId,
            trackType = TrackType.TEXT,
            label = "Lower-Third",
            startMs = midSubtitleStart,
            durationMs = midSubtitleDuration,
            colorHex = 0xFFD97706,
            textPayload = TextOverlayPayload(
                text = "Veo Studio • Multi-Track Synchronized Engine",
                fontSizeSp = 16f,
                textColorArgb = 0xFF38BDF8,
                backgroundColorArgb = 0xCC020617,
                position = TextPosition.LOWER_THIRD,
                isBold = true,
                hasBackground = true,
                animation = TextAnimation.SLIDE_UP
            )
        )

        val outroStart = (duration * 0.72f).toLong()
        val outroDuration = (duration - outroStart).coerceAtLeast(1000L)
        val outroClip = TimelineClip(
            id = "clip_t1_outro",
            trackId = textTrackId,
            trackType = TrackType.TEXT,
            label = "Credits CTA",
            startMs = outroStart,
            durationMs = outroDuration,
            colorHex = 0xFFB45309,
            textPayload = TextOverlayPayload(
                text = "Rendered with Ultra Precision • #VeoAI",
                fontSizeSp = 15f,
                textColorArgb = 0xFFFDE047,
                backgroundColorArgb = 0xD9111827,
                position = TextPosition.BOTTOM,
                isBold = false,
                hasBackground = true,
                animation = TextAnimation.PULSE
            )
        )

        val textTrack = EditorTrack(
            id = textTrackId,
            type = TrackType.TEXT,
            name = "T1 • Titles & Text",
            orderIndex = 1,
            clips = listOf(introTitleClip, subtitleClip, outroClip)
        )

        // 3. Pista de Audio / Música & SFX (A1)
        val audioTrackId = "track_a1_audio"
        val syntheticWaveform = List(36) { i ->
            (0.25f + 0.65f * abs(sin(i.toDouble() * 0.45))).toFloat().coerceIn(0.1f, 1.0f)
        }
        val bgmClip = TimelineClip(
            id = "clip_a1_bgm",
            trackId = audioTrackId,
            trackType = TrackType.AUDIO,
            label = "Cinematic Ambient Stem",
            startMs = 0L,
            durationMs = duration,
            colorHex = 0xFF10B981,
            audioPayload = AudioClipPayload(
                audioTitle = "Ambient Deep Horizon",
                category = AudioCategory.BGM,
                volume = 0.95f,
                fadeInMs = 400L,
                fadeOutMs = 600L,
                waveformSamples = syntheticWaveform
            )
        )

        val sfxStart = (duration * 0.30f).toLong()
        val sfxClip = TimelineClip(
            id = "clip_a1_sfx",
            trackId = audioTrackId,
            trackType = TrackType.AUDIO,
            label = "Whoosh SFX",
            startMs = sfxStart,
            durationMs = (duration * 0.15f).toLong().coerceIn(800L, 2000L),
            colorHex = 0xFF059669,
            audioPayload = AudioClipPayload(
                audioTitle = "Dynamic Transition Whoosh",
                category = AudioCategory.SFX,
                volume = 1.2f,
                fadeInMs = 100L,
                fadeOutMs = 300L,
                waveformSamples = listOf(0.2f, 0.4f, 0.9f, 0.8f, 0.5f, 0.2f)
            )
        )

        val audioTrack = EditorTrack(
            id = audioTrackId,
            type = TrackType.AUDIO,
            name = "A1 • Audio Stems",
            orderIndex = 2,
            volume = 1.0f,
            clips = listOf(bgmClip, sfxClip)
        )

        // 4. Pista de Efectos y Filtros Visuales (FX1)
        val fxTrackId = "track_fx1_effects"
        val fxTealOrange = TimelineClip(
            id = "clip_fx1_teal",
            trackId = fxTrackId,
            trackType = TrackType.EFFECT,
            label = "Teal & Orange",
            startMs = 0L,
            durationMs = (duration * 0.45f).toLong().coerceAtLeast(2000L),
            colorHex = 0xFFA855F7,
            effectPayload = EffectClipPayload(
                effectType = EffectType.CINEMATIC_TEAL_ORANGE,
                intensity = 0.85f
            )
        )

        val fxGlitchStart = (duration * 0.48f).toLong()
        val fxGlitch = TimelineClip(
            id = "clip_fx1_glitch",
            trackId = fxTrackId,
            trackType = TrackType.EFFECT,
            label = "VHS Glitch",
            startMs = fxGlitchStart,
            durationMs = (duration * 0.22f).toLong().coerceIn(1200L, 3000L),
            colorHex = 0xFF9333EA,
            effectPayload = EffectClipPayload(
                effectType = EffectType.VHS_RETRO_GLITCH,
                intensity = 0.70f
            )
        )

        val fxGrainStart = (duration * 0.73f).toLong()
        val fxGrain = TimelineClip(
            id = "clip_fx1_grain",
            trackId = fxTrackId,
            trackType = TrackType.EFFECT,
            label = "35mm Film Grain",
            startMs = fxGrainStart,
            durationMs = (duration - fxGrainStart).coerceAtLeast(1000L),
            colorHex = 0xFF7E22CE,
            effectPayload = EffectClipPayload(
                effectType = EffectType.FILM_GRAIN_NOIR,
                intensity = 0.75f
            )
        )

        val fxTrack = EditorTrack(
            id = fxTrackId,
            type = TrackType.EFFECT,
            name = "FX1 • Visual Filters",
            orderIndex = 3,
            clips = listOf(fxTealOrange, fxGlitch, fxGrain)
        )

        return MultiTrackComposition(
            tracks = listOf(videoTrack, textTrack, audioTrack, fxTrack),
            selectedClipId = introTitleClip.id,
            selectedTrackId = textTrackId,
            isSnappingEnabled = true
        )
    }

    /**
     * Añade un nuevo clip a una pista específica.
     */
    fun addClip(
        composition: MultiTrackComposition,
        trackId: String,
        clip: TimelineClip
    ): MultiTrackComposition {
        val updatedTracks = composition.tracks.map { track ->
            if (track.id == trackId) {
                track.copy(clips = (track.clips + clip).sortedBy { it.startMs })
            } else {
                track
            }
        }
        return composition.copy(
            tracks = updatedTracks,
            selectedClipId = clip.id,
            selectedTrackId = trackId
        )
    }

    /**
     * Actualiza las propiedades de un clip existente.
     */
    fun updateClip(
        composition: MultiTrackComposition,
        updatedClip: TimelineClip
    ): MultiTrackComposition {
        val updatedTracks = composition.tracks.map { track ->
            if (track.id == updatedClip.trackId) {
                track.copy(
                    clips = track.clips.map { if (it.id == updatedClip.id) updatedClip else it }
                        .sortedBy { it.startMs }
                )
            } else {
                track
            }
        }
        return composition.copy(tracks = updatedTracks)
    }

    /**
     * Elimina un clip por su identificador.
     */
    fun removeClip(
        composition: MultiTrackComposition,
        clipId: String
    ): MultiTrackComposition {
        val updatedTracks = composition.tracks.map { track ->
            track.copy(clips = track.clips.filterNot { it.id == clipId })
        }
        val newSelected = if (composition.selectedClipId == clipId) null else composition.selectedClipId
        return composition.copy(tracks = updatedTracks, selectedClipId = newSelected)
    }

    /**
     * Duplica un clip colocándolo a continuación o con un desplazamiento.
     */
    fun duplicateClip(
        composition: MultiTrackComposition,
        clipId: String,
        offsetMs: Long = 400L,
        maxDurationMs: Long = 60000L
    ): MultiTrackComposition {
        val targetClip = composition.findClip(clipId) ?: return composition
        val newStart = (targetClip.endMs + offsetMs).coerceAtMost(maxDurationMs - 500L)
        val newDuration = targetClip.durationMs.coerceAtMost(maxDurationMs - newStart)
        if (newDuration <= 200L) return composition

        val duplicated = targetClip.copy(
            id = UUID.randomUUID().toString(),
            label = "${targetClip.label} (Copy)",
            startMs = newStart,
            durationMs = newDuration
        )
        return addClip(composition, targetClip.trackId, duplicated)
    }

    /**
     * Divide un clip en dos sub-clips en la marca de tiempo indicada (Split at Playhead).
     */
    fun splitClipAt(
        composition: MultiTrackComposition,
        clipId: String,
        splitTimestampMs: Long
    ): MultiTrackComposition {
        val targetClip = composition.findClip(clipId) ?: return composition
        if (splitTimestampMs <= targetClip.startMs + 100L || splitTimestampMs >= targetClip.endMs - 100L) {
            return composition // Muy cerca del borde para dividir
        }

        val firstPartDuration = splitTimestampMs - targetClip.startMs
        val secondPartStart = splitTimestampMs
        val secondPartDuration = targetClip.endMs - splitTimestampMs

        val firstClip = targetClip.copy(
            durationMs = firstPartDuration,
            label = "${targetClip.label} • Pt 1"
        )
        val secondClip = targetClip.copy(
            id = UUID.randomUUID().toString(),
            startMs = secondPartStart,
            durationMs = secondPartDuration,
            label = "${targetClip.label} • Pt 2"
        )

        val updatedTracks = composition.tracks.map { track ->
            if (track.id == targetClip.trackId) {
                val newClips = track.clips.flatMap {
                    if (it.id == clipId) listOf(firstClip, secondClip) else listOf(it)
                }.sortedBy { it.startMs }
                track.copy(clips = newClips)
            } else {
                track
            }
        }

        return composition.copy(tracks = updatedTracks, selectedClipId = secondClip.id)
    }

    /**
     * Ajusta los puntos de inicio y duración de un clip (Trim / Resize).
     */
    fun trimClip(
        composition: MultiTrackComposition,
        clipId: String,
        newStartMs: Long,
        newDurationMs: Long
    ): MultiTrackComposition {
        val targetClip = composition.findClip(clipId) ?: return composition
        val coercedStart = newStartMs.coerceAtLeast(0L)
        val coercedDuration = newDurationMs.coerceAtLeast(200L)
        val updated = targetClip.copy(startMs = coercedStart, durationMs = coercedDuration)
        return updateClip(composition, updated)
    }

    /**
     * Alterna el estado de silenciado de una pista.
     */
    fun toggleTrackMute(
        composition: MultiTrackComposition,
        trackId: String
    ): MultiTrackComposition {
        val updatedTracks = composition.tracks.map { track ->
            if (track.id == trackId) track.copy(isMuted = !track.isMuted) else track
        }
        return composition.copy(tracks = updatedTracks)
    }

    /**
     * Alterna la visibilidad de una pista.
     */
    fun toggleTrackVisibility(
        composition: MultiTrackComposition,
        trackId: String
    ): MultiTrackComposition {
        val updatedTracks = composition.tracks.map { track ->
            if (track.id == trackId) track.copy(isHidden = !track.isHidden) else track
        }
        return composition.copy(tracks = updatedTracks)
    }

    /**
     * Alterna el bloqueo de una pista.
     */
    fun toggleTrackLock(
        composition: MultiTrackComposition,
        trackId: String
    ): MultiTrackComposition {
        val updatedTracks = composition.tracks.map { track ->
            if (track.id == trackId) track.copy(isLocked = !track.isLocked) else track
        }
        return composition.copy(tracks = updatedTracks)
    }

    /**
     * Ajusta el volumen maestro de una pista.
     */
    fun setTrackVolume(
        composition: MultiTrackComposition,
        trackId: String,
        volume: Float
    ): MultiTrackComposition {
        val updatedTracks = composition.tracks.map { track ->
            if (track.id == trackId) track.copy(volume = volume.coerceIn(0.0f, 2.0f)) else track
        }
        return composition.copy(tracks = updatedTracks)
    }

    /**
     * Calcula una marca de tiempo con atracción magnética a los límites de clips existentes.
     */
    fun calculateSnappedTimestamp(
        targetMs: Long,
        composition: MultiTrackComposition,
        thresholdMs: Long = 200L
    ): Long {
        if (!composition.isSnappingEnabled) return targetMs

        var closestDistance = Long.MAX_VALUE
        var bestSnap = targetMs

        // Snap a 0
        val distZero = abs(targetMs)
        if (distZero < thresholdMs && distZero < closestDistance) {
            closestDistance = distZero
            bestSnap = 0L
        }

        // Snap a inicios y fines de todos los clips
        for (track in composition.tracks) {
            for (clip in track.clips) {
                val distStart = abs(targetMs - clip.startMs)
                if (distStart < thresholdMs && distStart < closestDistance) {
                    closestDistance = distStart
                    bestSnap = clip.startMs
                }
                val distEnd = abs(targetMs - clip.endMs)
                if (distEnd < thresholdMs && distEnd < closestDistance) {
                    closestDistance = distEnd
                    bestSnap = clip.endMs
                }
            }
        }

        return bestSnap
    }
}
