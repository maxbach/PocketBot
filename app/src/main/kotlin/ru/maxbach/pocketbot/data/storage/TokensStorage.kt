package ru.maxbach.pocketbot.data.storage

class TokensStorage {

    private val tokensMap = mutableMapOf<Long, Tokens>()

    suspend fun getAccessToken(userId: Long): String? = tokensMap[userId]?.accessToken

    suspend fun getRequestToken(userId: Long): String? = tokensMap[userId]?.requestToken

    suspend fun setRequestToken(userId: Long, requestToken: String) {
        tokensMap[userId] = Tokens(userId, requestToken)
    }

    suspend fun setAccessToken(userId: Long, requestToken: String, accessToken: String) {
        tokensMap[userId] = Tokens(userId, requestToken, accessToken)
    }
}

private data class Tokens(
    val userId: Long,
    val requestToken: String,
    val accessToken: String? = null
)