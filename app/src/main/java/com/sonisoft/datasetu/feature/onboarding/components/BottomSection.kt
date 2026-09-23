package com.sonisoft.datasetu.feature.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BottomSection(
    currentPage: Int,
    pageCount: Int,
    onNextClick: () -> Unit,
    onSkipClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .navigationBarsPadding()
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {

            repeat(pageCount) { index ->

                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(
                            width = if (index == currentPage) 24.dp else 8.dp,
                            height = 8.dp
                        )
                        .background(
                            color = if (index == currentPage)
                                Color.White
                            else
                                Color.DarkGray,
                            shape = CircleShape
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNextClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            )
        ) {

            Text(
                text =
                    if (currentPage == pageCount - 1)
                        "Get Started"
                    else
                        "Next"
            )
        }

        if (currentPage != pageCount - 1) {

            TextButton(
                onClick = onSkipClick,
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = "Skip",
                    color = Color.LightGray
                )
            }
        }
    }
}