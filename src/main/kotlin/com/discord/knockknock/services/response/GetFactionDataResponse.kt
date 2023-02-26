package com.discord.knockknock.services.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

data class GetFactionDataResponse(
        val error: Error?,
        @JsonProperty("ID")
        val id: Int?,
        val name: String?,
        val members: Map<String, FactionMember>?
)

data class FactionMember(
        val name: String,
        val level: Int,
        @JsonProperty("days_in_faction")
        val daysInFaction: Int,
        @JsonProperty("last_action")
        val lastAction: LastAction,
        val status: Status,
        val position: String
)

data class LastAction(
        val status: String,
        val timestamp: Instant,
        val relative: String
)

data class Status(
        var description: String,
        val details: String,
        val state: String,
        val color: String,
        val until: Int
)
