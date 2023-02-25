package com.discord.knockknock.schedulers

import com.discord.knockknock.services.FactionRestService
import discord4j.core.GatewayDiscordClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component


@Component
class DiscordClientScheduler {

    @Autowired
    lateinit var discordClient: GatewayDiscordClient

    @Autowired
    lateinit var factionRestTemplate: FactionRestService

    @Scheduled(cron = "*/10 * * * * *")
    fun pingEvery10Seconds() {

//        discordClient.getChannelById(Snowflake.of(1069127723391406091))
//                .ofType(MessageChannel::class.java)
//                .flatMap { channel -> channel.createMessage(embed) }
//                .flatMap { message -> message.publish() }
//                .block()

    }
}