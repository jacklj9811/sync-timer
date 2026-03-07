package com.example.synctimer.domain

import com.example.synctimer.data.TaskEntity
import com.example.synctimer.data.TaskStatus
import kotlin.math.max

/**
 * Pure time-calculation helper to keep timer updates deterministic and testable.
 */
object TimerCalculator {

    fun applyElapsed(task: TaskEntity, elapsedMs: Long, nowMs: Long): TaskEntity {
        if (task.status != TaskStatus.Running) return task
        if (elapsedMs <= 0L) return task

        val elapsedSeconds = elapsedMs / 1000L
        if (elapsedSeconds <= 0L) return task

        val newRemaining = max(task.remainingSeconds - elapsedSeconds, 0L)
        val newStatus = if (newRemaining == 0L) TaskStatus.Finished else TaskStatus.Running

        return task.copy(
            remainingSeconds = newRemaining,
            status = newStatus,
            updatedAtEpochMs = nowMs
        )
    }

    fun restoreRunningState(task: TaskEntity, nowMs: Long): TaskEntity {
        if (task.status != TaskStatus.Running) return task
        val elapsed = max(nowMs - task.updatedAtEpochMs, 0L)
        return applyElapsed(task, elapsed, nowMs)
    }
}
