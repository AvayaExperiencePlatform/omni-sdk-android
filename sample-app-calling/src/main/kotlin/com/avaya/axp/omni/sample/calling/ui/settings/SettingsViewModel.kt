package com.avaya.axp.omni.sample.calling.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avaya.axp.omni.sample.calling.config.ConfigData
import com.avaya.axp.omni.sample.calling.config.SdkManager
import com.avaya.axp.omni.sample.calling.ui.settings.SettingsUiState.Loading
import com.avaya.axp.omni.sample.calling.ui.settings.SettingsUiState.Success
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class SettingsViewModel : ViewModel() {
    private val sdkManager: SdkManager = SdkManager.instance
    private val configDataSource = sdkManager.configDataSource

    val settingsUiState: StateFlow<SettingsUiState> =
        configDataSource.configDataFlow
            .map { configData ->
                Success(configData.asSettingsData)
            }
            .stateIn(
                scope = viewModelScope,
                started = WhileSubscribed(5.seconds.inWholeMilliseconds),
                initialValue = Loading
            )

    fun saveSettings(
        tokenServerUrl: String,
        userId: String,
        userName: String,
        emailAddress: String,
        verified: Boolean
    ) {
        viewModelScope.launch {
            configDataSource.saveSettingsData(
                tokenServerUrl = tokenServerUrl.trim(),
                userId = userId.trim(),
                userName = userName.trim(),
                emailAddress = emailAddress.trim(),
                verifiedCustomer = verified
            )
        }
    }
}

/**
 * The subset of [ConfigData] that is entered by the user on the settings
 * screen.
 */
data class SettingsData(
    val tokenServerUrl: String,
    val userName: String,
    val userId: String,
    val emailAddress: String,
    val verifiedCustomer: Boolean
)

private val ConfigData.asSettingsData: SettingsData
    get() = SettingsData(
        tokenServerUrl = tokenServerUrl,
        userName = userName,
        userId = userId,
        emailAddress = emailAddress,
        verifiedCustomer = verifiedCustomer
    )

sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    data class Success(val settingsData: SettingsData) : SettingsUiState
}
