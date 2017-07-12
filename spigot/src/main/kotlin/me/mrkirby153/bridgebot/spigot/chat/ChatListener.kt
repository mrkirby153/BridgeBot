package me.mrkirby153.bridgebot.spigot.chat

import me.mrkirby153.bridgebot.spigot.Bridge
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
                plugin.redisConnector.sendChatMessage(chan.server, chan.channel, event.player, event.message)
                /*val builder = MultipartBody.Builder().apply {
                    setType(MultipartBody.FORM)
                    addFormDataPart("content", event.message)
                    addFormDataPart("username", event.player.name)
                    addFormDataPart("avatar_url", "https://minotar.net/helm/${event.player.name}")
                }

                val request = Request.Builder().apply {
                    url(chan.webhook)
                    method("POST", RequestBody.create(null, ByteArray(0)))
                    post(builder.build())
                }.build()
                plugin.httpClient.newCall(request).enqueue(object : Callback {
                    override fun onResponse(call: Call?, response: Response?) {

                    }

                    override fun onFailure(call: Call?, e: IOException?) {
                        e?.printStackTrace()
                        plugin.logger.severe("Webhook ${chan.webhook} failed to execute!")
                    }

                })*/
            }
        }
    }

    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        ChatHandler.getChannels().forEach { chan ->
            if (chan.twoWay) {
                plugin.redisConnector.sendChatMessage(chan.server, chan.channel, null, event.deathMessage)
            }
        }
    }
}