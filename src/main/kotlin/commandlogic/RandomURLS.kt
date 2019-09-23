package commandlogic

import java.net.URL
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