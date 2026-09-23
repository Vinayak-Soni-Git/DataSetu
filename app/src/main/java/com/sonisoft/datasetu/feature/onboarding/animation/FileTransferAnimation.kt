package com.sonisoft.datasetu.feature.onboarding.animation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun FileTransferAnimation() {
    val transition = rememberInfiniteTransition(label = "transfer")

    // 1. Animate the file's progress from left to right
    val fileProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "fileProgress"
    )

    // 2. Animate the dashed line to create a "flowing data" effect
    val dashPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = -40f, // Negative moves the dashes to the right
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dashPhase"
    )

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp) // Slightly taller to accommodate the flight arc
    ) {
        val centerY = size.height / 2
        val leftPhoneX = size.width * 0.25f
        val rightPhoneX = size.width * 0.75f

        // --- DRAW DATA STREAM (Background) ---
        drawLine(
            color = Color(0xFF00E676).copy(alpha = 0.6f), // Neon Green
            start = Offset(leftPhoneX, centerY),
            end = Offset(rightPhoneX, centerY),
            strokeWidth = 6f,
            pathEffect = PathEffect.dashPathEffect(
                intervals = floatArrayOf(20f, 20f),
                phase = dashPhase
            ),
            cap = StrokeCap.Round
        )

        // --- HELPER FUNCTION: DRAW SMARTPHONE ---
        fun drawPhone(centerX: Float, centerY: Float) {
            val phoneWidth = 80f
            val phoneHeight = 160f
            val topLeft = Offset(centerX - phoneWidth / 2, centerY - phoneHeight / 2)

            // Phone Chassis
            drawRoundRect(
                color = Color(0xFF2C3E50), // Dark Slate
                topLeft = topLeft,
                size = Size(phoneWidth, phoneHeight),
                cornerRadius = CornerRadius(20f)
            )

            // Phone Screen (Glowing Gradient)
            val screenGradient = Brush.verticalGradient(
                colors = listOf(Color(0xFF64B5F6), Color(0xFF1E88E5)),
                startY = topLeft.y,
                endY = topLeft.y + phoneHeight
            )
            drawRoundRect(
                brush = screenGradient,
                topLeft = Offset(topLeft.x + 6f, topLeft.y + 6f),
                size = Size(phoneWidth - 12f, phoneHeight - 12f),
                cornerRadius = CornerRadius(14f)
            )

            // Camera Notch
            drawRoundRect(
                color = Color.Black,
                topLeft = Offset(centerX - 15f, topLeft.y + 14f),
                size = Size(30f, 8f),
                cornerRadius = CornerRadius(4f)
            )
        }

        // Draw Left and Right Phones
        drawPhone(leftPhoneX, centerY)
        drawPhone(rightPhoneX, centerY)

        // --- CALCULATE FILE DYNAMICS ---

        // Use a sine wave to create a parabolic arc (goes up in the middle)
        val sineValue = sin(fileProgress * PI).toFloat()
        val arcHeight = 100f

        val currentX = leftPhoneX + ((rightPhoneX - leftPhoneX) * fileProgress)
        val currentY = centerY - (sineValue * arcHeight) // Subtract to move UP on canvas

        // Scale up in the middle of the flight to simulate 3D depth
        val scale = 0.4f + (sineValue * 0.6f)

        // Fade in when leaving, fade out when arriving
        val alpha = when {
            fileProgress < 0.1f -> fileProgress * 10f
            fileProgress > 0.9f -> (1f - fileProgress) * 10f
            else -> 1f
        }

        // --- DRAW FILE ICON ---
        withTransform({
            translate(left = currentX, top = currentY)
            scale(scale, scale)
        }) {
            val fileWidth = 50f
            val fileHeight = 64f
            val fileTopLeft = Offset(-fileWidth / 2, -fileHeight / 2)

            // Document Base (Orange Gradient)
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFFFB74D), Color(0xFFFF9800))
                ),
                topLeft = fileTopLeft,
                size = Size(fileWidth, fileHeight),
                cornerRadius = CornerRadius(8f),
                alpha = alpha
            )

            // Document Text Lines
            val lineXStart = -fileWidth / 2 + 12f
            drawLine(
                color = Color.White.copy(alpha = alpha),
                start = Offset(lineXStart, -fileHeight / 2 + 20f),
                end = Offset(fileWidth / 2 - 12f, -fileHeight / 2 + 20f),
                strokeWidth = 4f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color.White.copy(alpha = alpha),
                start = Offset(lineXStart, -fileHeight / 2 + 34f),
                end = Offset(fileWidth / 2 - 24f, -fileHeight / 2 + 34f), // Shorter line
                strokeWidth = 4f,
                cap = StrokeCap.Round
            )
        }
    }
}