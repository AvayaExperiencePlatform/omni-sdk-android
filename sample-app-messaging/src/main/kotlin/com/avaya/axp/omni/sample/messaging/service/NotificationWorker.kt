package com.avaya.axp.omni.sample.messaging.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.avaya.axp.omni.sample.messaging.AppWebServerJwtProvider
import com.avaya.axp.omni.sample.messaging.MainActivity
import com.avaya.axp.omni.sample.messaging.R
import com.avaya.axp.omni.sample.messaging.model.NotificationMessage
import com.avaya.axp.omni.sample.messaging.model.NotificationParams
import com.avaya.axp.omni.sample.messaging.util.ATTACHMENT_EMOJI
import com.avaya.axp.omni.sample.messaging.util.AXP_API_KEY
import com.avaya.axp.omni.sample.messaging.util.AXP_HOSTNAME
import com.avaya.axp.omni.sample.messaging.util.AXP_INTEGRATION_ID
import com.avaya.axp.omni.sample.messaging.util.IMAGE_EMOJI
import com.avaya.axp.omni.sample.messaging.util.MAX_NOTIFICATIONS
import com.avaya.axp.omni.sample.messaging.util.NOTIFICATION_CHANNEL_DESCRIPTION
import com.avaya.axp.omni.sample.messaging.util.NOTIFICATION_CHANNEL_ID
import com.avaya.axp.omni.sample.messaging.util.NOTIFICATION_CHANNEL_NAME
import com.avaya.axp.omni.sample.messaging.util.NOTIFICATION_GROUP_KEY
import com.avaya.axp.omni.sample.messaging.util.NOTIFICATION_INTENT
import com.avaya.axp.omni.sdk.core.AxpFailureResult
import com.avaya.axp.omni.sdk.core.AxpOmniSdk
import com.avaya.axp.omni.sdk.core.AxpSuccessResult
import com.avaya.axp.omni.sdk.core.Participant
import com.avaya.axp.omni.sdk.core.ParticipantType
import com.avaya.axp.omni.sdk.messaging.AxpMessaging
import com.avaya.axp.omni.sdk.messaging.ElementType
import com.avaya.axp.omni.sdk.messaging.Message
import com.avaya.axp.omni.sdk.messaging.NotificationPayload
import com.avaya.axp.omni.sdk.messaging.NotificationResult


class NotificationWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        createNotificationBuilder(applicationContext)
        while (NotificationParams.queue.isNotEmpty()) {
            val notificationPayload = NotificationParams.queue.poll()
            var messageList = getMessagesList(notificationPayload!!,applicationContext)
            if (messageList.isNotEmpty()) {
                for (notificationData in messageList) {
                    notificationData.message?.let { messageData ->
                        val messageText = getMessageText(messageData)
                        if (NotificationParams.messagesList.size == MAX_NOTIFICATIONS) NotificationParams.messagesList.removeFirst()
                        NotificationParams.messagesList.add(
                            NotificationMessage(
                                messageText,
                                getSenderName(notificationData.participant)
                            )
                        )
                    }
                }
            } else {
                val messageText =
                    "You have one unread message"
                if (NotificationParams.messagesList.size == MAX_NOTIFICATIONS) NotificationParams.messagesList.removeFirst()
                NotificationParams.messagesList.add(NotificationMessage(messageText, null))
            }
        }

        var sender = Person.Builder()
            .setName("Agent")
            .build()
        val messagingStyle = NotificationCompat.MessagingStyle(sender)
        for (message in NotificationParams.messagesList) {
            sender = Person.Builder()
                .setName(message.sender)
                .build()
            val notificationMessage = NotificationCompat.MessagingStyle.Message(
                message.message,
                System.currentTimeMillis(),
                sender
            )
            messagingStyle.addMessage(notificationMessage)
        }

        NotificationParams.notificationBuilder?.setStyle(messagingStyle)
        NotificationParams.notificationManager?.notify(
            1,
            NotificationParams.notificationBuilder?.build()
        )
        return Result.success()
    }
}

fun createNotificationBuilder(context: Context) {
    if (NotificationParams.notificationBuilder != null) return
    val channelId = NOTIFICATION_CHANNEL_ID
    val intent = Intent(
        context,
        MainActivity::class.java
    )
    intent.putExtra(NOTIFICATION_INTENT, true)
    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_IMMUTABLE
    )
    val bitmap =
        BitmapFactory.decodeResource(context.resources, R.drawable.avaya_logo_square)
    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_message_received)
        .setLargeIcon(bitmap)
        .setContentTitle("Message")
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .setGroup(NOTIFICATION_GROUP_KEY)

    NotificationParams.notificationBuilder = builder
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    createNotificationChannel(notificationManager)
    NotificationParams.notificationManager = notificationManager
}

fun createNotificationChannel(notificationManager: NotificationManager) {
    val name = NOTIFICATION_CHANNEL_NAME
    val descriptionText = NOTIFICATION_CHANNEL_DESCRIPTION
    val importance = NotificationManager.IMPORTANCE_HIGH
    val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
        description = descriptionText
    }
    channel.importance = importance
    channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
    channel.enableVibration(true)
    channel.vibrationPattern = longArrayOf(100L, 500L)
    notificationManager.createNotificationChannel(channel)
}

suspend fun getMessagesList(
    notificationPayload: NotificationPayload,
    context: Context
): List<NotificationResult> {
    if (AxpOmniSdk.sdkConfig == null) {
        try {
            AxpOmniSdk.configureSdk(
                applicationContext = context,
                host = AXP_HOSTNAME,
                appKey = AXP_API_KEY,
                integrationId = AXP_INTEGRATION_ID,
                jwtProvider = AppWebServerJwtProvider()
            )
        } catch (e: Exception) {
            Log.d("NotificationWorker", "Error in configuring SDK: ${e.message}")
            return emptyList()
        }
    }
    return when (val result = AxpMessaging.getNotificationData(notificationPayload)) {

        is AxpSuccessResult -> {
            result.value
        }

        is AxpFailureResult -> {
            emptyList()
        }
    }
}

fun getSenderName(participant: Participant?): String {
    if (participant != null && participant.displayName.isNotBlank()) return participant.displayName
    return when (participant?.participantType) {
        ParticipantType.BOT, ParticipantType.SYSTEM -> "System"
        else -> "Agent"
    }
}

fun getMessageText(
    messageData: Message
): String {
    var messageText = messageData.text
    if (messageData.type == ElementType.IMAGE) {
        messageText = if (messageText.isBlank()) IMAGE_EMOJI + "Image"
        else IMAGE_EMOJI + messageText
    } else if (messageData.type == ElementType.FILE) {
        messageText = if (messageText.isBlank()) ATTACHMENT_EMOJI + "File"
        else ATTACHMENT_EMOJI + messageText
    }
    if (messageText.isBlank()) messageText =
        "You have unread message"
    return messageText
}
