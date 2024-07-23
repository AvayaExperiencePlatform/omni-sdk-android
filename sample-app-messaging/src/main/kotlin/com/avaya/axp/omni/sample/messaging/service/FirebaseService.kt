package com.avaya.axp.omni.sample.messaging.service

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.avaya.axp.omni.sample.messaging.model.NotificationParams
import com.avaya.axp.omni.sample.messaging.util.CONVERSATION_ID
import com.avaya.axp.omni.sample.messaging.util.EVENT_DATE
import com.avaya.axp.omni.sample.messaging.util.EVENT_ID
import com.avaya.axp.omni.sample.messaging.util.SESSION_ID
import com.avaya.axp.omni.sdk.messaging.NotificationPayload
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class FirebaseService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d("FirebaseService", "onMessageReceived: ${message.data}")
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED && !NotificationParams.pollingInProgress && NotificationParams.showNotification
        ) {
            val notificationPayload = getNotificationPayload(message.data)
            NotificationParams.queue.add(notificationPayload)
            val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                .build()
            WorkManager.getInstance(this).enqueue(workRequest)
        }
        super.onMessageReceived(message)
    }
}

fun getNotificationPayload(dataMap: Map<String, String>): NotificationPayload {
    return NotificationPayload(
        convertStringToDate(dataMap[EVENT_DATE] ?: ""),
        dataMap[EVENT_ID] ?: "",
        dataMap[CONVERSATION_ID] ?: "",
        dataMap[SESSION_ID] ?: ""
    )
}
fun convertStringToDate(dateString: String): OffsetDateTime {
    return try {
        OffsetDateTime.parse(dateString, DateTimeFormatter.ISO_INSTANT)
    } catch (e: Exception) {
        OffsetDateTime.now()
    }
}
