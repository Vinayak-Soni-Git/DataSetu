package com.sonisoft.datasetu.navigation.route

sealed class AuthRoute(
    override val route: String
) : NavigationDestination {

    data object Login : AuthRoute("login")

    data object Signup : AuthRoute("signup")
}