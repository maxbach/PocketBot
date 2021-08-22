package ru.maxbach.pocketbot

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.callbackQuery
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.entities.CallbackQuery
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.maxbach.pocketbot.data.service.PocketService

fun main() {

    val pocketService = PocketService()

    val bot = bot {

        println("Bot has started")

        token = "1771639743:AAGNjddjFGT7NC6mhEAr0QdTfWGwyw4_6RA"

        dispatch {

            callbackQuery {
                bot.addUrl(pocketService, callbackQuery)
            }

            message {
                val chatId = ChatId.fromId(message.chat.id)

                println(message.text)

                when {
                    chatId.id != 179425560L -> {
                        bot.sendMessage(chatId, "Пшел нафиг, хулиган!")
                    }
                    !pocketService.hasRequestToken() -> {
                        bot.getAuthUrl(chatId, pocketService)
                    }
                    !pocketService.hasAccessToken() -> {
                        bot.login(chatId, pocketService)
                    }
                    else -> {
                        val urls = message.getAllUrls()

                        if (urls.isEmpty()) {
                            bot.sendMessage(chatId, "Шото нету тут урлов")
                        } else {
                            bot.sendMessage(
                                chatId = chatId,
                                text = "А вот какие урлы я нашел",
                                replyToMessageId = message.messageId,
                                replyMarkup = InlineKeyboardMarkup.create(buttons = urls.map {
                                    listOf(
                                        InlineKeyboardButton.CallbackData(
                                            text = it,
                                            callbackData = Json.encodeToString(CallbackData(it, null))
                                        )
                                    )
                                })
                            )
                        }
                    }
                }
            }
        }
    }

    bot.startPolling()

}

private fun Bot.getAuthUrl(chatId: ChatId, pocketService: PocketService) {
    runBlocking {
        val authUrl = pocketService.retRequestToken()
        sendMessage(chatId, "Go to $authUrl and login")
    }
}

private fun Bot.login(chatId: ChatId, pocketService: PocketService) {
    runBlocking {
        val response = pocketService.getAccessToken()
        sendMessage(chatId, "Hooray! You've logged in, ${response.username} with token ${response.accessToken}")
    }
}

private fun Bot.addUrl(pocketService: PocketService, callbackQuery: CallbackQuery) {
    runBlocking {
        val data = Json.decodeFromString<CallbackData>(callbackQuery.data)
        pocketService.addUrl(data.url)
        answerCallbackQuery(callbackQuery.id, "${data.url} has added", showAlert = true)
    }
}



