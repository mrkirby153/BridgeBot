package me.mrkirby153.bridgebot.spigot

import me.mrkirby153.bridgebot.spigot.chat.ChatHandler
import me.mrkirby153.bridgebot.spigot.chat.ChatListener
import me.mrkirby153.bridgebot.spigot.redis.RedisConnector
import org.bukkit.plugin.java.JavaPlugin

class Bridge : JavaPlugin() {


    lateinit var redisConnector: RedisConnector

    override fun onEnable() {
        saveDefaultConfig()

        ChatHandler.plugin = this
        ChatHandler.loadChannels()

        val redisHost = config.getString("redis.host")
        val redisPort = config.getInt("redis.port")
        val redisPassword = config.getString("redis.password")

        redisConnector = RedisConnector(redisHost, redisPort, if (redisPassword.isEmpty()) null else redisPassword)
        redisConnector.listen()

        server.pluginManager.registerEvents(ChatListener(this), this)

        getCommand("discordbridge").executor = CommandDiscordBridge(this)
    }

    override fun onDisable() {

    }
}