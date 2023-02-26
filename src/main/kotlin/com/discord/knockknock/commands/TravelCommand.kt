package com.discord.knockknock.commands

import com.discord.knockknock.commands.utils.Command
import com.discord.knockknock.services.FactionApi
import com.discord.knockknock.services.response.GetFactionDataResponse
import discord4j.core.`object`.entity.Message
import discord4j.core.spec.EmbedCreateSpec
import discord4j.core.spec.MessageCreateSpec
import discord4j.rest.util.Color
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class TravelCommand(
        private val masterApiKey: String
): Command {

    override fun validate(arguments: List<String>): Boolean {
        var valid = arguments.first() == "travel"
        if (arguments.size == 2) {
            valid = valid && arguments[1].toIntOrNull() != null
        }
        return valid
    }

    override fun evaluate(arguments: List<String>): Flux<EmbedCreateSpec> =
            Flux.just(arguments)
                    .map { if (it.size < 2) "" else it[1] }
                    .flatMap { FactionApi.get(masterApiKey, it, emptyList()) }
                    .map { createEmbedSpec(it) }

    private fun createEmbedSpec(data: GetFactionDataResponse): EmbedCreateSpec {
        data.error?.let {
            return EmbedCreateSpec.builder()
                    .color(Color.RED)
                    .title("Error")
                    .description("Code ${it.code}: ${it.error}")
                    .build()
        }
        if (data.members.isNullOrEmpty()) {
            return EmbedCreateSpec.builder()
                    .color(Color.RED)
                    .title("Error")
                    .description("Could not retrieve data for this faction.")
                    .build()
        }

        for (member in data.members) {
            if (member.value.status.description.startsWith("Returning to Torn")) {
                member.value.status.description = "Returning to Torn"
            }
        }

        val travelList = data.members.entries
                .filter { it.value.status.state == "Abroad" || it.value.status.state == "Traveling" }
                .groupBy { it.value.status.description }
                .toSortedMap()

        if (travelList.isEmpty()) {
            return EmbedCreateSpec.builder()
                    .color(Color.GREEN)
                    .title("${data.name}")
                    .url("https://www.torn.com/factions.php?step=profile&ID=${data.id}")
                    .description("Nobody is travelling at the moment.")
                    .build()

        }

        val builder = EmbedCreateSpec.builder()
                .color(Color.GREEN)
                .title("${data.name}")
                .url("https://www.torn.com/factions.php?step=profile&ID=${data.id}")

        for (travelItem in travelList) {
            builder.addField(
                    travelItem.value[0].value.status.description,
                    travelItem.value.joinToString("\n") { "[`${it.value.name}`](${getProfileUrl(it.key)})" },
                    false
            )
        }

        return builder.build()

    }


    private fun getProfileUrl(id: String) = "https://www.torn.com/profiles.php?XID=${id}"
}