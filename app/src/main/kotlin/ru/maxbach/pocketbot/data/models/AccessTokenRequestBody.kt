package ru.maxbach.pocketbot.data.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccessTokenRequestBody(
    @SerialName("code")
    val requestToken: String,
    @SerialName("consumer_key")
    val consumerKey: String
)