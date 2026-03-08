package com.example.synctimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.synctimer.data.TaskStatus
import com.example.synctimer.service.TimerForegroundService
import com.example.synctimer.ui.navigation.NavRoutes
import com.example.synctimer.ui.screens.TaskDetailScreen
import com.example.synctimer.ui.screens.TaskListScreen
import com.example.synctimer.ui.theme.SyncTimerTheme
import com.example.synctimer.ui.viewmodel.TaskViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: TaskViewModel by viewModels {
        val app = application as SyncTimerApp
        TaskViewModel.Factory(app.repository) { shouldStart ->
            if (shouldStart) {
                TimerForegroundService.start(this)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            val hasRunning = (application as SyncTimerApp).repository.getAllTasks().any { it.status == TaskStatus.Running }
            if (hasRunning) {
                TimerForegroundService.start(this@MainActivity)
            }
        }
        setContent {
            SyncTimerTheme {
                val navController = rememberNavController()
                val tasks by viewModel.tasks.collectAsStateWithLifecycle()

                NavHost(navController = navController, startDestination = NavRoutes.LIST) {
                    composable(NavRoutes.LIST) {
                        TaskListScreen(
                            tasks = tasks,
                            onCreateTask = viewModel::createTask,
                            onTaskClick = { taskId -> navController.navigate("${NavRoutes.DETAIL}/$taskId") },
                            onStart = viewModel::startTask,
                            onPause = viewModel::pauseTask,
                            onReset = viewModel::resetTask,
                            onDelete = viewModel::deleteTask,
                            onStartAll = viewModel::startAll,
                            onPauseAll = viewModel::pauseAll
                        )
                    }

                    composable(
                        route = "${NavRoutes.DETAIL}/{taskId}",
                        arguments = listOf(navArgument("taskId") { type = NavType.StringType })
                    ) { entry ->
                        val taskId = entry.arguments?.getString("taskId").orEmpty()
                        val task by viewModel.observeTask(taskId).collectAsState(initial = null)
                        TaskDetailScreen(
                            task = task,
                            onBack = { navController.popBackStack() },
                            onStart = viewModel::startTask,
                            onPause = viewModel::pauseTask,
                            onReset = viewModel::resetTask
                        )
                    }
                }
            }
        }
    }
}
