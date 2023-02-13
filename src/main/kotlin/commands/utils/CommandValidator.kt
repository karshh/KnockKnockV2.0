package commands.utils

import net.dv8tion.jda.api.utils.messages.MessageCreateData

interface CommandFunction {
    fun validate(arguments: List<String>): Boolean
    fun evaluate(arguments: List<String>): MessageCreateData
}