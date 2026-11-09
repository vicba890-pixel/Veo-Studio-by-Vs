package com.example.data.editor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * =========================================================================================
 * VMSB(c) 2026 - REPOSITORIO DE HISTORIAL DE DESHACER/REHACER (UNDO/REDO MANAGER)
 * =========================================================================================
 *
 * Administra el stack persistente de snapshots en Room Database:
 * 1. Cuando el usuario realiza una nueva edición, si nos encontramos en un estado intermedio
 *    (se hicieron Undos previos), se eliminan los estados posteriores (rama de historial limpia).
 * 2. Guarda el nuevo snapshot con su índice secuencial.
 * 3. Mantiene un límite configurable (ej. 50 revisiones) para optimizar memoria en SQLite.
 */
class VideoEditorHistoryRepository(private val historyDao: VideoEditorHistoryDao) {

    companion object {
        const val MAX_HISTORY_SNAPSHOTS = 50
    }

    fun getHistoryForProject(projectId: Int): Flow<List<VideoEditorHistoryEntity>> {
        return historyDao.getHistoryForProject(projectId)
    }

    suspend fun getHistoryListForProject(projectId: Int): List<VideoEditorHistoryEntity> {
        return withContext(Dispatchers.IO) {
            historyDao.getHistoryListForProject(projectId)
        }
    }

    suspend fun getSnapshotByIndex(projectId: Int, index: Int): VideoEditorHistoryEntity? {
        return withContext(Dispatchers.IO) {
            historyDao.getSnapshotByIndex(projectId, index)
        }
    }

    suspend fun getMaxHistoryIndex(projectId: Int): Int {
        return withContext(Dispatchers.IO) {
            historyDao.getMaxHistoryIndex(projectId) ?: -1
        }
    }

    /**
     * Inserta un nuevo estado en la pila de historial en Room Database.
     * Retorna el nuevo índice del snapshot guardado.
     */
    suspend fun pushSnapshot(
        projectId: Int,
        currentIndex: Int,
        actionDescription: String,
        trimStart: Float,
        trimEnd: Float,
        overlayText: String,
        reframePanX: Float,
        reframePanY: Float,
        reframeZoom: Float,
        colorLutPresetId: String,
        colorBrightness: Float,
        colorContrast: Float,
        colorSaturation: Float,
        colorTemperature: Float,
        colorLutIntensity: Float,
        transitionTypeId: String,
        transitionDurationMs: Long,
        markersJson: String
    ): Int = withContext(Dispatchers.IO) {
        // 1. Si el usuario realizó undos y hace una nueva acción, podar los estados "hacia adelante"
        if (currentIndex >= 0) {
            historyDao.deleteFutureSnapshots(projectId, currentIndex)
        }

        val nextIndex = currentIndex + 1
        val snapshot = VideoEditorHistoryEntity(
            projectId = projectId,
            historyIndex = nextIndex,
            actionDescription = actionDescription,
            trimStart = trimStart,
            trimEnd = trimEnd,
            overlayText = overlayText,
            reframePanX = reframePanX,
            reframePanY = reframePanY,
            reframeZoom = reframeZoom,
            colorLutPresetId = colorLutPresetId,
            colorBrightness = colorBrightness,
            colorContrast = colorContrast,
            colorSaturation = colorSaturation,
            colorTemperature = colorTemperature,
            colorLutIntensity = colorLutIntensity,
            transitionTypeId = transitionTypeId,
            transitionDurationMs = transitionDurationMs,
            markersJson = markersJson,
            timestamp = System.currentTimeMillis()
        )

        historyDao.insertSnapshot(snapshot)

        // 2. Podar snapshots viejos si superan MAX_HISTORY_SNAPSHOTS
        val totalCount = historyDao.getHistoryCount(projectId)
        if (totalCount > MAX_HISTORY_SNAPSHOTS) {
            val minKeep = nextIndex - MAX_HISTORY_SNAPSHOTS + 1
            if (minKeep > 0) {
                historyDao.pruneOldSnapshots(projectId, minKeep)
            }
        }

        nextIndex
    }

    suspend fun clearHistory(projectId: Int) {
        withContext(Dispatchers.IO) {
            historyDao.clearHistoryForProject(projectId)
        }
    }
}
