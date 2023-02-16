package com.discord.knockknock.services

import com.discord.knockknock.services.utils.EventListener
import com.discord.knockknock.commands.JokeCommand
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.spec.EmbedCreateSpec
import discord4j.rest.util.Color
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class BasicCommandComponent: EventListener<MessageCreateEvent> {

    private val COMMAND_LIST = listOf(
            JokeCommand()
    )

    override fun execute(event: MessageCreateEvent): Mono<Void> {

        val messageContent = event.message.content
        if (!messageContent.startsWith("?knock ")) {
            return Mono.empty()
        }
        val instructions = messageContent.split(Regex("\\s+")).toMutableList()
        instructions.removeAt(0) // remove the ?knock command so all we got are instructions
        if (instructions.isEmpty()) {
            return Mono.empty()
        }
        val response = respondToInstructions(instructions)

        return event.message.channel.flatMap { channel -> channel.createMessage(response) }.then()
    }

    private fun respondToInstructions(arguments: List<String>): EmbedCreateSpec {
        if (arguments.isEmpty()) {
            return getErrorMessage("Empty Command")
        }

        for (command in COMMAND_LIST) {
            if (command.validate(arguments)) {
                return command.evaluate(arguments)
            }
        }

        return getErrorMessage("Invalid command.")
    }

    private fun getErrorMessage(description: String) = EmbedCreateSpec.builder()
                .color(Color.RED)
                .description(description)
                .build()

    override val eventType: Class<MessageCreateEvent> = MessageCreateEvent::class.java

}