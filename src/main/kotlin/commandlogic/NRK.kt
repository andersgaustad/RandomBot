package commandlogic

import com.jessecorbett.diskord.api.model.Message
import com.jessecorbett.diskord.util.words
import utils.isConnected
import java.net.HttpURLConnection
import java.net.URL

class NRK : Command() {
    override val name: String
        get() = "nrk"

    override fun parseMessage(message: Message): String {
        val words = message.words

        if (words.size >= 2) {
            // One argument
            val arg1 = words[1]
            val depth1 = arg1.toIntOrNull()

            if (words.size >= 3) {
                val arg2 = words[2]
                val depth2 = arg2.toIntOrNull()

                return if (depth2 != null) {
                    getNRKHeadlines(arg1, depth2)

                } else {
                    getNRKHeadlines(arg1)
                }

            } else {
                return if (depth1 != null) {
                    getNRKHeadlines("", depth1)

                } else {
                    getNRKHeadlines(arg1)
                }
            }

        } else {
            return getNRKHeadlines()

        }

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
        for (i in text.indices) {
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
