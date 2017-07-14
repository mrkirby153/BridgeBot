package me.mrkirby153.bridgebot.discord

import me.mrkirby153.bridgebot.discord.redis.RedisConnector
import org.json.JSONObject

class RedisBotHandler(val redis: RedisConnector) : BotHandler {

    override fun sendDiscordToMinecraft(channel: String, server: String, author: String, message: String) {
        redis.publish("bridge:$server.$channel", JSONObject().put("author", author).put("content", message).toString())
    }

    override fun getPlayers(channel: String, server: String): List<String>? {
        redis.publish("bridge:$server.$channel", JSONObject().put("action", "playercount").toString())
        return null
    }
}