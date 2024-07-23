package com.avaya.axp.omni.sample.messaging.model

import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import com.avaya.axp.omni.sdk.messaging.NotificationPayload
import java.util.LinkedList
import java.util.Queue

object NotificationParams {
    var showNotification = true
    var pollingInProgress = false
    val queue: Queue<NotificationPayload> = LinkedList()
    var notificationManager: NotificationManager? = null
    var notificationBuilder: NotificationCompat.Builder? = null
    val messagesList = arrayListOf<NotificationMessage>()
}

data class NotificationMessage(
    val message: String,
    val sender: String?
)
