@file:Suppress("EXPERIMENTAL_API_USAGE")

package core

// Imports work! :O
import com.jessecorbett.diskord.api.model.Emoji
import com.jessecorbett.diskord.dsl.bot
import com.jessecorbett.diskord.dsl.command
import com.jessecorbett.diskord.dsl.commands
import com.jessecorbett.diskord.util.isFromUser
import com.jessecorbett.diskord.util.mention
import com.jessecorbett.diskord.util.words
import commandlogic.*
import discordclient.DiscordClientWrapper
import events.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import utils.PokemonFixedParser
import java.util.Timer
import kotlin.concurrent.schedule


const val BOT_NAME = "RandomBot"
private const val COOL_KID_NAME = "Cousland"

val FRENCH_PEOPLE = arrayOf("Fairylight18")

val BOT_TOKEN = safe.getToken()

val DCW = DiscordClientWrapper(BOT_TOKEN)

private const val RANDOM_PREFIX = ""

// Flags
enum class Flags {
    GUESSTHENUMBER, HANGMAN, REACTTEST, DUELGAME
}



// Added because of serializing over json for bacon command
@ImplicitReflectionSerializer
@UnstableDefault
fun main() = runBlocking {
    // Initialize bot
    bot(BOT_TOKEN) {
        val prefix = "!"

        // Flags
        val flags = mutableSetOf<Flags>()

        // Reaction logic
        val reactionAddListeners = mutableSetOf<Reactable>()
        val reactionRemoveListeners = mutableSetOf<Reactable>()

        fun setupListeners(reactable: Reactable) {
            reactionAddListeners.add(reactable)
            reactionRemoveListeners.add(reactable)
        }

        fun retireListeners(reactable: Reactable) {
            reactionAddListeners.remove(reactable)
            reactionRemoveListeners.remove(reactable)
        }

        // Games
        var guessTheNumberGame = GuessTheNumberGame(0)
        var hangmanGame = HangmanGame()
        var duelGame = DuelGame(listOf())

        // Other events
        var reactTestEvent = ReactEvent(null)
        var duelGameReactEvent = ReactEvent(null)

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
            // Disabled for now
            /*
            command("r") {
                reply(Dice().executeCommand(this))
                delete()

            }

             */

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

            command("tofflus") {
                reply(Tofflus().executeCommand(this))
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

                if (!flags.contains(Flags.GUESSTHENUMBER)) {
                    // Create game
                    // This should resolve no matter what argument is used
                    guessTheNumberGame = createGame(this)
                    flags.add(Flags.GUESSTHENUMBER)

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
                    if (!flags.contains(Flags.HANGMAN)) {
                        hangmanGame = if (words.size >= 2 && words[1].toIntOrNull() != null) {
                            val guesses = words[1].toInt()
                            HangmanGame(guesses)

                        } else {
                            HangmanGame()
                        }

                        // Finally, give information
                        reply("Hangman game was created")
                        flags.add(Flags.HANGMAN)

                        // Show word (aka dashes)
                        reply(hangmanGame.getCurrentRevealedWord())
                        delete()
                    }

                } else {
                    reply(helpCheck)
                    delete()
                }
            }

            // Duel game
            command("duel") {
                val dgc = DuelGameCommand()
                val helpCheck = dgc.executeCommand(this)
                if (helpCheck == dgc.duelGameCreatedMessage) {
                    if (!flags.contains(Flags.DUELGAME)) {
                        // Starting game
                        flags.add(Flags.DUELGAME)

                        val root = reply(helpCheck)
                        val gunReaction = "\uD83D\uDD2B"
                        root.react(gunReaction)

                        duelGameReactEvent = ReactEvent(root, Emoji(null, gunReaction))

                        // Add listeners
                        setupListeners(duelGameReactEvent)

                        // Wait a couple of seconds
                        val secondsToWait = 10
                        Timer().schedule((secondsToWait * 1000).toLong()) {
                            runBlocking {
                                // For debugging
                                reply("I have now waited some time")
                                retireListeners(duelGameReactEvent)
                                val reactingUsers = duelGameReactEvent.getReactingUsers().toList()

                                // if list is empty we don't bother starting a new game
                                if (reactingUsers.size >= 2) {
                                    val mentions = reactingUsers.joinToString(", "){it.mention}
                                    reply("Duel Game created!\nContestants are: $mentions")
                                    duelGame = DuelGame(reactingUsers)

                                    // Start with first pair
                                    reply(duelGame.createDuelMessage())

                                    // Start duel
                                    reply(duelGame.countdown())

                                } else {
                                    reply("Need at least two players to start duel :frowning: Ending Duel Game...")
                                    flags.remove(Flags.DUELGAME)
                                }
                            }

                        }

                    }
                }
            }

            // Events
            command("reacttest") {
                if (words.size == 1) {
                    if (!flags.contains(Flags.REACTTEST)) {
                        // Set up react event
                        val root = reply(ReactTestCommand().executeCommand(this))
                        reactTestEvent = ReactEvent(root)
                        root.react("\uD83D\uDD25")

                        // Add to listeners
                        reactionAddListeners.add(reactTestEvent)
                        reactionRemoveListeners.add(reactTestEvent)

                        // Add flag
                        flags.add(Flags.REACTTEST)

                        delete()

                    } else {
                        reply("Test is live! Use !react check")
                    }
                } else {
                    if (words[1] == "check" && flags.contains(Flags.REACTTEST)) {
                        val sb = StringBuilder()
                        sb.append("The following reacted to the message:\n")
                        sb.append(reactTestEvent.getReactingUsers().joinToString { it.mention })

                        reply(sb.toString())

                        // Remove listeneres
                        reactionAddListeners.remove(reactTestEvent)
                        reactionRemoveListeners.remove(reactTestEvent)

                        // Remove flag
                        flags.remove(Flags.REACTTEST)

                        delete()
                    }
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
                        "clearflags", "cf" -> {
                            flags.clear()
                            "All flags where cleared!"
                        }
                        "timer" -> {
                            Timer().schedule(3000) {
                                runBlocking {
                                    this@command.reply("Reply!")
                                }

                            }
                            "Test running"
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


                // In case I forget how to do this...
                /*
                println("Testing dcw...")
                val testUser = dcw.getUser(message.author.id)
                println("Got result for ${testUser.username}")

                */
            }

            if (message.usersMentioned.isNotEmpty()) {
                if (message.usersMentioned.any {user -> user.isBot}) {
                    // React with robot emoji
                    message.react("\uD83E\uDD16")
                }

            }

            // Games
            // Guess the number functionality
            if (flags.contains(Flags.GUESSTHENUMBER)) {
                when(val guess = message.words[0].toIntOrNull()) {
                    is Int -> {
                        // Check if correct:
                        if (guessTheNumberGame.guessIsCorrect(guess)) {
                            flags.remove(Flags.GUESSTHENUMBER)

                        }

                        // Did the user guess correctly?
                        val feedback = guessTheNumberGame.constructFeedbackMessage(message)
                        message.reply(feedback)
                    }
                }
            }

            // Hangman functionality
            if (flags.contains((Flags.HANGMAN))) {
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
                        flags.remove(Flags.HANGMAN)

                        if (hangmanGame.lostGame()) {
                            message.reply("You lost :cry: The word was ${hangmanGame.word}")
                        }
                    }

                }
            }

            // Duel game functionality
            if (message.content == ":gun:" && flags.contains(Flags.DUELGAME) && duelGame.active && duelGame.getCurrentPair().contains(message.author)) {
                // Deactivate duel
                duelGame.deactivate()

                val winningPlayer = message.author

                // Declare winner of current duel
                duelGame.declareDuelWinner(winningPlayer)
                message.reply("${winningPlayer.mention} wins the duel!")

                // Ready for next duel
                duelGame.createDuelMessage()

                // Start next duel
                if(!duelGame.gameIsWon()) {
                    message.reply(duelGame.countdown())
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

            if (message.author.username in FRENCH_PEOPLE) {
                message.react("\uD83C\uDDEB\uD83C\uDDF7")
                message.delete()
            }

        }

         // For reactions
         reactionAdded {
             for (listener in reactionAddListeners) {
                 listener.onReactAdd(it)
             }
         }

         reactionRemoved {
             for (listener in reactionRemoveListeners) {
                 listener.onReactRemove(it)
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