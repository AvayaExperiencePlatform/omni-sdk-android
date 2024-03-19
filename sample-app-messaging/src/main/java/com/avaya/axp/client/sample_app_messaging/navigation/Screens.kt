package com.avaya.axp.client.sample_app_messaging.navigation

sealed class Screens(val route:String){
    data object HomeScreen : Screens(route = "home_screen")
    data object MessagingScreen : Screens(route = "messaging_screen")
}
