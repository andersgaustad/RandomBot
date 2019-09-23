package commandlogic

import java.net.HttpURLConnection
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

fun getNRKSearches(search : String, matches : Int = 3) : String {
    val linkPrefix = "<a href=\"/"
    val rootAddress = "https://tv.nrk.no"

    val nrkSearch = "$rootAddress/sok?q=$search"
    val url = URL(nrkSearch)
    val connection = url.openConnection() as HttpURLConnection
    println("Connecting...")
    connection.requestMethod = "GET"
    println("Connected with response ${connection.responseCode} ${connection.responseMessage}")

    // Okay, this check looks kinda weird, but let me explain
    // The response code is 200-299 when the GET request is successful
    // Therefore dividing by 100 and comparing to 2 should return if this was a success or not
    if ((connection.responseCode / 100) == 2) {
        // Successfully connected
        val links = ArrayList<String>()

        // Need a local function for conversion from list to string
        fun formatSearchListToString(search : String, links : ArrayList<String>) : String {
            return if (links.isNotEmpty()) {
                // Yes, this is valid in Kotlin...
                val sb = StringBuilder()
                links.forEach {
                    sb.append("\n$it")
                }
                "Found the following results for '$search: ${sb.toString()}"

            } else {
                "No result found for search term $search"
            }

        }

        val br = connection.inputStream.bufferedReader()
        var i = 0
        for (rawLine in br.lines()) {
            val line = rawLine.trim()
            println("$i: $line")
            i++
            if (line.contains(linkPrefix) && line.contains("class=\"tv-search-image")) {
                print("Added line $line")
                val href = line.split(" ")[1]
                print(" -> $href")
                val link = rootAddress + href.split("\"")[1]
                print(" -> $link")
                println()
                println("Uhm Kotlin?")
                links.add(link)
                println("List has now ${links.size} elements")

                // We only find the first "matches" results (default 3)
                if (links.size >= matches) {
                    println("Breaking!")
                    break
                }
            }
        }

        // Return the formatted list to string
        return formatSearchListToString(search, links)

    } else {
        return "Error: Could not connect to $nrkSearch\n" +
                "Response was ${connection.responseCode}: ${connection.responseMessage}"
    }

}