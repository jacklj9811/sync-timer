package com.example.synctimer.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.synctimer.data.TaskEntity
import com.example.synctimer.data.TaskStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    task: TaskEntity?,
    onBack: () -> Unit,
    onStart: (String) -> Unit,
    onPause: (String) -> Unit,
    onReset: (String) -> Unit
) {
    Scaffold(topBar = { TopAppBar(title = { Text("Task Detail") }) }) { padding ->
        if (task == null) {
            Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
                Text("Task not found")
                Button(onClick = onBack) { Text("Back") }
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Title: ${task.title}")
            Text("Duration: ${task.durationSeconds}s")
            Text("Remaining: ${task.remainingSeconds}s")
            Text("Status: ${task.status}")

            Button(
                onClick = { onStart(task.id) },
                enabled = task.status != TaskStatus.Running && task.status != TaskStatus.Finished
            ) { Text("Start") }
            Button(onClick = { onPause(task.id) }, enabled = task.status == TaskStatus.Running) { Text("Pause") }
            Button(onClick = { onReset(task.id) }) { Text("Reset") }
            Button(onClick = onBack) { Text("Back") }
        }
    }
}
