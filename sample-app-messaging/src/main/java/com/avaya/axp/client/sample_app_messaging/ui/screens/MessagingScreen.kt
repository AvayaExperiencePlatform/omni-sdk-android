package com.avaya.axp.client.sample_app_messaging.ui.screens

import androidx.compose.runtime.Composable
import com.avaya.axp.client.sample_app_messaging.viewmodel.MessagingSdkViewModel
import com.avaya.messaging_ui.ShowMessagingUI

@Composable
fun MessagingScreen(messagingSdkViewModel: MessagingSdkViewModel) {
    ShowMessagingUI(conversationHandler = messagingSdkViewModel.conversationHandler!!)
}