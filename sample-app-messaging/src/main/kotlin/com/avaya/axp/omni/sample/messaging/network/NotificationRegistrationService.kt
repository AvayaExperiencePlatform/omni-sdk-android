package com.avaya.axp.omni.sample.messaging.network

import com.avaya.axp.omni.sample.messaging.network.pushNotification.NotificationRegistrationRequest
import com.avaya.axp.omni.sample.messaging.network.pushNotification.NotificationRegistrationResponse
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

interface NotificationRegistrationService {
    @PUT("custom-notification-connector/device-registrations/{deviceToken}")
    suspend fun saveDeviceRegistration(
        @Path("deviceToken") deviceToken: String,
        @Body registrationRequest: NotificationRegistrationRequest
    ): Response<NotificationRegistrationResponse>

    companion object {

        fun create(baseUrl: String): NotificationRegistrationService {
            val moshi = Moshi.Builder().build()
            val logger =
                HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .readTimeout(15L, TimeUnit.SECONDS)
                .connectTimeout(15L, TimeUnit.SECONDS).build()

            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(NotificationRegistrationService::class.java)
        }
    }
}