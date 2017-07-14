package me.mrkirby153.bridgebot.spigot

import me.mrkirby153.bridgebot.discord.BotHandler
import me.mrkirby153.bridgebot.spigot.chat.ChatHandler
import org.bukkit.Bukkit
import org.json.JSONObject

class StandaloneBotHandler : BotHandler {

    override val allowShutdown: Boolean = false

    override fun getPlayers(channel: String, server: String): List<String>? {
        return Bukkit.getOnlinePlayers().map { it.name }.toList()
    }

    override fun sendDiscordToMinecraft(channel: String, server: String, author: String, message: String) {
        ChatHandler.process(server, channel, JSONObject().put("author", author).put("content", message))
    }
}