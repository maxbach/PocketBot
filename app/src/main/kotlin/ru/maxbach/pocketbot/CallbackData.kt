package ru.maxbach.pocketbot

import kotlinx.serialization.Serializable

@Serializable
data class CallbackData(
    val index: Int,
    val tag: String? = null
)
