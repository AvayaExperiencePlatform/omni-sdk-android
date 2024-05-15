package com.avaya.axp.client.sample_app_messaging.network.pushNotification

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NotificationRegistrationRequest(
    val configId: String?,
    val sessionId: String?
)

@JsonClass(generateAdapter = true)
data class NotificationRegistrationResponse(
    val configId: String? = null,
    val sessionId: String? = null,
    val deviceToken: String? = null
)