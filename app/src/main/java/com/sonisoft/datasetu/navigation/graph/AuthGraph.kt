package com.sonisoft.datasetu.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sonisoft.datasetu.feature.auth.login.LoginRoute
import com.sonisoft.datasetu.feature.auth.signup.SignupRoute
import com.sonisoft.datasetu.navigation.AppNavigator
import com.sonisoft.datasetu.navigation.route.AuthRoute

fun NavGraphBuilder.authGraph(navigator: AppNavigator) {
    composable(
        route = AuthRoute.Login.route
    ) {
        LoginRoute(
            onNavigateToSignup = { navigator.navigateToSignup() },
            onNavigateToHome = { navigator.navigateToHome() }
        )
    }

    composable(
        route = AuthRoute.Signup.route
    ) {
        SignupRoute(
            onNavigateToLogin = { navigator.popBack() },
            onNavigateToHome = { navigator.navigateToHome() }
        )
    }
}