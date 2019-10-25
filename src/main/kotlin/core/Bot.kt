@file:Suppress("EXPERIMENTAL_API_USAGE")

package core

// Imports work! :O
import com.jessecorbett.diskord.dsl.bot
import com.jessecorbett.diskord.dsl.command
import com.jessecorbett.diskord.dsl.commands
import com.jessecorbett.diskord.util.isFromUser
import com.jessecorbett.diskord.util.words
import commandlogic.*
import games.GuessTheNumberGame
import games.HangmanGame
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import utils.PokemonFixedParser


const val BOT_NAME = "RandomBot"
private const val COOL_KID_NAME = "Cousland"
val FRENCH_PEOPLE = arrayOf("Fairylight18")

val BOT_TOKEN = safe.getToken()

private const val RANDOM_PREFIX = ""

// Flags
enum class States {
    GUESSTHENUMBER, HANGMAN
}

private val flags = mutableSetOf<States>()

// Added because of serializing over json for bacon command
@ImplicitReflectionSerializer
@UnstableDefault
fun main() = runBlocking {
    // Initialize bot
     bot(BOT_TOKEN) {
         val prefix = "!"

         // Games
         var guessTheNumberGame = GuessTheNumberGame(0)
         var hangmanGame = HangmanGame()


         // Commands
         commands(prefix) {

            // Ping command
             command("ping") {
                 reply(Ping().executeCommand(this))
                 delete()

             }

             // Roll command:
             command("roll") {
                 reply(Dice().executeCommand(this))
                 delete()

             }
             command("r") {
                 reply(Dice().executeCommand(this))
                 delete()

             }

             // NRK news
             command ("nrk") {
                 reply(NRK().executeCommand(this))
                 delete()

             }

             // Calculate
             command("calculate") {
                 reply(Calculator().executeCommand(this))
                 delete()

             }

             // RD-commands
             // Random wiki article
             command(RANDOM_PREFIX + "wiki") {
                 reply(Wiki().executeCommand(this))
                 delete()

             }

             // Random mtg commander card
             command(RANDOM_PREFIX + "commander") {
                 reply(MTGCommander().executeCommand(this))
                 delete()

             }


             // Bacon
             /*
             DISCLAIMER: I do not own any of the videos fetched by using this command
             All videos fetched with the command !bacon are the property of youtuber Bacon_
             Bacon_'s youtube channel may be accessed by following the link below:
             https://www.youtube.com/channel/UCcybVOrBgpzUxm-mlBT0WTA
             */
             command(RANDOM_PREFIX + "bacon") {
                 reply(Bacon().executeCommand(this))
                 delete()
             }

             // Pokemon:
             command(RANDOM_PREFIX + "pokelist") {
                 reply(Pokelist().executeCommand(this))
                 delete()
             }
             command(RANDOM_PREFIX + "pokemon") {
                 reply(Pokemon().executeCommand(this))
                 delete()
             }


             // Games
             // Guess the number game
             command("gtn") {
                 val helpCheck = GuessTheNumberCommand().executeCommand(this)
                 if (helpCheck.isNotEmpty()) {
                     reply(helpCheck)
                     delete()
                 }

                 if (!flags.contains(States.GUESSTHENUMBER)) {
                     // Create game
                     // This should resolve no matter what argument is used
                     guessTheNumberGame = createGame(this)
                     flags.add(States.GUESSTHENUMBER)

                     // Confirm creation
                     reply("Created game with a number between 0 and ${guessTheNumberGame.limit}")
                     delete()
                 }
             }

             // Hangman game
             command("hangman") {
                 val helpCheck = HangmanCommand().executeCommand(this)
                 // Should we run game or fetch detailed help
                 if (helpCheck.isEmpty()) {
                     // Only run if game is not created already
                     if (!flags.contains(States.HANGMAN)) {
                         val words = this.words
                         hangmanGame = if (words.size >= 2 && words[1].toIntOrNull() != null) {
                             val guesses = words[1].toInt()
                             HangmanGame(guesses)

                         } else {
                             HangmanGame()
                         }

                         // Finally, give information
                         reply("Hangman game was created")
                         flags.add(States.HANGMAN)

                         // Show word (aka dashes)
                         reply(hangmanGame.getCurrentRevealedWord())
                         delete()
                     }

                 } else {
                     reply(helpCheck)
                     delete()
                 }
             }




             // Help
             command("help") {
                 reply(Help().executeCommand(this))
                 delete()

             }
             command("h") {
                 reply(Help().executeCommand(this))
                 delete()
             }

             // Dev commands
             command("dev") {
                 val words = this.words

                 // Check minimum size
                 if (words.size >= 2) {
                     val confirmation = when(val arg = words[1]){
                         "pokeparse", "pp" -> {
                             val root = "src/main/resources"
                             // Create a any parser compatible with the parser interface
                             val parser = PokemonFixedParser()
                             parser.parseRawFile(
                                 "$root/raw/ListOfPokemonPageSource.txt",
                                 "$root/json/Pokemon.json"
                             )
                         }
                         "resetflags", "rf" -> {
                             flags.clear()
                             "All flags where reset!"
                         }

                         else -> "Error; no command named $arg"
                     }

                     reply(confirmation)
                     delete()
                 }
             }

        }

        // Messages
        messageCreated { message ->
            if (message.content.contains("echo") && message.isFromUser) {
                val echo = message.content
                message.reply(echo)
            }

            if (message.usersMentioned.isNotEmpty()) {
                if (message.usersMentioned.any {user -> user.isBot}) {
                    // React with robot emoji
                    message.react("\uD83E\uDD16")
                }

            }

            // Games
            // Guess the number functionality
            if (flags.contains(States.GUESSTHENUMBER)) {
                when(val guess = message.words[0].toIntOrNull()) {
                    is Int -> {
                        // Check if correct:
                        if (guessTheNumberGame.guessIsCorrect(guess)) {
                            flags.remove(States.GUESSTHENUMBER)
                        }

                        // Did the user guess correctly?
                        val feedback = guessTheNumberGame.constructFeedbackMessage(message)
                        message.reply(feedback)
                    }
                }
            }

            // Hangman functionality
            if (flags.contains((States.HANGMAN))) {
                val words = message.words
                // First of all, only guess for one sentence words
                if (words.size == 1 && words[0][0].isLetter()) {
                    // Then check if we are guessing char or word
                    val word = words[0].toLowerCase()
                    if (word.length == 1) {
                        message.reply(hangmanGame.guessCharacter(word[0]))

                    } else {
                        message.reply(hangmanGame.guessWord(word))
                    }

                    // Show the current word
                    message.reply(hangmanGame.getCurrentRevealedWord())

                    // Check if game is won or lost, and reset flag if it is
                    if (hangmanGame.wonGame() || hangmanGame.lostGame()) {
                        flags.remove(States.HANGMAN)

                        if (hangmanGame.lostGame()) {
                            message.reply("You lost :cry: The word was ${hangmanGame.word}")
                        }
                    }

                }
            }

            // Mentions
            if (message.usersMentioned.isNotEmpty()) {
                message.usersMentioned.forEach {
                    val username = it.username

                    if (username == BOT_NAME) {
                        message.reply(getRandomGuardDialog())
                    }

                    if (username == COOL_KID_NAME) {
                        message.react("🔥")
                    }
                }
            }

            // Author:
            if (message.author.username in FRENCH_PEOPLE) {
                message.react("\uD83C\uDDEB\uD83C\uDDF7")
                message.delete()
            }

        }


    }
}

fun debug() {
    //println(BOT_TOKEN)
    println("Bot is active")
}

@ImplicitReflectionSerializer
@UnstableDefault
fun main(args: Array<String>) {
    debug()
    main()
}