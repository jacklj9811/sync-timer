package com.example.synctimer.service

import com.example.synctimer.data.TaskStatus
import com.example.synctimer.domain.TimerCalculator
import com.example.synctimer.repository.TaskRepository

class TimerEngine(
    private val repository: TaskRepository
) {
    suspend fun tick(nowMs: Long) {
        val tasks = repository.getAllTasks()
        val updated = tasks.map { task ->
            TimerCalculator.restoreRunningState(task, nowMs)
        }

        val finishedTaskIds = updated
            .filter { it.status == TaskStatus.Finished }
            .map { it.id }

        repository.upsertTasks(updated)

        finishedTaskIds.forEach { repository.setStatus(it, TaskStatus.Finished, nowMs) }
    }
}
