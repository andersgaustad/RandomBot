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

fun getNRKHeadlines(search : String = "", depth : Int = 1) : String {
    if (depth <= 0) {
        return "Cannot fetch $depth results..."
    }

    val titleTag = "<title>"
    val linkTag = "<link>"

    val lastUpdatedStringBuilder = StringBuilder()
    lastUpdatedStringBuilder.append("Last updated ")
    val lastUpdatedTag = "<lastBuildDate>"

    val address = "https://www.nrk.no/toppsaker.rss"

    val results = ArrayList<String>(depth)

    val url = URL(address)

    // Local function for stripping tags:
    fun stripTags(raw : String, startTagLength : Int) : String {
        // Get the line without the starting tag
        val startStripped = raw.substring(startTagLength)
        // Get the line without the end tag from this
        return startStripped.substring(0, startStripped.length - startTagLength - 1)
    }

    if (isConnected(url.openConnection() as HttpURLConnection)) {
        val text = url.readText().split("\n")

        println("searching for $search")

        // Need the index for this one
        for (i in 0 until text.size) {
            val line = text[i].trim()

            if (line.contains(lastUpdatedTag)) {
                val stripped = stripTags(line, lastUpdatedTag.length)
                lastUpdatedStringBuilder.append(stripped)
                continue
            }

            if (line.contains(titleTag) && !line.contains("NRK")) {
                // Found a title
                val title = stripTags(line, titleTag.length).toLowerCase()
                val description = text[i+2].toLowerCase().trim()

                // Look if search term is present
                // It always contains the default search term ""
                if (title.contains(search.toLowerCase()) || description.contains(search.toLowerCase())) {
                    val link = stripTags(text[i+1].trim(), linkTag.length)
                    results.add(link)

                    // Check if we found as many searches as desired:
                    if (results.size >= depth) {
                        break
                    }

                }


            }


        }

        return if (results.isNotEmpty()) {
            val sb = StringBuilder()

            results.forEach {
                sb.append("\n${results.indexOf(it)+1}: $it")
            }

            "$lastUpdatedStringBuilder\nFound results: $sb"


        } else {
            "No matches found!"
        }

    } else {
        return "Could not connect to $address"
    }

}

fun isConnected(connection : HttpURLConnection) : Boolean =
    connection.responseCode / 100 == 2