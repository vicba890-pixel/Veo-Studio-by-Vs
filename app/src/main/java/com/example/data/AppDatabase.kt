package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.audit.VmsbAuditLogDao
import com.example.data.audit.VmsbAuditLogEntity
import com.example.data.metadata.VideoMetadataDao
import com.example.data.metadata.VideoMetadataEntity
import com.example.data.editor.VideoEditorDraftDao
import com.example.data.editor.VideoEditorDraftEntity
import com.example.data.editor.VideoEditorHistoryDao
import com.example.data.editor.VideoEditorHistoryEntity

@Database(
    entities = [
        Project::class,
        VmsbAuditLogEntity::class,
        VideoMetadataEntity::class,
        VideoEditorDraftEntity::class,
        VideoEditorHistoryEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun videoProjectDao(): VideoProjectDao
    abstract fun vmsbAuditLogDao(): VmsbAuditLogDao
    abstract fun videoMetadataDao(): VideoMetadataDao
    abstract fun videoEditorDraftDao(): VideoEditorDraftDao
    abstract fun videoEditorHistoryDao(): VideoEditorHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "veo_studio_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
