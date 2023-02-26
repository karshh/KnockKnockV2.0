package com.discord.knockknock

import com.discord.knockknock.commands.*
import com.discord.knockknock.commands.utils.Command
import com.discord.knockknock.eventlisteners.EventListener
import discord4j.common.ReactorResources
import discord4j.core.DiscordClientBuilder
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.Event
import discord4j.core.event.domain.lifecycle.DisconnectEvent
import discord4j.rest.RestClient
import discord4j.rest.RestClientBuilder
import discord4j.rest.request.RouterOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.client.reactive.ReactorResourceFactory
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import java.lang.Exception


@Configuration
class BeanConfig {

    @Value("\${discord.bot.token}")
    private lateinit var token: String

    @Value("\${discord.torn.masterApiKey}")
    private lateinit var masterApiKey: String

    @Bean
    fun <T : Event> discordClient(eventListeners: List<EventListener<T>>, commands: List<Command>): GatewayDiscordClient {
        val client = DiscordClientBuilder.create(token)
                .setExtraOptions { RouterOptions(
                        it.token,
                        it.reactorResources,
                        it.exchangeStrategies,
                        it.responseTransformers,
                        it.globalRateLimiter,
                        it.requestQueueFactory,
                        it.discordBaseUrl
                ) }
                .build()
                .login()
                .block()
                ?: throw Exception("Could not instantiate discord bot.")

        eventListeners.forEach {
            client.on(it.eventType)
                    .flatMap(it::execute)
                    .onErrorResume(it::handleError)
                    .subscribe()
        }
        client.on(DisconnectEvent::class.java)
                .subscribe {println("Bot has disconnected, $it")}

        return client
    }

    @Bean
    fun commandList(): List<Command> {
        return listOf(
                JokeCommand(),
                TimeCommand(),
                HelpCommand(),
                InactiveCommand(masterApiKey),
                TravelCommand(masterApiKey),
                OnlineCommand(masterApiKey),
                CrashTheBotCommand()
        )
    }


}