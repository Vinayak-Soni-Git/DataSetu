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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SecurityAnimation() {

    val transition = rememberInfiniteTransition(
        label = "security"
    )

    val scale = transition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lock"
    )

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
    ) {

        val centerX = size.width / 2
        val centerY = size.height / 2

        drawRoundRect(
            color = Color.White,
            topLeft = Offset(
                centerX - 50f * scale.value,
                centerY - 30f * scale.value
            ),
            size = androidx.compose.ui.geometry.Size(
                100f * scale.value,
                80f * scale.value
            ),
            cornerRadius = CornerRadius(18f)
        )

        drawCircle(
            color = Color.LightGray,
            radius = 28f * scale.value,
            center = Offset(centerX, centerY - 50f)
        )
    }
}