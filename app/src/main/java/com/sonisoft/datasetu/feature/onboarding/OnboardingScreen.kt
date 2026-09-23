package com.sonisoft.datasetu.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sonisoft.datasetu.R
import com.sonisoft.datasetu.core.ui.components.DataSetuLottie
import com.sonisoft.datasetu.feature.onboarding.components.BottomSection
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    onGetStarted: () -> Unit
) {

    val pages = remember {
        listOf(
            OnboardingPage(
                animationRes = R.raw.transfer,
                title = "Lightning Fast Transfers",
                description = "Transfer photos, videos, documents and folders between devices instantly."
            ),
            OnboardingPage(
                animationRes = R.raw.security,
                title = "Private & Secure",
                description = "Your files move directly between devices without relying on cloud storage."
            ),
            OnboardingPage(
                animationRes = R.raw.network,
                title = "Bridge Every Device",
                description = "Connect phones, tablets and computers through one seamless network."
            )
        )
    }

    val pagerState = rememberPagerState(
        pageCount = { pages.size }
    )

    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .systemBarsPadding()
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->

                val item = pages[page]

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(modifier = Modifier.height(32.dp))

                    DataSetuLottie(
                        rawRes = item.animationRes,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = item.title,
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = item.description,
                        color = Color.LightGray,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }

            BottomSection(
                currentPage = pagerState.currentPage,
                pageCount = pages.size,
                onNextClick = {

                    if (pagerState.currentPage < pages.lastIndex) {

                        scope.launch {
                            pagerState.animateScrollToPage(
                                pagerState.currentPage + 1
                            )
                        }

                    } else {

                        onGetStarted()
                    }
                },
                onSkipClick = onGetStarted
            )
        }
    }
}