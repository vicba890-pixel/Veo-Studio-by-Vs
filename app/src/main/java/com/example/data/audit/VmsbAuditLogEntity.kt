package com.example.data.audit

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * Entidad de Registro de Auditoría Local en Room para Handshakes del Protocolo de Berna
 * y Eventos de Sincronización del Motor VMSB©.
 */
@Entity(tableName = "vmsb_audit_logs")
data class VmsbAuditLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val eventType: String, // "BERNE_HANDSHAKE", "VMSB_SYNC_PULSE", "LEGAL_DISPATCH"
    val titleOrNode: String,
    val timestampMillis: Long = System.currentTimeMillis(),
    val timestampIso: String,
    val cryptographicHash: String,
    val latencyMs: Long = 0L,
    val statusVerdict: String,
    val detailsPayload: String = "",
    val isVerified: Boolean = true
) : Serializable {

    fun getMctsRolloutsPerSec(): Long {
        return try {
            val match = Regex("Rollouts:\\s*([0-9]+)").find(detailsPayload)
            match?.groupValues?.get(1)?.toLongOrNull() ?: 125000L
        } catch (_: Exception) {
            125000L
        }
    }

    fun getMctsTreeDepth(): Int {
        return try {
            val match = Regex("Profundidad:\\s*([0-9]+)").find(detailsPayload)
            match?.groupValues?.get(1)?.toIntOrNull() ?: 16
        } catch (_: Exception) {
            16
        }
    }

    fun getMctsUcb1Score(): Float {
        return try {
            val match = Regex("UCB1:\\s*([0-9.]+)").find(detailsPayload)
            match?.groupValues?.get(1)?.toFloatOrNull() ?: 0.92f
        } catch (_: Exception) {
            0.92f
        }
    }

    companion object {
        const val TYPE_BERNE_HANDSHAKE = "BERNE_HANDSHAKE"
        const val TYPE_VMSB_SYNC = "VMSB_SYNC_PULSE"
        const val TYPE_LEGAL_DISPATCH = "LEGAL_DISPATCH"
        const val TYPE_MCTS_ROLLOUT = "MCTS_ROLLOUT"
    }
}
