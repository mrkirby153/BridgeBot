package me.mrkirby153.bridgebot.spigot.chat

import me.mrkirby153.bridgebot.spigot.Bridge
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.AsyncPlayerChatEvent

class ChatListener(val plugin: Bridge) : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onChat(event: AsyncPlayerChatEvent) {
        ChatHandler.getChannels().forEach { chan ->
            if (chan.twoWay) {
                plugin.redisConnector.sendChatMessage(chan.server, chan.channel, event.player, ChatColor.stripColor(event.message))
            }
        }
    }

    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        ChatHandler.getChannels().forEach { chan ->
            if (chan.twoWay) {
                plugin.redisConnector.sendChatMessage(chan.server, chan.channel, null, ChatColor.stripColor(event.deathMessage))
            }
        }
    }
}