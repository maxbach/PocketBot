package ru.maxbach.pocketbot

import kotlinx.serialization.Serializable

@Serializable
data class CallbackData(
    val url: String,
    val tag: String? = null
)
