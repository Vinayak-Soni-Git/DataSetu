package com.sonisoft.datasetu.navigation.route

sealed class MainRoute(
    override val route: String
) : NavigationDestination {

    data object Home : MainRoute("home")

    data object Storage : MainRoute("storage")

    data object Profile : MainRoute("profile")

    data object Discovery : MainRoute("discovery")

    data object Send : MainRoute("send")

    data object Receive : MainRoute("receive")

    data object Transfer : MainRoute("transfer")

    data object History : MainRoute("history")

    data object Settings : MainRoute("settings")
}