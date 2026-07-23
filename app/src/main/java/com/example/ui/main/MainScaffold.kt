package com.example.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.NeuButton
import com.example.ui.screens.CoursesScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FocusZoneScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.theme.NeuColors
import com.example.ui.viewmodel.AuraViewModel

data class NavTabItem(val label: String, val icon: String, val tag: String)

val navTabs = listOf(
    NavTabItem("Home", "🏠", "tab_home"),
    NavTabItem("Courses", "📚", "tab_courses"),
    NavTabItem("Focus", "⏱️", "tab_focus"),
    NavTabItem("Stats", "📊", "tab_stats")
)

@Composable
fun MainScaffold(viewModel: AuraViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val neuColors = NeuColors()

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(neuColors.background)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    navTabs.forEachIndexed { index, item ->
                        val isSelected = state.selectedTab == index
                        NeuButton(
                            onClick = { viewModel.selectTab(index) },
                            cornerRadius = 14.dp,
                            elevation = if (isSelected) 2.dp else 5.dp,
                            neuColors = neuColors,
                            testTag = item.tag
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(text = item.icon, fontSize = 13.sp)
                                if (isSelected) {
                                    Text(
                                        text = " ${item.label}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF6366F1)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (state.selectedTab) {
                0 -> DashboardScreen(
                    state = state,
                    onToggleTask = viewModel::toggleTaskChecked,
                    onDeleteTask = viewModel::deleteTask,
                    onOpenAddTaskDialog = { viewModel.setAddTaskDialogOpen(true) },
                    onCloseAddTaskDialog = { viewModel.setAddTaskDialogOpen(false) },
                    onCreateTask = { title, courseId, priority ->
                        viewModel.createNewTask(title, courseId, priority)
                    },
                    onNavigateToFocus = { viewModel.selectTab(2) }
                )
                1 -> CoursesScreen(
                    state = state,
                    onOpenAddCourseDialog = { viewModel.setAddCourseDialogOpen(true) },
                    onCloseAddCourseDialog = { viewModel.setAddCourseDialogOpen(false) },
                    onCreateCourse = viewModel::createNewCourse,
                    onDeleteCourse = viewModel::deleteCourse
                )
                2 -> FocusZoneScreen(
                    state = state,
                    onToggleTimer = viewModel::toggleFocusTimer,
                    onResetTimer = viewModel::resetFocusTimer,
                    onScratchpadChanged = viewModel::setScratchpadNotes,
                    onSelectCourse = viewModel::selectCourseForFocus
                )
                3 -> StatsScreen(state = state)
            }
        }
    }
}
