package com.discord.knockknock.commands

import com.discord.knockknock.commands.utils.Command
import discord4j.core.spec.EmbedCreateSpec
import discord4j.rest.util.Color
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class TimeCommand: Command {
    override fun validate(arguments: List<String>): Boolean {
        return arguments == listOf("time")
    }

    override fun evaluate(arguments: List<String>): Mono<EmbedCreateSpec> =
            Mono.just(EmbedCreateSpec.builder()
                .color(Color.GREEN)
                .description(getTornTime())
                .build()
            )

    private fun getTornTime() = LocalDateTime.now(ZoneId.of("UTC")).format(
            DateTimeFormatter.ofPattern("`yyyy-MM-dd h:mm:ss a`")
    )
}