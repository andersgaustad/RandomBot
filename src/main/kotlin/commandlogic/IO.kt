package commandlogic

import json.JsonBaconParser
import json.JsonPokemonParser
import json.JsonType1Parser
import json.JsonType2Parser
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.parse
import java.io.File
import java.util.concurrent.ThreadLocalRandom

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

    val jsonparser = Json.parse<JsonBaconParser>(data)

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

@ImplicitReflectionSerializer
fun getRandomPokemon(single : Boolean = true, type1 : String = "all", type2 : String = "all", generation : String = "all") : String {
    val pokemonJsonFilePath = "src/main/resources/json/Pokemon.json"

    val data = File(pokemonJsonFilePath).readText()

    try {
        Json.parse<JsonPokemonParser>(data)
    } catch(e: Exception) {
        e.printStackTrace()
        return "Something went wrong!"
    }

    val jsonparser = Json.parse<JsonPokemonParser>(data)

    fun getJsonMatches(matchedType1 : String, matchedType2: String, matchedGeneration : String) : ArrayList<String> {
        val all = ArrayList<String>()

        fun getGenObjects(matchedGeneration : String) : ArrayList<JsonType1Parser> {
            val allGenObjects = ArrayList<JsonType1Parser>()

            when(matchedGeneration) {
                "gen1" -> allGenObjects.add(jsonparser.gen1)
                "gen2" -> allGenObjects.add(jsonparser.gen2)
                "gen3" -> allGenObjects.add(jsonparser.gen3)
                "gen4" -> allGenObjects.add(jsonparser.gen4)
                "gen5" -> allGenObjects.add(jsonparser.gen5)
                else -> {
                    for (key in utils.genAliases.keys) {
                        // Add generations to the list
                        utils.genAliases[key]?.let { value ->
                            getGenObjects(value) }?.let {
                            allGenObjects.addAll(it) }
                    }
                }
            }

            return allGenObjects

        }

        val genObjects = getGenObjects(matchedGeneration)

        val primaryType1Objects = ArrayList<JsonType2Parser>()
        val secondaryType1Objects = ArrayList<JsonType2Parser>()

        fun getType1Objects(type : String, genObject : JsonType1Parser) : ArrayList<JsonType2Parser> {
            val allType1Objects = ArrayList<JsonType2Parser>()

            when(type) {
                "normal" -> allType1Objects.add(genObject.normal)
                "fighting" -> allType1Objects.add(genObject.fighting)
                "flying" -> allType1Objects.add(genObject.flying)
                "poison" -> allType1Objects.add(genObject.poison)
                "ground" -> allType1Objects.add(genObject.ground)
                "rock" -> allType1Objects.add(genObject.rock)
                "bug" -> allType1Objects.add(genObject.bug)
                "ghost" -> allType1Objects.add(genObject.ghost)
                "steel" -> allType1Objects.add(genObject.steel)
                "fire" -> allType1Objects.add(genObject.fire)
                "water" -> allType1Objects.add(genObject.water)
                "grass" -> allType1Objects.add(genObject.grass)
                "electric" -> allType1Objects.add(genObject.electric)
                "psychic" -> allType1Objects.add(genObject.psychic)
                "ice" -> allType1Objects.add(genObject.ice)
                "dragon" -> allType1Objects.add(genObject.dragon)
                "dark" -> allType1Objects.add(genObject.dark)
                "fairy" -> allType1Objects.add(genObject.fairy)
                else -> {
                    // Add all types to the arraylist
                    for (lookedUpType in utils.types) {
                        allType1Objects.addAll(getType1Objects(lookedUpType, genObject))
                    }
                }
            }

            return allType1Objects

        }

        // If we have to identical types, there is no need to add or check the secondary type
        val identicalTypes = matchedType1 == matchedType2

        for (genObject in genObjects) {
            primaryType1Objects.addAll(getType1Objects(matchedType1, genObject))

            // Only do this if types are different
            if (!identicalTypes) {
                secondaryType1Objects.addAll(getType1Objects(matchedType2, genObject))
            }

        }

        fun getPokemonFromObject(lastType : String, typeObject : JsonType2Parser) : ArrayList<String> {
            val allPokemon = ArrayList<String>()

            when(lastType) {
                "normal" -> allPokemon.addAll(typeObject.normal)
                "fighting" -> allPokemon.addAll(typeObject.fighting)
                "flying" -> allPokemon.addAll(typeObject.flying)
                "poison" -> allPokemon.addAll(typeObject.poison)
                "ground" -> allPokemon.addAll(typeObject.ground)
                "rock" -> allPokemon.addAll(typeObject.rock)
                "bug" -> allPokemon.addAll(typeObject.bug)
                "ghost" -> allPokemon.addAll(typeObject.ghost)
                "steel" -> allPokemon.addAll(typeObject.steel)
                "fire" -> allPokemon.addAll(typeObject.fire)
                "water" -> allPokemon.addAll(typeObject.water)
                "grass" -> allPokemon.addAll(typeObject.grass)
                "electric" -> allPokemon.addAll(typeObject.electric)
                "psychic" -> allPokemon.addAll(typeObject.psychic)
                "ice" -> allPokemon.addAll(typeObject.ice)
                "dragon" -> allPokemon.addAll(typeObject.dragon)
                "dark" -> allPokemon.addAll(typeObject.dark)
                "fairy" -> allPokemon.addAll(typeObject.fairy)
                else -> {
                    // Add all types to the arraylist
                    for (lookedUpType in utils.types) {
                        allPokemon.addAll(getPokemonFromObject(lookedUpType, typeObject))
                    }
                }
            }

            return allPokemon
        }


        for (firstTypeObject in primaryType1Objects) {
            all.addAll(getPokemonFromObject(matchedType2, firstTypeObject))
        }

        for (secondTypeObject in secondaryType1Objects) {
            all.addAll(getPokemonFromObject(matchedType1, secondTypeObject))
        }

        return all

    }

    val all = getJsonMatches(type1, type2, generation)

    val bulbapediaRoot = "https://bulbapedia.bulbagarden.net"


    if (single) {
        val index = ThreadLocalRandom.current().nextInt(0, all.size)
        return bulbapediaRoot + all[index]

    } else {
        val sb = StringBuilder()
        sb.append("Matched pokemon:")

        var i = 1
        for (finalPokemonLink in all) {
            sb.append("\n $i: $bulbapediaRoot$finalPokemonLink")
            i++
        }

        return sb.toString()
    }


}