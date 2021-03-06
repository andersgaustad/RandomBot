package commandlogic

import com.jessecorbett.diskord.api.model.Message
import com.jessecorbett.diskord.util.words
import json.JsonPokemonObject
import json.JsonType1Object
import json.JsonType2Object
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.parse
import java.io.File
import java.util.concurrent.ThreadLocalRandom

class Pokemon : Command() {
    override val name: String
        get() = "pokemon"

    @ImplicitReflectionSerializer
    override fun parseMessage(message: Message): String {
        return getPokemonCommand(message.words, true)
    }

    @ImplicitReflectionSerializer
    fun getRandomPokemon(single : Boolean = true, type1 : String = "all", type2 : String = "all", generation : String = "all") : String {
        val pokemonJsonFilePath = "src/main/resources/json/Pokemon.json"

        val data = File(pokemonJsonFilePath).readText()

        val jsonparser = Json.parse<JsonPokemonObject>(data)

        // Function returning the whole list
        fun getJsonMatches(matchedType1 : String, matchedType2: String, matchedGeneration : String) : ArrayList<String> {
            val all = ArrayList<String>()

            // Function returing all relevant parse object given a generation
            fun getGenObjects(matchedGeneration : String) : ArrayList<JsonType1Object> {
                val allGenObjects = ArrayList<JsonType1Object>()

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

            // Getting the relevant generation
            val genObjects = getGenObjects(matchedGeneration)

            // Creating two lists:
            // 1. For "normal" fetching type1 type2
            // 2. For aliases using the format type2 type1
            // E.g. If a user wants water/flying pokemon, flying/water pokemon should be fetched as well
            val primaryType1Objects = ArrayList<JsonType2Object>()
            val secondaryType1Objects = ArrayList<JsonType2Object>()

            // Gets the pokemon given a type and a generation
            fun getType1Objects(type : String, genObject : JsonType1Object) : ArrayList<JsonType2Object> {
                val allType1Objects = ArrayList<JsonType2Object>()

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

            // Parse all genObjects
            // When searching within a specific generation
            for (genObject in genObjects) {
                primaryType1Objects.addAll(getType1Objects(matchedType1, genObject))

                // Only do this if types are different
                if (!identicalTypes) {
                    secondaryType1Objects.addAll(getType1Objects(matchedType2, genObject))
                }

            }

            fun getPokemonFromObject(lastType : String, typeObject : JsonType2Object) : ArrayList<String> {
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

        // Does the user want a single pokemon or a list?
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

            val message = sb.toString()
            return if (message.length<2000) {
                message

            } else {
                "Fetched too many pokemon! Discord won't let med post them all :frowning:"
            }
        }


    }

    @ImplicitReflectionSerializer
    fun getPokemonCommand(words : List<String>, single: Boolean = true) : String {
        if (words.size >= 2) {
            val arg1 = words[1]
            if (words.size >= 3) {
                val arg2 = words[2]
                if (words.size >= 4) {
                    // We should have all possible arguments present
                    val arg3 = words[3]
                    return getRandomPokemon(single, arg1, arg2, arg3)

                } else {
                    // Two arguments
                    // First should be type, other may be type or gen
                    return if (utils.genAliases.containsValue(arg2)) {
                        getRandomPokemon(single, arg1, arg1, arg2)

                    } else {
                        getRandomPokemon(single, arg1, arg2)

                    }

                }

            } else {
                // One argument, check if this is gen.
                // If not, assume it is type
                return if (utils.genAliases.containsValue(arg1)) {
                    getRandomPokemon(single, "all", "all", arg1)

                } else {
                    getRandomPokemon(single, arg1, arg1)
                }
            }

        } else {
            // No arguments, return default
            return getRandomPokemon(single)
        }
    }
}

class Pokelist : Command() {
    override val name: String
        get() = "pokelist"

    @ImplicitReflectionSerializer
    override fun parseMessage(message: Message): String {
        return Pokemon().getPokemonCommand(message.words, false)
    }

}