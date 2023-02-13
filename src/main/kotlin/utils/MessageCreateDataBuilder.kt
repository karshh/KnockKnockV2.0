package utils

import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.utils.messages.MessageCreateData
import java.awt.Color

class MessageCreateDataBuilder {

    private var description: String? = null
    private var color = 0

    fun setEmbedDescription(description: String): MessageCreateDataBuilder {
        this.description = description
        return this
    }

    fun setEmbedColor(color: Color): MessageCreateDataBuilder {
        this.color = color.rgb
        return this
    }

    fun build(): MessageCreateData {
        return MessageCreateData.fromEmbeds(
                MessageEmbed(
                        null,
                        null,
                        description,
                        null,
                        null,
                        color,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                )
        )
    }
}