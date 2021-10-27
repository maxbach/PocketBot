package ru.maxbach.pocketbot.data.repo

import ru.maxbach.pocketbot.data.exceptions.NoAccessTokenException
import ru.maxbach.pocketbot.data.exceptions.NoRequestTokenException
import ru.maxbach.pocketbot.data.models.AccessTokenResponseBody
import ru.maxbach.pocketbot.data.service.PocketService
import ru.maxbach.pocketbot.data.storage.TokensStorage

class PocketRepository {
    private val pocketService = PocketService()
    private val tokensStorage = TokensStorage()

    suspend fun addUrl(userId: Long, url: String, tags: List<String> = emptyList()) {
        val accessToken = tokensStorage.getAccessToken(userId)
        
        if (accessToken != null) {
            pocketService.addUrl(CONSUMER_KEY, accessToken, url, tags)
        } else {
            throw NoAccessTokenException()
        }
        
    }

    suspend fun getAuthUrl(userId: Long): String {
        val requestTokenResponse = pocketService.getRequestToken(CONSUMER_KEY, REDIRECT_URL)
        val requestToken = requestTokenResponse.code
        
        tokensStorage.setRequestToken(userId, requestToken)

        return "https://getpocket.com/auth/authorize?request_token=$requestToken&redirect_uri=$REDIRECT_URL"
    }

    suspend fun login(userId: Long): AccessTokenResponseBody {
        val requestToken = tokensStorage.getRequestToken(userId)
        
        if (requestToken != null) {
            val accessTokenResponse = pocketService.getAccessToken(CONSUMER_KEY, requestToken)
            val accessToken = accessTokenResponse.accessToken
            
            tokensStorage.setAccessToken(userId, requestToken, accessToken)
            
            return accessTokenResponse
        } else {
            throw NoRequestTokenException()
        }
    }

    suspend fun hasAccessToken(userId: Long): Boolean = tokensStorage.getAccessToken(userId) != null

    suspend fun hasRequestToken(userId: Long): Boolean = tokensStorage.getRequestToken(userId) != null
    
    companion object {
        const val DEEPLINK_TAG = "we_have_authed"
        private const val CONSUMER_KEY = "98364-844864e4c9f1540e1bb0e190"
        private const val REDIRECT_URL = "https://t.me/pocket_maxbach_test_bot?start=${DEEPLINK_TAG}"
    }

}