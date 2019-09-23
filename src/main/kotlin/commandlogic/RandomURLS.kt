package commandlogic

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

fun getRandomPokemon() : String {
    // The prefixes we are looking for:
    val prefix = "<a href=\"/wiki/"

    // Adding each link to a pokemon to an arraylist
    val pokelinks = ArrayList<String>()

    println("Connecting")
    val raw = URL("view-source:https://bulbapedia.bulbagarden.net/wiki/List_of_Pok%C3%A9mon_by_National_Pok%C3%A9dex_number").readText()
    println(raw)

    val connection = URL("https://bulbapedia.bulbagarden.net/wiki/List_of_Pok%C3%A9mon_by_National_Pok%C3%A9dex_number").openConnection() as HttpsURLConnection
    connection.requestMethod = "GET"

    // Construct list of all pokemons
    val br = connection.inputStream.bufferedReader()
    for (line in br.lines()) {
        //println("Found line $line")
        if (line.contains(prefix)) {
            // Get href element of line
            val href = line.split(" ")[1]
            val link = href.split("\"")[1]
            pokelinks.add(link)

        }
    }

    // Kotlin magic :O
    return if (pokelinks.isNotEmpty()) {
        val index = ThreadLocalRandom.current().nextInt(0, pokelinks.size)
        pokelinks[index]

    } else {
        "Hmmm, something went wrong..."
    }

}