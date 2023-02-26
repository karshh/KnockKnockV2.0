package com.discord.knockknock.services

import com.discord.knockknock.services.response.GetFactionDataResponse
import com.discord.knockknock.services.utils.FactionDataSelection
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono


object FactionApi {
        private val BASE_URL = "https://api.torn.com/faction"

        fun get(apiKey: String, factionId: String? = "", selections: List<FactionDataSelection>) =
                WebClient.create("$BASE_URL/$factionId?selections=${selections.joinToString(",")}&key=$apiKey")
                        .get()
                        .retrieve()
                        .bodyToMono(GetFactionDataResponse::class.java)
}