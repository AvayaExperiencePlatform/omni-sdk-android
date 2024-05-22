package com.avaya.axp.client.sample_app_messaging.viewmodel

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.avaya.axp.client.sample_app_messaging.AppWebServerJwtProvider
import com.avaya.axp.client.sample_app_messaging.BuildConfig
import com.avaya.axp.client.sample_app_messaging.model.NotificationMessage
import com.avaya.axp.client.sample_app_messaging.model.NotificationParams
import com.avaya.axp.client.sample_app_messaging.service.createNotificationBuilder
import com.avaya.axp.client.sample_app_messaging.service.getSenderName
import com.avaya.axp.client.sample_app_messaging.util.ADDRESS
import com.avaya.axp.client.sample_app_messaging.util.ATTACHMENT_EMOJI
import com.avaya.axp.client.sample_app_messaging.util.AXP_APP_KEY
import com.avaya.axp.client.sample_app_messaging.util.AXP_CONFIG_ID
import com.avaya.axp.client.sample_app_messaging.util.AXP_HOSTNAME
import com.avaya.axp.client.sample_app_messaging.util.AXP_INTEGRATION_ID
import com.avaya.axp.client.sample_app_messaging.util.CONTEXT_PARAMETERS
import com.avaya.axp.client.sample_app_messaging.util.IMAGE_EMOJI
import com.avaya.axp.client.sample_app_messaging.util.LATITUDE
import com.avaya.axp.client.sample_app_messaging.util.LONGITUDE
import com.avaya.axp.client.sample_app_messaging.util.MAX_NOTIFICATIONS
import com.avaya.axp.client.sample_app_messaging.util.USER_NAME
import com.avaya.axp.client.sdk.core.AxpClientSdk
import com.avaya.axp.client.sdk.core.AxpFailureResult
import com.avaya.axp.client.sdk.core.AxpSdkConfig
import com.avaya.axp.client.sdk.core.AxpSuccessResult
import com.avaya.axp.client.sdk.core.SdkConfigKey
import com.avaya.axp.client.sdk.core.UserSession
import com.avaya.axp.client.sdk.messaging.AxpMessaging
import com.avaya.axp.client.sdk.messaging.ElementType
import com.avaya.axp.client.sdk.messaging.EventState
import com.avaya.axp.client.sdk.messaging.LocationMessage
import com.avaya.axp.client.sdk.messaging.Message
import com.avaya.axp.client.sdk.messaging.messageArrivedFlow
import com.avaya.axp.client.sdk.util.summary
import com.avaya.messaging_ui.AvayaMessagingUiSDK
import com.avaya.messaging_ui.ConversationHandler
import com.avaya.messaging_ui.UiEventHandler
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.logging.HttpLoggingInterceptor
import org.slf4j.LoggerFactory

class MessagingSdkViewModel : ViewModel() {

    var initStatus = false
    var conversationHandler: ConversationHandler? = null

    private lateinit var messagingSdkConfig: AxpSdkConfig
    private lateinit var session: UserSession
    private val log = LoggerFactory.getLogger("MessagingSdkViewModel")

    fun configureSdk(context: Context) {
        try {
            messagingSdkConfig = AxpClientSdk.sdkConfig ?: AxpClientSdk.configureSdk(
                applicationContext = context,
                host = AXP_HOSTNAME,
                appKey = AXP_APP_KEY,
                integrationId = AXP_INTEGRATION_ID,
                jwtProvider = AppWebServerJwtProvider(),
                configMap = mapOf(
                    SdkConfigKey.HTTP_LOG_LEVEL to if (BuildConfig.DEBUG) {
                        HttpLoggingInterceptor.Level.BODY
                    } else {
                        HttpLoggingInterceptor.Level.BASIC
                    },
                    SdkConfigKey.DISPLAY_NAME to USER_NAME,
                    SdkConfigKey.PUSH_CONFIG_ID to AXP_CONFIG_ID
                )
            )
            setUiFlags()
            setUiEventHandler()
        } catch (e: Exception) {
            log.warn("Failed to configure AxpClientSdk: {}", e.summary)
        }
    }

