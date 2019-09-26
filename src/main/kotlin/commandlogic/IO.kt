package commandlogic

import core.JSONParser
import core.JSONTest
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.parse
import java.io.File

// Serializing over json file
@ImplicitReflectionSerializer

/*
 DISCLAIMER: I do not own any of the videos fetched by using this command
 All videos fetched with the command !bacon are the property of youtuber Bacon_
 Bacon_'s youtube channel may be accessed by following the link below:
 https://www.youtube.com/channel/UCcybVOrBgpzUxm-mlBT0WTA
 */
// NOTE: These videos are up to date as of 26th of September 2019
fun getRandomBaconVideoLink(search : String = "") : String {
    val path = "src/main/resources/json/BaconLinks.json"

    val data = File(path).readText()

    val jsonparser = Json.parse<JSONParser>(data)

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
        // Return a random element in this list
        utils.getRandomStringInArrayList(listOfHits)

    } else {
        "Could not find any cool links!"
    }


}