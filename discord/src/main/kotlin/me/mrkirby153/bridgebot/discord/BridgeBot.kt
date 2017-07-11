package me.mrkirby153.bridgebot.discord

import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.JDABuilder
import java.util.*

class BridgeBot(val properties: Properties) {


    val jda: JDA

    init {
        jda = buildJDA(properties.getProperty("api_token"))
    }

    private fun buildJDA(token: String) = JDABuilder(AccountType.BOT).apply {
        setToken(token)
    }.buildBlocking()
}