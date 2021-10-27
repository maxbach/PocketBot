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

    suspend fun getRequestToken(consumerKey: String, redirectUrl: String): RequestTokenResponseBody {
        val responseBody = client.post<String>("https://getpocket.com/v3/oauth/request") {
            body = json.encodeToString(
                RequestTokenRequestBody(consumerKey = consumerKey, redirectUri = redirectUrl)
            )

            headers {
                contentType(ContentType.Application.Json)
                append("X-Accept", "application/json")
            }
        }

        return json.decodeFromString(responseBody)
    }

    suspend fun getAccessToken(consumerKey: String, requestToken: String): AccessTokenResponseBody {
        val responseBody = client.post<String>("https://getpocket.com/v3/oauth/authorize") {
            body = json.encodeToString(
                AccessTokenRequestBody(consumerKey = consumerKey, requestToken = requestToken)
            )

            headers {
                contentType(ContentType.Application.Json)
                append("X-Accept", "application/json")
            }
        }

        return json.decodeFromString(responseBody)
    }

    suspend fun addUrl(consumerKey: String, accessToken: String, url: String, tags: List<String>) {
        client.post<String>("https://getpocket.com/v3/add") {
            body = json.encodeToString(
                AddArticleRequestBody(
                    consumerKey = consumerKey,
                    accessToken = accessToken,
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

