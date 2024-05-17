package com.avaya.axp.client.sample_app_messaging.ui.screens

import android.Manifest
import android.app.Activity
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.avaya.axp.client.sample_app_messaging.R
import com.avaya.axp.client.sample_app_messaging.navigation.Screens
import com.avaya.axp.client.sample_app_messaging.network.provideNotificationService
import com.avaya.axp.client.sample_app_messaging.repository.NotificationRegistrationRepositoryImpl
import com.avaya.axp.client.sample_app_messaging.ui.LoadingLayout
import com.avaya.axp.client.sample_app_messaging.ui.theme.AXPClientSDKForAndroidTheme
import com.avaya.axp.client.sample_app_messaging.util.AXP_CONFIG_ID
import com.avaya.axp.client.sample_app_messaging.viewmodel.MessagingSdkViewModel
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun HomeScreen(messagingSdkViewModel: MessagingSdkViewModel, navController: NavController) {
    val context = LocalContext.current
    val activity = context as Activity
    val loading = remember {
        mutableStateOf(false)
    }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(true) {
        activity.requestNotificationPermission()
    }
    BackHandler {
        coroutineScope.launch {
            messagingSdkViewModel.clear()
        }
        navController.popBackStack()
    }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (messagingSdkViewModel.initStatus) {
                        navController.navigate(Screens.MessagingScreen.route)
                    } else {
                        loading.value = true
                        messagingSdkViewModel.configureSdk(context.applicationContext)
                        messagingSdkViewModel.initSession(context) { sessionId ->
                            loading.value = false
                            if (sessionId != null) {
                                saveDeviceRegistration(sessionId)
                                navController.navigate(Screens.MessagingScreen.route)
                            } else {
                                Toast.makeText(context, "something went wrong", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Message, contentDescription = null)
            }
        }
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)
        HomeScreenContent(
            contentModifier
        )
    }
    if (loading.value) {
        LoadingLayout()
    }
}

@Composable
private fun HomeScreenContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.avaya_logo),
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth(0.75f),
            contentScale = ContentScale.FillWidth,
        )
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    AXPClientSDKForAndroidTheme {
        HomeScreenContent()
    }
}

fun Activity.requestNotificationPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            1
        )
    }
}

fun saveDeviceRegistration(sessionId: String) {
    val repository = NotificationRegistrationRepositoryImpl(provideNotificationService())
    CoroutineScope(Dispatchers.IO).launch {
        val token = getDeviceToken()
        Log.d("saveDeviceRegistration", "token = $token")
        token?.let {
            repository.saveDeviceRegistration(it, AXP_CONFIG_ID, sessionId)
        } ?: false
    }
}

suspend fun getDeviceToken(): String? {
    return try {
        FirebaseMessaging.getInstance().token.await()
    } catch (e: Exception) {
        Log.d("getDeviceToken", "getDeviceToken error = $e")
        null
    }
}