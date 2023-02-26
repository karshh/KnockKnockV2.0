package com.discord.knockknock.services

import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

object ShibaApi {

    fun get(): Mono<String> = WebClient.create("https://shibe.online/api/shibes?count=1")
                .get()
                .retrieve()
                .bodyToMono(Array<String>::class.java)
                .filter { !it.isNullOrEmpty() }
                .map { it.first() }
}