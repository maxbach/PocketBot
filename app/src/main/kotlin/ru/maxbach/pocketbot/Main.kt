package ru.maxbach.pocketbot

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.callbackQuery
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.entities.CallbackQuery
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import com.github.kotlintelegrambot.logging.LogLevel
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.maxbach.pocketbot.data.repo.PocketRepository

private val pocketRepository = PocketRepository()

fun main() {

    val bot = bot {

        logLevel = LogLevel.Error

        println("Bot has started")

        token = "1771639743:AAGNjddjFGT7NC6mhEAr0QdTfWGwyw4_6RA"

        dispatch {
            callbackQuery {
                GlobalScope.launch {
                    bot.addUrl(callbackQuery.from.id, callbackQuery)
                }
            }

            message {
                GlobalScope.launch {
                    bot.handleMessage(message)
                }
            }
        }
    }

    bot.startPolling()

    embeddedServer(Netty, port = System.getenv("PORT").toInt()) {
        routing {
            get("/") {
                call.respondText("Hello, world!")
            }
        }
    }.start(wait = true)

}

private suspend fun Bot.handleMessage(message: Message) {
    val chatId = ChatId.fromId(message.chat.id)
    val userId = message.from?.id ?: return

    println("Message has came ${message.messageId}")

    when {
        chatId.id != 179425560L -> {
            sendMessage(chatId, "Пшел нафиг, хулиган!")
        }
        !pocketRepository.hasRequestToken(userId) -> {
            getAuthUrl(userId, chatId)
        }
        !pocketRepository.hasAccessToken(userId) -> {
            login(userId, chatId)
        }
        message.caption != null || message.text != null -> {
            findUrls(message)
        }
        else -> {
            deleteMessage(chatId, message.messageId)
        }
    }
}

private suspend fun Bot.getAuthUrl(userId: Long, chatId: ChatId) {
    val authUrl = pocketRepository.getAuthUrl(userId)
    sendMessage(chatId, "Go to $authUrl and login")
}

private suspend fun Bot.login(userId: Long, chatId: ChatId) {
    val response = pocketRepository.login(userId)
    sendMessage(chatId, "Hooray! You've logged in, ${response.username} with token ${response.accessToken}")
}

private suspend fun Bot.addUrl(userId: Long, callbackQuery: CallbackQuery) {
    val data = Json.decodeFromString<CallbackData>(callbackQuery.data)
    val url = callbackQuery.message?.replyMarkup?.inlineKeyboard?.getOrNull(data.index)?.firstOrNull()?.text

    if (url != null) {
        pocketRepository.addUrl(userId, url)
        answerCallbackQuery(callbackQuery.id, "$url has added")
        callbackQuery.message?.let { callbackMessage ->
            deleteMessage(ChatId.fromId(callbackMessage.chat.id), callbackMessage.messageId)
            callbackMessage.replyToMessage?.let { replyToMessage ->
                deleteMessage(ChatId.fromId(replyToMessage.chat.id), replyToMessage.messageId)
            }
        }
    } else {
        answerCallbackQuery(callbackQuery.id, "Problems with adding. Url is not found")
    }

}

private fun Bot.findUrls(message: Message) {
    val urls = message.getAllUrls()
    val chatId = ChatId.fromId(message.chat.id)

    if (urls.isEmpty()) {
        sendMessage(
            chatId = chatId,
            text = "Шото нету тут урлов",
            replyToMessageId = message.messageId,
        )
    } else {
        sendMessage(
            chatId = chatId,
            text = "А вот какие урлы я нашел",
            replyToMessageId = message.messageId,
            replyMarkup = InlineKeyboardMarkup.create(buttons = urls.mapIndexed { index, url ->
                listOf(
                    InlineKeyboardButton.CallbackData(
                        text = url,
                        callbackData = Json.encodeToString(CallbackData(index, null))
                    )
                )
            })
        )
    }
}



