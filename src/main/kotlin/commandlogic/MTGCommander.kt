package commandlogic

import com.jessecorbett.diskord.api.model.Message
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class MTGCommander : Command() {
    override val name: String
        get() = "commander"

    override fun parseMessage(message: Message): String {
        return getRandomMTGCommanderCard()
    }
}

fun getRandomMTGCommanderCard(): String {
    // Fetch the entire text
    val redirect = URL("https://edhrec.com/random/").readText().split("\"")[1]

    val secureRedirect = "https://" + redirect.split("://")[1]

    // Find the image path
    fun getMTGImagePath(address: String): String {
        val connection = URL(address).openConnection() as HttpsURLConnection
        connection.requestMethod = "GET"

        val br = connection.inputStream.bufferedReader()
        for (line in br.lines()) {
            if (line.trim().contains("oneimage")) {
                return line.split("\"")[3]
            }
        }

        return "Could not find image path?"
    }

    val imagePath = getMTGImagePath(secureRedirect)
    return "$secureRedirect\n$imagePath"

}