package com.example.synctimer.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.synctimer.data.TaskEntity
import com.example.synctimer.data.TaskStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    tasks: List<TaskEntity>,
    onCreateTask: (String, Long) -> Unit,
    onTaskClick: (String) -> Unit,
    onStart: (String) -> Unit,
    onPause: (String) -> Unit,
    onReset: (String) -> Unit,
    onDelete: (String) -> Unit,
    onStartAll: () -> Unit,
    onPauseAll: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var duration by remember { mutableLongStateOf(300L) }

    Scaffold(topBar = { TopAppBar(title = { Text("Sync Timer") }) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Task title") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = duration.toString(),
                onValueChange = { duration = it.toLongOrNull() ?: 60L },
                label = { Text("Duration (seconds)") },
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    if (title.isNotBlank() && duration > 0L) {
                        onCreateTask(title, duration)
                        title = ""
                    }
                }) { Text("Create") }
                Button(onClick = onStartAll) { Text("Start All") }
                Button(onClick = onPauseAll) { Text("Pause All") }
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(tasks, key = { it.id }) { task ->
                    TaskCard(task, onTaskClick, onStart, onPause, onReset, onDelete)
                }
            }
        }
    }
}

@Composable
private fun TaskCard(
    task: TaskEntity,
    onTaskClick: (String) -> Unit,
    onStart: (String) -> Unit,
    onPause: (String) -> Unit,
    onReset: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth().clickable { onTaskClick(task.id) }) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(task.title)
            Text("Remaining: ${task.remainingSeconds}s")
            Text("Status: ${task.status}")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { onStart(task.id) }, enabled = task.status != TaskStatus.Running && task.status != TaskStatus.Finished) { Text("Start") }
                Button(onClick = { onPause(task.id) }, enabled = task.status == TaskStatus.Running) { Text("Pause") }
                Button(onClick = { onReset(task.id) }) { Text("Reset") }
                Button(onClick = { onDelete(task.id) }) { Text("Delete") }
            }
        }
    }
}
