@file:Suppress("EXPERIMENTAL_API_USAGE")

package core

// Imports work! :O
import com.jessecorbett.diskord.dsl.bot
import com.jessecorbett.diskord.dsl.command
import com.jessecorbett.diskord.dsl.commands
import com.jessecorbett.diskord.util.isFromUser
import com.jessecorbett.diskord.util.words
import commandlogic.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import utils.PokemonFixedParser


const val BOT_NAME = "RandomBot"
private const val COOL_KID_NAME = "Cousland"
val BOT_TOKEN = safe.getToken()

private const val RANDOM_PREFIX = ""
val FRENCH_PEOPLE = Array(1) {"Fairylight18"}

// Added because of serializing over json for bacon command
@ImplicitReflectionSerializer
@UnstableDefault
fun main() = runBlocking {
    // Initialize bot
     bot(BOT_TOKEN) {
         val prefix = "!"


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
             // Should clean up this later
             command("dev") {
                 val words = this.words

                 if (words.size >= 2) {
                     val arg = words[1]

                     if (arg == "pokeparse" || arg == "pp") {
                         val root = "src/main/resources"
                         val parseMessage = PokemonFixedParser().parseRawFile(
                             "$root/raw/ListOfPokemonPageSource.txt",
                             "$root/json/Pokemon.json"
                         )

                         reply(parseMessage)
                         delete()
                     }
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