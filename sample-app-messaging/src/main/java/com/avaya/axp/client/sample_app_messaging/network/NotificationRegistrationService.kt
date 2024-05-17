package com.avaya.axp.client.sample_app_messaging.network

import com.avaya.axp.client.sample_app_messaging.network.pushNotification.NotificationRegistrationRequest
import com.avaya.axp.client.sample_app_messaging.network.pushNotification.NotificationRegistrationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Path

interface NotificationRegistrationService {
    @PUT("custom-notification-connector/device-registrations/{deviceToken}")
    suspend fun saveDeviceRegistration(
        @Path("deviceToken") deviceToken: String,
        @Body registrationRequest: NotificationRegistrationRequest
    ): Response<NotificationRegistrationResponse>
}