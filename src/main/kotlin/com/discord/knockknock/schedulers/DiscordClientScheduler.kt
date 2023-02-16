package com.discord.knockknock.schedulers

import discord4j.common.util.Snowflake
import discord4j.core.GatewayDiscordClient
import discord4j.core.`object`.entity.channel.MessageChannel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component


@Component
class DiscordClientScheduler {

    @Autowired
    lateinit var discordClient: GatewayDiscordClient

    @Scheduled(cron = "*/10 * * * * *")
    fun pingEvery10Seconds() {
        discordClient.getChannelById(Snowflake.of("test-channel-id"))
                .ofType(MessageChannel::class.java)
                .flatMap { channel -> channel.createMessage("Hello World!") }
                .flatMap { message -> message.publish() }
                .block()
    }
}