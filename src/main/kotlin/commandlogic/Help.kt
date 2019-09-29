package commandlogic

import com.jessecorbett.diskord.api.model.Message
import com.jessecorbett.diskord.util.words
import java.io.File

const val rootPath = "src/main/resources/documentation/"
const val pathToBasicHelp = rootPath + "Help.txt"
const val pathToDevHelp = rootPath + "DevHelp.txt"

class Help : Command() {
    override val name: String
        get() = "help"

    override fun parseMessage(message: Message): String {
        val words = message.words

        return if (words.size >= 2) {
            val arg2 = words[1]

            help(arg2 == "dev")

        } else {
            help()
        }
    }
}

fun help(devOptions : Boolean = false) : String {
    val allOptions = StringBuilder()
    allOptions.append("```\n")
    allOptions.append(File(pathToBasicHelp).readText())

    if (devOptions) {
        allOptions.append("\n\n")
        allOptions.append(File(pathToDevHelp).readText())
    }

    allOptions.append("\n```")

    return allOptions.toString()
}