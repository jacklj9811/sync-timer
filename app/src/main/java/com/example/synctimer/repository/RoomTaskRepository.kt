package com.example.synctimer.repository

import com.example.synctimer.data.TaskDao
import com.example.synctimer.data.TaskEntity
import com.example.synctimer.data.TaskStatus
import java.util.UUID
import kotlinx.coroutines.flow.Flow

class RoomTaskRepository(
    private val taskDao: TaskDao
) : TaskRepository {

    override fun observeTasks(): Flow<List<TaskEntity>> = taskDao.observeAll()

    override fun observeTask(id: String): Flow<TaskEntity?> = taskDao.observeById(id)

    override suspend fun getAllTasks(): List<TaskEntity> = taskDao.getAll()

    override suspend fun createTask(title: String, durationSeconds: Long) {
        val nowMs = System.currentTimeMillis()
        taskDao.insert(
            TaskEntity(
                id = UUID.randomUUID().toString(),
                title = title,
                durationSeconds = durationSeconds,
                remainingSeconds = durationSeconds,
                status = TaskStatus.Idle,
                updatedAtEpochMs = nowMs
            )
        )
    }

    override suspend fun startTask(id: String, nowMs: Long) {
        val task = taskDao.getAll().firstOrNull { it.id == id } ?: return
        if (task.status == TaskStatus.Running || task.status == TaskStatus.Finished) return
        taskDao.update(task.copy(status = TaskStatus.Running, updatedAtEpochMs = nowMs))
    }

    override suspend fun pauseTask(id: String, nowMs: Long) {
        val task = taskDao.getAll().firstOrNull { it.id == id } ?: return
        if (task.status != TaskStatus.Running) return
        taskDao.update(task.copy(status = TaskStatus.Paused, updatedAtEpochMs = nowMs))
    }

    override suspend fun resetTask(id: String, nowMs: Long) {
        val task = taskDao.getAll().firstOrNull { it.id == id } ?: return
        taskDao.update(
            task.copy(
                remainingSeconds = task.durationSeconds,
                status = TaskStatus.Idle,
                updatedAtEpochMs = nowMs
            )
        )
    }

    override suspend fun deleteTask(id: String) {
        taskDao.deleteById(id)
    }

    override suspend fun pauseAll(nowMs: Long) {
        val updated = taskDao.getAll().map {
            if (it.status == TaskStatus.Running) it.copy(status = TaskStatus.Paused, updatedAtEpochMs = nowMs) else it
        }
        taskDao.insertAll(updated)
    }

    override suspend fun startAll(nowMs: Long) {
        val updated = taskDao.getAll().map {
            if ((it.status == TaskStatus.Idle || it.status == TaskStatus.Paused) && it.remainingSeconds > 0L) {
                it.copy(status = TaskStatus.Running, updatedAtEpochMs = nowMs)
            } else {
                it
            }
        }
        taskDao.insertAll(updated)
    }

    override suspend fun upsertTasks(tasks: List<TaskEntity>) {
        taskDao.insertAll(tasks)
    }

    override suspend fun setStatus(id: String, status: TaskStatus, nowMs: Long) {
        val task = taskDao.getAll().firstOrNull { it.id == id } ?: return
        taskDao.update(task.copy(status = status, updatedAtEpochMs = nowMs))
    }
}
