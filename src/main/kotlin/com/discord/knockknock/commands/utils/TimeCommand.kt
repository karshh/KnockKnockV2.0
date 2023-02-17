package com.discord.knockknock.commands.utils

import discord4j.core.spec.EmbedCreateSpec
import discord4j.rest.util.Color
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class TimeCommand: Command {
    override fun validate(arguments: List<String>): Boolean {
        return arguments == listOf("time")
    }

    override fun evaluate(arguments: List<String>): EmbedCreateSpec {
        return EmbedCreateSpec.builder()
                .color(Color.GREEN)
                .description(
                        LocalDateTime.now(ZoneId.of("UTC")).format(
                                DateTimeFormatter.ofPattern("`yyyy-MM-dd h:mm:ss a`")
                        ))
                .build()
    }
}