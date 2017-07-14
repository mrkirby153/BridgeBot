package me.mrkirby153.bridgebot.discord

import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import org.json.JSONObject
import java.util.*

class BridgeBot(val properties: Properties) : ListenerAdapter() {

    val jda: JDA

    val owner = properties.getProperty("owner_id") ?: ""

    init {
        jda = buildJDA(properties.getProperty("api_token"))
    }

    private fun buildJDA(token: String) = JDABuilder(AccountType.BOT).apply {
        setToken(token)
        addEventListener(this@BridgeBot)
    }.buildBlocking()

    override fun onMessageReceived(event: MessageReceivedEvent?) {
        if (event == null)
            return
        if(event.author == jda.selfUser)
            return
        if(event.message.rawContent == "%%shutdown" && event.author.id == this.owner) {
            event.message.channel.sendMessage("Shutting down... :wave:").complete()
            jda.shutdown()
            return
        }
        if(event.message.rawContent.equals("%%list", true)){
            val obj = JSONObject().put("action", "playercount")
//            redis.publish("bridge:${event.guild.id}.${event.channel.id}", obj.toString())
        }
//        redis.publish(event.message)
    }
}