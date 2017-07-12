package me.mrkirby153.bridgebot.spigot.redis

import org.bukkit.entity.Player
import org.json.JSONObject
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig

class RedisConnector(val host: String, val port: Int, val password: String?, val db: Int = 0) {

    private val CHANNEL_BASE = "bridge"

    val jedisPool: JedisPool

    init {
        val previousClassloader = Thread.currentThread().contextClassLoader
        Thread.currentThread().contextClassLoader = RedisConnector::class.java.classLoader

        val config = JedisPoolConfig().apply {
            maxWaitMillis = 1000
            minIdle = 1
            testOnBorrow = true
            maxTotal = 20
            blockWhenExhausted = true
        }

        jedisPool = JedisPool(config, host, port, 1000, if (password?.isEmpty() ?: true) null else password)
        Thread.currentThread().contextClassLoader = previousClassloader
    }

    fun get(): Jedis = jedisPool.resource.apply {
        if (this.db.toInt() != this@RedisConnector.db)
            select(this@RedisConnector.db)
    }

    fun publish(channel: String, message: String) {
        get().use {
            it.publish(channel, message)
        }
    }

    fun listen() {
        val thread = Thread {
            get().use {
                it.psubscribe(RedisHandler(), "$CHANNEL_BASE:*")
            }
        }
        thread.name = "Redis PubSub listener"
        thread.isDaemon = true
        thread.start()
    }

    fun sendChatMessage(server: String, channel: String, author: Player?, message: String) {
        val obj = JSONObject().apply {
            if (author != null)
                put("author", author.name)
            put("message", message)
            put("from_mc", true)
        }
        publish("$CHANNEL_BASE:$server.$channel", obj.toString())
    }
}