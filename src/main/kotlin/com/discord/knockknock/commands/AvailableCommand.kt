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

class AvailableCommand(private val masterApiKey: String) : Command {
    override fun validate(arguments: List<String>): Boolean {
        var valid = arguments.first() == "okay"
        if (arguments.size == 2) {
            valid = valid && arguments[1].toIntOrNull() != null
        }
        return valid
    }

    override fun evaluate(arguments: List<String>) = Flux.just(arguments)
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

        val okayList = data.members.filter { it.value.status.state == "Okay" }
        val okayListString = okayList.map { "[`${it.value.name}`](${getProfileUrl(it.key)})" }.joinToString(" ")

        if (okayListString.length > 3800) {
            return MessageCreateSpecData(MessageCreateSpec.builder()
                    .content("`Too many members of ${data.name} are currently Okay [size=${okayList.size}]. I've attached a file containing a list of them.`")
                    .addFile(MessageCreateFields.File.of("${data.name}_available.html", generateHtmlFile(okayList)))
                    .build())
        }

        return EmbedCreateSpecData(EmbedCreateSpec.builder()
                .color(Color.GREEN)
                .title("${data.name}")
                .url("https://www.torn.com/factions.php?step=profile&ID=${data.id}")
                .description(okayListString)
                .build()
        )
    }

    private fun getProfileUrl(id: String) = "https://www.torn.com/profiles.php?XID=${id}"

    private fun generateHtmlFile(okayMap: Map<String, FactionMember>) =
            okayMap.map {
                """<a href=${getProfileUrl(it.key)} target="_blank">${it.value.name}</a>"""
            }.joinToString("<br>\n").byteInputStream(Charset.forName("UTF-8"))

}