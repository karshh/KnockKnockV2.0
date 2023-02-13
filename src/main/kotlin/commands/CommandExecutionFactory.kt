package commands

import net.dv8tion.jda.api.utils.messages.MessageCreateData
import utils.MessageCreateDataBuilder
import java.awt.Color


class CommandExecutionFactory {

    private val COMMAND_LIST = listOf(
            JokeCommand
    )

    fun execute(arguments: List<String>): MessageCreateData {
        if (arguments.isEmpty()) {
            return getErrorMessage("Empty Command")
        }

        for (command in COMMAND_LIST) {
            if (command.validate(arguments)) {
                return command.evaluate(arguments)
            }
        }

        return getErrorMessage("Invalid command.")
    }

    private fun getErrorMessage(description: String): MessageCreateData {
        return MessageCreateDataBuilder()
                .setEmbedColor(Color.RED)
                .setEmbedDescription(description)
                .build()

    }
}