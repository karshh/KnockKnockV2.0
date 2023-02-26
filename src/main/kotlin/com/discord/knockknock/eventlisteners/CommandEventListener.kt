package com.discord.knockknock.eventlisteners

import com.discord.knockknock.commands.utils.Command
import com.discord.knockknock.commands.utils.CreateSpecData
import com.discord.knockknock.commands.utils.EmbedCreateSpecData
import com.discord.knockknock.commands.utils.MessageCreateSpecData
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.spec.EmbedCreateSpec
import discord4j.core.spec.MessageCreateSpec
import discord4j.core.spec.MessageEditSpec
import discord4j.rest.util.Color
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.lang.Exception

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
                when (it) {
                    is MessageCreateSpecData ->
                        event.message.channel.flatMap { channel ->
                            channel.createMessage(it.message)
                        }
                    is EmbedCreateSpecData ->
                        event.message.channel.flatMap { channel ->
                            channel.createMessage(it.embed)
                        }
                    else -> throw Exception("Error invalid instance of data class $it")
                }
            }
            .onErrorResume { exception ->
                handleError(exception)
                Flux.empty()
            }
            .then()

    private fun respondToInstructions(arguments: List<String>): Flux<CreateSpecData> =
            commandList.firstOrNull { it.validate(arguments) }?.evaluate(arguments)
                    ?: Flux.just(getErrorMessage("Invalid Command. Hint: `?knock help`"))
                            .map { MessageCreateSpecData(it) }

    private fun getErrorMessage(description: String) = MessageCreateSpec.builder()
            .content(description)
            .build()

    override val eventType: Class<MessageCreateEvent> = MessageCreateEvent::class.java

}