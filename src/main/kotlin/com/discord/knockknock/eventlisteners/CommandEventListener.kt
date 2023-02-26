package com.discord.knockknock.eventlisteners

import com.discord.knockknock.commands.utils.Command
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.spec.EmbedCreateSpec
import discord4j.core.spec.MessageCreateSpec
import discord4j.rest.util.Color
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class CommandEventListener @Autowired constructor(private val commandList: List<Command>) : EventListener<MessageCreateEvent> {

    override fun execute(event: MessageCreateEvent): Mono<Void> = Mono.just(event)
        .filter { it.message.content.startsWith("?knock ") }
        .map { it.message.content.split(Regex("\\s+")).toMutableList() }
        .map { it.subList(1, it.size) }
        .filter { it.isNotEmpty() }
        .switchIfEmpty(Mono.empty())
        .flux()
        .flatMap { respondToInstructions(it) }
        .flatMap {
            event.message.channel.flatMap {
                channel -> channel.createMessage(it)
            }
        }
        .onErrorResume { exception ->
            handleError(exception)
            event.message.channel.flatMap {
                it.createMessage("I have crashed. Check my logs to see if I am recoverable.")
            }
        }
        .then()

    private fun respondToInstructions(arguments: List<String>): Flux<EmbedCreateSpec> =
            commandList.firstOrNull { it.validate(arguments) }?.evaluate(arguments) ?:
            Flux.just(getErrorMessage("Invalid Command. Hint: `?knock help`"))

    private fun getErrorMessage(description: String) = EmbedCreateSpec.builder()
            .description(description)
            .build()

    override val eventType: Class<MessageCreateEvent> = MessageCreateEvent::class.java

}