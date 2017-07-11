package me.mrkirby153.bridgebot.discord.redis

import net.dv8tion.jda.core.entities.Message
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
        select(this@RedisConnector.db)
    }

    fun publish(channel: String, message: String) {
        get().use {
            it.publish(channel, message)
        }
    }

    fun publish(message: Message) {
        val author = message.member.effectiveName
        val content = message.content

        val json = JSONObject().append("author", author).append("content", content)
        publish("$CHANNEL_BASE:${message.guild.id}.${message.channel.id}", json.toString())
    }
}