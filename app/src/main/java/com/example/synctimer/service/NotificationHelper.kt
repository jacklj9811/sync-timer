package com.example.synctimer.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.synctimer.MainActivity
import com.example.synctimer.R
import com.example.synctimer.data.TaskEntity

object NotificationHelper {
    const val FOREGROUND_CHANNEL_ID = "running_tasks"
    const val COMPLETION_CHANNEL_ID = "completed_tasks"
    const val FOREGROUND_NOTIFICATION_ID = 1001

    fun ensureChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val fg = NotificationChannel(
            FOREGROUND_CHANNEL_ID,
            "Running tasks",
            NotificationManager.IMPORTANCE_LOW
        )
        val done = NotificationChannel(
            COMPLETION_CHANNEL_ID,
            "Completed tasks",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        manager.createNotificationChannel(fg)
        manager.createNotificationChannel(done)
    }

    fun createForegroundNotification(context: Context, runningTasks: List<TaskEntity>): Notification {
        val content = if (runningTasks.isEmpty()) {
            "No running task"
        } else {
            "Running ${runningTasks.size} task(s)"
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, FOREGROUND_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(content)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .build()
    }

    fun showCompletionNotification(context: Context, taskTitle: String, taskId: Int) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, COMPLETION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle("Task completed")
            .setContentText(taskTitle)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(2000 + taskId, notification)
    }
}
