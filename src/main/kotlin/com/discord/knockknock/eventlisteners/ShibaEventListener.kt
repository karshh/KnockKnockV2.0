package com.discord.knockknock.eventlisteners

import com.discord.knockknock.services.ShibaApi
import discord4j.core.event.domain.message.MessageCreateEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class ShibaEventListener : EventListener<MessageCreateEvent> {
    override val eventType: Class<MessageCreateEvent> = MessageCreateEvent::class.java

    override fun execute(event: MessageCreateEvent): Mono<Void> {
        if (event.message.content.trimEnd() != "?shiba") {
            return Mono.empty()
        }

        return ShibaApi.get().filter { it.isNotEmpty() }
                .flatMap { event.message.channel.flatMap { channel -> channel.createMessage(it) } }
                .then()
    }
}