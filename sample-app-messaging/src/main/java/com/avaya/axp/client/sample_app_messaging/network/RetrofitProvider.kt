package com.avaya.axp.client.sample_app_messaging.network

import com.avaya.axp.client.sample_app_messaging.util.NOTIFICATION_BASE_URL
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

fun provideNotificationService(): NotificationRegistrationService {
    val moshi = Moshi.Builder().build()

    return Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(NOTIFICATION_BASE_URL)
        .client(
            OkHttpClient.Builder()
                .readTimeout(15L, TimeUnit.SECONDS)
                .connectTimeout(15L, TimeUnit.SECONDS).build()
        )
        .build()
        .create(NotificationRegistrationService::class.java)
}