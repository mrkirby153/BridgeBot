package me.mrkirby153.bridgebot.spigot.chat

import me.mrkirby153.bridgebot.spigot.Bridge
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.json.JSONArray
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
            val twoWay = cfg.getBoolean("channels.$key.twoWay")
            val joins = cfg.getBoolean("channels.$key.joins")
            val deaths = cfg.getBoolean("channels.$key.deaths")

            val chan = Channel(prefix, nameColor, nameFormat, textColor, server, channel, twoWay, joins, deaths)
            plugin.logger.info("Loaded channel $chan")
            channels["$server.$channel"] = chan
        }
    }

    fun process(server: String, channel: String, data: JSONObject) {
        val channelSettings = this.channels["$server.$channel"] ?: return

        if (data.has("action")) {
            val action = data.getString("action")
            if (action == "playercount") {
                val obj = JSONObject()
                obj.put("action", "playercount_resp")
                obj.put("players", JSONArray())
                Bukkit.getOnlinePlayers().forEach {
                    obj.append("players", it.name)
                }
                plugin.redisConnector.publish("action:$server.$channel", obj.toString())
            }
            return
        }

        val message = buildString {
            append(ChatColor.translateAlternateColorCodes('&', channelSettings.prefix))
            append(ChatColor.RESET)
            append(channelSettings.nameColor)
            append(channelSettings.nameFormat.format(data.optString("author")))
            append(channelSettings.textColor)
            append(data.optString("content"))
        }

        if (Bukkit.getOnlinePlayers().isNotEmpty())
            Bukkit.broadcastMessage(message)
    }

    fun getChannels(): Collection<Channel> {
        return this.channels.values
    }

    fun reload(){
        this.channels.clear()
        this.loadChannels()
    }


    data class Channel(val prefix: String, val nameColor: ChatColor, val nameFormat: String, val textColor: ChatColor,
                       val server: String, val channel: String, val twoWay: Boolean, val sendJoin: Boolean, val sendDeaths: Boolean)
}