package com.discord.knockknock.commands

import com.discord.knockknock.commands.utils.Command
import com.discord.knockknock.commands.utils.CreateSpecData
import com.discord.knockknock.commands.utils.EmbedCreateSpecData
import discord4j.core.spec.EmbedCreateFields
import discord4j.core.spec.EmbedCreateSpec
import discord4j.rest.util.Color
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class HelpCommand: Command {

    private val HELP_MANUAL = mapOf(
            "`?knock joke`" to "Responds a knock-knock joke.",
            "`?knock time`" to "Responds time in TCT.",
            "`?knock inactive <optional-faction-id>`" to "Lists all users that are fedded or inactive for 1 day or more.",
            "`?knock travel <optional-faction-id>`" to "List all users that are travelling",
            "`?knock online <minutes> <optional-faction-id>`" to "List all users that were online `<minutes>` minutes ago",
            "`?shiba`" to "Responds with a picture of a shiba inu"
    )
    override fun validate(arguments: List<String>): Boolean = arguments == listOf("help")

    override fun evaluate(arguments: List<String>): Flux<CreateSpecData> =
            Flux.just(EmbedCreateSpec.builder()
                .title("Knock Knock Manual")
                .color(Color.GREEN)
                .fields(HELP_MANUAL.map { EmbedCreateFields.Field.of(it.key, it.value, false) })
                .build()
            ).map {
                EmbedCreateSpecData(it)
            }
}