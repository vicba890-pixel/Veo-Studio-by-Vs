package com.example.data.multitrack.command

import com.example.data.multitrack.MultiTrackComposition
import com.example.data.multitrack.TimelineClip
import java.util.UUID

/**
 * Fábrica de comandos para el editor multi-pista.
 * Inspecciona el estado actual de la composición para capturar con precisión atómica
 * el estado anterior y posterior requerido para la reversibilidad perfecta (undo/redo).
 */
object MultiTrackCommandFactory {

    fun createAddClipCommand(
        trackId: String,
        clip: TimelineClip
    ): AddClipCommand {
        return AddClipCommand(
            trackId = trackId,
            clip = clip,
            title = "Add ${clip.trackType.name.lowercase().replaceFirstChar { it.uppercase() }}: ${clip.label}"
        )
    }

    fun createRemoveClipCommand(
        composition: MultiTrackComposition,
        clipId: String
    ): RemoveClipCommand? {
        val clip = composition.findClip(clipId) ?: return null
        return RemoveClipCommand(
            trackId = clip.trackId,
            clip = clip,
            title = "Delete Clip: ${clip.label}"
        )
    }

    fun createUpdateClipCommand(
        composition: MultiTrackComposition,
        updatedClip: TimelineClip
    ): UpdateClipCommand? {
        val oldClip = composition.findClip(updatedClip.id) ?: return null
        return UpdateClipCommand(
            oldClip = oldClip,
            newClip = updatedClip,
            title = "Edit: ${updatedClip.label}"
        )
    }

    fun createSplitClipCommand(
        composition: MultiTrackComposition,
        clipId: String,
        splitTimestampMs: Long
    ): SplitClipCommand? {
        val targetClip = composition.findClip(clipId) ?: return null
        if (splitTimestampMs <= targetClip.startMs + 100L || splitTimestampMs >= targetClip.endMs - 100L) {
            return null
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

        return SplitClipCommand(
            originalClip = targetClip,
            part1 = firstClip,
            part2 = secondClip,
            title = "Split: ${targetClip.label}"
        )
    }

    fun createDuplicateClipCommand(
        composition: MultiTrackComposition,
        clipId: String,
        offsetMs: Long = 300L,
        maxDurationMs: Long = 60000L
    ): DuplicateClipCommand? {
        val targetClip = composition.findClip(clipId) ?: return null
        val newStart = (targetClip.endMs + offsetMs).coerceAtMost(maxDurationMs - 500L)
        val newDuration = targetClip.durationMs.coerceAtMost(maxDurationMs - newStart)
        if (newDuration <= 200L) return null

        val duplicated = targetClip.copy(
            id = UUID.randomUUID().toString(),
            label = "${targetClip.label} (Copy)",
            startMs = newStart,
            durationMs = newDuration
        )

        return DuplicateClipCommand(
            originalClipId = targetClip.id,
            duplicatedClip = duplicated,
            title = "Duplicate: ${targetClip.label}"
        )
    }

    fun createTrimClipCommand(
        composition: MultiTrackComposition,
        clipId: String,
        newStartMs: Long,
        newDurationMs: Long
    ): MoveOrTrimClipCommand? {
        val targetClip = composition.findClip(clipId) ?: return null
        val coercedStart = newStartMs.coerceAtLeast(0L)
        val coercedDuration = newDurationMs.coerceAtLeast(200L)
        return MoveOrTrimClipCommand(
            clipId = clipId,
            trackId = targetClip.trackId,
            oldStartMs = targetClip.startMs,
            oldDurationMs = targetClip.durationMs,
            newStartMs = coercedStart,
            newDurationMs = coercedDuration,
            title = "Trim: ${targetClip.label}"
        )
    }

    fun createToggleMuteCommand(
        composition: MultiTrackComposition,
        trackId: String
    ): ToggleTrackMuteCommand? {
        val track = composition.findTrack(trackId) ?: return null
        val action = if (track.isMuted) "Unmute" else "Mute"
        return ToggleTrackMuteCommand(
            trackId = trackId,
            trackName = track.name,
            title = "$action Track • ${track.name}"
        )
    }

    fun createToggleVisibilityCommand(
        composition: MultiTrackComposition,
        trackId: String
    ): ToggleTrackVisibilityCommand? {
        val track = composition.findTrack(trackId) ?: return null
        val action = if (track.isHidden) "Show" else "Hide"
        return ToggleTrackVisibilityCommand(
            trackId = trackId,
            trackName = track.name,
            title = "$action Track • ${track.name}"
        )
    }

    fun createToggleLockCommand(
        composition: MultiTrackComposition,
        trackId: String
    ): ToggleTrackLockCommand? {
        val track = composition.findTrack(trackId) ?: return null
        val action = if (track.isLocked) "Unlock" else "Lock"
        return ToggleTrackLockCommand(
            trackId = trackId,
            trackName = track.name,
            title = "$action Track • ${track.name}"
        )
    }

    fun createSetVolumeCommand(
        composition: MultiTrackComposition,
        trackId: String,
        newVolume: Float
    ): SetTrackVolumeCommand? {
        val track = composition.findTrack(trackId) ?: return null
        return SetTrackVolumeCommand(
            trackId = trackId,
            trackName = track.name,
            oldVolume = track.volume,
            newVolume = newVolume,
            title = "Volume: ${track.name} ${(newVolume * 100).toInt()}%"
        )
    }
}
