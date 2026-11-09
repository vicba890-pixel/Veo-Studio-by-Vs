package com.example.data

import kotlinx.coroutines.flow.Flow

class ProjectRepository(private val projectDao: ProjectDao) {
    val allProjects: Flow<List<Project>> = projectDao.getAllProjects()

    fun getProjectById(id: Int): Flow<Project?> {
        return projectDao.getProjectById(id)
    }

    fun searchProjects(query: String): Flow<List<Project>> {
        return projectDao.searchProjects(query)
    }

    suspend fun insertProject(project: Project): Long {
        return projectDao.insertProject(project)
    }

    suspend fun updateProject(project: Project) {
        projectDao.updateProject(project)
    }

    suspend fun deleteProject(project: Project) {
        projectDao.deleteProject(project)
    }

    suspend fun deleteProjectById(id: Int) {
        projectDao.deleteProjectById(id)
    }
}
