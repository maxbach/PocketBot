package ru.maxbach.pocketbot.data.service

import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.maxbach.pocketbot.data.models.AccessTokenRequestBody
import ru.maxbach.pocketbot.data.models.AccessTokenResponseBody
import ru.maxbach.pocketbot.data.models.AddArticleRequestBody
import ru.maxbach.pocketbot.data.models.RequestTokenRequestBody
import ru.maxbach.pocketbot.data.models.RequestTokenResponseBody

class PocketService {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val client = HttpClient(Apache) {
        Logging {
            this.level = LogLevel.NONE
        }
    }

    private val consumerKey = "98364-844864e4c9f1540e1bb0e190"

    private var requestToken: String? = "775d1f62-c5b4-6586-0ae4-b9f6a2"
    private var accessToken: String? = "f2c3180f-2704-8d2c-04ea-a0cc6c"

    fun hasRequestToken() = requestToken != null

    fun hasAccessToken() = accessToken != null

    suspend fun retRequestToken(): String {
        val redirectUrl = "https://t.me/pocket_maxbach_test_bot?start=$DEEPLINK_TAG"
        val responseBody = client.post<String>("https://getpocket.com/v3/oauth/request") {
            body = json.encodeToString(
                RequestTokenRequestBody(consumerKey = consumerKey, redirectUri = redirectUrl)
            )

            headers {
                contentType(ContentType.Application.Json)
                append("X-Accept", "application/json")
            }
        }

        val response = json.decodeFromString<RequestTokenResponseBody>(responseBody)
        requestToken = response.code
        return "https://getpocket.com/auth/authorize?request_token=$requestToken&redirect_uri=$redirectUrl"
    }

    suspend fun getAccessToken(): AccessTokenResponseBody {
        val responseBody = client.post<String>("https://getpocket.com/v3/oauth/authorize") {
            body = json.encodeToString(
                AccessTokenRequestBody(consumerKey = consumerKey, requestToken = requestToken.orEmpty())
            )

            headers {
                contentType(ContentType.Application.Json)
                append("X-Accept", "application/json")
            }
        }

        val response = json.decodeFromString<AccessTokenResponseBody>(responseBody)
        accessToken = response.accessToken
        return response
    }

    suspend fun addUrl(url: String, tags: List<String> = emptyList()) {
        client.post<String>("https://getpocket.com/v3/add") {
            body = json.encodeToString(
                AddArticleRequestBody(
                    consumerKey = consumerKey,
                    accessToken = accessToken.orEmpty(),
                    url = url,
                    tags = tags.joinToString()
                )
            )

            headers {
                contentType(ContentType.Application.Json)
                append("X-Accept", "application/json")
            }
        }
    }

    companion object {
        const val DEEPLINK_TAG = "we_have_authed"
    }
}

