package core

// Imports work! :O
import com.jessecorbett.diskord.api.model.Emoji
import com.jessecorbett.diskord.api.model.UserStatus
import com.jessecorbett.diskord.dsl.bot
import com.jessecorbett.diskord.dsl.command
import com.jessecorbett.diskord.dsl.commands
import com.jessecorbett.diskord.dsl.message
import com.jessecorbett.diskord.util.isFromBot
import com.jessecorbett.diskord.util.isFromUser
import com.jessecorbett.diskord.util.words
import kotlinx.coroutines.runBlocking

const val BOT_NAME = "RandomBot"
val BOT_TOKEN = safe.getToken()

var PREFIX = "!"


fun main() = runBlocking {
    // Initialize bot
     bot(BOT_TOKEN) {

         val randomBot = this

         // Commands
         commands(PREFIX) {
            // Ping command
            command("ping") {
                reply("pong!")
                delete()

            }

            // Prefix command
            /*
            command("prefix") {
                if (this.words.size > 1) {
                    val newPrefix = this.words[1]
                    PREFIX = newPrefix
                    reply("New prefix set to $newPrefix")
                    delete()
                    randomBot.restart()

                } else {
                    reply("No prefix found, try again?")
                    delete()

                }

            }


             */

        }

        // Messages
        messageCreated { message ->
            if (message.content.contains("echo") && message.isFromUser) {
                val echo = message.content
                message.reply(echo)
            }

            if (message.content.contains("Cousland", true)) {
                message.react("💯")
            }

            if (message.usersMentioned.isNotEmpty()) {
                if (message.usersMentioned.any {user -> user.isBot}) {
                    // React with robot emoji
                    message.react("\uD83E\uDD16")
                }

            }

            if (message.usersMentioned.isNotEmpty()) {
                message.usersMentioned.forEach {
                    val userName = it.username
                    println("User $userName:")

                    if (userName == BOT_NAME) {
                        message.reply("Hello ${message.author.username}!")
                    }
                }
            }

        }


    }
}

fun debug() {
    //println(BOT_TOKEN)
    println("Bot is active")
}

fun main(args: Array<String>) {
    debug()
    main()
}