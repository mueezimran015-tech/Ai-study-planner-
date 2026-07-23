package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeuColors
import com.example.ui.theme.NeuStyle
import com.example.ui.theme.neumorphic

@Composable
fun NeuButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    elevation: Dp = 6.dp,
    neuColors: NeuColors = NeuColors(),
    testTag: String? = null,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val style = if (isPressed) NeuStyle.INSET else NeuStyle.EXTRUDED
    val appliedModifier = if (testTag != null) modifier.testTag(testTag) else modifier

    Box(
        modifier = appliedModifier
            .neumorphic(
                neuStyle = style,
                colors = neuColors,
                cornerRadius = cornerRadius,
                elevation = elevation
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(neuColors.background)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
fun NeuCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    elevation: Dp = 6.dp,
    neuColors: NeuColors = NeuColors(),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .neumorphic(
                neuStyle = NeuStyle.EXTRUDED,
                colors = neuColors,
                cornerRadius = cornerRadius,
                elevation = elevation
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(neuColors.background)
            .padding(18.dp),
        content = content
    )
}

@Composable
fun NeuInsetBox(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    elevation: Dp = 5.dp,
    neuColors: NeuColors = NeuColors(),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .neumorphic(
                neuStyle = NeuStyle.INSET,
                colors = neuColors,
                cornerRadius = cornerRadius,
                elevation = elevation
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(neuColors.background)
            .padding(14.dp),
        content = content
    )
}

@Composable
fun NeuIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector,
    contentDescription: String,
    size: Dp = 44.dp,
    tint: Color = LocalContentColor.current,
    neuColors: NeuColors = NeuColors(),
    testTag: String? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val style = if (isPressed) NeuStyle.INSET else NeuStyle.EXTRUDED

    val tagModifier = if (testTag != null) modifier.testTag(testTag) else modifier

    Box(
        modifier = tagModifier
            .size(size)
            .neumorphic(
                neuStyle = style,
                colors = neuColors,
                cornerRadius = size / 2,
                elevation = 5.dp
            )
            .clip(CircleShape)
            .background(neuColors.background)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
fun NeuTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    singleLine: Boolean = true,
    neuColors: NeuColors = NeuColors(),
    testTag: String? = null
) {
    val tagModifier = if (testTag != null) modifier.testTag(testTag) else modifier

    NeuInsetBox(
        modifier = tagModifier.fillMaxWidth(),
        cornerRadius = 16.dp,
        elevation = 4.dp,
        neuColors = neuColors
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = singleLine,
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    ),
                    cursorBrush = SolidColor(Color(0xFF6366F1)),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun NeuProgressGauge(
    currentValue: Float,
    targetValue: Float,
    unit: String,
    title: String = "PROGRESS",
    neuColors: NeuColors = NeuColors()
) {
    NeuCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 20.dp,
        neuColors = neuColors
    ) {
        Column {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = String.format("%.1f", currentValue),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "/ ${targetValue.toInt()} $unit target",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            
            // Inset Track
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .neumorphic(NeuStyle.INSET, neuColors, cornerRadius = 7.dp, elevation = 4.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(neuColors.background)
            ) {
                val fraction = if (targetValue > 0) (currentValue / targetValue).coerceIn(0f, 1f) else 0f
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction)
                        .background(
                            color = Color(0xFF6366F1),
                            shape = RoundedCornerShape(7.dp)
                        )
                )
            }
        }
    }
}

@Composable
fun NeuPriorityChip(
    priority: String,
    neuColors: NeuColors = NeuColors()
) {
    val (bgColor, textColor) = when (priority.uppercase()) {
        "CRITICAL" -> Color(0xFFFEE2E2) to Color(0xFFDC2626)
        "HIGH" -> Color(0xFFFFEDD5) to Color(0xFFEA580C)
        "MEDIUM" -> Color(0xFFFEF3C7) to Color(0xFFD97706)
        else -> Color(0xFFD1FAE5) to Color(0xFF059669)
    }

    Box(
        modifier = Modifier
            .neumorphic(NeuStyle.EXTRUDED, neuColors, cornerRadius = 8.dp, elevation = 3.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = priority.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}
