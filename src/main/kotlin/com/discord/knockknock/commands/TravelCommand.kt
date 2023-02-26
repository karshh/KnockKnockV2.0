package com.discord.knockknock.commands

import com.discord.knockknock.commands.utils.Command
import com.discord.knockknock.commands.utils.CreateSpecData
import com.discord.knockknock.commands.utils.EmbedCreateSpecData
import com.discord.knockknock.commands.utils.MessageCreateSpecData
import com.discord.knockknock.services.FactionApi
import com.discord.knockknock.services.response.GetFactionDataResponse
import discord4j.core.spec.EmbedCreateSpec
import discord4j.core.spec.MessageCreateFields
import discord4j.core.spec.MessageCreateSpec
import discord4j.rest.util.Color
import reactor.core.publisher.Flux
import java.nio.charset.Charset
import java.util.*


data class FactionMemberData(
        val id: String,
        val name: String,
        val description: String
)

class TravelCommand(
        private val masterApiKey: String
) : Command {

    override fun validate(arguments: List<String>): Boolean {
        var valid = arguments.first() == "travel"
        if (arguments.size == 2) {
            valid = valid && arguments[1].toIntOrNull() != null
        }
        return valid
    }

    override fun evaluate(arguments: List<String>): Flux<CreateSpecData> =
            Flux.just(arguments)
                    .map { if (it.size < 2) "" else it[1] }
                    .flatMap { FactionApi.get(masterApiKey, it, emptyList()) }
                    .map { createEmbedSpec(it) }

    private fun createEmbedSpec(data: GetFactionDataResponse): CreateSpecData {
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

        for (member in data.members) {
            if (member.value.status.description.startsWith("Returning to Torn")) {
                member.value.status.description = "Returning to Torn"
            }
        }

        val travelList = data.members.entries
                .filter { it.value.status.state == "Abroad" || it.value.status.state == "Traveling" }
                .groupBy { it.value.status.description }
                .mapValues { map -> map.value.map { FactionMemberData(it.key, it.value.name, it.value.status.description) } }
                .toSortedMap()

        if (travelList.isEmpty()) {
            return EmbedCreateSpecData(EmbedCreateSpec.builder()
                    .color(Color.WHITE)
                    .title("${data.name}")
                    .url("https://www.torn.com/factions.php?step=profile&ID=${data.id}")
                    .description("Nobody is travelling at the moment.")
                    .build())
        }

        if (travelList.keys.size > 15 ||
                travelList.any { it.value.toString().length > 1024 - (it.value.size * 45) }) {
            return MessageCreateSpecData(MessageCreateSpec.builder()
                    .content("`Too many members of ${data.name} are currently travelling. I've attached a file containing a list of them.`")
                    .addFile(MessageCreateFields.File.of("${data.name}_travel.html", generateHtmlFile(travelList.values)))
                    .build())

        }

        val builder = EmbedCreateSpec.builder()
                .color(Color.GREEN)
                .title("${data.name}")
                .url("https://www.torn.com/factions.php?step=profile&ID=${data.id}")

        for (travelItem in travelList) {
            builder.addField(
                    travelItem.value[0].description,
                    travelItem.value.joinToString(" ") { "[`${it.name}`](${getProfileUrl(it.id)})" },
                    false
            )
        }

        return EmbedCreateSpecData(builder.build())

    }

    private fun generateHtmlFile(factionMemberData: MutableCollection<List<FactionMemberData>>) =
            factionMemberData.joinToString("<hr>") { members ->
                """
                    <h3>${members[0].description}</h3>
                    ${members.joinToString("<br>") { """
                        <a href="${getProfileUrl(it.id)}" target="_blank">${it.name}</a>
                        """.trimIndent() 
                    }}
                """.trimIndent()
            }.byteInputStream(Charset.forName("UTF-8"))

    private fun getProfileUrl(id: String) = "https://www.torn.com/profiles.php?XID=${id}"
}