package com.example.data.metadata

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para la persistencia local de metadatos de video
 * en SQLite con Room Database.
 */
@Dao
interface VideoMetadataDao {

    @Query("SELECT * FROM video_metadata ORDER BY updatedAt DESC")
    fun getAllMetadata(): Flow<List<VideoMetadataEntity>>

    @Query("SELECT * FROM video_metadata WHERE projectId = :projectId ORDER BY updatedAt DESC LIMIT 1")
    fun getMetadataByProjectId(projectId: Int): Flow<VideoMetadataEntity?>

    @Query("SELECT * FROM video_metadata WHERE projectId = :projectId ORDER BY updatedAt DESC LIMIT 1")
    suspend fun getMetadataByProjectIdSync(projectId: Int): VideoMetadataEntity?

    @Query("SELECT * FROM video_metadata WHERE id = :id LIMIT 1")
    fun getMetadataById(id: Long): Flow<VideoMetadataEntity?>

    @Query("SELECT * FROM video_metadata WHERE videoUri = :videoUri LIMIT 1")
    fun getMetadataByVideoUri(videoUri: String): Flow<VideoMetadataEntity?>

    @Query("""
        SELECT * FROM video_metadata 
        WHERE title LIKE '%' || :query || '%' 
           OR description LIKE '%' || :query || '%' 
           OR tags LIKE '%' || :query || '%' 
        ORDER BY updatedAt DESC
    """)
    fun searchMetadata(query: String): Flow<List<VideoMetadataEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMetadata(metadata: VideoMetadataEntity): Long

    @Update
    suspend fun updateMetadata(metadata: VideoMetadataEntity)

    @Query("DELETE FROM video_metadata WHERE id = :id")
    suspend fun deleteMetadataById(id: Long)

    @Query("DELETE FROM video_metadata WHERE projectId = :projectId")
    suspend fun deleteMetadataByProjectId(projectId: Int)

    @Query("SELECT COUNT(*) FROM video_metadata")
    fun getMetadataCount(): Flow<Int>
}
