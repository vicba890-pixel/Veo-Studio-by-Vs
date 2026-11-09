package com.example.data.editor

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object para gestionar la pila de estados de Deshacer/Rehacer en Room Database.
 */
@Dao
interface VideoEditorHistoryDao {

    @Query("SELECT * FROM video_editor_history WHERE projectId = :projectId ORDER BY historyIndex ASC")
    fun getHistoryForProject(projectId: Int): Flow<List<VideoEditorHistoryEntity>>

    @Query("SELECT * FROM video_editor_history WHERE projectId = :projectId ORDER BY historyIndex ASC")
    suspend fun getHistoryListForProject(projectId: Int): List<VideoEditorHistoryEntity>

    @Query("SELECT * FROM video_editor_history WHERE projectId = :projectId AND historyIndex = :index LIMIT 1")
    suspend fun getSnapshotByIndex(projectId: Int, index: Int): VideoEditorHistoryEntity?

    @Query("SELECT MAX(historyIndex) FROM video_editor_history WHERE projectId = :projectId")
    suspend fun getMaxHistoryIndex(projectId: Int): Int?

    @Query("SELECT COUNT(*) FROM video_editor_history WHERE projectId = :projectId")
    suspend fun getHistoryCount(projectId: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSnapshot(snapshot: VideoEditorHistoryEntity): Long

    @Query("DELETE FROM video_editor_history WHERE projectId = :projectId AND historyIndex > :afterIndex")
    suspend fun deleteFutureSnapshots(projectId: Int, afterIndex: Int)

    @Query("DELETE FROM video_editor_history WHERE projectId = :projectId")
    suspend fun clearHistoryForProject(projectId: Int)

    @Query("DELETE FROM video_editor_history WHERE projectId = :projectId AND historyIndex < :minKeepIndex")
    suspend fun pruneOldSnapshots(projectId: Int, minKeepIndex: Int)
}
