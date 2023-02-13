package commands.utils
import net.dv8tion.jda.api.utils.messages.MessageCreateData
import java.io.InvalidObjectException

internal interface Command {

    companion object: CommandFunction {
        override fun validate(arguments: List<String>): Boolean {
            return false
        }

        override fun evaluate(arguments: List<String>): MessageCreateData {
            throw InvalidObjectException("This implementation of command cannot be instantiated.")
        }
    }

}


