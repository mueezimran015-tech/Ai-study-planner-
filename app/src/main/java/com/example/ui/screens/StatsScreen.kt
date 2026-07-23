package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Fireplace
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.NeuCard
import com.example.ui.components.NeuInsetBox
import com.example.ui.theme.NeuColors
import com.example.ui.theme.NeuStyle
import com.example.ui.theme.neumorphic
import com.example.ui.viewmodel.AuraUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StatsScreen(state: AuraUiState) {
    val neuColors = NeuColors()

    val totalStudyHours = state.studySessions.sumOf { it.durationSeconds } / 3600f
    val completedTasksCount = state.allTasks.count { it.status == "COMPLETED" }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(neuColors.background)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "Analytics & Study Stats",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Personal Productivity & Focus Performance",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        // 2x2 Metric Cards Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    StatCard(
                        title = "TOTAL FOCUS",
                        value = String.format("%.1f hrs", totalStudyHours),
                        icon = Icons.Default.Timer,
                        iconTint = Color(0xFF6366F1),
                        modifier = Modifier.weight(1f),
                        neuColors = neuColors
                    )
                    StatCard(
                        title = "STREAK",
                        value = "${state.streakDays} Days",
                        icon = Icons.Default.Fireplace,
                        iconTint = Color(0xFFFF6D00),
                        modifier = Modifier.weight(1f),
                        neuColors = neuColors
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    StatCard(
                        title = "COMPLETED",
                        value = "$completedTasksCount Tasks",
                        icon = Icons.Default.CheckCircle,
                        iconTint = Color(0xFF10B981),
                        modifier = Modifier.weight(1f),
                        neuColors = neuColors
                    )
                    StatCard(
                        title = "SESSIONS",
                        value = "${state.studySessions.size} Logs",
                        icon = Icons.Default.DateRange,
                        iconTint = Color(0xFF8B5CF6),
                        modifier = Modifier.weight(1f),
                        neuColors = neuColors
                    )
                }
            }
        }

        // Weekly Distribution Neumorphic Bar Chart
        item {
            NeuCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 20.dp,
                neuColors = neuColors
            ) {
                Column {
                    Text(
                        text = "WEEKLY FOCUS DISTRIBUTION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val days = listOf("M", "T", "W", "T", "F", "S", "S")
                    val heights = listOf(0.6f, 0.8f, 0.4f, 0.9f, 0.75f, 0.5f, 0.3f)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        days.forEachIndexed { idx, day ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.weight(1f)
                            ) {
                                // Inset Bar
                                Box(
                                    modifier = Modifier
                                        .width(18.dp)
                                        .height(90.dp)
                                        .neumorphic(NeuStyle.INSET, neuColors, cornerRadius = 9.dp, elevation = 3.dp)
                                        .clip(RoundedCornerShape(9.dp))
                                        .background(neuColors.background),
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight(heights[idx])
                                            .background(
                                                color = if (idx == 3) Color(0xFF6366F1) else Color(0xFF818CF8).copy(alpha = 0.6f),
                                                shape = RoundedCornerShape(9.dp)
                                            )
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = day, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }

        // Study Session Logs History
        item {
            Text(
                text = "Recent Study Sessions",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        if (state.studySessions.isEmpty()) {
            item {
                NeuInsetBox(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 16.dp,
                    neuColors = neuColors
                ) {
                    Text(
                        text = "No recorded sessions yet. Start a session in the Focus Zone!",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        } else {
            items(state.studySessions) { session ->
                val durationMins = session.durationSeconds / 60
                val dateStr = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(session.timestamp))

                NeuCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 16.dp,
                    elevation = 4.dp,
                    neuColors = neuColors
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = session.courseTitle ?: "Focus Session",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = dateStr,
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                            if (session.notes.isNotEmpty()) {
                                Text(
                                    text = "Note: ${session.notes}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF6366F1)
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .neumorphic(NeuStyle.INSET, neuColors, cornerRadius = 10.dp, elevation = 2.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(neuColors.background)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${durationMins} min",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6366F1)
                            )
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

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier,
    neuColors: NeuColors = NeuColors()
) {
    NeuCard(
        modifier = modifier,
        cornerRadius = 18.dp,
        elevation = 5.dp,
        neuColors = neuColors
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
        }
    }
}
