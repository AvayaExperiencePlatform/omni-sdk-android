package com.avaya.axp.client.sample.ui.call

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.avaya.android.calling.webrtc.DtmfTone
import com.avaya.axp.client.sdk.webrtc.TelecomCall
import com.avaya.axp.client.sdk.webrtc.TelecomCallRepository
import kotlinx.coroutines.flow.StateFlow

class CallViewModel(
    telecomCallRepository: TelecomCallRepository
) : ViewModel() {

    val currentCallFlow: StateFlow<TelecomCall> = telecomCallRepository.currentCallFlow

    private val currentCall: TelecomCall.Registered?
        get() = currentCallFlow.value as? TelecomCall.Registered?

    fun sendAndPlayDtmfTone(tone: DtmfTone) {
        currentCall?.call?.sendDtmfTones(tone.asString, true)
    }

    companion object {
        fun provideFactory(
            telecomCallRepository: TelecomCallRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CallViewModel(telecomCallRepository) as T
            }
        }
    }
}
