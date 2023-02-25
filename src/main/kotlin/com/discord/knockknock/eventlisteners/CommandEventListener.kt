package com.discord.knockknock.eventlisteners

import com.discord.knockknock.commands.utils.Command
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.spec.EmbedCreateSpec
import discord4j.rest.util.Color
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class CommandEventListener @Autowired constructor(private val commandList: List<Command>) : EventListener<MessageCreateEvent> {

    override fun execute(event: MessageCreateEvent): Mono<Void> = Mono.just(event)
        .filter { it.message.content.startsWith("?knock ") }
        .map { it.message.content.split(Regex("\\s+")).toMutableList() }
        .map { it.subList(1, it.size) }
        .filter { it.isNotEmpty() }
        .switchIfEmpty(Mono.empty())
        .flatMap { respondToInstructions(it) }
        .flatMap {
            event.message.channel.flatMap {
                channel -> channel.createMessage(it)
            }
        }.then()

    private fun respondToInstructions(arguments: List<String>): Mono<EmbedCreateSpec> =
            commandList.firstOrNull { it.validate(arguments) }?.evaluate(arguments) ?:
                    Mono.just(getErrorMessage("Invalid Command"))

    private fun getErrorMessage(description: String) = EmbedCreateSpec.builder()
            .color(Color.RED)
            .description(description)
            .build()

    override val eventType: Class<MessageCreateEvent> = MessageCreateEvent::class.java

}