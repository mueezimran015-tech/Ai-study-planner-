package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CourseEntity
import com.example.ui.components.NeuButton
import com.example.ui.components.NeuInsetBox
import com.example.ui.theme.NeuColors
import com.example.ui.theme.NeuStyle
import com.example.ui.theme.neumorphic
import com.example.ui.viewmodel.AuraUiState

@Composable
fun FocusZoneScreen(
    state: AuraUiState,
    onToggleTimer: () -> Unit,
    onResetTimer: (Int) -> Unit,
    onScratchpadChanged: (String) -> Unit,
    onSelectCourse: (CourseEntity?) -> Unit
) {
    val neuColors = NeuColors()

    val minutes = state.focusSecondsLeft / 60
    val seconds = state.focusSecondsLeft % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    val progressFraction = if (state.focusTotalSeconds > 0) {
        state.focusSecondsLeft.toFloat() / state.focusTotalSeconds.toFloat()
    } else 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(neuColors.background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Smart Focus Dial",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = state.selectedCourseForFocus?.title ?: "General Deep Work Session",
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Neumorphic Circular Timer Dial
        Box(
            modifier = Modifier
                .size(260.dp)
                .neumorphic(NeuStyle.EXTRUDED, neuColors, cornerRadius = 130.dp, elevation = 8.dp)
                .clip(CircleShape)
                .background(neuColors.background),
            contentAlignment = Alignment.Center
        ) {
            // Inner Inset Track
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .neumorphic(NeuStyle.INSET, neuColors, cornerRadius = 100.dp, elevation = 6.dp)
                    .clip(CircleShape)
                    .background(neuColors.background),
                contentAlignment = Alignment.Center
            ) {
                // Circular Progress Arc
                Canvas(modifier = Modifier.size(180.dp)) {
                    // Background track arc
                    drawArc(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                    )
                    // Active progress arc
                    drawArc(
                        color = Color(0xFF6366F1),
                        startAngle = -90f,
                        sweepAngle = progressFraction * 360f,
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formattedTime,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (state.isFocusRunning) "FOCUSING" else "READY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6366F1),
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Duration Presets Row
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(15, 25, 50).forEach { mins ->
                val isSelected = (state.focusTotalSeconds == mins * 60)
                NeuButton(
                    onClick = { onResetTimer(mins) },
                    cornerRadius = 12.dp,
                    elevation = 4.dp,
                    neuColors = neuColors
                ) {
                    Text(
                        text = "${mins}m",
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color(0xFF6366F1) else Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Timer Controls Row
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NeuButton(
                onClick = onToggleTimer,
                cornerRadius = 20.dp,
                elevation = 6.dp,
                neuColors = neuColors,
                testTag = "focus_timer_toggle_button"
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (state.isFocusRunning) "⏸ Pause" else "▶ Start Session",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6366F1)
                    )
                }
            }

            NeuButton(
                onClick = { onResetTimer(state.focusTotalSeconds / 60) },
                cornerRadius = 20.dp,
                elevation = 6.dp,
                neuColors = neuColors,
                testTag = "focus_timer_reset_button"
            ) {
                Text("🔄", fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Contextual Scratchpad
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Contextual Scratchpad",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            NeuInsetBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                cornerRadius = 16.dp,
                elevation = 4.dp,
                neuColors = neuColors
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (state.scratchpadNotes.isEmpty()) {
                        Text(
                            text = "Jot down formulas, distractors, or quick thoughts...",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    BasicTextField(
                        value = state.scratchpadNotes,
                        onValueChange = onScratchpadChanged,
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 13.sp,
                            color = Color.White
                        ),
                        cursorBrush = SolidColor(Color(0xFF6366F1)),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
