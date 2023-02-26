package com.discord.knockknock.commands

import com.discord.knockknock.commands.utils.Command
import com.discord.knockknock.services.FactionApi
import com.discord.knockknock.services.response.FactionMember
import com.discord.knockknock.services.response.GetFactionDataResponse
import discord4j.core.spec.EmbedCreateSpec
import discord4j.rest.util.Color
import reactor.core.publisher.Flux
import java.sql.Timestamp
import java.time.Instant
import java.time.temporal.ChronoUnit


class InactiveCommand(
        private val masterApiKey: String,
): Command {

    override fun validate(arguments: List<String>): Boolean {
        var valid = arguments.first() == "inactive"
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
        if (data.members.isNullOrEmpty() || data.name == null || data.id == null) {
            return EmbedCreateSpec.builder()
                    .color(Color.RED)
                    .title("Error")
                    .description("Could not retrieve data for this faction.")
                    .build()
        }

        val inactiveList = data.members.entries
                .filter { !listOf("Federal", "Fallen").contains(it.value.status.state) && isInactive(it) }
                .sortedByDescending { it.value.lastAction.timestamp }
                .map { entry -> "[`${entry.value.name}`](${getProfileUrl(entry.key)})` ${entry.value.lastAction.relative}`" }

        val fallenList = data.members.entries
                .filter { it.value.status.state == "Fallen" }
                .map { entry -> "[`${entry.value.name}`](${getProfileUrl(entry.key)})" }

        val federalList = data.members.entries
                .filter { it.value.status.state == "Federal" }
                .map { entry -> "[`${entry.value.name}`](${getProfileUrl(entry.key)})" }

        if (federalList.isEmpty() && inactiveList.isEmpty() && fallenList.isEmpty()) {
            return EmbedCreateSpec.builder()
                    .color(Color.GREEN)
                    .title("")
                    .description("Nobody has been inactive.")
                    .build()
        }
        val builder = EmbedCreateSpec.builder()
                .color(Color.ORANGE)
                .title("${data.name}")
                .url("https://www.torn.com/factions.php?step=profile&ID=${data.id}")

        if (fallenList.isNotEmpty()) {
            builder.addField(
                    "In Heaven (RIP)",
                    fallenList.joinToString("\n"),
                    false
            )
        }

        if (federalList.isNotEmpty()) {
            builder.addField(
                    "In Federal Jail",
                    federalList.joinToString("\n"),
                    false )
        }

        if (inactiveList.isNotEmpty()) {
            builder.addField(
                    "Inactive",
                    inactiveList.joinToString("\n"),
                    false
            )
        }

        return builder.build()
    }

    private fun getProfileUrl(id: String) = "https://www.torn.com/profiles.php?XID=${id}"
    private fun isInactive(entry: Map.Entry<String, FactionMember>) =
            entry.value.lastAction.timestamp
                    .isBefore(Instant.now().minus(24, ChronoUnit.HOURS))

}