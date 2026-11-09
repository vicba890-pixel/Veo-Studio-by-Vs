package com.example.data.editor

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.colorgrading.ColorGradingPresets
import com.example.data.colorgrading.ColorGradingState
import com.example.data.transition.TransitionType
import com.example.data.transition.VideoTransitionConfig
import com.example.ui.components.preview.TimelineEditMarker
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable
import java.util.UUID

/**
 * =========================================================================================
 * VMSB(c) 2026 - ENTIDAD ROOM PARA HISTORIAL DE DESHACER/REHACER (UNDO/REDO STACK)
 * =========================================================================================
 *
 * Registra cada cambio de estado editorial en la base de datos local SQLite:
 * - Recorte de video (trimStart / trimEnd)
 * - Reencuadre y posición de cámara (Pan X, Pan Y, Zoom)
 * - Etalonaje y Color Grading (Lut Preset, Brillo, Contraste, Saturación, Temperatura)
 * - Transición de video activa (Tipo, Duración)
 * - Marcadores y puntos de corte de la línea de tiempo
 * - Textos de superposición
 *
 * Permite deshacer (Undo) y rehacer (Redo) de manera instantánea y persistente,
 * sobreviviendo a cierres accidentales o reinicios de la aplicación.
 */
@Entity(
    tableName = "video_editor_history",
    indices = [
        Index(value = ["projectId", "historyIndex"], unique = true),
        Index(value = ["projectId", "timestamp"])
    ]
)
data class VideoEditorHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Int,
    val historyIndex: Int,
    val actionDescription: String,
    val trimStart: Float = 0f,
    val trimEnd: Float = 1f,
    val overlayText: String = "",
    val reframePanX: Float = 0f,
    val reframePanY: Float = 0f,
    val reframeZoom: Float = 1.0f,
    val colorLutPresetId: String = "rec709_director",
    val colorBrightness: Float = 0.0f,
    val colorContrast: Float = 1.0f,
    val colorSaturation: Float = 1.0f,
    val colorTemperature: Float = 0.0f,
    val colorLutIntensity: Float = 1.0f,
    val transitionTypeId: String = "cross_dissolve",
    val transitionDurationMs: Long = 750L,
    val markersJson: String = "[]",
    val timestamp: Long = System.currentTimeMillis()
) : Serializable {

    fun parseTimelineMarkers(): List<TimelineEditMarker> {
        if (markersJson.isBlank() || markersJson == "[]") return emptyList()
        return try {
            val array = JSONArray(markersJson)
            val list = mutableListOf<TimelineEditMarker>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val id = obj.optString("id", UUID.randomUUID().toString())
                val pos = obj.optLong("positionMs", 0L)
                val label = obj.optString("label", "Marcador")
                val type = obj.optString("type", "EDIT_POINT")
                val colorVal = obj.optLong("color", 0xFFF59E0BL)
                list.add(
                    TimelineEditMarker(
                        id = id,
                        positionMs = pos,
                        label = label,
                        type = type,
                        color = Color(colorVal.toULong())
                    )
                )
            }
            list
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun toColorGradingState(): ColorGradingState {
        val preset = ColorGradingPresets.ALL_PRESETS.find { it.id == colorLutPresetId }
            ?: ColorGradingPresets.REC709_DIRECTOR
        return ColorGradingState(
            selectedLut = preset,
            brightness = colorBrightness,
            contrast = colorContrast,
            saturation = colorSaturation,
            temperature = colorTemperature,
            lutIntensity = colorLutIntensity
        )
    }

    fun toTransitionConfig(): VideoTransitionConfig {
        return VideoTransitionConfig(
            transitionType = TransitionType.fromId(transitionTypeId),
            durationMs = transitionDurationMs,
            isEnabled = transitionTypeId != TransitionType.NONE.id
        )
    }

    companion object {
        fun serializeMarkers(markers: List<TimelineEditMarker>): String {
            val array = JSONArray()
            for (m in markers) {
                val obj = JSONObject()
                obj.put("id", m.id)
                obj.put("positionMs", m.positionMs)
                obj.put("label", m.label)
                obj.put("type", m.type)
                obj.put("color", m.color.value.toLong())
                array.put(obj)
            }
            return array.toString()
        }
    }
}
