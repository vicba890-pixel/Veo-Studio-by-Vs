package com.example.data.editor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Repositorio para la capa de auto-guardado local en Room Database del editor de video.
 */
class VideoEditorDraftRepository(private val draftDao: VideoEditorDraftDao) {

    val allDrafts: Flow<List<VideoEditorDraftEntity>> = draftDao.getAllDrafts()

    fun getDraftByProjectId(projectId: Int): Flow<VideoEditorDraftEntity?> {
        return draftDao.getDraftByProjectId(projectId)
    }

    suspend fun getDraftByProjectIdOnce(projectId: Int): VideoEditorDraftEntity? {
        return withContext(Dispatchers.IO) {
            draftDao.getDraftByProjectIdOnce(projectId)
        }
    }

    suspend fun saveOrUpdateDraft(draft: VideoEditorDraftEntity): Long {
        return withContext(Dispatchers.IO) {
            draftDao.insertOrUpdateDraft(draft)
        }
    }

    suspend fun deleteDraftByProjectId(projectId: Int) {
        withContext(Dispatchers.IO) {
            draftDao.deleteDraftByProjectId(projectId)
        }
    }

    suspend fun getDraftCount(): Int {
        return withContext(Dispatchers.IO) {
            draftDao.getDraftCount()
        }
    }
}
