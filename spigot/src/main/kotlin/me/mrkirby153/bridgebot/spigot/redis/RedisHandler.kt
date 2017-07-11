package me.mrkirby153.bridgebot.spigot.redis

import me.mrkirby153.bridgebot.spigot.chat.ChatHandler
import org.json.JSONObject
import org.json.JSONTokener
import redis.clients.jedis.JedisPubSub

class RedisHandler : JedisPubSub() {

    override fun onPMessage(pattern: String, channel: String, message: String) {
        if(!channel.startsWith("bridge"))
            return
        val encodedServer = channel.split(":")[1]
        val split = encodedServer.split(".")
        val server = split[0]
        val chan = split[1]

        val json = JSONObject(JSONTokener(message))

        if(json.has("action")){
            val action = json.getString("action")
            if(action == "playercount"){
                // Construct playercount
            }
            return
        }

        ChatHandler.process(server, chan, json)
    }
}