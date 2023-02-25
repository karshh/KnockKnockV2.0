package com.discord.knockknock.eventlisteners

import com.discord.knockknock.services.ShibaRestService
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.spec.MessageCreateFields
import discord4j.core.spec.MessageCreateSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class ShibaEventListener @Autowired constructor(private val shibaRestService: ShibaRestService)
    : EventListener<MessageCreateEvent> {
    override val eventType: Class<MessageCreateEvent> = MessageCreateEvent::class.java

    override fun execute(event: MessageCreateEvent): Mono<Void> =  Mono.zip(
                Mono.just(event).filter { it.message.content == "?shiba" },
                shibaRestService.getShibaUrl().filter { it.isNotEmpty() }
        ).flatMap { tuple ->
            tuple.t1.message.channel.flatMap { it.createMessage(tuple.t2).then() }
        }
}