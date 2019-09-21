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
                 // Check validity if command:
                 val words = this.words
                 if (words.size >= 2) {
                     // Check if dice format is correct:
                     val diceChoices = words[1].split("d")
                     // Check that this is now a list of 2 elements, and that all can be casted to ints
                     if (diceChoices.size == 2) {
                         val numberOfDice = diceChoices[0].toIntOrNull()
                         val diceSize =  diceChoices[1].toIntOrNull()

                         if (numberOfDice != null && diceSize!= null) {
                             // Check number of dice:
                             if (numberOfDice > 0) {
                                 // Check sides on dice
                                 if (diceSize > 0) {
                                     // Create a list of dice
                                     val dice = Array<Int>(numberOfDice) {
                                         ThreadLocalRandom.current().nextInt(1, diceSize+1)
                                     }

                                     val dicesToString = dice.joinToString { i->
                                         "[$i] "
                                     }.replace(" , ", " ")

                                     val sumOfDice = dice.sum()

                                     val reply = "${this.author.mention} rolled: ${dicesToString}for a total of $sumOfDice"

                                     reply(reply)
                                     delete()

                                 } else {
                                     reply("Cannot roll dice of size $diceSize")
                                     delete()

                                 }

                             } else {
                                 reply("Cannot roll $numberOfDice dice")
                                 delete()

                             }

                         } else {
                             reply("Hmmm, I don't think I can roll ${diceChoices[0]}d${diceChoices[1]}...")
                             delete()
                         }

                     }

                 } else {
                     reply("Wrong format; Roll command should be '!roll ndX'")
                     delete()
                 }

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