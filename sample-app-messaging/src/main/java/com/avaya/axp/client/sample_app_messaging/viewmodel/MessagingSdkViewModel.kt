package com.avaya.axp.client.sample_app_messaging.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.avaya.axp.client.sample_app_messaging.handlers.MyJwtProvider
import com.avaya.axp.client.sample_app_messaging.util.ADDRESS
import com.avaya.axp.client.sample_app_messaging.util.AXP_APP_KEY
import com.avaya.axp.client.sample_app_messaging.util.AXP_BASE_URL
import com.avaya.axp.client.sample_app_messaging.util.AXP_INTEGRATION_ID
import com.avaya.axp.client.sample_app_messaging.util.CONTEXT_PARAMETER_KEY
import com.avaya.axp.client.sample_app_messaging.util.CONTEXT_PARAMETER_VALUE
import com.avaya.axp.client.sample_app_messaging.util.LATITUDE
import com.avaya.axp.client.sample_app_messaging.util.LONGITUDE
import com.avaya.axp.client.sample_app_messaging.util.USER_NAME
import com.avaya.axp.client.sdk.core.AxpClientSdk
import com.avaya.axp.client.sdk.core.AxpFailureResult
import com.avaya.axp.client.sdk.core.AxpSdkConfig
import com.avaya.axp.client.sdk.core.AxpSuccessResult
import com.avaya.axp.client.sdk.core.SdkConfigKey
import com.avaya.axp.client.sdk.core.UserSession
import com.avaya.axp.client.sdk.messaging.AxpMessaging
import com.avaya.axp.client.sdk.messaging.EventState
import com.avaya.axp.client.sdk.messaging.LocationMessage
import com.avaya.axp.client.sdk.util.HttpLogLevel
import com.avaya.messaging_ui.AvayaMessagingUiSDK
import com.avaya.messaging_ui.ConversationHandler
import com.avaya.messaging_ui.UiEventHandler
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MessagingSdkViewModel : ViewModel() {

    var initStatus = false
    var conversationHandler: ConversationHandler? = null

    private lateinit var messagingSdkConfig: AxpSdkConfig
    private lateinit var session: UserSession
    private val logTag = "MessagingSdkViewModel"

    fun configureSdk() {
        messagingSdkConfig = AxpClientSdk.sdkConfig ?: AxpClientSdk.configureSdk(
            host = AXP_BASE_URL,
            appKey = AXP_APP_KEY,
            integrationId = AXP_INTEGRATION_ID,
            jwtProvider = MyJwtProvider(),
            configMap = mapOf(
                SdkConfigKey.HTTP_LOG_LEVEL to HttpLogLevel.BASIC,
                SdkConfigKey.DISPLAY_NAME to USER_NAME
            )
        )
        setUiFlags()
        setUiEventHandler()
    }

    fun initSession(onComplete: (String?) -> Unit) {
        viewModelScope.launch() {
            val conversation =
                try {
                    when (val result = AxpClientSdk.getDefaultConversation()) {
                        is AxpSuccessResult -> {
                            result.value
                        }

                        is AxpFailureResult -> {
                            onComplete(result.error.toString())
                            Log.d(logTag, result.error.toString())
                            null
                        }
                    }
                } catch (e: Exception) {
                    Log.d(logTag, e.message.toString())
                    onComplete(e.message.toString())
                    null
                }
            if (conversation == null) {
                initStatus = false
                return@launch
            }
            session = conversation.session
            conversation.contextParameters =
                mapOf(CONTEXT_PARAMETER_KEY to CONTEXT_PARAMETER_VALUE)
            initStatus = true
            conversationHandler =
                AvayaMessagingUiSDK.getConversationHandler(conversation)
            onComplete(null)
            this.launch {
                AxpMessaging.eventFlow.filter { it.state == EventState.CLOSED }.collect()
                {
                    try {
                        AxpMessaging.reconnect()
                    } catch (e: Exception) {
                        Log.d(logTag, e.message.toString())
                    }
                }
            }
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

    fun setUiEventHandler() {
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
                Log.d(logTag, e.message.toString())
                false
            }

        }
        initStatus = false
        conversationHandler = null
    }

    private fun setUiFlags() {

        AvayaMessagingUiSDK.showAgentEvents = true
        AvayaMessagingUiSDK.showAgentEvents = true
        AvayaMessagingUiSDK.showAgentName = true
        AvayaMessagingUiSDK.showActiveParticipants = true
    }
}



