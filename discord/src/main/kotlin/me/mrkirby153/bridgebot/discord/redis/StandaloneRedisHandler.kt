package me.mrkirby153.bridgebot.discord.redis

import me.mrkirby153.bridgebot.discord.Bot
import net.dv8tion.jda.core.JDA
import org.json.JSONObject
import org.json.JSONTokener
import redis.clients.jedis.JedisPubSub

class StandaloneRedisHandler(private val jda: JDA) : JedisPubSub() {

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
                    val pList = mutableListOf<String>()
                    players.forEach { pList.add(it.toString()) }
                    val c = jda.getGuildById(server)?.getTextChannelById(chan) ?: return
                    Bot.bot.sendPlayers(c, pList.toList())
                }
            }
        }
        if(channel.startsWith("bridge")){
            if(!json.has("from_mc"))
                return
            val author = json.optString("author") ?: null
            val m = json.getString("message")
            val c = jda.getGuildById(server)?.getTextChannelById(chan) ?: return
            Bot.bot.sendChatToDiscord(c, author, m)
        }
    }
}