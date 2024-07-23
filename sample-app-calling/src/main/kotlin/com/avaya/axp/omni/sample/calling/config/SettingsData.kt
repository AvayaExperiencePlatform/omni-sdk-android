package com.avaya.axp.omni.sample.calling.config

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.avaya.axp.omni.sdk.core.IntegrationId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val TOKEN_SERVER_URL_KEY = stringPreferencesKey("token_server_URL")
private val USER_ID_KEY = stringPreferencesKey("user_ID")
private val USER_NAME_KEY = stringPreferencesKey("user_name")
private val VERIFIED_CUSTOMER_KEY = booleanPreferencesKey("verified_customer")
private val HOSTNAME_KEY = stringPreferencesKey("AXP_hostname")
private val INTEGRATION_ID_KEY = stringPreferencesKey("integration_ID")
private val APP_KEY_KEY = stringPreferencesKey("app_key")
private val REMOTE_ADDRESS_KEY = stringPreferencesKey("called_remote_address")

/** All of the data saved in shared preferences by the app. */
data class ConfigData(

    /*
     * These are data entered by the user on the settings screen.
     */

    /** The full URL for the token web server API endpoint. */
    val tokenServerUrl: String = "",

    /** User ID presented to AXP. */
    val userId: String = "",

    /** The user's display name. */
    val userName: String = "",

    /**
     * Indicates if the end-customer has been verified and the customer name and
     * identifiers can be trusted by the Contact Center.
     *
     * This should be set to `false` for anonymous end-customers.
     */
    val verifiedCustomer: Boolean = false,

    /*
     * These are data returned from the application token web server.
     */

    /** Hostname of the AXP API endpoint. */
    val hostname: String = "",

    /** AXP integration ID */
    val integrationId: IntegrationId = "",

    /** AXP application key for the API gateway */
    val appKey: String = "",

    /** Address of the AXP agent queue to make calls to. */
    val remoteAddress: String = ""
) {

    /**
     * Has the user provided enough settings data to be able to query the token
     * server for SDK config?
     */
    val hasRequiredSettings: Boolean
        get() = tokenServerUrl.isNotBlank() && userId.isNotBlank()

    /** Do we have all of the data needed to configure the SDK? */
    val hasRequiredSdkConfigData: Boolean
        get() = hostname.isNotBlank() && integrationId.isNotBlank()
                && appKey.isNotBlank() && remoteAddress.isNotBlank()
}

/** Data source for the persisted config data. */
class ConfigDataSource(
    private val dataStore: DataStore<Preferences>,
) {

    /** Flow for the entire set of config data. */
    val configDataFlow: Flow<ConfigData> = dataStore.data.map { preferences ->
        ConfigData(
            preferences[TOKEN_SERVER_URL_KEY] ?: "",
            preferences[USER_ID_KEY] ?: "",
            preferences[USER_NAME_KEY] ?: "",
            preferences[VERIFIED_CUSTOMER_KEY] ?: false,
            preferences[HOSTNAME_KEY] ?: "",
            preferences[INTEGRATION_ID_KEY] ?: "",
            preferences[APP_KEY_KEY] ?: "",
            preferences[REMOTE_ADDRESS_KEY] ?: ""
        )
    }

    val displayNameFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[USER_NAME_KEY] ?: ""
    }

    val remoteAddressFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[REMOTE_ADDRESS_KEY] ?: ""
    }

    suspend fun saveSettingsData(
        tokenServerUrl: String,
        userId: String,
        userName: String,
        verifiedCustomer: Boolean
    ) {
        dataStore.edit { preferences ->
            preferences[TOKEN_SERVER_URL_KEY] = tokenServerUrl
            preferences[USER_ID_KEY] = userId
            preferences[USER_NAME_KEY] = userName
            preferences[VERIFIED_CUSTOMER_KEY] = verifiedCustomer
        }
    }

    suspend fun saveSdkConfigData(
        hostname: String,
        integrationId: IntegrationId,
        appKey: String,
        remoteAddress: String
    ) {
        dataStore.edit { preferences ->
            preferences[HOSTNAME_KEY] = hostname
            preferences[INTEGRATION_ID_KEY] = integrationId
            preferences[APP_KEY_KEY] = appKey
            preferences[REMOTE_ADDRESS_KEY] = remoteAddress
        }
    }

    suspend fun clearSdkConfigData() {
        dataStore.edit { preferences ->
            preferences[HOSTNAME_KEY] = ""
            preferences[INTEGRATION_ID_KEY] = ""
            preferences[APP_KEY_KEY] = ""
            preferences[REMOTE_ADDRESS_KEY] = ""
        }
    }
}
