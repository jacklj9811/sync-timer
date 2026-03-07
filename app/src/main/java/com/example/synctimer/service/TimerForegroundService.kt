package com.example.synctimer.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import androidx.room.Room
import com.example.synctimer.data.AppDatabase
import com.example.synctimer.data.TaskStatus
import com.example.synctimer.repository.RoomTaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimerForegroundService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var repository: RoomTaskRepository
    private lateinit var engine: TimerEngine

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.ensureChannels(this)

        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "sync_timer.db").build()
        repository = RoomTaskRepository(db.taskDao())
        engine = TimerEngine(repository)

        startForeground(
            NotificationHelper.FOREGROUND_NOTIFICATION_ID,
            NotificationHelper.createForegroundNotification(this, emptyList())
        )

        serviceScope.launch {
            var knownFinished = emptySet<String>()
            while (true) {
                val nowMs = System.currentTimeMillis()
                engine.tick(nowMs)
                val tasks = repository.getAllTasks()
                val running = tasks.filter { it.status == TaskStatus.Running }
                NotificationManagerCompat.from(this@TimerForegroundService).notify(
                    NotificationHelper.FOREGROUND_NOTIFICATION_ID,
                    NotificationHelper.createForegroundNotification(this@TimerForegroundService, running)
                )

                val currentFinished = tasks.filter { it.status == TaskStatus.Finished }
                val newlyFinished = currentFinished.filterNot { knownFinished.contains(it.id) }
                newlyFinished.forEachIndexed { index, task ->
                    NotificationHelper.showCompletionNotification(
                        this@TimerForegroundService,
                        task.title,
                        index + task.id.hashCode()
                    )
                }
                knownFinished = currentFinished.map { it.id }.toSet()

                if (running.isEmpty()) {
                    // Keep service alive briefly for quick resume; stop if no running tasks.
                    stopSelf()
                    break
                }
                delay(1000)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        fun start(context: android.content.Context) {
            val intent = Intent(context, TimerForegroundService::class.java)
            androidx.core.content.ContextCompat.startForegroundService(context, intent)
        }
    }
}
