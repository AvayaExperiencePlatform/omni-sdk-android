package com.avaya.axp.client.sample_app_messaging.network

import com.avaya.axp.client.sample_app_messaging.util.AUTH_BASE_URL
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


fun provideAuthService(): AuthenticationService? {
    return try {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .baseUrl(AUTH_BASE_URL)
            .client(
                OkHttpClient.Builder()
                    .readTimeout(15L, TimeUnit.SECONDS)
                    .connectTimeout(15L, TimeUnit.SECONDS).build()
            )
            .build()
            .create(AuthenticationService::class.java)
    } catch (e: Exception) {
        null
    }
}