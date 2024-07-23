package com.avaya.axp.omni.sample.calling.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.avaya.axp.omni.sample.calling.ui.homeScreen.HomeScreen
import com.avaya.axp.omni.sample.calling.ui.settings.SettingsScreen

sealed class NavigationRoute(val route: String) {
    data object HomeRoute : NavigationRoute("home")
    data object SettingsRoute : NavigationRoute("settings")
}

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavigationRoute.HomeRoute.route
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = NavigationRoute.HomeRoute.route) {
            HomeScreen(onSettingsClicked = {
                navController.navigate(NavigationRoute.SettingsRoute.route)
            })
        }
        composable(route = NavigationRoute.SettingsRoute.route) {
             SettingsScreen(onNavUp = navController::navigateUp)
        }
    }
}
