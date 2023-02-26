package com.discord.knockknock.commands

import com.discord.knockknock.commands.utils.Command
import com.discord.knockknock.commands.utils.CreateSpecData
import com.discord.knockknock.commands.utils.MessageCreateSpecData
import discord4j.core.spec.EmbedCreateSpec
import discord4j.core.spec.MessageCreateSpec
import discord4j.rest.util.Color
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class TimeCommand: Command {
    override fun validate(arguments: List<String>): Boolean {
        return arguments == listOf("time")
    }

    override fun evaluate(arguments: List<String>): Flux<CreateSpecData> =
            Flux.just(MessageCreateSpec.builder()
                .content(getTornTime())
                .build()
            ).map { MessageCreateSpecData(it) }

    private fun getTornTime() = LocalDateTime.now(ZoneId.of("UTC")).format(
            DateTimeFormatter.ofPattern("`yyyy-MM-dd h:mm:ss a`")
    )
}