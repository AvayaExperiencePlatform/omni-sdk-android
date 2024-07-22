package com.avaya.axp.omni.sample.calling.config

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.avaya.axp.omni.sample.calling.BuildConfig
import com.avaya.axp.omni.sample.calling.SampleApplication
import com.avaya.axp.omni.sample.calling.TokenServiceClient
import com.avaya.axp.omni.sample.calling.config.SdkConfigurationState.APP_NOT_CONFIGURED
import com.avaya.axp.omni.sample.calling.config.SdkConfigurationState.INITIALIZING
import com.avaya.axp.omni.sample.calling.config.SdkConfigurationState.SDK_CONFIGURED
import com.avaya.axp.omni.sample.calling.config.SdkConfigurationState.SDK_NOT_CONFIGURED
import com.avaya.axp.omni.sdk.core.AxpOmniSdk
import com.avaya.axp.omni.sdk.core.IntegrationId
import com.avaya.axp.omni.sdk.core.SdkConfigKey
import com.avaya.axp.omni.sdk.util.safeMessage
import com.avaya.axp.omni.sdk.util.summary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.logging.HttpLoggingInterceptor
import org.slf4j.LoggerFactory.getLogger

enum class SdkConfigurationState {
    /** The app is just starting up and hasn't looked at saved settings yet. */
    INITIALIZING,

    /** The user hasn't provided the user ID and token server URL yet. */
    APP_NOT_CONFIGURED,

    /** User ID and token server URL available but SDK not started. */
    SDK_NOT_CONFIGURED,

    /** The SDK was started. */
    SDK_CONFIGURED
}

/**
 * Singleton to manage the configuration of the AXP Omni SDK.
 */
class SdkManager(
    private val applicationContext: Context,
    dataStore: DataStore<Preferences>,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {

    private val log = getLogger("SdkManager")

    private val _stateFlow: MutableStateFlow<SdkConfigurationState> =
        MutableStateFlow(INITIALIZING)
    val stateFlow: StateFlow<SdkConfigurationState> = _stateFlow

    private val state: SdkConfigurationState
        get() = stateFlow.value

    val configDataSource: ConfigDataSource = ConfigDataSource(dataStore)

    private val _errorFlow: MutableStateFlow<String?> =
        MutableStateFlow(null)
    val errorFlow: StateFlow<String?> = _errorFlow

    private var configApi: TokenServiceClient? = null

    private var previousSettings: SettingsData? = null
    private var previousServerResults: ConfigResultsData? = null

    private val sdkConfigured: Boolean
        get() = previousSettings != null

    private val mutex = Mutex()

    init {
        coroutineScope.launch {
            configDataSource.configDataFlow.collect {
                mutex.withLock {
                    onConfigDataChanged(it)
                }
            }
        }
    }

    private suspend fun setState(newState: SdkConfigurationState) {
        log.debug("State change: {} -> {}", state, newState)
        _stateFlow.emit(newState)
    }

    private suspend fun onConfigDataChanged(configData: ConfigData) {
        if (BuildConfig.DEBUG) {
            log.debug("Config data changed: {}", configData)
        }

        when {
            state === INITIALIZING -> {
                // If we have the URL for the token server at app startup,
                // query it in case the SDK config data has changed.
                if (configData.hasRequiredSettings) {
                    if (queryTokenServer(configData)) {
                        configureSdk(configData)
                    }
                } else {
                    setState(APP_NOT_CONFIGURED)
                }
            }

            sdkConfigured -> {
                if (configData.settingsData != previousSettings) {
                    // Settings have changed, so reconfigure the SDK
                    shutDownSdk()
                    if (queryTokenServer(configData)) {
                        configureSdk(configData)
                    }
                } else if (configData.configResultsData != previousServerResults) {
                    shutDownSdk()
                    configureSdk(configData)
                } else {
                    setState(SDK_CONFIGURED)
                }
            }

            configData.hasRequiredSdkConfigData -> {
                setState(SDK_NOT_CONFIGURED)
                configureSdk(configData)
            }

            configData.hasRequiredSettings -> queryTokenServer(configData)

            else -> setState(APP_NOT_CONFIGURED)
        }
    }

    private suspend fun queryTokenServer(configData: ConfigData): Boolean {
        log.debug("Querying token server for config")

        // In case we had any leftover config data
        configApi = null

        val api = getConfigApi(configData)
        return try {
            _errorFlow.emit(null)
            val response = api.queryTokenService()
            configDataSource.saveSdkConfigData(
                response.axpHostName,
                response.axpIntegrationId,
                response.appKey,
                response.callingRemoteAddress
            )
            true
        } catch (re: RuntimeException) {
            log.warn("Failed to get config from server: {}", re.summary)
            setState(APP_NOT_CONFIGURED)
            _errorFlow.emit(re.safeMessage) // Don't need the exception name in the UI
            false
        }
    }

    private fun getConfigApi(configData: ConfigData): TokenServiceClient =
        configApi ?: instantiateTokenServiceClient(configData)

    private fun instantiateTokenServiceClient(configData: ConfigData): TokenServiceClient =
        TokenServiceClient(
            configData.tokenServerUrl,
            configData.userId,
            configData.userName,
            configData.verifiedCustomer
        ).also {
            configApi = it
        }

    private suspend fun configureSdk(configData: ConfigData) {
        log.debug("Configuring the SDK for {}", configData.hostname)

        getConfigApi(configData)

        AxpOmniSdk.configureSdk(
            applicationContext = applicationContext,
            host = configData.hostname,
            appKey = configData.appKey,
            integrationId = configData.integrationId,
            jwtProvider = configApi!!,
            configMap = mapOf(
                SdkConfigKey.DISPLAY_NAME to configData.userName,
                SdkConfigKey.HTTP_LOG_LEVEL to if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.BASIC
                }
            )
        )
        previousSettings = configData.settingsData
        previousServerResults = configData.configResultsData
        setState(SDK_CONFIGURED)
    }

    private suspend fun shutDownSdk() {
        log.debug("Shutting down the SDK")

        AxpOmniSdk.shutDown()
        previousSettings = null
        setState(SDK_NOT_CONFIGURED)
    }

    companion object {

        lateinit var instance: SdkManager
            private set

        fun SampleApplication.instantiateSdkManager() {
            check(!Companion::instance.isInitialized)
            instance = SdkManager(this, dataStore)
        }
    }
}

/** The data supplied by the user on the settings screen. */
private data class SettingsData(
    val tokenServerUrl: String,
    val userId: String,
    val userName: String,
    val verifiedCustomer: Boolean
)

/** The data returned from the config server. */
private data class ConfigResultsData(
    val hostname: String,
    val integrationId: IntegrationId,
    val appKey: String,
    val remoteAddress: String
)

private val ConfigData.settingsData: SettingsData
    get() = SettingsData(tokenServerUrl, userId, userName, verifiedCustomer)

private val ConfigData.configResultsData: ConfigResultsData
    get() = ConfigResultsData(hostname, integrationId, appKey, remoteAddress)
