package com.discord.knockknock.commands

import com.discord.knockknock.commands.utils.Command
import com.discord.knockknock.commands.utils.Joke
import discord4j.core.spec.EmbedCreateSpec


class JokeCommand: Command {

        private val JOKES = listOf(
                Joke(name="Dozen", value="anybody want to let me in?"),
                Joke(name="Avenue", value="knocked on this door before?"),
                Joke(name="Ice Cream", value="if you don\"t let me in!"),
                Joke(name="Adore", value="is between us. Open up!"),
                Joke(name="Lettuce", value="in. Its cold out here!"),
                Joke(name="Bed", value="you can not guess who I am."),
                Joke(name="Al", value="give you a kiss if you open the door."),
                Joke(name="Olive", value="you!"),
                Joke(name="Abby", value="birthday to you!"),
                Joke(name="Rufus", value="the most important part of your house."),
                Joke(name="Cheese", value="a cute girl."),
                Joke(name="Wanda", value="hang out with me right now?"),
                Joke(name="Ho-ho.", value="You know, your Santa impression could use a little work."),
                Joke(name="Mary and Abbey.", value="Mary Christmas and Abbey New Year!"),
                Joke(name="Carmen", value="let me in already!"),
                Joke(name="Ya", value="I’m excited to see you too!"),
                Joke(name="Scold", value="outside—let me in!"),
                Joke(name="Robin", value="you! Hand over your cash!"),
                Joke(name="Irish", value="you a Merry Christmas!"),
                Joke(name="Otto", value="know whats taking you so long!"),
                Joke(name="Needle", value="little help gettin in the door."),
                Joke(name="Luke", value="through the keyhole to see!"),
                Joke(name="Justin", value="the neighborhood and thought Id come over."),
                Joke(name="Europe", value="No, you are a poo"),
                Joke(name="To", value="To Whom."),
                Joke(name="Etch", value="Bless You!"),
                Joke(name="Mikey", value="doesnt fit through this keyhole.")
        )

        override fun validate(arguments: List<String>): Boolean {
            return arguments == listOf("joke")
        }

        override fun evaluate(arguments: List<String>): EmbedCreateSpec {
            val joke = JOKES.random().let {
                listOf(
                        "Knock Knock.",
                        "Who's there?",
                        it.name + ".",
                        it.name + " who?",
                        it.value
                ).joinToString("\n")
            }

            return EmbedCreateSpec.create()
                    .withColor(discord4j.rest.util.Color.GREEN)
                    .withDescription(joke)
        }
}