package commandlogic

import java.io.File

const val rootPath = "src/main/resources/documentation/"
const val pathToBasicHelp = rootPath + "Help.txt"
const val pathToDevHelp = rootPath + "DevHelp.txt"

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

fun helpCommand(words : List<String>) : String {
    return if (words.size >= 2) {
        val arg2 = words[1]

        help(arg2 == "dev")

    } else {
        help()
    }

}