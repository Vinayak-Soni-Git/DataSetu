package com.sonisoft.datasetu.feature.onboarding

import com.sonisoft.datasetu.R

data class OnboardingPage(
    val animationRes: Int,
    val title: String,
    val description: String
)

val pages = listOf(

    OnboardingPage(
        animationRes = R.raw.transfer,
        title = "Lightning Fast Transfers",
        description = "Transfer photos, videos and documents instantly."
    ),

    OnboardingPage(
        animationRes = R.raw.security,
        title = "Private & Secure",
        description = "Files move directly between devices."
    ),

    OnboardingPage(
        animationRes = R.raw.network,
        title = "Bridge Every Device",
        description = "Connect phones, tablets and computers."
    )
)