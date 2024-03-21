package com.avaya.axp.client.sample_app_messaging.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.avaya.axp.client.sample_app_messaging.ui.screens.MessagingScreen
import com.avaya.axp.client.sample_app_messaging.ui.screens.HomeScreen
import com.avaya.axp.client.sample_app_messaging.viewmodel.MessagingSdkViewModel

@Composable
fun SetupNavGraph(
    navController: NavHostController
) {
    val messagingSdkViewModel: MessagingSdkViewModel = viewModel(
        factory = MessagingSdkViewModel.provideFactory()
    )
    NavHost(navController = navController, startDestination = Screens.HomeScreen.route) {
        composable(Screens.HomeScreen.route) {
            HomeScreen(messagingSdkViewModel,navController)
        }
        composable(Screens.MessagingScreen.route) {
            MessagingScreen(messagingSdkViewModel = messagingSdkViewModel)
        }
    }
}