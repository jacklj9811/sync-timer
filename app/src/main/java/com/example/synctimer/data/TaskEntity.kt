package com.example.synctimer.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val durationSeconds: Long,
    val remainingSeconds: Long,
    val status: TaskStatus,
    val updatedAtEpochMs: Long
)
