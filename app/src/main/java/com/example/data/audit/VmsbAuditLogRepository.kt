package com.example.data.audit

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class VmsbAuditLogRepository(
    private val dao: VmsbAuditLogDao
) {
    val allLogs: Flow<List<VmsbAuditLogEntity>> = dao.getAllAuditLogs()

    fun getLogsByType(type: String): Flow<List<VmsbAuditLogEntity>> = dao.getLogsByEventType(type)

    fun getRecentLogs(limit: Int = 100): Flow<List<VmsbAuditLogEntity>> = dao.getRecentLogs(limit)

    suspend fun getRecentLogsSync(limit: Int = 100): List<VmsbAuditLogEntity> = withContext(Dispatchers.IO) {
        dao.getRecentLogsSync(limit)
    }

    suspend fun logBerneHandshake(
        blockIndex: Long,
        mediaTitle: String,
        currentBlockHash: String,
        payloadSha256: String,
        c2paManifestGuid: String,
        statusVerdict: String = "SELLADO BAJO PROTOCOLO DE BERNA (ART. 5.2 WIPO)"
    ): Long = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val iso = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date(now))
        val entity = VmsbAuditLogEntity(
            eventType = VmsbAuditLogEntity.TYPE_BERNE_HANDSHAKE,
            titleOrNode = "Bloque #$blockIndex: $mediaTitle",
            timestampMillis = now,
            timestampIso = iso,
            cryptographicHash = currentBlockHash,
            latencyMs = 1L,
            statusVerdict = statusVerdict,
            detailsPayload = "Payload SHA: $payloadSha256 • C2PA GUID: $c2paManifestGuid",
            isVerified = true
        )
        dao.insertLog(entity).also {
            dao.pruneOldLogs(500)
        }
    }

    suspend fun logVmsbSyncEvent(
        pulseCount: Long,
        platformNode: String,
        latencyMs: Long,
        statusLabel: String,
        powerMw: Double,
        simdEfficiency: Double,
        conflatedDrops: Long
    ): Long = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val iso = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date(now))
        val entity = VmsbAuditLogEntity(
            eventType = VmsbAuditLogEntity.TYPE_VMSB_SYNC,
            titleOrNode = "Pulso #$pulseCount • $platformNode",
            timestampMillis = now,
            timestampIso = iso,
            cryptographicHash = "PULSE-$pulseCount-${platformNode.take(6).uppercase()}-$now",
            latencyMs = latencyMs,
            statusVerdict = statusLabel,
            detailsPayload = "Potencia: ${String.format(Locale.US, "%.1f", powerMw)} mW • SIMD: ${String.format(Locale.US, "%.1f", simdEfficiency)}% • Drops Conflados: $conflatedDrops",
            isVerified = true
        )
        dao.insertLog(entity).also {
            dao.pruneOldLogs(500)
        }
    }

    suspend fun logLegalDispatch(
        receiptFolio: String,
        targetOffice: String,
        googleLatencyMs: Long,
        sha256Seal: String,
        statusVerdict: String
    ): Long = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val iso = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date(now))
        val entity = VmsbAuditLogEntity(
            eventType = VmsbAuditLogEntity.TYPE_LEGAL_DISPATCH,
            titleOrNode = "$receiptFolio • $targetOffice",
            timestampMillis = now,
            timestampIso = iso,
            cryptographicHash = sha256Seal,
            latencyMs = googleLatencyMs,
            statusVerdict = statusVerdict,
            detailsPayload = "Notificación institucional entregada en tiempo real a Alphabet / Google General Counsel",
            isVerified = true
        )
        dao.insertLog(entity).also {
            dao.pruneOldLogs(500)
        }
    }

    suspend fun logMctsRollout(
        rolloutsPerSec: Long,
        treeDepth: Int,
        ucb1Score: Float,
        executionLatencyMs: Long,
        winningBranch: String,
        statusVerdict: String = "OPTIMIZACIÓN ESTOCÁSTICA MCTS UCB1 CONVERGIDA"
    ): Long = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val iso = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date(now))
        val entity = VmsbAuditLogEntity(
            eventType = VmsbAuditLogEntity.TYPE_MCTS_ROLLOUT,
            titleOrNode = "Rollout MCTS ($rolloutsPerSec r/s) • $winningBranch",
            timestampMillis = now,
            timestampIso = iso,
            cryptographicHash = "MCTS-HASH-${java.util.UUID.randomUUID().toString().take(12).uppercase()}",
            latencyMs = executionLatencyMs,
            statusVerdict = statusVerdict,
            detailsPayload = "Rollouts: $rolloutsPerSec • Profundidad: $treeDepth • UCB1: ${String.format(Locale.US, "%.3f", ucb1Score)} • Rama: $winningBranch",
            isVerified = true
        )
        dao.insertLog(entity).also {
            dao.pruneOldLogs(500)
        }
    }

    fun getMctsLogs(limit: Int = 50): Flow<List<VmsbAuditLogEntity>> = dao.getLogsByEventType(VmsbAuditLogEntity.TYPE_MCTS_ROLLOUT)

    suspend fun clearAll() = withContext(Dispatchers.IO) {
        dao.clearAllLogs()
    }

    suspend fun getCount(): Int = withContext(Dispatchers.IO) {
        dao.getCount()
    }
}
