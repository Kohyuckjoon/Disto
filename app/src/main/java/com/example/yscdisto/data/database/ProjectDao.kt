package com.example.yscdisto.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.yscdisto.data.model.ProjectCreate

@Dao
interface ProjectDao {
    @Insert
    suspend fun insertProject(projectCreate: ProjectCreate)

    @Query("SELECT * FROM PROJECTS")
    suspend fun getAllProjects(): List<ProjectCreate>
}