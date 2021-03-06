package commandlogic

import com.jessecorbett.diskord.api.model.Message
import com.jessecorbett.diskord.util.words
import events.GuessTheNumberGame

private const val defaultLimit = 100

class GuessTheNumberCommand : Command() {
    override val name: String
        get() = "gtn"

    override fun parseMessage(message: Message): String {
        return ""
    }

}

// Let kotlin handle crashes
fun createGame(message : Message) : GuessTheNumberGame {
    return try {
        createGame(message.words[1].toInt())

    } catch (iobe : IndexOutOfBoundsException) {
        createGame(defaultLimit)
    }

}

fun createGame(limit : Int) : GuessTheNumberGame = GuessTheNumberGame(limit)