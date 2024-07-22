package com.avaya.axp.omni.sample.messaging.network.pushNotification

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
