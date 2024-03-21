package com.avaya.axp.client.sample_app_messaging.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.avaya.axp.client.sample_app_messaging.navigation.Screens
import com.avaya.axp.client.sample_app_messaging.ui.LoadingLayout
import com.avaya.axp.client.sample_app_messaging.viewmodel.MessagingSdkViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(messagingSdkViewModel: MessagingSdkViewModel,navController: NavController) {
    val context = LocalContext.current
    val loading = remember {
        mutableStateOf(false)
    }
    val coroutineScope = rememberCoroutineScope()
    BackHandler {
        coroutineScope.launch {
            messagingSdkViewModel.clear()
        }
        navController.popBackStack()
    }
    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 16.dp,
                    top = padding.calculateTopPadding() + 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
        ) {
            FloatingActionButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                onClick = {
                    if(messagingSdkViewModel.initStatus) {
                        navController.navigate(Screens.MessagingScreen.route)
                    }
                    else{
                        loading.value = true
                        messagingSdkViewModel.configureSdk()
                        messagingSdkViewModel.initSession { error ->
                            loading.value = false
                            if(error == null) {
                                navController.navigate(Screens.MessagingScreen.route)
                            }else {
                                Toast.makeText(context,error,Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Message, contentDescription = null)
            }
        }

    }
    if(loading.value) {
         LoadingLayout()
    }
}