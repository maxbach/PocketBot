package ru.maxbach.pocketbot

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId

fun main() {

    val bot = bot {

        token = "1771639743:AAGNjddjFGT7NC6mhEAr0QdTfWGwyw4_6RA"

        dispatch {

            text {
                bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = message.text.orEmpty())
                println(text)
            }
        }
    }

    bot.startPolling()
}