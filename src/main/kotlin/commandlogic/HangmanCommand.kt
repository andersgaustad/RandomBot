package commandlogic

import com.jessecorbett.diskord.api.model.Message
import games.HangmanGame
import games.defaultNumberOfHangmanGuesses

class HangmanCommand : Command() {
    override val name: String
        get() = "hangman"

    override fun parseMessage(message: Message): String {
        return ""
    }

    // Create a game
    fun createGame(guesses : Int = defaultNumberOfHangmanGuesses) : HangmanGame = HangmanGame(guesses)
}