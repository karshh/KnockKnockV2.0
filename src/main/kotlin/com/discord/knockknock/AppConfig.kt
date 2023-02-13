package com.discord.knockknock

import commands.CommandExecutionFactory
import eventlisteners.CommandEventListener
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.requests.GatewayIntent
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class AppConfig() {

    @Value("\${discord.bot.token}")
    private lateinit var token: String

    @Bean
    fun commandExecutionFactory(): CommandExecutionFactory {
        return CommandExecutionFactory()
    }

    @Bean
    fun restTemplate(builder: RestTemplateBuilder): RestTemplate {
        return builder.build()
    }

    @Bean
    fun commandEventListener(): EventListener {
        return CommandEventListener()
    }

    @Bean
    fun discordBot(commandEventListener: CommandEventListener): JDA {
        return JDABuilder
                .create(token,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_MESSAGES)
                .addEventListeners(commandEventListener)
                .build()
    }

}