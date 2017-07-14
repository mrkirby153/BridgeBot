package me.mrkirby153.bridgebot.spigot

import me.mrkirby153.bridgebot.discord.redis.RedisConnector
import org.json.JSONObject

fun RedisConnector.sendChatMessage(server: String, channel: String, author: String?, message: String){
    val obj = JSONObject().apply {
        if(author != null)
            put("author", author)
        put("message", message)
        put("from_mc", true)
    }
    publish("bridge:$server.$channel", obj.toString())
}