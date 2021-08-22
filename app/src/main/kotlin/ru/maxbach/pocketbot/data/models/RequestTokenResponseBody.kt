package ru.maxbach.pocketbot.data.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestTokenResponseBody(
    @SerialName("code")
    val code: String
)