package com.avaya.axp.omni.sample.messaging

import com.avaya.axp.omni.sample.messaging.util.TOKEN_PROVIDER_URL
import com.avaya.axp.omni.sample.messaging.util.USER_ID
import com.avaya.axp.omni.sample.messaging.util.USER_NAME
import com.avaya.axp.omni.sdk.core.JWT
import com.avaya.axp.omni.sdk.core.JwtProvider
import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.slf4j.LoggerFactory.getLogger
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

class AppWebServerJwtProvider : JwtProvider {

    private val log = getLogger("JwtProvider")

    private val authenticationService = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create())
        .baseUrl(TOKEN_PROVIDER_URL)
        .client(
            OkHttpClient.Builder()
                .readTimeout(10L, TimeUnit.SECONDS)
                .connectTimeout(10L, TimeUnit.SECONDS)
                .addNetworkInterceptor(HttpLoggingInterceptor { msg ->
                    log.info("{}", msg)
                }.apply {
                    setLevel(HttpLoggingInterceptor.Level.BASIC)
                })
                .build()
        )
        .build()
        .create(AuthenticationService::class.java)

    private val jwtTokenRequest = JwtTokenRequest(
        userId = USER_ID,
        userIdentifiers = UserIdentifiers(
            emailAddresses = listOf(USER_ID),
        ),
        userName = USER_NAME,
        verifiedCustomer = true
    )

    override suspend fun fetchJwt(): JWT? =
        try {
            authenticationService.getJwt(jwtTokenRequest = jwtTokenRequest).jwtToken
        } catch (e: Exception) {
            log.warn("Error fetching JWT: {}", e.message ?: e.javaClass.name)
            null
        }
}

fun interface AuthenticationService {
    @POST("/token")
    suspend fun getJwt(
        @Body jwtTokenRequest: JwtTokenRequest
    ): JwtResponse
}

@JsonClass(generateAdapter = true)
data class JwtTokenRequest(
    val userId: String? = null,
    val userIdentifiers: UserIdentifiers? = null,
    val userName: String? = null,
    val verifiedCustomer: Boolean? = null
)

@JsonClass(generateAdapter = true)
data class UserIdentifiers(
    val emailAddresses: List<String>? = null,
)

@JsonClass(generateAdapter = true)
data class JwtResponse(
    val jwtToken: String,
    val axpIntegrationId: String,
    val axpHostName: String
)
