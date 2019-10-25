package events

import com.jessecorbett.diskord.api.model.Message
import com.jessecorbett.diskord.util.mention
import com.jessecorbett.diskord.util.words

import java.util.concurrent.ThreadLocalRandom

class GuessTheNumberGame(val limit : Int) {
    val number = ThreadLocalRandom.current().nextInt(0, limit+1)

    fun constructFeedbackMessage(message: Message) : String {
        val playerMention = message.author.mention
        val words = message.words

        // Let Kotlin handle crashes
        val delta = checkGuessedNumber(words[0].toInt())

        return when {
            delta > 0 -> "Too high, $playerMention!"
            delta < 0 -> "Too low, $playerMention!"
            else -> ":tada: :tada: $playerMention guessed right! :tada: :tada:\n The correct number was $number"
        }
    }

    // Returns negative when below, positive when over, and 0 when correct!
    private fun checkGuessedNumber(guess : Int) = guess - number

    fun guessIsCorrect(guess : Int) = checkGuessedNumber(guess) == 0
}

