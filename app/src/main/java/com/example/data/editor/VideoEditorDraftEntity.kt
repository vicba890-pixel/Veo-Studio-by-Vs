package com.example.data.editor

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.colorgrading.ColorGradingPresets
import com.example.data.colorgrading.ColorGradingState
import com.example.ui.components.preview.TimelineEditMarker
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable
import java.util.UUID

/**
 * =========================================================================================
 * VMSB(c) 2026 - ENTIDAD ROOM PARA AUTO-GUARDADO REACTIVO DEL EDITOR DE VIDEO
 * =========================================================================================
 * Persiste localmente en SQLite el progreso integral del usuario en el editor de video
 * tras cada cambio:
 * 1. Puntos de recorte / Trimming (In / Out).
 * 2. Posicionamiento de reencuadre inteligente (Pan X, Pan Y, Zoom).
 * 3. Ajustes de etalonaje y color grading (Preset LUT, brillo, contraste, saturación, temperatura).
 * 4. Marcadores y cortes en la línea de tiempo (Timeline Markers).
 * 5. Textos superpuestos y subtítulos (Overlay text).
 * 6. Metadatos editoriales (Mood, Aspect Ratio, Resolución, Idioma de narración, Música).
 * 7. Estampa de tiempo y contador de versión del auto-guardado.
 */
@Entity(tableName = "video_editor_drafts")
data class VideoEditorDraftEntity(
    @PrimaryKey
    val projectId: Int,
    val projectTitle: String = "",
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
    val selectedMood: String = "Cinematic Teal & Orange",
    val aspectRatio: String = "16:9",
    val resolution: String = "1080p",
    val markersJson: String = "[]",
    val voiceOverLanguage: String = "English",
    val bgMusicPrompt: String = "",
    val scriptText: String = "",
    val storyboardText: String = "",
    val tagsText: String = "",
    val lastSavedTimestamp: Long = System.currentTimeMillis(),
    val saveVersion: Long = 1L,
    val autoSaveReason: String = "Edición inicial"
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

    companion object {
        fun serializeTimelineMarkers(markers: List<TimelineEditMarker>): String {
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
