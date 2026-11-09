package com.example.data.media

import java.io.Serializable
import java.util.Locale

/**
 * Metadatos detallados de video extraídos mediante el servicio de selección de medios Veo.
 */
data class LocalVideoMetadata(
    val uri: String,
    val name: String,
    val sizeBytes: Long,
    val durationMs: Long,
    val width: Int,
    val height: Int,
    val fps: Float = 30f,
    val bitrateKbps: Long = 0L,
    val mimeType: String = "video/mp4",
    val format: String = "MP4",
    val aspectRatio: String = "16:9",
    val resolutionLabel: String = "1080p FHD",
    val hasAudio: Boolean = true,
    val rotation: Int = 0,
    val isValid: Boolean = true,
    val validationError: String? = null
) : Serializable {

    val formattedDuration: String
        get() {
            val totalSeconds = (durationMs / 1000).coerceAtLeast(0)
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            val millisFraction = (durationMs % 1000) / 100
            return String.format(Locale.US, "%02d:%02d.%d", minutes, seconds, millisFraction)
        }

    val formattedSize: String
        get() {
            val mb = sizeBytes / (1024.0 * 1024.0)
            return if (mb >= 1.0) {
                String.format(Locale.US, "%.1f MB", mb)
            } else {
                val kb = sizeBytes / 1024.0
                String.format(Locale.US, "%.0f KB", kb)
            }
        }

    val dimensionsLabel: String
        get() = "${width}x${height}"
}

/**
 * Modo de inserción del video seleccionado en la línea de tiempo del editor Veo.
 */
enum class VideoPlacementMode(val title: String, val subtitle: String) {
    SET_AS_MASTER(
        title = "Pista Master (V1)",
        subtitle = "Reemplaza video base y ajusta duración total del proyecto"
    ),
    INSERT_AT_PLAYHEAD(
        title = "Insertar en Cabezal",
        subtitle = "Coloca el clip de video en la posición actual de reproducción"
    ),
    APPEND_TO_TIMELINE(
        title = "Añadir al Final",
        subtitle = "Concatena el clip al final de la línea de tiempo"
    ),
    CREATE_NEW_PROJECT(
        title = "Nuevo Proyecto",
        subtitle = "Crea un proyecto independiente en la biblioteca y abre el editor"
    )
}

/**
 * Resultado de la inspección asíncrona de un URI de video local.
 */
sealed class VideoInspectionResult : Serializable {
    data class Success(val metadata: LocalVideoMetadata) : VideoInspectionResult()
    data class Error(val message: String, val rawUri: String? = null) : VideoInspectionResult()
}

/**
 * Video predefinido de estudio para pruebas rápidas de alta fidelidad.
 */
data class CuratedStudioVideo(
    val id: String,
    val title: String,
    val description: String,
    val thumbnailEmoji: String,
    val uriString: String,
    val metadata: LocalVideoMetadata
) : Serializable
