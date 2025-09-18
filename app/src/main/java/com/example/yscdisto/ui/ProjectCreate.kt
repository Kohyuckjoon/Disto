package com.example.yscdisto.ui

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectCreate(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,        // 프로젝트명
    val location: String,    // 현장 조사 지역
    val sheetNumber:String,    // 도엽 번호
    val memo: String? = null // 메모(선택)
)
