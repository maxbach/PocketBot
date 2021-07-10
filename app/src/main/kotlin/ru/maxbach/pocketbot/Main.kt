package ru.maxbach.pocketbot

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.entities.ChatId

fun main() {

    val bot = bot {

        token = "1771639743:AAGNjddjFGT7NC6mhEAr0QdTfWGwyw4_6RA"

        dispatch {
            message {
                println(message.allEntities)
                bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = message.getAllUrls().joinToString())
            }
        }
    }

    bot.startPolling()
}

