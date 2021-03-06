package commandlogic

import com.jessecorbett.diskord.api.model.Message
import com.jessecorbett.diskord.util.words
import json.JsonDetailedHelpObject
import kotlinx.io.IOException
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.parse
import java.io.File
import java.io.FileNotFoundException

abstract class Command {

    private val rootToJson = "src/main/resources/json/DetailedHelp.json"
    abstract val name : String

    abstract fun parseMessage(message: Message) : String

    // Every command may be executed with a "help" argument after the command
    @ImplicitReflectionSerializer
    fun executeCommand(message: Message) : String {
        return if (shouldReturnHelp(message.words)) {
            getDetailedHelp()

        } else {
            parseMessage(message)
        }
    }

    @ImplicitReflectionSerializer
    fun getDetailedHelp() : String {
        val data = try {
            File(rootToJson).readText()

        } catch (fnf: FileNotFoundException) {
            fnf.printStackTrace()
            "Could not open file :frowning:"

        } catch (e: Exception) {
            e.printStackTrace()
            "An unknown error occurred :frowning:"

        }

        val jsonparser = Json.parse<JsonDetailedHelpObject>(data)

        val sb = StringBuilder()

        sb.append("$name:\n```")

        sb.append(when(name) {
            // Add more when needed
            "animerec" -> jsonparser.animerec
            "bacon" -> jsonparser.bacon
            "commander" -> jsonparser.commander
            "calculate" -> jsonparser.calculate
            "hangman" -> jsonparser.hangman
            "help" -> jsonparser.help
            "gtn" -> jsonparser.gtn
            "nrk" -> jsonparser.nrk
            "ping" -> jsonparser.ping
            "pokelist" -> jsonparser.pokelist
            "pokemon" -> jsonparser.pokemon
            "roll" -> jsonparser.roll
            "tofflus" -> jsonparser.tofflus
            "wiki" -> jsonparser.wiki
            else -> "Error: Help not found. Is Command.kt updated?"
        })

        sb.append("```")

        return sb.toString()

    }

    private fun shouldReturnHelp(words : List<String>) : Boolean {
        if (words.size >= 2) {
            return words[1] == "help"
        }
        return false
    }

}