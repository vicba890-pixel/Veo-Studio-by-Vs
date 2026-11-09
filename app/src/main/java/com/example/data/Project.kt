package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "projects")
data class Project(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val prompt: String,
    val storyboardText: String = "",
    val scriptText: String = "",
    val transcriptionText: String = "",
    val voiceOverLanguage: String = "English",
    val selectedMood: String = "Cinematic Teal & Orange",
    val isSubjectReframingEnabled: Boolean = false,
    val isUpscalingEnabled: Boolean = false,
    val bgMusicPrompt: String = "",
    val bgMusicUrl: String = "",
    val tagsText: String = "",
    val complianceStatus: String = "Pending",
    val complianceDetails: String = "",
    val aspectRatio: String = "16:9",
    val resolution: String = "1080p",
    val durationSec: Int = 15,
    val cloudSyncStatus: String = "Not Synced",
    val videoUri: String = "",
    val createdAt: Long = System.currentTimeMillis()
) : Serializable
