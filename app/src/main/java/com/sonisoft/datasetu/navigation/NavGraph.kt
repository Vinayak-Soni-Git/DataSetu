package com.sonisoft.datasetu.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.Modifier
import com.sonisoft.datasetu.feature.splash.SplashRoute
import com.sonisoft.datasetu.navigation.graph.authGraph
import com.sonisoft.datasetu.navigation.graph.mainGraph
import com.sonisoft.datasetu.navigation.graph.onboardingGraph
import com.sonisoft.datasetu.navigation.route.OnboardingRoute
import com.sonisoft.datasetu.navigation.route.MainRoute

@Composable
fun AppNavGraph() {

    val navController = rememberNavController()

    val navigator = remember(navController) {
        AppNavigator(navController)
    }

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val bottomBarRoutes = listOf(MainRoute.Home.route, MainRoute.Storage.route, MainRoute.Profile.route)
    val isBottomBarVisible = currentRoute in bottomBarRoutes

    Scaffold(
        bottomBar = {
            if (isBottomBarVisible) {
                AnimatedBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route -> navigator.navigateBottomTab(route) }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = OnboardingRoute.Splash.route,
            modifier = Modifier.padding(paddingValues),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            }
        ) {

            composable(
                OnboardingRoute.Splash.route
            ) {
                SplashRoute(
                    onNavigateToOnboarding = { navigator.navigateToOnboarding() },
                    onNavigateToLogin = { navigator.navigateToLogin() },
                    onNavigateToHome = { navigator.navigateToHome() }
                )
            }

            onboardingGraph(navigator)
            authGraph(navigator)
            mainGraph(navigator)
        }
    }
}