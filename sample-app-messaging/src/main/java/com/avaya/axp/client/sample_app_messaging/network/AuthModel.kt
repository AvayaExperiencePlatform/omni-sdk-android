package com.avaya.axp.client.sample_app_messaging.network

import com.google.gson.annotations.SerializedName


data class JwtTokenRequest(
    @SerializedName("userId") val userId: String? = null,
    @SerializedName("verified") val verified: Boolean? = null,
    @SerializedName("userName") val userName: String? = null,
    @SerializedName("userIdentifiers") val userIdentifiers: UserIdentifiers? = UserIdentifiers()
)

data class UserIdentifiers(
    @SerializedName("emailAddresses") val emailAddresses: List<String>? = null,
    @SerializedName("phoneNumbers") val phoneNumbers: List<String>? = null
)

data class JwtResponse(
    @SerializedName("jwtToken")
    val jwt: String,
    @SerializedName("axpIntegrationId")
    val axpIntegrationId: String,
    @SerializedName("configId")
    val configId: String? = null,
    @SerializedName("axpHostName")
    val axpHostName: String,
    @SerializedName("fcmConnectorBaseUrl")
    val fcmConnectorBaseUrl: String? = null,
    @SerializedName("appKey")
    val appKey: String? = null,
)