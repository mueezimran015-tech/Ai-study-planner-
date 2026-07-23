package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.CourseEntity
import com.example.ui.components.NeuButton
import com.example.ui.components.NeuCard
import com.example.ui.components.NeuInsetBox
import com.example.ui.components.NeuTextField
import com.example.ui.theme.NeuColors
import com.example.ui.theme.NeuStyle
import com.example.ui.theme.neumorphic
import com.example.ui.viewmodel.AuraUiState

@Composable
fun CoursesScreen(
    state: AuraUiState,
    onOpenAddCourseDialog: () -> Unit,
    onCloseAddCourseDialog: () -> Unit,
    onCreateCourse: (String, String, Int, String, Int) -> Unit,
    onDeleteCourse: (String) -> Unit
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
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Academic Courses",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${state.courses.size} Active Enrolled Subjects",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    NeuButton(
                        onClick = onOpenAddCourseDialog,
                        cornerRadius = 12.dp,
                        elevation = 5.dp,
                        neuColors = neuColors,
                        testTag = "add_course_button"
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Course",
                                tint = Color(0xFF6366F1),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Course",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6366F1)
                            )
                        }
                    }
                }
            }

            // Courses Grid/List
            if (state.courses.isEmpty()) {
                item {
                    NeuInsetBox(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 20.dp,
                        neuColors = neuColors
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("📚", fontSize = 40.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No courses added yet",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Add your enrolled subjects to organize tasks.",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            } else {
                items(state.courses, key = { it.courseId }) { course ->
                    val daysUntilExam = ((course.examDateEpochMs - System.currentTimeMillis()) / 86400000L).coerceAtLeast(0)

                    NeuCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 20.dp,
                        elevation = 6.dp,
                        neuColors = neuColors
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .neumorphic(NeuStyle.EXTRUDED, neuColors, cornerRadius = 21.dp, elevation = 4.dp)
                                            .clip(CircleShape)
                                            .background(neuColors.background),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Book,
                                            contentDescription = null,
                                            tint = Color(0xFF6366F1),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = course.title,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "${course.courseCode} • ${course.creditHours} Credit Hours",
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { onDeleteCourse(course.courseId) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Course",
                                        tint = Color.Gray.copy(alpha = 0.6f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            // Exam Countdown Badge
                            NeuInsetBox(
                                modifier = Modifier.fillMaxWidth(),
                                cornerRadius = 12.dp,
                                elevation = 3.dp,
                                neuColors = neuColors
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "🎯 Upcoming Final Exam",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = if (daysUntilExam == 0L) "Exam Today!" else "In $daysUntilExam Days",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (daysUntilExam <= 7) Color(0xFFEF4444) else Color(0xFF10B981)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    if (state.isAddCourseDialogOpen) {
        AddCourseDialog(
            onDismiss = onCloseAddCourseDialog,
            onConfirm = onCreateCourse
        )
    }
}

@Composable
fun AddCourseDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int, String, Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var creditHours by remember { mutableStateOf(3) }
    var examDaysOffset by remember { mutableStateOf(14) }
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
                    text = "Add Academic Course",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                NeuTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = "Course Title (e.g. Organic Chemistry)",
                    testTag = "course_title_input"
                )

                NeuTextField(
                    value = code,
                    onValueChange = { code = it },
                    placeholder = "Course Code (e.g. CHEM-201)",
                    testTag = "course_code_input"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Credit Hours", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(1, 2, 3, 4).forEach { hours ->
                            NeuButton(
                                onClick = { creditHours = hours },
                                cornerRadius = 8.dp,
                                elevation = 3.dp,
                                neuColors = neuColors
                            ) {
                                Text(
                                    text = "$hours CR",
                                    fontSize = 11.sp,
                                    fontWeight = if (creditHours == hours) FontWeight.Bold else FontWeight.Normal,
                                    color = if (creditHours == hours) Color(0xFF6366F1) else Color.Gray
                                )
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
                        onClick = { onConfirm(title, code, creditHours, "#6366F1", examDaysOffset) },
                        cornerRadius = 12.dp,
                        testTag = "confirm_add_course_button"
                    ) {
                        Text("Save Course", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6366F1))
                    }
                }
            }
        }
    }
}
