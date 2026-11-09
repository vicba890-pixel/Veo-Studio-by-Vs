package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoProjectDao {
    @Query("SELECT * FROM projects ORDER BY createdAt DESC")
    fun getAllProjects(): Flow<List<VideoProject>>

    @Query("SELECT * FROM projects ORDER BY createdAt DESC")
    fun getAll(): Flow<List<VideoProject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(videoProject: VideoProject): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(videoProject: VideoProject): Long

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteProjectById(id: Int)

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteById(id: Int)
}
