package com.avaya.axp.omni.sample.calling

import com.avaya.axp.omni.sdk.core.JWT
import com.avaya.axp.omni.sdk.core.JwtProvider
import com.avaya.axp.omni.sdk.util.summary
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CompletableDeferred
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import java.io.IOException
import java.util.concurrent.TimeUnit

fun interface ConfigApi {
    suspend fun queryTokenService(): TokenResponse
}

/**
 * Object to make a REST API call to the customer's token serving web app to
 * get the configuration data as well as a JWT for authentication.
 */
class TokenServiceClient(
    tokenProviderUrl: String,
    userId: String,
    userName: String? = null,
    emailAddress: String? = null,
    verifiedCustomer: Boolean? = null
) : JwtProvider, ConfigApi {

    private val log = getLogger("TokenServiceClient")

    private val tokenService = TokenService(tokenProviderUrl, log)

    private val tokenRequest = TokenRequest(
        userId = userId,
        userName = userName,
        verifiedCustomer = verifiedCustomer,
        userIdentifiers = emailAddress?.takeIf { it.isNotBlank() }
            ?.let { UserIdentifiers(emailAddresses = listOf(it)) }
    )

    override suspend fun queryTokenService(): TokenResponse =
        tokenService.getToken(tokenRequest = tokenRequest)

    override suspend fun fetchJwt(): JWT? =
        try {
            queryTokenService().jwtToken
        } catch (e: Exception) {
            log.warn("Error fetching JWT: {}", e.message ?: e.javaClass.name)
            null
        }
}

private class TokenService(
    private val url: String,
    private val log: Logger
) {

    private val jsonMediaType = "application/json".toMediaType()

    private val moshi = Moshi.Builder().build()

    private val httpLogLevel = if (BuildConfig.DEBUG) {
        HttpLoggingInterceptor.Level.BODY
    } else {
        HttpLoggingInterceptor.Level.BASIC
    }

    private val okHttp = OkHttpClient.Builder()
        .readTimeout(10L, TimeUnit.SECONDS)
        .connectTimeout(10L, TimeUnit.SECONDS)
        .addNetworkInterceptor(
            HttpLoggingInterceptor { msg ->
                log.info("{}", msg)
            }.apply {
                setLevel(httpLogLevel)
            }
        )
        .build()

    suspend fun getToken(tokenRequest: TokenRequest): TokenResponse {
        val requestBody = tokenRequest.toJsonString().toRequestBody(jsonMediaType)
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
        try {
            val response = okHttp.newCall(request).await()
            if (response.isSuccessful) {
                return response.body?.let { body ->
                    body.use {
                        body.string().asTokenResponse()
                    }
                } ?: throw RuntimeException("Config server response was empty")
            } else {
                throw RuntimeException("Error contacting config server: ${response.code} ${response.message}")
            }
        } catch (ioe: IOException) {
            log.warn("Caught {}", ioe.summary)
            throw RuntimeException(ioe)
        }
    }

    private fun TokenRequest.toJsonString(): String =
        moshi.adapter(TokenRequest::class.java).toJson(this)

    private fun String.asTokenResponse(): TokenResponse =
        moshi.adapter(TokenResponse::class.java).fromJson(this)
            ?: throw RuntimeException("Failed to parse JSON: $this")
}

@JsonClass(generateAdapter = true)
data class TokenRequest(
    val userId: String,
    val userName: String? = null,
    val verifiedCustomer: Boolean? = null,
    val userIdentifiers: UserIdentifiers? = null
)

@JsonClass(generateAdapter = true)
data class UserIdentifiers(
    val emailAddresses: List<String>? = null,
    val phoneNumbers: List<String>? = null
)

@JsonClass(generateAdapter = true)
data class TokenResponse(
    val jwtToken: String,
    val axpIntegrationId: String,
    val axpHostName: String,
    val appKey: String,
    val callingRemoteAddress: String
)

private suspend fun Call.await(): Response {
    val deferred = CompletableDeferred<Response>()
    enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            deferred.completeExceptionally(e)
        }

        override fun onResponse(call: Call, response: Response) {
            deferred.complete(response)
        }
    })
    return deferred.await()
}
