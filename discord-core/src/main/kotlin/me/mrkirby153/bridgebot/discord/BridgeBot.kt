package me.mrkirby153.bridgebot.discord

import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.entities.MessageChannel
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import java.util.*

class BridgeBot(properties: Properties, val handler: BotHandler) : ListenerAdapter() {

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
            handler.getPlayers(event.channel.id, event.guild.id)?.let {
                sendPlayers(event.channel, it)
            }
        }
        handler.sendDiscordToMinecraft(event.channel.id, event.guild.id, event.member.effectiveName, event.message.content)
    }

    fun sendPlayers(channel: MessageChannel, players: List<String>){
        channel.sendMessage(buildString {
            append("Online Players ")
            append("(${players.size})")
            if(players.isNotEmpty()) {
                append(": `")
                append(players.joinToString(", "))
                append("`")
            }
        }).queue()
    }

    fun sendChatToDiscord(channel: MessageChannel, author: String?, message: String){
        channel.sendMessage(buildString {
            if(author != null && author.isNotEmpty())
                append("**<$author>** ")
            append(message)
        }).queue()
    }
}