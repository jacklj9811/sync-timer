package com.example.synctimer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.synctimer.data.TaskEntity
import com.example.synctimer.data.TaskStatus
import com.example.synctimer.repository.TaskRepository
import com.example.synctimer.service.TimerForegroundService
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(
    private val repository: TaskRepository,
    private val serviceStarter: (Boolean) -> Unit
) : ViewModel() {

    val tasks: StateFlow<List<TaskEntity>> = repository.observeTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun observeTask(taskId: String): StateFlow<TaskEntity?> = repository.observeTask(taskId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val runningCount: StateFlow<Int> = tasks.map { list -> list.count { it.status == TaskStatus.Running } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    fun createTask(title: String, durationSeconds: Long) = viewModelScope.launch {
        repository.createTask(title, durationSeconds)
    }

    fun startTask(id: String) = viewModelScope.launch {
        repository.startTask(id, System.currentTimeMillis())
        serviceStarter(true)
    }

    fun pauseTask(id: String) = viewModelScope.launch {
        repository.pauseTask(id, System.currentTimeMillis())
    }

    fun resetTask(id: String) = viewModelScope.launch {
        repository.resetTask(id, System.currentTimeMillis())
    }

    fun deleteTask(id: String) = viewModelScope.launch {
        repository.deleteTask(id)
    }

    fun startAll() = viewModelScope.launch {
        repository.startAll(System.currentTimeMillis())
        serviceStarter(true)
    }

    fun pauseAll() = viewModelScope.launch {
        repository.pauseAll(System.currentTimeMillis())
    }

    class Factory(
        private val repository: TaskRepository,
        private val serviceStarter: (Boolean) -> Unit
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TaskViewModel(repository, serviceStarter) as T
        }
    }
}
