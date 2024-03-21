package com.avaya.axp.client.sample_app_messaging.network

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface AuthenticationService {
    @POST
    suspend fun getJwt(
        @Url path: String = "",
        @Body jwtTokenRequest: JwtTokenRequest
    ): JwtResponse
}