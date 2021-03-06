package utils

import java.io.File
import kotlin.system.measureTimeMillis


private const val lastGen = 5

private const val pokePrefix = "<td> <a href=\"/wiki/"
private const val generationPrefix = "id=\"Generation_"


class PokemonFixedParser : Parsing {
    override fun parseRawFile(fromPath: String, toPath: String) : String {
        // Measure time spent
        val time = measureTimeMillis {
            // The primary map for looking up types
            val genMapping = LinkedHashMap<Int, LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>>>()

            // Initializing genMapping to retain sorted relation
            // The JSON parser needs this to not crash
            for (g in genAliases.keys) {
                for (t1 in types) {
                    for (t2 in types) {
                        val genMap = genMapping.getOrPut(g){ LinkedHashMap() }
                        val type1Map = genMap.getOrPut(t1){ LinkedHashMap() }
                        type1Map.getOrPut(t2) { ArrayList() }
                    }
                }
            }


            val raw = File(fromPath).readLines()

            var gen = 0

            for (i in raw.indices) {
                val line = raw[i].trim()

                if (line.contains(generationPrefix)) {
                    gen++

                    if (gen > lastGen) {
                        break
                    }
                }

                if (line.contains(pokePrefix) && line.contains("Pokémon")) {
                    val linkAppendix = line.split("\"")[1]
                    val type1Line = raw[i+2].trim()
                    val type2Line = raw[i+3].trim()

                    fun getType(typeLine : String) : String {
                        return if (typeLine.toLowerCase().contains("type")) {
                            typeLine.split("title=\"")[1].split(" ")[0]

                        } else {
                            ""
                        }

                    }

                    // Type 1 is one of the valid types
                    val type1 = getType(type1Line).toLowerCase()
                    // Pokemon may only have one type.
                    // When this happens set type2 = type1 (e.g. (grass, grass))
                    val check = getType(type2Line).toLowerCase()
                    val type2 = if (check.isNotEmpty()) {
                        check
                    } else {
                        type1
                    }

                    // Kotlin magic
                    val primaryTypeMapping = genMapping.getOrPut(gen) {LinkedHashMap()}
                    val secondaryTypeMapping = primaryTypeMapping.getOrPut(type1) { LinkedHashMap() }
                    val links = secondaryTypeMapping.getOrPut(type2) { ArrayList() }

                    // Duplicates may occur!
                    // Burmy, Gastrodon, Jellicent, and Girantina are examples of pokemon with variants
                    // Although different forms, the links will be identical, so we only need one
                    if (!links.contains(linkAppendix)) {
                        links.add(linkAppendix)
                    }

                }

            }

            // After creating tree/map, parse this to a .json file
            var indents = 0

            // Clear contents of file
            val clear = File(toPath)
            clear.writeText("")

            // Function global out = file to write
            val out = File(toPath)

            fun appendFull(text : String)
                    = out.appendText(indent(indents) + text)

            // Begin writing
            appendFull("{\n")
            indents++

            // Write content for each generation
            for (genNumber in genMapping.keys) {
                // Add gen tag and info to file
                val genText = "\"${genAliases[genNumber]}\": {\n"
                appendFull(genText)
                indents++

                val genMap = genMapping[genNumber]

                if (genMap != null) {
                    for (primaryType in genMap.keys) {
                        // Write for first type
                        val primaryTypeText = "\"$primaryType\": {\n"
                        appendFull(primaryTypeText)
                        indents++

                        val primaryTypeMap = genMap[primaryType]

                        if (primaryTypeMap != null) {
                            for (secondaryType in primaryTypeMap.keys) {
                                // Write for second type
                                val secondaryTypeText = "\"$secondaryType\": [\n"
                                appendFull(secondaryTypeText)
                                indents++

                                val links = primaryTypeMap[secondaryType]

                                if (links != null) {
                                    for (link in links) {
                                        appendFull("\"$link\"")

                                        if (links.indexOf(link) != links.lastIndex) {
                                            out.appendText(",")
                                        }

                                        out.appendText("\n")

                                    }
                                }
                                indents--

                                appendFull("]")
                                if (primaryTypeMap.keys.indexOf(secondaryType) != primaryTypeMap.keys.size - 1) {
                                    out.appendText(",")
                                }
                                out.appendText("\n")
                            }
                        }
                        indents--

                        appendFull("}")
                        if (genMap.keys.indexOf(primaryType) != genMap.keys.size - 1) {
                            out.appendText(",")
                        }
                        out.appendText("\n")
                    }

                }
                indents--

                appendFull("}")
                if (genNumber < lastGen) {
                    out.appendText(",")
                }
                out.appendText("\n")

            }
            indents--

            appendFull("}")
        }

        return "Parsing complete! Took $time ms"

    }

    // This might be moved to a util class/package later
    private fun indent(indents : Int) : String {
        val sb = StringBuilder()
        if (indents > 0) {
            // Add one indent
            sb.append("  ")
            // You know what's fun? Recursion ;)
            sb.append(indent(indents - 1))

        }
        return sb.toString()
    }

}