package com.example.synctimer

import android.app.Application
import androidx.room.Room
import com.example.synctimer.data.AppDatabase
import com.example.synctimer.repository.RoomTaskRepository

class SyncTimerApp : Application() {
    lateinit var repository: RoomTaskRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val db = Room.databaseBuilder(this, AppDatabase::class.java, "sync_timer.db").build()
        repository = RoomTaskRepository(db.taskDao())
    }
}
