package core

// Imports work! :O
import com.jessecorbett.diskord.dsl.bot
import com.jessecorbett.diskord.dsl.command
import com.jessecorbett.diskord.dsl.commands
import com.jessecorbett.diskord.util.isFromUser
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.UnstableDefault


const val BOT_NAME = "RandomBot"
private const val COOL_KID_NAME = "Cousland"
val BOT_TOKEN = safe.getToken()

private const val RANDOM_PREFIX = ""
val FRENCH_PEOPLE = Array<String>(1) {"Fairylight18"}

@UnstableDefault
fun main() = runBlocking {
    // Initialize bot
     bot(BOT_TOKEN) {
         val prefix = "!"


         // Commands
         commands(prefix) {

            // Ping command
             command("ping") {
                 reply("pong!")
                 delete()

             }

             // Roll command:
             command("roll") {
                 reply(commandlogic.roll(this))
                 delete()

             }
             command("r") {
                 reply(commandlogic.roll(this))
                 delete()

             }

             // RD-commands
             // Random wiki article
             command(RANDOM_PREFIX + "wiki") {
                 reply(commandlogic.getRandomWikiArticle())
                 delete()

             }

             command(RANDOM_PREFIX + "commander") {
                 reply(commandlogic.getRandomMTGCommanderCard())
                 delete()

             }


            /*
            // Prefix command
            command("prefix") {
                if (this.words.size > 1) {
                    val newPrefix = this.words[1]
                    prefix = newPrefix
                    reply("New prefix set to $newPrefix")
                    delete()
                    //randomBot.restart()

                } else {
                    reply("No prefix found, try again?")
                    delete()

                }

            }

             */

             command("help") {
                 reply(commandlogic.help())
                 delete()

             }
             command("h") {
                 reply(commandlogic.help())
                 delete()
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
                        message.reply("Hello ${message.author.username}!")
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

@UnstableDefault
fun main(args: Array<String>) {
    debug()
    main()
}