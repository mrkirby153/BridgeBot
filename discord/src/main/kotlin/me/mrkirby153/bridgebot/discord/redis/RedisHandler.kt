package me.mrkirby153.bridgebot.discord.redis

import me.mrkirby153.bridgebot.discord.Bot
import org.json.JSONObject
import org.json.JSONTokener
import redis.clients.jedis.JedisPubSub

class RedisHandler() : JedisPubSub() {

    override fun onPMessage(pattern: String, channel: String, message: String) {
        if (!(channel.startsWith("action") || channel.startsWith("bridge")))
            return
        val encodedServer = channel.split(":")[1]
        val split = encodedServer.split(".")
        val server = split[0]
        val chan = split[1]

        val json = JSONObject(JSONTokener(message))
        if(channel.startsWith("action")) {
            if (!json.has("action"))
                return

            val action = json.getString("action")

            when (action) {
                "playercount_resp" -> {
                    val players = json.getJSONArray("players")
                    Bot.bot.jda.getGuildById(server)?.getTextChannelById(chan)?.sendMessage(buildString {
                        append("Online Players ")
                        append("(${players.length()}): ")
                        if (players.length() > 0)
                            append("`")
                        append(buildString {
                            players.forEach { player ->
                                append("$player, ")
                            }
                        }.dropLast(2))
                        if (players.length() > 0)
                            append("`")
                    })?.queue()
                }
            }
        }
        if(channel.startsWith("bridge")){
            if(!json.has("from_mc"))
                return
            val author = json.optString("author") ?: null
            val m = json.getString("message")
            Bot.bot.jda.getGuildById(server)?.getTextChannelById(chan)?.sendMessage(buildString {
                if(author != null && author.isNotEmpty())
                    append("**<$author>** ")
                append(m)
            })?.queue()
        }
    }
}