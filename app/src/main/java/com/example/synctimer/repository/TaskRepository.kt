package com.example.synctimer.repository

import com.example.synctimer.data.TaskEntity
import com.example.synctimer.data.TaskStatus
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun observeTasks(): Flow<List<TaskEntity>>
    fun observeTask(id: String): Flow<TaskEntity?>
    suspend fun getAllTasks(): List<TaskEntity>
    suspend fun createTask(title: String, durationSeconds: Long)
    suspend fun startTask(id: String, nowMs: Long)
    suspend fun pauseTask(id: String, nowMs: Long)
    suspend fun resetTask(id: String, nowMs: Long)
    suspend fun deleteTask(id: String)
    suspend fun pauseAll(nowMs: Long)
    suspend fun startAll(nowMs: Long)
    suspend fun upsertTasks(tasks: List<TaskEntity>)
    suspend fun setStatus(id: String, status: TaskStatus, nowMs: Long)
}
