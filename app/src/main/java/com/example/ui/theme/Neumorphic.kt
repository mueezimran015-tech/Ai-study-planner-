package com.example.ui.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class NeuStyle {
    EXTRUDED, INSET
}

data class NeuColors(
    val background: Color = Color(0xFF181A20),
    val lightShadow: Color = Color(0xFF262A34),
    val darkShadow: Color = Color(0xFF0E0F13)
)

val DarkNeuColors = NeuColors(
    background = Color(0xFF181A20),
    lightShadow = Color(0xFF262A34),
    darkShadow = Color(0xFF0E0F13)
)

val LightNeuColors = NeuColors(
    background = Color(0xFFE0E5EC),
    lightShadow = Color(0xFFFFFFFF),
    darkShadow = Color(0xFFA3B1C6)
)

fun Modifier.neumorphic(
    neuStyle: NeuStyle = NeuStyle.EXTRUDED,
    colors: NeuColors = NeuColors(),
    cornerRadius: Dp = 16.dp,
    elevation: Dp = 6.dp
): Modifier = this.drawBehind {
    val cornerRadiusPx = cornerRadius.toPx()
    val elevationPx = elevation.toPx()

    drawIntoCanvas { canvas ->
        val paintLight = Paint().apply {
            color = colors.lightShadow
            asFrameworkPaint().apply {
                isAntiAlias = true
                maskFilter = android.graphics.BlurMaskFilter(
                    elevationPx.coerceAtLeast(1f),
                    android.graphics.BlurMaskFilter.Blur.NORMAL
                )
            }
        }

        val paintDark = Paint().apply {
            color = colors.darkShadow
            asFrameworkPaint().apply {
                isAntiAlias = true
                maskFilter = android.graphics.BlurMaskFilter(
                    elevationPx.coerceAtLeast(1f),
                    android.graphics.BlurMaskFilter.Blur.NORMAL
                )
            }
        }

        if (neuStyle == NeuStyle.EXTRUDED) {
            // Top-Left Light Soft Shadow
            canvas.drawRoundRect(
                left = -elevationPx / 2,
                top = -elevationPx / 2,
                right = size.width - elevationPx / 2,
                bottom = size.height - elevationPx / 2,
                radiusX = cornerRadiusPx,
                radiusY = cornerRadiusPx,
                paint = paintLight
            )
            // Bottom-Right Dark Soft Shadow
            canvas.drawRoundRect(
                left = elevationPx / 2,
                top = elevationPx / 2,
                right = size.width + elevationPx / 2,
                bottom = size.height + elevationPx / 2,
                radiusX = cornerRadiusPx,
                radiusY = cornerRadiusPx,
                paint = paintDark
            )
        } else {
            // INSET / PRESSED SURFACE
            val path = Path().apply {
                addRoundRect(
                    RoundRect(
                        0f, 0f, size.width, size.height,
                        CornerRadius(cornerRadiusPx)
                    )
                )
            }
            canvas.save()
            canvas.clipPath(path)

            // Inner dark shadow top-left
            canvas.drawRoundRect(
                left = -elevationPx,
                top = -elevationPx,
                right = size.width,
                bottom = size.height,
                radiusX = cornerRadiusPx,
                radiusY = cornerRadiusPx,
                paint = paintDark
            )
            // Inner light shadow bottom-right
            canvas.drawRoundRect(
                left = 0f,
                top = 0f,
                right = size.width + elevationPx,
                bottom = size.height + elevationPx,
                radiusX = cornerRadiusPx,
                radiusY = cornerRadiusPx,
                paint = paintLight
            )
            canvas.restore()
        }
    }
}
