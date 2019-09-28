package utils

val genAliases = mapOf(
    1 to "gen1",
    2 to "gen2",
    3 to "gen3",
    4 to "gen4",
    5 to "gen5"
)

val types = arrayOf(
    "normal",
    "fighting",
    "flying",
    "poison",
    "ground",
    "rock",
    "bug",
    "ghost",
    "steel",
    "fire",
    "water",
    "grass",
    "electric",
    "psychic",
    "ice",
    "dragon",
    "dark",
    "fairy"
)

interface Parsing {
    fun parseRawFile(fromPath : String, toPath : String) : String

}