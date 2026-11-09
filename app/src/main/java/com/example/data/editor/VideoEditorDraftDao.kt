package com.example.data.editor

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object para la persistencia del borrador de edición de video en Room Database.
 */
@Dao
interface VideoEditorDraftDao {

    @Query("SELECT * FROM video_editor_drafts WHERE projectId = :projectId LIMIT 1")
    fun getDraftByProjectId(projectId: Int): Flow<VideoEditorDraftEntity?>

    @Query("SELECT * FROM video_editor_drafts WHERE projectId = :projectId LIMIT 1")
    suspend fun getDraftByProjectIdOnce(projectId: Int): VideoEditorDraftEntity?

    @Query("SELECT * FROM video_editor_drafts ORDER BY lastSavedTimestamp DESC")
    fun getAllDrafts(): Flow<List<VideoEditorDraftEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateDraft(draft: VideoEditorDraftEntity): Long

    @Query("DELETE FROM video_editor_drafts WHERE projectId = :projectId")
    suspend fun deleteDraftByProjectId(projectId: Int)

    @Query("SELECT COUNT(*) FROM video_editor_drafts")
    suspend fun getDraftCount(): Int
}
