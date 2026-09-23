package com.sonisoft.datasetu.feature.onboarding.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PagerIndicator(
    pageCount: Int,
    currentPage: Int
) {

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        repeat(pageCount) { page ->

            Box(
                modifier = Modifier
                    .animateContentSize()
                    .height(8.dp)
                    .width(
                        if (page == currentPage)
                            28.dp
                        else
                            8.dp
                    )
                    .background(
                        if (page == currentPage)
                            Color.White
                        else
                            Color.DarkGray,
                        RoundedCornerShape(50)
                    )
            )
        }
    }
}