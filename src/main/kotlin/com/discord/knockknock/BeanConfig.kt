package com.discord.knockknock

import com.discord.knockknock.commands.HelpCommand
import com.discord.knockknock.commands.InactiveCommand
import com.discord.knockknock.commands.JokeCommand
import com.discord.knockknock.commands.utils.Command
import com.discord.knockknock.commands.TimeCommand
import com.discord.knockknock.services.FactionRestService
import com.discord.knockknock.eventlisteners.EventListener
import discord4j.core.DiscordClientBuilder
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.Event
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.lang.Exception


@Configuration
class BeanConfig {

    @Value("\${discord.bot.token}")
    private lateinit var token: String

    @Value("\${discord.torn.masterApiKey}")
    private lateinit var masterApiKey: String

    @Bean
    fun factionRestTemplate(): FactionRestService {
        return FactionRestService()
    }

    @Bean
    fun <T : Event> discordClient(eventListeners: List<EventListener<T>>, commands: List<Command>): GatewayDiscordClient {
        val client = DiscordClientBuilder.create(token).build().login().block()
                ?: throw Exception("Could not instantiate discord bot.")

        eventListeners.forEach {
            client.on(it.eventType)
                    .flatMap(it::execute)
                    .onErrorResume(it::handleError)
                    .subscribe()
        }

        return client
    }

    @Bean
    fun commandList(factionRestTemplate: FactionRestService): List<Command> {
        return listOf(
                JokeCommand(),
                TimeCommand(),
                HelpCommand(),
                InactiveCommand(masterApiKey, factionRestTemplate)
        )
    }


}