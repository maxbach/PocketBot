package ru.maxbach.pocketbot.data.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddArticleRequestBody(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("consumer_key")
    val consumerKey: String,
    @SerialName("url")
    val url: String,
    @SerialName("tags")
    val tags: String? = null
)