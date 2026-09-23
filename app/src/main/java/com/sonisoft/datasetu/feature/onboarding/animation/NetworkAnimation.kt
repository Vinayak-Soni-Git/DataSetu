package com.sonisoft.datasetu.feature.onboarding.animation

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun NetworkAnimation() {

    val transition = rememberInfiniteTransition(
        label = "network"
    )

    val particle = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Restart
        ),
        label = "particle"
    )

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
    ) {

        val centerX = size.width / 2
        val centerY = size.height / 2

        val nodes = listOf(
            Offset(centerX, centerY - 100),
            Offset(centerX - 120, centerY),
            Offset(centerX + 120, centerY),
            Offset(centerX, centerY + 100)
        )

        nodes.forEach {
            drawCircle(
                color = Color.White,
                radius = 14f,
                center = it
            )
        }

        drawLine(Color.White, nodes[0], nodes[1], 3f)
        drawLine(Color.White, nodes[0], nodes[2], 3f)
        drawLine(Color.White, nodes[1], nodes[3], 3f)
        drawLine(Color.White, nodes[2], nodes[3], 3f)

        val p = Offset(
            nodes[0].x + (nodes[2].x - nodes[0].x) * particle.value,
            nodes[0].y + (nodes[2].y - nodes[0].y) * particle.value
        )

        drawCircle(
            color = Color.LightGray,
            radius = 6f,
            center = p
        )
    }
}