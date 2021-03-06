package ru.chimchima

import dev.kord.core.Kord
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.chimchima.properties.DISCORD_TOKEN
import ru.chimchima.properties.LocalProperties
import ru.chimchima.server.PingServer

fun startPingServer() {
    println("Starting ping server...")
    val port = LocalProperties.port ?: 8080
    PingServer(port).start()
}

suspend fun main() = runBlocking {
    val token = LocalProperties.discordToken ?: throw RuntimeException("$DISCORD_TOKEN is not set")
    val kord = Kord(token)
    if (LocalProperties.heroku) {
        startPingServer()
    }

    val lavaPlayerManager = LavaPlayerManager()
    val commands = ChimberCommands(lavaPlayerManager)

    kord.on<MessageCreateEvent> {
        if (message.author?.isBot != false) return@on
        when (message.content.substringBefore(" ")) {
            "!plink" -> commands.plink(this)
            "!play" -> commands.play(this)
            "!stop" -> commands.stop(this)
            "!pirat" -> commands.pirat(this)
            "!shuffled" -> commands.shuffled(this)
            "!skip" -> commands.skip(this)
            "!queue" -> commands.queue(this)
            "!help" -> commands.help(this)
        }
    }

    launch {
        delay(5000)
        kord.editPresence {
            listening("!help")
        }
    }

    kord.login {
        @OptIn(PrivilegedIntent::class)
        intents += Intent.MessageContent
    }
}
