package com.sonisoft.datasetu.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun DataSetuLottie(
    rawRes: Int,
    modifier: Modifier = Modifier
) {

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(rawRes)
    )

    if (composition != null) {

        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = modifier
        )
    }
}