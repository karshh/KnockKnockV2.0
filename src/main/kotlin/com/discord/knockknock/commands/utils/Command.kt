package com.discord.knockknock.commands.utils

import discord4j.core.spec.EmbedCreateSpec

internal interface Command {

    fun validate(arguments: List<String>): Boolean

    fun evaluate(arguments: List<String>): EmbedCreateSpec
}


