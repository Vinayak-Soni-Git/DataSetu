package com.sonisoft.datasetu.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sonisoft.datasetu.feature.onboarding.OnboardingScreen
import com.sonisoft.datasetu.feature.profile.ProfileScreen
import com.sonisoft.datasetu.feature.splash.SplashScreen
import com.sonisoft.datasetu.navigation.route.OnboardingRoute

@Composable
fun AppNavGraph() {

    val navController = rememberNavController()

    val navigator = remember(navController) {
        AppNavigator(navController)
    }

    NavHost(
        navController = navController,
        startDestination = OnboardingRoute.Splash.route
    ) {

        composable(
            OnboardingRoute.Splash.route
        ) {

            SplashScreen(
                onSplashFinished = {
                    navigator.navigateToOnboarding()
                }
            )
        }

        composable(
            OnboardingRoute.Onboarding.route
        ) {

            OnboardingScreen(
                onGetStarted = {
                    navigator.navigateToCreateProfile()
                }
            )
        }

//        composable(
//            OnboardingRoute.CreateProfile.route
//        ) {
//
//            ProfileScreen(
//                onContinue = {
//                    navigator.navigateToHome()
//                }
//            )
//        }
    }
}