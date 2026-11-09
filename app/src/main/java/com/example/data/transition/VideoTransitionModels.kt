package com.example.data.transition

import androidx.compose.ui.graphics.Color
import java.io.Serializable

/**
 * =========================================================================================
 * VMSB(c) 2026 - MODELOS DE TRANSICIONES DE VIDEO CINEMATOGRÁFICAS
 * =========================================================================================
 *
 * Catálogo de transiciones profesionales entre tomas, clips o cortes de línea de tiempo:
 * - DISSOLVE: Disolvencia cruzada estándar (Cross Dissolve).
 * - FADE_BLACK: Fundido a negro y aparición (Fade to Black / From Black).
 * - WIPE_RIGHT: Barrido direccional horizontal suave.
 * - ZOOM_IN: Transición de zoom dinámico a corte.
 * - GLITCH_CYBER: Glitch digital de aberración cromática estilo cyberpunk.
 * - CROSS_BLUR: Desenfoque gaussiano direccional de cruce.
 * - SLIDE_UP: Desplazamiento cinemático vertical hacia arriba.
 * - WHIP_PAN: Latigazo de cámara con desenfoque de movimiento rápido.
 */

enum class TransitionType(
    val id: String,
    val displayName: String,
    val category: String,
    val description: String,
    val defaultDurationMs: Long,
    val iconEmoji: String,
    val accentColor: Color
) : Serializable {
    NONE(
        id = "none",
        displayName = "Corte Directo (Hard Cut)",
        category = "Básico",
        description = "Corte instantáneo entre fotogramas sin interpolación temporal.",
        defaultDurationMs = 0L,
        iconEmoji = "✂️",
        accentColor = Color(0xFF94A3B8)
    ),
    CROSS_DISSOLVE(
        id = "cross_dissolve",
        displayName = "Disolvencia Cruzada",
        category = "Cinemático Clásico",
        description = "Transición suave y fluida donde el clip saliente se mezcla linealmente con el entrante.",
        defaultDurationMs = 750L,
        iconEmoji = "🌫️",
        accentColor = Color(0xFF38BDF8)
    ),
    FADE_TO_BLACK(
        id = "fade_to_black",
        displayName = "Fundido a Negro (Dip to Black)",
        category = "Dramático",
        description = "Atenuación total a negro para separar escenas, capítulos o cambios temporales mayores.",
        defaultDurationMs = 1000L,
        iconEmoji = "🌑",
        accentColor = Color(0xFF1E293B)
    ),
    WIPE_RIGHT(
        id = "wipe_right",
        displayName = "Barrido Lateral (Wipe)",
        category = "Direccional",
        description = "Línea de barrido de izquierda a derecha revelando la siguiente toma de forma limpia.",
        defaultDurationMs = 600L,
        iconEmoji = "➡️",
        accentColor = Color(0xFF10B981)
    ),
    ZOOM_IN(
        id = "zoom_in",
        displayName = "Zoom Dinámico (Warp Zoom)",
        category = "Energético",
        description = "Aceleración de escala central que absorbe al espectador directamente hacia la nueva toma.",
        defaultDurationMs = 500L,
        iconEmoji = "🔍",
        accentColor = Color(0xFFF59E0B)
    ),
    GLITCH_CYBER(
        id = "glitch_cyber",
        displayName = "Glitch Digital Cyberpunk",
        category = "Efectos Visuales (VFX)",
        description = "Distorsión de barrido analógico con desplazamiento de canales RGB y artefactos cibernéticos.",
        defaultDurationMs = 450L,
        iconEmoji = "⚡",
        accentColor = Color(0xFFEC4899)
    ),
    CROSS_BLUR(
        id = "cross_blur",
        displayName = "Desenfoque Cruzado (Motion Blur)",
        category = "Elegante",
        description = "Desenfoque óptico gradual simulando movimiento de lente entre fotogramas clave.",
        defaultDurationMs = 700L,
        iconEmoji = "💧",
        accentColor = Color(0xFF8B5CF6)
    ),
    WHIP_PAN(
        id = "whip_pan",
        displayName = "Latigazo de Cámara (Whip Pan)",
        category = "Acción Rápida",
        description = "Giro ultrarrápido simulado de cámara horizontal con ráfaga de motion blur.",
        defaultDurationMs = 400L,
        iconEmoji = "🌪️",
        accentColor = Color(0xFFF97316)
    );

    companion object {
        fun fromId(id: String): TransitionType {
            return entries.find { it.id.equals(id, ignoreCase = true) } ?: NONE
        }
    }
}

data class VideoTransitionConfig(
    val transitionType: TransitionType = TransitionType.CROSS_DISSOLVE,
    val durationMs: Long = 750L,
    val isEnabled: Boolean = true
) : Serializable
