package com.discord.knockknock

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

@SpringBootApplication
@EnableScheduling
class KnockknockApplication

fun main(args: Array<String>) {
    ObjectMapper().registerKotlinModule()
    runApplication<KnockknockApplication>(*args)
}
