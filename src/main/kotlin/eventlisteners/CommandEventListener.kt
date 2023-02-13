package eventlisteners

import commands.CommandExecutionFactory
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.EventListener
import org.springframework.beans.factory.annotation.Autowired

class CommandEventListener: EventListener {

    @Autowired
    lateinit var commandExecutionFactory: CommandExecutionFactory

    override fun onEvent(event: GenericEvent) {
        val discordBot = event.jda
        if (event !is MessageReceivedEvent) {
            return
        }
        val messageContent = event.message.contentRaw
        if (!messageContent.startsWith("?knock ")) {
            return
        }
        val instructions = messageContent.split(Regex("\\s+")).toMutableList()
        instructions.removeAt(0) // remove the ?knock command so all we got are instructions
        if (instructions.isEmpty()) {
             return
        }
        val response = commandExecutionFactory.execute(instructions)
        discordBot.getTextChannelById(event.channel.id)?.sendMessage(response)?.queue()
    }
}