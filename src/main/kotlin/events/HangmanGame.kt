package events

import java.io.File
import kotlin.random.Random

const val defaultNumberOfHangmanGuesses = 7

private const val rootToWords = "src/main/resources/lists/words/google-10000-english-usa-no-swears-"
private val sizes = arrayOf("medium", "long")
private const val textFileEnd = ".txt"

class HangmanGame(guesses : Int = defaultNumberOfHangmanGuesses) {
    // Get a random word from the list
    val word = fetchRandomWord()

    private val guessedCharacters = mutableSetOf<Char>()

    private var guessesLeft = guesses

    private fun fetchRandomWord() : String {
        val allWords = ArrayList<String>()

        for (size in sizes) {
            val path = rootToWords + size + textFileEnd
            allWords.addAll(File(path).readLines())
        }

        val index = Random.nextInt(allWords.size)
        return allWords[index]
    }

    fun guessCharacter(guess : Char) : String {
        // Add to guesses
        guessedCharacters.add(guess)

        return if (checkCharacter(guess)) {
            "$guess was correct!"

        } else {
            guessesLeft--
            "$guess was not correct."
        }
    }

    fun checkCharacter(guess : Char) : Boolean =  word.contains(guess)


    fun guessWord(guess : String) : String {
        return if (checkWord(guess)) {
            for (char in word) {
                guessedCharacters.add(char)
            }

            ""

        } else {
            guessesLeft--
            "$guess was not the correct word"
        }
    }

    fun checkWord(guess : String) : Boolean = guess == word

    fun wonGame() : Boolean {
        for (char in word) {
            if (char !in guessedCharacters) {
                return false
            }
        }

        return true
    }

    fun lostGame() : Boolean = guessesLeft <= 0

    fun getCurrentRevealedWord() : String {
        val sb = StringBuilder()

        for (char in word) {
            if (char in guessedCharacters) {
                sb.append(char.toUpperCase())

            } else {
                sb.append("-")
            }
        }

        sb.append("   (${word.length} characters) ($guessesLeft guesses left)\n")

        if (wonGame()) {
            sb.append(":tada: You won! The word was $word :tada:")


        } else {
            sb.append("Guessed characters: ${guessedCharacters.joinToString()}")

        }

        return sb.toString()
    }
}