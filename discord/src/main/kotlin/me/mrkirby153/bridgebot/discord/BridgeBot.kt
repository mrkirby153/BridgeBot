package me.mrkirby153.bridgebot.discord

import me.mrkirby153.bridgebot.discord.redis.RedisConnector
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import java.util.*

class BridgeBot(val properties: Properties) : ListenerAdapter() {

    val jda: JDA

    val redis: RedisConnector

    val owner = properties.getProperty("owner_id") ?: ""

    init {
        jda = buildJDA(properties.getProperty("api_token"))
        redis = RedisConnector(properties.getProperty("redis_host", "localhost"), properties.getProperty("redis_port", "6379").toInt(),
                properties.getProperty("redis_password", null), properties.getProperty("redis_db", "0").toInt())
    }

    private fun buildJDA(token: String) = JDABuilder(AccountType.BOT).apply {
        setToken(token)
        addEventListener(this@BridgeBot)
    }.buildBlocking()

    override fun onMessageReceived(event: MessageReceivedEvent?) {
        if (event == null)
            return
        if(event.message.rawContent == "%%shutdown" && event.author.id == this.owner) {
            event.message.channel.sendMessage("Shutting down... :wave:").complete()
            jda.shutdown()
            return
        }
        redis.publish(event.message)
    }
}