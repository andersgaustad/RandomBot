package core

// Imports work! :O
import com.jessecorbett.diskord.dsl.bot
import com.jessecorbett.diskord.dsl.command
import com.jessecorbett.diskord.dsl.commands
import kotlinx.coroutines.runBlocking

val BOT_TOKEN = safe.getToken()

fun main() = runBlocking {
    bot(BOT_TOKEN) {
        commands {
            command("ping") {
                reply("pong!")
                delete()
            }
        }
    }
}