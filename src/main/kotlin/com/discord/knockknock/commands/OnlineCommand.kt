package com.discord.knockknock.commands

import com.discord.knockknock.commands.utils.Command
import com.discord.knockknock.services.FactionApi
import com.discord.knockknock.services.response.GetFactionDataResponse
import discord4j.core.spec.EmbedCreateSpec
import discord4j.core.spec.MessageCreateSpec
import discord4j.rest.util.Color
import reactor.core.publisher.Flux
import java.time.Instant
import java.time.temporal.ChronoUnit

class OnlineCommand(
        private val masterApiKey: String
): Command {

    override fun validate(arguments: List<String>): Boolean {
        var valid = arguments.size >= 2 && // minimum of 2 elements
                arguments.first() == "online" && // first element is the command itself "online"
                arguments[1].toIntOrNull() != null // second element is the minutes online
        if (arguments.size == 3) { // if we need to specify a faction id
            valid = valid && arguments[2].toIntOrNull() != null
        }
        return valid
    }

    override fun evaluate(arguments: List<String>): Flux<EmbedCreateSpec> =
            Flux.just(arguments)
                    .map { if (it.size < 3) "" else it[2] }
                    .flatMap { FactionApi.get(masterApiKey, it, emptyList()) }
                    .map { createEmbedSpec(it, arguments[1]) }

    private fun createEmbedSpec(data: GetFactionDataResponse, minutes: String): EmbedCreateSpec {

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

        val threshold = Instant.now().minus(minutes.toLong(), ChronoUnit.MINUTES)

        val onlineMembers = data.members.entries
                .filter { it.value.lastAction.timestamp.isAfter(threshold) }
                .sortedByDescending { it.value.lastAction.timestamp }
                .map { "[`${it.value.name}`](${getProfileUrl(it.key)}) ${it.value.lastAction.relative}" }

        if (onlineMembers.isEmpty()) {
            return EmbedCreateSpec.builder()
                    .color(Color.RED)
                    .title("${data.name}")
                    .url("https://www.torn.com/factions.php?step=profile&ID=${data.id}")
                    .description("Nobody has been active in the last $minutes minutes.")
                    .build()
        }

        if (onlineMembers.toString().length > 3600) {
            return EmbedCreateSpec.builder()
                    .color(Color.RED)
                    .title("${data.name}")
                    .url("https://www.torn.com/factions.php?step=profile&ID=${data.id}")
                    .description("Too many members have been active in the last $minutes minutes. Please reduce your minute threshold.")
                    .build()

        }

        return EmbedCreateSpec.builder()
                .color(Color.GREEN)
                .title("${data.name}")
                .url("https://www.torn.com/factions.php?step=profile&ID=${data.id}")
                .description(onlineMembers.joinToString("\n"))
                .build()
    }

    private fun getProfileUrl(id: String) = "https://www.torn.com/profiles.php?XID=${id}"
}