package commandlogic

import com.jessecorbett.diskord.api.model.Message
import com.jessecorbett.diskord.util.words
import json.JsonBaconObject
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.parse
import java.io.File

class Bacon() : Command() {
    override val name: String
        get() = "bacon"

    @ImplicitReflectionSerializer
    override fun parseMessage(message: Message): String {
        // Get words, and parse optional argument
        val words = message.words
        return if (words.size >= 2) {
            getRandomBaconVideoLink(words[1])
        } else {
            getRandomBaconVideoLink()
        }
    }

}

/*
DISCLAIMER: I do not own any of the videos fetched by using this command
All videos fetched with the command !bacon are the property of youtuber Bacon_
Bacon_'s youtube channel may be accessed by following the link below:
https://www.youtube.com/channel/UCcybVOrBgpzUxm-mlBT0WTA
*/
// NOTE: These videos are up to date as of 26th of September 2019
@ImplicitReflectionSerializer
fun getRandomBaconVideoLink(search : String = "") : String {
    val path = "src/main/resources/json/BaconLinks.json"

    val data = File(path).readText()

    val jsonparser = Json.parse<JsonBaconObject>(data)

    fun getJsonMatches(search : String) : ArrayList<String> {
        val all = ArrayList<String>()
        when (search) {
            "skyrim" -> all.addAll(jsonparser.skyrim)
            "oblivion" -> all.addAll(jsonparser.oblivion)
            else -> {
                // Get all tags
                val allTags = arrayOf(
                    "skyrim",
                    "oblivion"
                )

                // Add all tags to array
                for (tag in allTags) {
                    all.addAll(getJsonMatches(tag))
                }

            }
        }
        // Return the final list
        return all
    }

    // Fetch hits
    val listOfHits = getJsonMatches(search)

    return if (listOfHits.isNotEmpty()) {
        // Return a random element in this list, and strip it so we don't autoplay videos from list
        utils.getRandomStringInArrayList(listOfHits).substring(0, 43)

    } else {
        "Could not find any cool links!"
    }


}