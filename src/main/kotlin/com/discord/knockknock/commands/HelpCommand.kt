package com.discord.knockknock.commands

import com.discord.knockknock.commands.utils.Command
import discord4j.core.spec.EmbedCreateFields
import discord4j.core.spec.EmbedCreateSpec
import discord4j.rest.util.Color
import reactor.core.publisher.Mono

class HelpCommand: Command {

    private val HELP_MANUAL = mapOf(
            "`?knock joke`" to "Responds a knock-knock joke.",
            "`?knock time`" to "Responds time in TCT.",
            "`?knock inactive <optional-faction-id>`" to "Lists all users that are fedded or inactive for 1 day or more."
    )
    override fun validate(arguments: List<String>): Boolean = arguments == listOf("help")

    override fun evaluate(arguments: List<String>): Mono<EmbedCreateSpec> =
            Mono.just(EmbedCreateSpec.builder()
                .title("Knock Knock Manual")
                .color(Color.GREEN)
                .fields(HELP_MANUAL.map { EmbedCreateFields.Field.of(it.key, it.value, false) })
                .build()
            )
}