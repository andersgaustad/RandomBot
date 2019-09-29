package commandlogic

import com.jessecorbett.diskord.api.model.Message
import java.net.URL

class Wiki : Command() {
    override val name: String
        get() = "wiki"

    override fun parseMessage(message: Message): String {
        return getRandomWikiArticle()
    }
}

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