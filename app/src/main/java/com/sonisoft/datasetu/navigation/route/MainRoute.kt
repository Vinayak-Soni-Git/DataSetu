package com.sonisoft.datasetu.navigation.route

import android.net.Uri

sealed class MainRoute(
    override val route: String
) : NavigationDestination {

    data object Home : MainRoute("home")

    data object Storage : MainRoute("storage")

    data object FilesList : MainRoute("files_list/{categoryName}") {
        fun createRoute(categoryName: String) = "files_list/$categoryName"
    }

    data object ImageViewer : MainRoute("image_viewer/{uri}") {
        fun createRoute(uri: String) = "image_viewer/${Uri.encode(uri)}"
    }

    data object Profile : MainRoute("profile")

    data object Permission : MainRoute("permission")

    data object Discovery : MainRoute("discovery")

    data object Send : MainRoute("send")

    data object Receive : MainRoute("receive")

    data object Transfer : MainRoute("transfer")

    data object History : MainRoute("history")

    data object Settings : MainRoute("settings")
}