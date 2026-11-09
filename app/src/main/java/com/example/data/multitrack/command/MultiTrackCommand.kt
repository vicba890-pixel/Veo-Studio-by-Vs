package com.example.data.multitrack.command

import com.example.data.multitrack.EditorTrack
import com.example.data.multitrack.MultiTrackComposition
import com.example.data.multitrack.TimelineClip
import java.util.UUID

/**
 * Contrato base para el Patrón Command en el sistema de edición multi-pista.
 * Encapsula de forma granular la ejecución y reversión completa (undo/redo) de cada acción editorial.
 */
interface MultiTrackCommand {
    /** Título descriptivo para la barra de herramientas, historial e inspección visual */
    val title: String

    /** Marca de tiempo de invocación de la acción */
    val timestamp: Long

    /** Aplica la acción sobre la composición devolviendo el nuevo estado inmutable */
    fun execute(composition: MultiTrackComposition): MultiTrackComposition

    /** Revierte exactamente la acción devolviendo el estado anterior */
    fun undo(composition: MultiTrackComposition): MultiTrackComposition
}

/**
 * Comando para añadir un nuevo clip a una pista específica.
 */
data class AddClipCommand(
    val trackId: String,
    val clip: TimelineClip,
    override val title: String = "Add Clip • ${clip.label}",
    override val timestamp: Long = System.currentTimeMillis()
) : MultiTrackCommand {

    override fun execute(composition: MultiTrackComposition): MultiTrackComposition {
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

    override fun undo(composition: MultiTrackComposition): MultiTrackComposition {
        val updatedTracks = composition.tracks.map { track ->
            if (track.id == trackId) {
                track.copy(clips = track.clips.filterNot { it.id == clip.id })
            } else {
                track
            }
        }
        val newSelected = if (composition.selectedClipId == clip.id) null else composition.selectedClipId
        return composition.copy(tracks = updatedTracks, selectedClipId = newSelected)
    }
}

/**
 * Comando para eliminar un clip existente preservando toda su información para reversión.
 */
data class RemoveClipCommand(
    val trackId: String,
    val clip: TimelineClip,
    override val title: String = "Delete Clip • ${clip.label}",
    override val timestamp: Long = System.currentTimeMillis()
) : MultiTrackCommand {

    override fun execute(composition: MultiTrackComposition): MultiTrackComposition {
        val updatedTracks = composition.tracks.map { track ->
            if (track.id == trackId) {
                track.copy(clips = track.clips.filterNot { it.id == clip.id })
            } else {
                track
            }
        }
        val newSelected = if (composition.selectedClipId == clip.id) null else composition.selectedClipId
        return composition.copy(tracks = updatedTracks, selectedClipId = newSelected)
    }

    override fun undo(composition: MultiTrackComposition): MultiTrackComposition {
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
}

/**
 * Comando para actualizar las propiedades de un clip (inspector, duración, estilo).
 */
data class UpdateClipCommand(
    val oldClip: TimelineClip,
    val newClip: TimelineClip,
    override val title: String = "Update Clip • ${newClip.label}",
    override val timestamp: Long = System.currentTimeMillis()
) : MultiTrackCommand {

    override fun execute(composition: MultiTrackComposition): MultiTrackComposition {
        val updatedTracks = composition.tracks.map { track ->
            if (track.id == newClip.trackId) {
                track.copy(
                    clips = track.clips.map { if (it.id == newClip.id) newClip else it }.sortedBy { it.startMs }
                )
            } else {
                track
            }
        }
        return composition.copy(tracks = updatedTracks, selectedClipId = newClip.id)
    }

    override fun undo(composition: MultiTrackComposition): MultiTrackComposition {
        val updatedTracks = composition.tracks.map { track ->
            if (track.id == oldClip.trackId) {
                track.copy(
                    clips = track.clips.map { if (it.id == oldClip.id) oldClip else it }.sortedBy { it.startMs }
                )
            } else {
                track
            }
        }
        return composition.copy(tracks = updatedTracks, selectedClipId = oldClip.id)
    }
}

/**
 * Comando para dividir un clip en dos partes independientes en la aguja de reproducción.
 */
data class SplitClipCommand(
    val originalClip: TimelineClip,
    val part1: TimelineClip,
    val part2: TimelineClip,
    override val title: String = "Split Clip • ${originalClip.label}",
    override val timestamp: Long = System.currentTimeMillis()
) : MultiTrackCommand {

    override fun execute(composition: MultiTrackComposition): MultiTrackComposition {
        val updatedTracks = composition.tracks.map { track ->
            if (track.id == originalClip.trackId) {
                val newClips = track.clips.flatMap {
                    if (it.id == originalClip.id) listOf(part1, part2) else listOf(it)
                }.sortedBy { it.startMs }
                track.copy(clips = newClips)
            } else {
                track
            }
        }
        return composition.copy(tracks = updatedTracks, selectedClipId = part2.id)
    }

    override fun undo(composition: MultiTrackComposition): MultiTrackComposition {
        val updatedTracks = composition.tracks.map { track ->
            if (track.id == originalClip.trackId) {
                val restoredClips = track.clips.filterNot { it.id == part1.id || it.id == part2.id } + originalClip
                track.copy(clips = restoredClips.sortedBy { it.startMs })
            } else {
                track
            }
        }
        return composition.copy(tracks = updatedTracks, selectedClipId = originalClip.id)
    }
}

/**
 * Comando para duplicar un clip existente.
 */
data class DuplicateClipCommand(
    val originalClipId: String,
    val duplicatedClip: TimelineClip,
    override val title: String = "Duplicate • ${duplicatedClip.label}",
    override val timestamp: Long = System.currentTimeMillis()
) : MultiTrackCommand {

    override fun execute(composition: MultiTrackComposition): MultiTrackComposition {
        val updatedTracks = composition.tracks.map { track ->
            if (track.id == duplicatedClip.trackId) {
                track.copy(clips = (track.clips + duplicatedClip).sortedBy { it.startMs })
            } else {
                track
            }
        }
        return composition.copy(tracks = updatedTracks, selectedClipId = duplicatedClip.id)
    }

    override fun undo(composition: MultiTrackComposition): MultiTrackComposition {
        val updatedTracks = composition.tracks.map { track ->
            if (track.id == duplicatedClip.trackId) {
                track.copy(clips = track.clips.filterNot { it.id == duplicatedClip.id })
            } else {
                track
            }
        }
        val newSelected = if (composition.selectedClipId == duplicatedClip.id) originalClipId else composition.selectedClipId
        return composition.copy(tracks = updatedTracks, selectedClipId = newSelected)
    }
}

/**
 * Comando para recortar o desplazar un clip (Trim / Move).
 */
data class MoveOrTrimClipCommand(
    val clipId: String,
    val trackId: String,
    val oldStartMs: Long,
    val oldDurationMs: Long,
    val newStartMs: Long,
    val newDurationMs: Long,
    override val title: String = "Trim Clip",
    override val timestamp: Long = System.currentTimeMillis()
) : MultiTrackCommand {

    override fun execute(composition: MultiTrackComposition): MultiTrackComposition {
        val updatedTracks = composition.tracks.map { track ->
            if (track.id == trackId) {
                val updatedClips = track.clips.map { clip ->
                    if (clip.id == clipId) {
                        clip.copy(startMs = newStartMs, durationMs = newDurationMs)
                    } else clip
                }.sortedBy { it.startMs }
                track.copy(clips = updatedClips)
            } else track
        }
        return composition.copy(tracks = updatedTracks, selectedClipId = clipId)
    }

    override fun undo(composition: MultiTrackComposition): MultiTrackComposition {
        val updatedTracks = composition.tracks.map { track ->
            if (track.id == trackId) {
                val restoredClips = track.clips.map { clip ->
                    if (clip.id == clipId) {
                        clip.copy(startMs = oldStartMs, durationMs = oldDurationMs)
                    } else clip
                }.sortedBy { it.startMs }
                track.copy(clips = restoredClips)
            } else track
        }
        return composition.copy(tracks = updatedTracks, selectedClipId = clipId)
    }
}

/**
 * Comando para conmutar el estado de silenciado (Mute) de una pista.
 */
data class ToggleTrackMuteCommand(
    val trackId: String,
    val trackName: String,
    override val title: String = "Toggle Mute • $trackName",
    override val timestamp: Long = System.currentTimeMillis()
) : MultiTrackCommand {

    override fun execute(composition: MultiTrackComposition): MultiTrackComposition {
        val updatedTracks = composition.tracks.map { track ->
            if (track.id == trackId) track.copy(isMuted = !track.isMuted) else track
        }
        return composition.copy(tracks = updatedTracks)
    }

    override fun undo(composition: MultiTrackComposition): MultiTrackComposition {
        return execute(composition) // Conmutación simétrica
    }
}

/**
 * Comando para conmutar la visibilidad de una pista.
 */
data class ToggleTrackVisibilityCommand(
    val trackId: String,
    val trackName: String,
    override val title: String = "Toggle Visibility • $trackName",
    override val timestamp: Long = System.currentTimeMillis()
) : MultiTrackCommand {

    override fun execute(composition: MultiTrackComposition): MultiTrackComposition {
        val updatedTracks = composition.tracks.map { track ->
            if (track.id == trackId) track.copy(isHidden = !track.isHidden) else track
        }
        return composition.copy(tracks = updatedTracks)
    }

    override fun undo(composition: MultiTrackComposition): MultiTrackComposition {
        return execute(composition) // Conmutación simétrica
    }
}

/**
 * Comando para conmutar el bloqueo de edición de una pista.
 */
data class ToggleTrackLockCommand(
    val trackId: String,
    val trackName: String,
    override val title: String = "Toggle Lock • $trackName",
    override val timestamp: Long = System.currentTimeMillis()
) : MultiTrackCommand {

    override fun execute(composition: MultiTrackComposition): MultiTrackComposition {
        val updatedTracks = composition.tracks.map { track ->
            if (track.id == trackId) track.copy(isLocked = !track.isLocked) else track
        }
        return composition.copy(tracks = updatedTracks)
    }

    override fun undo(composition: MultiTrackComposition): MultiTrackComposition {
        return execute(composition) // Conmutación simétrica
    }
}

/**
 * Comando para ajustar el volumen maestro de una pista.
 */
data class SetTrackVolumeCommand(
    val trackId: String,
    val trackName: String,
    val oldVolume: Float,
    val newVolume: Float,
    override val title: String = "Set Volume • $trackName",
    override val timestamp: Long = System.currentTimeMillis()
) : MultiTrackCommand {

    override fun execute(composition: MultiTrackComposition): MultiTrackComposition {
        val updatedTracks = composition.tracks.map { track ->
            if (track.id == trackId) track.copy(volume = newVolume.coerceIn(0f, 2f)) else track
        }
        return composition.copy(tracks = updatedTracks)
    }

    override fun undo(composition: MultiTrackComposition): MultiTrackComposition {
        val updatedTracks = composition.tracks.map { track ->
            if (track.id == trackId) track.copy(volume = oldVolume.coerceIn(0f, 2f)) else track
        }
        return composition.copy(tracks = updatedTracks)
    }
}

/**
 * Comando compuesto que agrupa múltiples acciones atómicas (ej. refactorización de timeline o batch delete).
 */
data class BatchCommand(
    override val title: String,
    val commands: List<MultiTrackCommand>,
    override val timestamp: Long = System.currentTimeMillis()
) : MultiTrackCommand {

    override fun execute(composition: MultiTrackComposition): MultiTrackComposition {
        return commands.fold(composition) { current, cmd -> cmd.execute(current) }
    }

    override fun undo(composition: MultiTrackComposition): MultiTrackComposition {
        return commands.reversed().fold(composition) { current, cmd -> cmd.undo(current) }
    }
}
