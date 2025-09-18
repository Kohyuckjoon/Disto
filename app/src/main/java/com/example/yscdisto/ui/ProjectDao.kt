package com.example.yscdisto.ui

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ProjectDao {
    @Insert
    suspend fun insertProject(projectCreate: ProjectCreate)

    @Query("SELECT * FROM PROJECTS")
    suspend fun getAllProjects(): List<ProjectCreate>
}