package me.mrkirby153.bridgebot.discord

interface BotHandler {

    val allowShutdown : Boolean

    fun getPlayers(channel: String, server: String): List<String>?

    fun sendDiscordToMinecraft(channel: String, server: String, author: String, message: String)
}