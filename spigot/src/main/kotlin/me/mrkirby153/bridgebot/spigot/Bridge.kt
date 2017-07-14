package me.mrkirby153.bridgebot.spigot

import me.mrkirby153.bridgebot.discord.BridgeBot
import me.mrkirby153.bridgebot.discord.redis.RedisConnector
import me.mrkirby153.bridgebot.spigot.chat.ChatHandler
import me.mrkirby153.bridgebot.spigot.chat.ChatListener
import me.mrkirby153.bridgebot.spigot.redis.SpigotDiscordHandler
import org.bukkit.plugin.java.JavaPlugin

class Bridge : JavaPlugin() {


    var redisConnector: RedisConnector? = null

    var bot: BridgeBot? = null


    override fun onEnable() {
        saveDefaultConfig()

        ChatHandler.plugin = this
        ChatHandler.loadChannels()

        if (config.getBoolean("redis.enabled")) {
            val redisHost = config.getString("redis.host")
            val redisPort = config.getInt("redis.port")
            val redisPassword = config.getString("redis.password")

            redisConnector = RedisConnector(redisHost, redisPort, if (redisPassword.isEmpty()) null else redisPassword)
            redisConnector?.listen(SpigotDiscordHandler())
        } else {
            logger.info("Operating in standalone mode!")

            val token = config.getString("bot.token")
            if (token.isNullOrEmpty()) {
                logger.severe("API TOKEN NOT SET! Disabling plugin...")
                pluginLoader.disablePlugin(this)
                return
            }

            bot = BridgeBot(config.getString("bot.owner", ""), config.getString("bot.token"), StandaloneBotHandler())
        }

        server.pluginManager.registerEvents(ChatListener(this), this)

        getCommand("discordbridge").executor = CommandDiscordBridge(this)
    }

    override fun onDisable() {
        bot?.jda?.shutdown()
    }

    fun sendToDiscord(channel: ChatHandler.Channel, author: String?, message: String) {
        redisConnector?.sendChatMessage(channel.server, channel.channel, author, message)
        bot?.let { bot ->
            val c = bot.jda.getTextChannelById(channel.channel) ?: return
            bot.sendChatToDiscord(c, author, message)
        }
    }
}