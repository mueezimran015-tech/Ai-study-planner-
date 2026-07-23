package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.components.NeuButton
import com.example.ui.components.NeuCard
import com.example.ui.components.NeuInsetBox
import com.example.ui.components.NeuPriorityChip
import com.example.ui.components.NeuProgressGauge
import com.example.ui.components.NeuTextField
import com.example.ui.theme.NeuColors
import com.example.ui.theme.NeuStyle
import com.example.ui.theme.neumorphic
import com.example.ui.viewmodel.AuraUiState

@Composable
fun DashboardScreen(
    state: AuraUiState,
    onToggleTask: (String, Boolean) -> Unit,
    onDeleteTask: (String) -> Unit,
    onOpenAddTaskDialog: () -> Unit,
    onCloseAddTaskDialog: () -> Unit,
    onCreateTask: (String, String, String) -> Unit,
    onNavigateToFocus: () -> Unit
) {
    val neuColors = NeuColors()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(neuColors.background)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Header Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .neumorphic(NeuStyle.EXTRUDED, neuColors, 25.dp, elevation = 6.dp)
                                .clip(CircleShape)
                                .background(neuColors.background),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👤", fontSize = 24.sp)
                        }
                        Column {
                            Text(
                                text = "Welcome back,",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = state.userName,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Streak Badge
                    Box(
                        modifier = Modifier
                            .neumorphic(NeuStyle.EXTRUDED, neuColors, cornerRadius = 14.dp, elevation = 5.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(neuColors.background)
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🔥 ", fontSize = 14.sp)
                            Text(
                                text = "${state.streakDays} Days",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF6D00)
                            )
                        }
                    }
                }
            }

            // Quick Progress Gauge Card
            item {
                NeuProgressGauge(
                    currentValue = state.todayStudyMinutes / 60f,
                    targetValue = state.targetStudyMinutes / 60f,
                    unit = "hrs",
                    title = "TODAY'S STUDY GOAL",
                    neuColors = neuColors
                )
            }

            // Task List Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Today's Tasks (${state.todayTasks.size})",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    NeuButton(
                        onClick = onOpenAddTaskDialog,
                        cornerRadius = 12.dp,
                        elevation = 4.dp,
                        neuColors = neuColors,
                        testTag = "add_task_button"
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Task",
                                tint = Color(0xFF6366F1),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Add Task",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6366F1)
                            )
                        }
                    }
                }
            }

            // Empty State
            if (state.todayTasks.isEmpty()) {
                item {
                    NeuInsetBox(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 20.dp,
                        neuColors = neuColors
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🎉", fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "All tasks completed for today!",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            Text(
                                text = "Tap + Add Task to plan your next study goal.",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            } else {
                // Today Tasks List
                items(state.todayTasks, key = { it.taskId }) { task ->
                    val isDone = task.status == "COMPLETED"
                    NeuCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 18.dp,
                        elevation = 5.dp,
                        neuColors = neuColors
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Checkbox(
                                    checked = isDone,
                                    onCheckedChange = { checked -> onToggleTask(task.taskId, checked) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = Color(0xFF6366F1),
                                        uncheckedColor = Color.Gray
                                    ),
                                    modifier = Modifier.testTag("task_checkbox_${task.taskId}")
                                )
                                Column {
                                    Text(
                                        text = task.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isDone) Color.Gray else Color.White,
                                        textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        NeuPriorityChip(priority = task.priority, neuColors = neuColors)
                                        if (task.subtasks.isNotEmpty()) {
                                            Text(
                                                text = "• ${task.subtasks.size} subtasks",
                                                fontSize = 11.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                }
                            }

                            IconButton(
                                onClick = { onDeleteTask(task.taskId) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Task",
                                    tint = Color.Gray.copy(alpha = 0.6f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp)) // Floating button padding
            }
        }

        // Quick Focus Launcher FAB
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 12.dp)
        ) {
            NeuButton(
                onClick = onNavigateToFocus,
                cornerRadius = 28.dp,
                elevation = 8.dp,
                neuColors = neuColors,
                testTag = "quick_focus_fab"
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("⏱️", fontSize = 18.sp)
                    Text(
                        text = "Focus Zone",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6366F1)
                    )
                }
            }
        }
    }

    // Add Task Dialog Modal
    if (state.isAddTaskDialogOpen) {
        AddTaskDialog(
            courses = state.courses,
            onDismiss = onCloseAddTaskDialog,
            onConfirm = onCreateTask
        )
    }
}

@Composable
fun AddTaskDialog(
    courses: List<com.example.data.local.CourseEntity>,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCourseId by remember { mutableStateOf(courses.firstOrNull()?.courseId ?: "") }
    var selectedPriority by remember { mutableStateOf("HIGH") }
    val neuColors = NeuColors()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = neuColors.background,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .neumorphic(NeuStyle.EXTRUDED, neuColors, cornerRadius = 24.dp, elevation = 10.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "New Study Goal",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                NeuTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = "Task title (e.g., Read Ch. 4)...",
                    testTag = "task_title_input"
                )

                // Priority Selector
                Column {
                    Text("Priority Level", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("LOW", "MEDIUM", "HIGH", "CRITICAL").forEach { prio ->
                            NeuButton(
                                onClick = { selectedPriority = prio },
                                cornerRadius = 10.dp,
                                elevation = 3.dp,
                                neuColors = neuColors,
                                modifier = Modifier.padding(2.dp)
                            ) {
                                Text(
                                    text = prio,
                                    fontSize = 10.sp,
                                    fontWeight = if (selectedPriority == prio) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedPriority == prio) Color(0xFF6366F1) else Color.Gray
                                )
                            }
                        }
                    }
                }

                // Course Selector
                if (courses.isNotEmpty()) {
                    Column {
                        Text("Associated Course", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            courses.take(3).forEach { course ->
                                val isSelected = selectedCourseId == course.courseId
                                NeuButton(
                                    onClick = { selectedCourseId = course.courseId },
                                    cornerRadius = 10.dp,
                                    elevation = 3.dp,
                                    neuColors = neuColors
                                ) {
                                    Text(
                                        text = course.courseCode.ifEmpty { course.title.take(6) },
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color(0xFF6366F1) else Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NeuButton(onClick = onDismiss, cornerRadius = 12.dp) {
                        Text("Cancel", fontSize = 12.sp, color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    NeuButton(
                        onClick = { onConfirm(title, selectedCourseId, selectedPriority) },
                        cornerRadius = 12.dp,
                        testTag = "confirm_add_task_button"
                    ) {
                        Text("Save Task", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6366F1))
                    }
                }
            }
        }
    }
}
