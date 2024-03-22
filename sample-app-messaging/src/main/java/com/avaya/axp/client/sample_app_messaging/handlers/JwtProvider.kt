package com.avaya.axp.client.sample_app_messaging.handlers

import com.avaya.axp.client.sample_app_messaging.network.AuthenticationService
import com.avaya.axp.client.sample_app_messaging.network.JwtTokenRequest
import com.avaya.axp.client.sample_app_messaging.network.UserIdentifiers
import com.avaya.axp.client.sample_app_messaging.network.provideAuthService
import com.avaya.axp.client.sample_app_messaging.repository.AuthenticationRepository
import com.avaya.axp.client.sample_app_messaging.repository.AuthenticationRepositoryImpl
import com.avaya.axp.client.sample_app_messaging.util.USER_ID
import com.avaya.axp.client.sample_app_messaging.util.USER_NAME
import com.avaya.axp.client.sdk.core.JWT
import com.avaya.axp.client.sdk.core.JwtProvider

class MyJwtProvider : JwtProvider {
    private var authenticationService: AuthenticationService? = null
    private lateinit var authenticationRepository: AuthenticationRepository
    private val jwtTokenRequest = JwtTokenRequest(
        userId = USER_ID,
        verified = true,
        userName = USER_NAME,
        userIdentifiers = UserIdentifiers(
            emailAddresses = listOf(USER_ID)
        )
    )

    override suspend fun fetchJwt(): JWT {
        if (authenticationService == null) {
            authenticationService = provideAuthService()
            if (authenticationService == null) {
                return ""
            }
            authenticationRepository = AuthenticationRepositoryImpl(authenticationService!!)
        }
        return authenticationRepository.getJwt(jwtTokenRequest).getOrNull()?.jwt ?: ""
    }
}