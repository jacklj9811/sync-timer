package com.example.synctimer.data

import androidx.room.TypeConverter

class TaskTypeConverters {
    @TypeConverter
    fun toStatus(value: String): TaskStatus = TaskStatus.valueOf(value)

    @TypeConverter
    fun fromStatus(status: TaskStatus): String = status.name
}
