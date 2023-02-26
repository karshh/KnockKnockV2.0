package com.discord.knockknock.commands

import com.discord.knockknock.commands.utils.Command
import com.discord.knockknock.commands.utils.CreateSpecData
import com.discord.knockknock.commands.utils.EmbedCreateSpecData
import com.discord.knockknock.commands.utils.MessageCreateSpecData
import com.discord.knockknock.services.FactionApi
import com.discord.knockknock.services.response.FactionMember
import com.discord.knockknock.services.response.GetFactionDataResponse
import discord4j.core.spec.EmbedCreateSpec
import discord4j.core.spec.MessageCreateFields
import discord4j.core.spec.MessageCreateSpec
import discord4j.rest.util.Color
import reactor.core.publisher.Flux
import java.nio.charset.Charset
import java.time.Instant
import java.time.temporal.ChronoUnit

class OnlineCommand(
        private val masterApiKey: String
) : Command {

    override fun validate(arguments: List<String>): Boolean {
        var valid = arguments.size >= 2 && // minimum of 2 elements
                arguments.first() == "online" && // first element is the command itself "online"
                arguments[1].toIntOrNull() != null // second element is the minutes online
        if (arguments.size == 3) { // if we need to specify a faction id
            valid = valid && arguments[2].toIntOrNull() != null
        }
        return valid
    }

    override fun evaluate(arguments: List<String>): Flux<CreateSpecData> =
            Flux.just(arguments)
                    .map { if (it.size < 3) "" else it[2] }
                    .flatMap { FactionApi.get(masterApiKey, it, emptyList()) }
                    .map { createEmbedSpec(it, arguments[1]) }

    private fun createEmbedSpec(data: GetFactionDataResponse, minutes: String): CreateSpecData {

        data.error?.let {
            return EmbedCreateSpecData(EmbedCreateSpec.builder()
                    .color(Color.RED)
                    .title("Error")
                    .description("Code ${it.code}: ${it.error}")
                    .build())
        }
        if (data.members.isNullOrEmpty()) {
            return EmbedCreateSpecData(EmbedCreateSpec.builder()
                    .color(Color.RED)
                    .title("Error")
                    .description("Could not retrieve data for this faction.")
                    .build())
        }

        val threshold = Instant.now().minus(minutes.toLong(), ChronoUnit.MINUTES)

        val onlineMembersSet = data.members.entries
                .filter { it.value.lastAction.timestamp.isAfter(threshold) }
                .sortedByDescending { it.value.lastAction.timestamp }
        if (onlineMembersSet.isEmpty()) {
            return EmbedCreateSpecData(EmbedCreateSpec.builder()
                    .color(Color.RED)
                    .title("${data.name}")
                    .url("https://www.torn.com/factions.php?step=profile&ID=${data.id}")
                    .description("Nobody has been active in the last $minutes minutes.")
                    .build())
        }

        val onlineMembersStringList = onlineMembersSet.map {
            "[`${it.value.name}`](${getProfileUrl(it.key)}) ${it.value.lastAction.relative}"
        }

        if (onlineMembersStringList.toString().length > 3850) {
            return MessageCreateSpecData(MessageCreateSpec.builder()
                    .content("`Too many members of ${data.name} were Online [size=${onlineMembersStringList.size}] in the last ${minutes} minutes. I've attached a file containing a list of them.`")
                    .addFile(MessageCreateFields.File.of("${data.name}_online.html", generateHtmlFile(onlineMembersSet)))
                    .build())

        }
        return EmbedCreateSpecData(EmbedCreateSpec.builder()
                .color(Color.GREEN)
                .title("${data.name}")
                .url("https://www.torn.com/factions.php?step=profile&ID=${data.id}")
                .description(onlineMembersStringList.joinToString("\n"))
                .build())
    }

    private fun getProfileUrl(id: String) = "https://www.torn.com/profiles.php?XID=${id}"

    private fun generateHtmlFile(onlineMembersSet: List<Map.Entry<String, FactionMember>>) = """
        <table>
            <tr>
                <th>Player</th>
                <th>Last Action</th>
            </tr>
            ${
        onlineMembersSet.joinToString("") {
            """
                <tr>
                   <td><a href=${getProfileUrl(it.key)} target="_blank">${it.value.name}</a></td>
                   <td>${it.value.lastAction.relative}</td>
                </tr>
            """
        }
    }
        </table>
        """.trimIndent().byteInputStream(Charset.forName("UTF-8"))

}