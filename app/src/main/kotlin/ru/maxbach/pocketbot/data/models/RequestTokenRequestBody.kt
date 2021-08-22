package ru.maxbach.pocketbot.data.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestTokenRequestBody(
    @SerialName("consumer_key")
    val consumerKey: String,
    @SerialName("redirect_uri")
    val redirectUri: String
)