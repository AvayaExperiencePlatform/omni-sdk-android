package com.avaya.axp.client.sample.ui.homeScreen

import androidx.lifecycle.ViewModel
import com.avaya.axp.client.sample.config.SdkConfigurationState.APP_NOT_CONFIGURED
import com.avaya.axp.client.sample.config.SdkConfigurationState.SDK_CONFIGURED
import com.avaya.axp.client.sample.config.SdkManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.transform

class HomeViewModel : ViewModel() {

    private val sdkManager: SdkManager = SdkManager.instance

    private val configDataSource = sdkManager.configDataSource

    val displayNameFlow: Flow<String> = configDataSource.displayNameFlow
    val remoteAddressFlow: Flow<String> = configDataSource.remoteAddressFlow

    val configurationNeededFlow: Flow<Boolean> = sdkManager.stateFlow.transform {
        emit(it == APP_NOT_CONFIGURED)
    }
    val isSdkConfiguredFlow: Flow<Boolean> = sdkManager.stateFlow.transform {
        emit(it == SDK_CONFIGURED)
    }

    val configErrorFlow: StateFlow<String?> = sdkManager.errorFlow
}
