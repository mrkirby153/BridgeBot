package me.mrkirby153.bridgebot.discord

import me.mrkirby153.bridgebot.discord.redis.RedisConnector
import me.mrkirby153.bridgebot.discord.redis.StandaloneRedisHandler
import me.mrkirby153.bridgebot.discord.utils.createFileIfNotExist
import me.mrkirby153.bridgebot.discord.utils.readProperties
import java.io.File

object Bot {

    private val properties = File("config.properties").createFileIfNotExist().readProperties()

    lateinit var bot: BridgeBot

    lateinit var redis: RedisConnector

    @JvmStatic fun main(args: Array<String>) {
       redis = RedisConnector(properties.getProperty("redis_host", "localhost"), properties.getProperty("redis_port", "6379").toInt(),
                properties.getProperty("redis_password", null), properties.getProperty("redis_db", "0").toInt())
        bot = BridgeBot(properties, RedisBotHandler(redis))
        redis.listen(StandaloneRedisHandler(bot.jda))
    }
}