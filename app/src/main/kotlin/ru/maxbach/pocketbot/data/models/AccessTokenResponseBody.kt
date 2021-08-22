package ru.maxbach.pocketbot.data.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccessTokenResponseBody(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("username")
    val username: String
)