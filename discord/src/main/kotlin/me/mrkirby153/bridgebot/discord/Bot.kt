package me.mrkirby153.bridgebot.discord

import me.mrkirby153.bridgebot.discord.utils.createFileIfNotExist
import me.mrkirby153.bridgebot.discord.utils.readProperties
import java.io.File

object Bot {

    private val properties = File("config.properties").createFileIfNotExist().readProperties()

    lateinit var bot: BridgeBot

    @JvmStatic fun main(args: Array<String>) {
        bot = BridgeBot(properties)
    }
}