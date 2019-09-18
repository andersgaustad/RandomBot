package core

// Imports work! :O
import com.jessecorbett.diskord.dsl.bot
import com.jessecorbett.diskord.dsl.command
import com.jessecorbett.diskord.dsl.commands
import com.jessecorbett.diskord.dsl.message
import kotlinx.coroutines.runBlocking

val BOT_TOKEN = safe.getToken()

fun main() = runBlocking {
    // Initialize bot
    bot(BOT_TOKEN) {
        // Commands
        commands {
            command("ping") {
                reply("pong!")
                delete()
            }
        }

        // Messages
        messageCreated {
            if (it.content.contains("echo")) {
                val echo = it.content
                it.reply(echo)
            }

            if (it.content.contains("Cousland", true)) {
                it.react("💯")
            }

        }
    }
}

fun debug() {
    println(BOT_TOKEN)
}

fun main(args: Array<String>) {
    //debug()
    main()
}