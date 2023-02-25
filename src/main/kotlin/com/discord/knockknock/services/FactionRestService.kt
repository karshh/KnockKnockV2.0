package com.discord.knockknock.services

import com.discord.knockknock.services.response.GetFactionDataResponse
import com.discord.knockknock.services.utils.FactionDataSelection
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

class FactionRestService {
    fun getFactionData(apiKey: String, factionId: String, selections: List<FactionDataSelection>): Mono<GetFactionDataResponse> {
        val client = WebClient.create("https://api.torn.com/faction/$factionId?selections=${selections.joinToString(",")}&key=$apiKey")
        return client.get().retrieve().bodyToMono(GetFactionDataResponse::class.java)

    }
}