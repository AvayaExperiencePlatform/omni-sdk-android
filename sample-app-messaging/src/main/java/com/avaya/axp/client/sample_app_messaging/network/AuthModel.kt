package com.avaya.axp.client.sample_app_messaging.network

import com.google.gson.annotations.SerializedName


data class JwtTokenRequest(
    @SerializedName("userId") val userId: String? = null,
    @SerializedName("verifiedCustomer") val verifiedCustomer: Boolean? = null,
    @SerializedName("customerName") val customerName: String? = null,
    @SerializedName("customerIdentifiers") val customerIdentifiers: CustomerIdentifiers? = CustomerIdentifiers()
)

data class CustomerIdentifiers(
    @SerializedName("emailAddresses") val emailAddresses: List<String>? = null,
    @SerializedName("phoneNumbers") val phoneNumbers: List<String>? = null
)

data class JwtResponse(
    @SerializedName("jwtToken")
    val jwt: String,
    @SerializedName("axpIntegrationId")
    val axpIntegrationId: String,
    @SerializedName("axpHostName")
    val axpHostName: String
)