package me.mrkirby153.bridgebot.spigot.chat

import me.mrkirby153.bridgebot.spigot.Bridge
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.json.JSONObject


object ChatHandler {
    lateinit var plugin: Bridge

    private val channels = mutableMapOf<String, Channel>()

    fun loadChannels() {
        val cfg = plugin.config

        cfg.getConfigurationSection("channels").getKeys(false).forEach { key ->
            plugin.logger.info("Loading channel $key")

            val prefix = cfg.getString("channels.$key.prefix")
            val nameColor = ChatColor.valueOf(cfg.getString("channels.$key.name_color"))
            val nameFormat = cfg.getString("channels.$key.name_format")
            val textColor = ChatColor.valueOf(cfg.getString("channels.$key.text_color"))
            val server = cfg.getString("channels.$key.server")
            val channel = cfg.getString("channels.$key.channel")
            val webhook = cfg.getString("channels.$key.webhook")

            val chan = Channel(prefix, nameColor, nameFormat, textColor, server, channel, webhook)
            plugin.logger.info("Loaded channel $chan")
            channels["$server.$channel"] = chan
        }
    }

    fun process(server: String, channel: String, data: JSONObject){
        val channelSettings = this.channels["$server.$channel"] ?: return

        val message = buildString {
            append(ChatColor.WHITE)
            append(channelSettings.prefix)
            append(channelSettings.nameColor)
            append(channelSettings.nameFormat.format(data.optString("author")))
            append(channelSettings.textColor)
            append(data.optString("content"))
        }

        Bukkit.broadcastMessage(message)
    }


    data class Channel(val prefix: String, val nameColor: ChatColor, val nameFormat: String, val textColor: ChatColor,
                       val server: String, val channel: String, val webhook: String)
}