    fun initSession(context: Context, onComplete: (String?) -> Unit) {
        viewModelScope.launch {
            val conversation =
                try {
                    when (val result = AxpClientSdk.getDefaultConversation()) {
                        is AxpSuccessResult -> {
                            onComplete(result.value.session.sessionId)
                            result.value
                        }

                        is AxpFailureResult -> {
                            onComplete(null)
                            log.warn("Failed to get default conversation: {}", result.error)
                            null
                        }
                    }
                } catch (e: Exception) {
                    log.warn("Failed to get default conversation: {}", e.summary)
                    onComplete(null)
                    null
                }
            if (conversation == null) {
                initStatus = false
                return@launch
            }
            session = conversation.session
            conversation.contextParameters =
                CONTEXT_PARAMETERS
            initStatus = true
            conversationHandler =
                AvayaMessagingUiSDK.getConversationHandler(conversation, null)
//            onComplete(null)
            this.launch {
                AxpMessaging.eventFlow.collect { messagingStreamState ->
                    if (messagingStreamState.state == EventState.CLOSED) {
                        NotificationParams.pollingInProgress = false
                    } else if (messagingStreamState.state == EventState.CONNECTED) {
                        NotificationParams.pollingInProgress = true
                    }
                }
            }
            this.launch {
                conversation.messageArrivedFlow.filterNotNull().collect { message ->
                    showNotification(context, message)
                }
            }
        }
    }

    private fun showNotification(context: Context,message: Message){
        if (NotificationParams.showNotification) {
            createNotificationBuilder(context = context)
            var messageText = message.text
            if (message.type == ElementType.IMAGE) {
                messageText =
                    if (messageText.isBlank()) IMAGE_EMOJI + "Image"
                    else IMAGE_EMOJI + messageText
            } else if (message.type == ElementType.FILE) {
                messageText =
                    if (messageText.isBlank()) ATTACHMENT_EMOJI + "File"
                    else ATTACHMENT_EMOJI + messageText
            }
            if (messageText.isBlank()) messageText =
                "You have one unread message"
            if (NotificationParams.messagesList.size == MAX_NOTIFICATIONS) NotificationParams.messagesList.removeFirst()
            NotificationParams.messagesList.add(
                NotificationMessage(
                    messageText,
                    getSenderName(
                        message.senderParticipant
                    )
                )
            )

            var sender = Person.Builder()
                .setName("Agent")
                .build()
            val messagingStyle = NotificationCompat.MessagingStyle(sender)
            for (notification in NotificationParams.messagesList) {
                sender = Person.Builder()
                    .setName(notification.sender)
                    .build()
                val notificationMessage = NotificationCompat.MessagingStyle.Message(notification.message, System.currentTimeMillis(), sender)
                messagingStyle.addMessage(notificationMessage)
            }

            NotificationParams.notificationBuilder?.setStyle(messagingStyle)
            NotificationParams.notificationManager?.notify(
                1,
                NotificationParams.notificationBuilder?.build()
            )

        }
    }

    private fun setUiEventHandler() {
        AvayaMessagingUiSDK.setUiEventHandler(
            object : UiEventHandler {
                override fun getLocationDetails(
                    onComplete: (LocationMessage?) -> Unit
                ) {
                    onComplete(
                        LocationMessage(
                            lat = LATITUDE.toDouble(),
                            long = LONGITUDE.toDouble(),
                            address = ADDRESS
                        )
                    )
                }

                override fun getLocationUrl(latitude: Double?, longitude: Double?): String {
                    if (latitude == null || longitude == null) return ""
                    return "https://maps.google.com/maps?q=$latitude,$longitude"
                }

            }
        )
    }

    fun clear() {
        runBlocking {
            try {
                AxpClientSdk.shutDown() is AxpSuccessResult
            } catch (e: Exception) {
                log.warn("Failed to shutdown: {}", e.summary)
                false
            }

        }
        initStatus = false
        conversationHandler = null
    }

    private fun setUiFlags() {
        AvayaMessagingUiSDK.apply {
            showAgentEvents = true
            showAgentEvents = true
            showAgentName = true
            showActiveParticipants = true
            showIdleTimeoutDialog = true
        }
    }

    companion object {
        fun provideFactory(): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MessagingSdkViewModel() as T
                }
            }
    }

}



