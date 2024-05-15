package com.avaya.axp.client.sample_app_messaging

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.avaya.axp.client.sample_app_messaging.navigation.Screens
import com.avaya.axp.client.sample_app_messaging.navigation.SetupNavGraph
import com.avaya.axp.client.sample_app_messaging.ui.theme.AXPClientSDKForAndroidTheme
import com.avaya.axp.client.sample_app_messaging.util.NOTIFICATION_INTENT

class MainActivity : ComponentActivity() {

    private var navController: NavHostController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AXPClientSDKForAndroidTheme {
                navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SetupNavGraph(navController = navController!!)
                }
            }
        }
        handleIntent(intent = intent)
    }

    override fun onNewIntent(intent: Intent) {
        handleIntent(intent)
        super.onNewIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (intent.hasExtra(NOTIFICATION_INTENT)) {
            navController?.let { controller ->
                if (controller.currentBackStackEntry?.destination?.route == Screens.HomeScreen.route)
                    controller.navigate(Screens.MessagingScreen.route)
            }
        }
    }
}
