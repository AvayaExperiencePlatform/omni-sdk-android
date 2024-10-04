package com.avaya.axp.omni.sample.messaging.ui.screens

import android.app.NotificationManager
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.avaya.axp.omni.sample.messaging.model.NotificationParams
import com.avaya.axp.omni.sample.messaging.viewmodel.MessagingSdkViewModel
import com.avaya.messaging_ui.ShowMessagingUI

@Composable
fun MessagingScreen(messagingSdkViewModel: MessagingSdkViewModel) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val lifecycle = lifecycleOwner.lifecycle
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                NotificationParams.showNotification = false
                context.removeNotification()
            } else if(event == Lifecycle.Event.ON_STOP) {
                NotificationParams.showNotification = true
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            NotificationParams.showNotification = true
            lifecycle.removeObserver(observer)
        }
    }
    ShowMessagingUI(conversationHandler = messagingSdkViewModel.conversationHandler!!)
}

fun Context.removeNotification() {
    val notificationManager =
        this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.cancelAll()
    NotificationParams.messagesList.clear()
}
