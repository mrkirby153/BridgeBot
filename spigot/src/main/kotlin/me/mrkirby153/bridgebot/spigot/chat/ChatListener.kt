package me.mrkirby153.bridgebot.spigot.chat

import me.mrkirby153.bridgebot.spigot.Bridge
import me.mrkirby153.bridgebot.spigot.sendChatMessage
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class ChatListener(val plugin: Bridge) : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onChat(event: AsyncPlayerChatEvent) {
        ChatHandler.getChannels().forEach { chan ->
            if (chan.twoWay) {
                plugin.redisConnector.sendChatMessage(chan.server, chan.channel, event.player.name, ChatColor.stripColor(event.message))
            }
        }
    }

    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        ChatHandler.getChannels().forEach { chan ->
            if (chan.twoWay && chan.sendDeaths) {
                plugin.redisConnector.sendChatMessage(chan.server, chan.channel, null, ChatColor.stripColor(event.deathMessage))
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onJoin(event: PlayerJoinEvent) {
        if (event.joinMessage.isNotEmpty())
            ChatHandler.getChannels().filter { it.twoWay && it.sendJoin }.forEach { chan ->
                plugin.redisConnector.sendChatMessage(chan.server, chan.channel, null,ChatColor.stripColor(event.joinMessage))
            }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onLeave(event: PlayerQuitEvent){
        if(event.quitMessage.isNotEmpty())
            ChatHandler.getChannels().filter { it.twoWay && it.sendJoin }.forEach{ chan ->
                plugin.redisConnector.sendChatMessage(chan.server, chan.channel, null, ChatColor.stripColor(event.quitMessage))
            }
    }
}