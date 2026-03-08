package com.example.synctimer.domain

import com.example.synctimer.data.TaskEntity
import com.example.synctimer.data.TaskStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class TimerCalculatorTest {

    @Test
    fun `applyElapsed should reduce remaining and keep running`() {
        val task = TaskEntity("1", "A", 100, 100, TaskStatus.Running, 0)

        val updated = TimerCalculator.applyElapsed(task, elapsedMs = 3_500, nowMs = 4_000)

        assertEquals(97, updated.remainingSeconds)
        assertEquals(TaskStatus.Running, updated.status)
    }

    @Test
    fun `restoreRunningState should finish task when elapsed exceeds remaining`() {
        val task = TaskEntity("1", "A", 10, 2, TaskStatus.Running, 0)

        val updated = TimerCalculator.restoreRunningState(task, nowMs = 5_000)

        assertEquals(0, updated.remainingSeconds)
        assertEquals(TaskStatus.Finished, updated.status)
    }
}
