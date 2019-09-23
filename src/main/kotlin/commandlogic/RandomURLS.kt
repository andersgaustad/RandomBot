package commandlogic

import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ThreadLocalRandom
import javax.net.ssl.HttpsURLConnection

fun getRandomWikiArticle(): String {
    val linkPrefix = "<link rel=\"canonical\" href=\"https://"

    // Fetch the entire HTTPS text
    val rawLines = URL("https://en.wikipedia.org/wiki/Special:Random").readText().split("\n")

    // Repeating until we have found a link
    for (line in rawLines) {

        // Check if we have found a matching line
        if (line.contains(linkPrefix)) {
            // From the canonical line get the element that contains the link
            // This is the rightmost word (word #3 with index 2)
            val href = line.split(" ")[2]
            //println(href)

            // We are left with "href="link"\>
            // Spilt on " and take the second element with index 1 to get the link
            val link = href.split("\"")[1]

            if (link.contains("https://")) {
                return link
            }
        }

        //println("Line not found, checking next line...")
    }

    return "Could not find a link to wikipedia article :("

}

fun getRandomMTGCommanderCard() : String {
    // Fetch the entire text
    val redirect = URL("https://edhrec.com/random/").readText().split("\"")[1]

    val secureRedirect = "https://" + redirect.split("://")[1]

    // Find the image path
    fun getMTGImagePath(address : String) : String {
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

fun getRandomGuardDialog () : String {
    val stringPrefix = "<td>\""

    val address = "https://en.uesp.net/wiki/Skyrim:Guard_Dialogue"

    val url = URL(address)

    if (isConnected(url.openConnection() as HttpURLConnection)) {
        val rawtext = url.readText().split("\n")
        val choices = ArrayList<String>()

        for (line in rawtext) {
            if (line.contains(stringPrefix) && !line.contains("href")) {
                choices.add(line.split("\"")[1])
            }
        }

        return if (choices.isNotEmpty()) {
            val index = ThreadLocalRandom.current().nextInt(0, choices.size)
            choices[index]

        } else {
            "Could not find anything cool to reply with"
        }

    } else {
        return "Could not connect to $address"
    }

}

fun isConnected(connection : HttpURLConnection) : Boolean =
    connection.responseCode / 100 == 2