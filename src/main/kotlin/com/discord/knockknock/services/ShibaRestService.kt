package com.discord.knockknock.services

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.lang.Exception

@Component
class ShibaRestService {

    fun getShibaUrl(): Mono<String> {
        val client = WebClient.create("https://shibe.online/api/shibes?count=1")

        return client
                .get()
                .retrieve()
                .bodyToMono(Array<String>::class.java)
                .filter { !it.isNullOrEmpty() }
                .map { it.first() }
    }
}