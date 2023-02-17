package com.discord.knockknock.schedulers

import com.discord.knockknock.services.FactionRestTemplate
import com.discord.knockknock.services.response.FactionMember
import com.discord.knockknock.services.utils.FactionDataSelection
import discord4j.common.util.Snowflake
import discord4j.core.GatewayDiscordClient
import discord4j.core.`object`.entity.channel.MessageChannel
import discord4j.core.spec.EmbedCreateSpec
import discord4j.rest.util.Color
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.lang.Exception
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit
import java.util.concurrent.TimeUnit
import java.util.function.Predicate


@Component
class DiscordClientScheduler {

    @Autowired
    lateinit var discordClient: GatewayDiscordClient

    @Autowired
    lateinit var factionRestTemplate: FactionRestTemplate

    @Scheduled(cron = "*/10 * * * * *")
    fun pingEvery10Seconds() {

//        discordClient.getChannelById(Snowflake.of(1069127723391406091))
//                .ofType(MessageChannel::class.java)
//                .flatMap { channel -> channel.createMessage(embed) }
//                .flatMap { message -> message.publish() }
//                .block()

    }
}