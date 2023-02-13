package com.discord.knockknock

import eventlisteners.CommandEventListener
import net.dv8tion.jda.internal.JDAImpl
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KnockknockApplication

fun main(args: Array<String>) {
    runApplication<KnockknockApplication>(*args)
}


