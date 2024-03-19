package com.avaya.axp.client.sample_app_messaging.repository

import android.util.Log
import com.avaya.axp.client.sample_app_messaging.network.AuthenticationService
import com.avaya.axp.client.sample_app_messaging.network.JwtResponse
import com.avaya.axp.client.sample_app_messaging.network.JwtTokenRequest

interface AuthenticationRepository {
    suspend fun getJwt(jwtTokenRequest: JwtTokenRequest): Result<JwtResponse>
}

class AuthenticationRepositoryImpl(private val authenticationService: AuthenticationService) :
    AuthenticationRepository {
    override suspend fun getJwt(jwtTokenRequest: JwtTokenRequest): Result<JwtResponse> {
        return try {
            val authResponse =
                authenticationService.getJwt(
                    jwtTokenRequest = jwtTokenRequest
                )
            Result.success(authResponse)
        } catch (e: Exception) {
            Log.d("Authentication_repository", e.message.toString())
            Result.failure(e)
        }
    }

}