package ru.maxbach.pocketbot

import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.MessageEntity

fun Message.getAllUrls(): List<String> = getUrls() + getTextLinks()

fun Message.getTextLinks(): List<String> = this.allEntities
        .filter { it.type == MessageEntity.Type.TEXT_LINK }
        .mapNotNull { it.url }

fun Message.getUrls(): List<String> = this.allEntities
        .filter { it.type == MessageEntity.Type.URL }
        .mapNotNull { findUrl(it.offset, it.length) }

fun Message.findUrl(offset: Int, length: Int) = text?.slice(IntRange(offset, length + offset - 1))
        ?: caption?.slice(IntRange(offset, length + offset - 1))

val Message.allEntities
    get() = this.entities.orEmpty() + this.captionEntities.orEmpty()