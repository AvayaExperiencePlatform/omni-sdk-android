/*
 * Copyright 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.avaya.axp.client.sample.call

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telecom.DisconnectCause
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.content.PermissionChecker
import com.avaya.axp.client.sample.R
import com.avaya.axp.client.sample.ui.call.CallActivity
import com.avaya.axp.client.sdk.webrtc.CallNotificationManager
import com.avaya.axp.client.sdk.webrtc.TelecomCall
import com.avaya.axp.client.sdk.webrtc.TelecomCallAction

/**
 * Handles call status changes and updates the notification accordingly. For more guidance around
 * notifications check https://developer.android.com/develop/ui/views/notifications
 *
 * @see updateCallNotification
 */
class TelecomCallNotificationManager(
    private val context: Context
) : CallNotificationManager {

    internal companion object {
        const val TELECOM_NOTIFICATION_ID = 200
        const val TELECOM_NOTIFICATION_ACTION = "telecom_action"
        const val TELECOM_NOTIFICATION_CALL_CHANNEL_ID = "telecom_call_channel"
    }

    private val notificationManager: NotificationManagerCompat =
        NotificationManagerCompat.from(context)

    /**
     * Updates, creates or dismisses a CallStyle notification based on the given
     * [TelecomCall].
     */
    @SuppressLint("MissingPermission")
    override fun updateCallNotification(call: TelecomCall) {
        // If notifications are not granted, skip it.
        if (context.doesNotHavePermission(Manifest.permission.POST_NOTIFICATIONS)) {
            return
        }

        // Ensure that the channel is created
        createNotificationChannel()

        // Update or dismiss notification
        when (call) {
            TelecomCall.None, is TelecomCall.Unregistered -> {
                notificationManager.cancel(TELECOM_NOTIFICATION_ID)
            }

            is TelecomCall.Registered -> {
                val notification = createNotification(call)
                notificationManager.notify(TELECOM_NOTIFICATION_ID, notification)
            }
        }
    }

    private fun createNotification(call: TelecomCall.Registered): Notification {
        // To display the caller information
        val caller = Person.Builder()
            .setName(call.callAttributes.displayName)
            .setUri(call.callAttributes.address.toString())
            .setImportant(true)
            .build()

        // Defines the full screen notification activity or the activity to launch once the user taps
        // on the notification
        val contentIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, CallActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        // Define the call style based on the call state and set the right actions
        val callStyle = NotificationCompat.CallStyle.forOngoingCall(
            caller,
            getPendingIntent(
                TelecomCallAction.Disconnect(DisconnectCause.LOCAL)
            )
        )

        return NotificationCompat.Builder(context, TELECOM_NOTIFICATION_CALL_CHANNEL_ID)
            .setContentIntent(contentIntent)
            .setFullScreenIntent(contentIntent, true)
            .setSmallIcon(R.drawable.ic_round_call_24)
            .setOngoing(true)
            .setStyle(callStyle)
            .addAction(
                if (call.isMuted) R.drawable.ic_round_mic_off_24 else R.drawable.ic_round_mic_24,
                if (call.isMuted) "Unmute" else "Mute",
                getPendingIntent(
                    TelecomCallAction.ToggleMute(!call.isMuted),
                )
            )
            .build()
    }

    /**
     * Creates a PendingIntent for the given [TelecomCallAction]. Since the actions are parcelable
     * we can directly pass them as extra parameters in the bundle.
     */
    private fun getPendingIntent(action: TelecomCallAction): PendingIntent {
        val callIntent = Intent(context, TelecomCallBroadcast::class.java).apply {
            putExtra(TELECOM_NOTIFICATION_ACTION, action)
        }

        return PendingIntent.getBroadcast(
            context,
            callIntent.hashCode(),
            callIntent,
            PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun createNotificationChannel() {
        notificationManager.createNotificationChannelsCompat(
            listOf(
                NotificationChannelCompat.Builder(
                    TELECOM_NOTIFICATION_CALL_CHANNEL_ID,
                    NotificationManagerCompat.IMPORTANCE_DEFAULT
                )
                    .setName("Calls")
                    .setDescription("Displays the ongoing call notifications")
                    .build(),
            ),
        )
    }
}

private fun Context.doesNotHavePermission(permission: String) =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            (PermissionChecker.checkSelfPermission(this, permission)
                    != PermissionChecker.PERMISSION_GRANTED)
