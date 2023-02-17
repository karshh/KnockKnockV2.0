package com.discord.knockknock.commands

import com.discord.knockknock.commands.utils.Command
import com.discord.knockknock.services.FactionRestTemplate
import com.discord.knockknock.services.response.FactionMember
import discord4j.core.spec.EmbedCreateSpec
import discord4j.rest.util.Color
import java.time.Instant
import java.time.temporal.ChronoUnit


class InactiveCommand(
        private val masterApiKey: String,
        private val factionRestTemplate: FactionRestTemplate
): Command {


    override fun validate(arguments: List<String>): Boolean {
        var valid = arguments.first() == "inactive"
        if (arguments.size == 2) {
            valid = valid && arguments[1].toIntOrNull() != null
        }
        return valid
    }

    override fun evaluate(arguments: List<String>): EmbedCreateSpec {
        val factionId =  if (arguments.size < 2) "" else arguments[1]
        val response = factionRestTemplate.getFactionData(masterApiKey, factionId, emptyList()).block() ?:
        throw Exception("Faction API did not return a response.")

        response.error?.let {
            return EmbedCreateSpec.builder()
                    .color(Color.RED)
                    .title("Error")
                    .description("Code ${it.code}: ${it.error}")
                    .build()
        }

        if (response.members.isNullOrEmpty() || response.name == null || response.id == null) {
            return EmbedCreateSpec.builder()
                    .color(Color.RED)
                    .title("Error")
                    .description("Could not retrieve data for this faction.")
                    .build()
        }

        val federalSet = mutableMapOf<String, FactionMember>()
        val inactiveSet = mutableMapOf<String, FactionMember>()

        for (member in response.members.entries) {
            if (member.value.status.state == "Federal") {
                federalSet[member.key] = member.value
            }

            else if (isInactive(member)) {
                inactiveSet[member.key] = member.value
            }
        }

        if (federalSet.isEmpty() && inactiveSet.isEmpty()) {
            return EmbedCreateSpec.builder()
                    .color(Color.GREEN)
                    .title("")
                    .description("Nobody has been inactive.")
                    .build()
        }

        val builder = EmbedCreateSpec.builder()
                .color(Color.ORANGE)
                .title("${response.name}")
                .url("https://www.torn.com/factions.php?step=profile&ID=${response.id}")

        if (federalSet.isNotEmpty()) {
            builder.addField(
                    "In Federal Jail",
                    federalSet.map { entry -> "[`${entry.value.name} [${entry.key}]`](${getProfileUrl(entry.key)})" }.joinToString("\n"),
                    false )
        }

        if (inactiveSet.isNotEmpty()) {
            builder.addField(
                    "Inactive",
                    inactiveSet.map {
                        entry -> "[`${entry.value.name} [${entry.key}]`](${getProfileUrl(entry.key)})`" +
                            spacing(entry) +
                            "${entry.value.lastAction.relative}`"
                    }.joinToString("\n"),
                    false
            )
        }

        return builder.build()
    }

    private fun getProfileUrl(id: String) = "https://www.torn.com/profiles.php?XID=${id}"

    private fun spacing(entry: Map.Entry<String, FactionMember>): String {
        var space = " "
        // account for the '[', ']' and the space in between name and ID
        var length = 26 - entry.key.length - entry.value.name.length - 3
        while (length > 0) {
            space += " "
            length--
        }
        return space
    }
    private fun isInactive(entry: Map.Entry<String, FactionMember>) =
            entry.value.lastAction.timestamp
                    .isBefore(Instant.now().minus(24, ChronoUnit.HOURS))

}