package com.sonisoft.datasetu.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sonisoft.datasetu.feature.discovery.DiscoveryScreen
import com.sonisoft.datasetu.feature.history.HistoryScreen
import com.sonisoft.datasetu.feature.home.HomeScreen
import com.sonisoft.datasetu.feature.receive.ReceiveScreen
import com.sonisoft.datasetu.feature.send.SendScreen
import com.sonisoft.datasetu.feature.settings.SettingsScreen
import com.sonisoft.datasetu.feature.transfer.TransferScreen
import com.sonisoft.datasetu.navigation.route.MainRoute

fun NavGraphBuilder.mainGraph() {
    composable(
        route = MainRoute.Home.route
    ) {
        HomeScreen()
    }

    composable(
        route = MainRoute.Discovery.route
    ) {
        DiscoveryScreen()
    }

    composable(
        route = MainRoute.Send.route
    ) {
        SendScreen()
    }

    composable(
        route = MainRoute.Receive.route
    ) {
        ReceiveScreen()
    }

    composable(
        route = MainRoute.Transfer.route
    ) {
        TransferScreen()
    }

    composable(
        route = MainRoute.History.route
    ) {
        HistoryScreen()
    }

    composable(
        route = MainRoute.Settings.route
    ) {
        SettingsScreen()
    }
}