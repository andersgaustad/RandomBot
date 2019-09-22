package core

// Imports work! :O
import com.jessecorbett.diskord.dsl.bot
import com.jessecorbett.diskord.dsl.command
import com.jessecorbett.diskord.dsl.commands
import com.jessecorbett.diskord.util.isFromUser
import com.jessecorbett.diskord.util.mention
import com.jessecorbett.diskord.util.words
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.UnstableDefault
import java.util.concurrent.ThreadLocalRandom


const val BOT_NAME = "RandomBot"
const val COOL_KID_NAME = "Cousland"
val BOT_TOKEN = safe.getToken()


@UnstableDefault
fun main() = runBlocking {
    // Initialize bot
     bot(BOT_TOKEN) {
         var prefix = "!"

         val randomBot = this


         // Commands
         commands(prefix) {

            // Ping command
             command("ping") {
                 reply("pong!")
                 delete()

             }

             // Roll command:
             command("roll") {
                 reply(helper.roll(this))
                 delete()

             }
             command("r") {
                 reply(helper.roll(this))
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