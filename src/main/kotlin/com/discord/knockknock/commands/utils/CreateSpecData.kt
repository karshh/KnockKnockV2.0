package com.discord.knockknock.commands.utils

import discord4j.core.spec.EmbedCreateSpec
import discord4j.core.spec.MessageCreateSpec

interface CreateSpecData

data class MessageCreateSpecData(
        val message: MessageCreateSpec,
): CreateSpecData

data class EmbedCreateSpecData(
        val embed: EmbedCreateSpec
): CreateSpecData