package json

import kotlinx.serialization.Serializable

@Serializable
data class JsonPokemonParser(val gen1 : JsonType1Parser,
                          val gen2 : JsonType1Parser,
                          val gen3 : JsonType1Parser,
                          val gen4 : JsonType1Parser,
                          val gen5 : JsonType1Parser)

@Serializable
data class JsonType1Parser(val normal : JsonType2Parser,
                            val fighting : JsonType2Parser,
                            val flying : JsonType2Parser,
                            val poison : JsonType2Parser,
                            val ground : JsonType2Parser,
                            val rock : JsonType2Parser,
                            val bug : JsonType2Parser,
                            val ghost : JsonType2Parser,
                            val steel : JsonType2Parser,
                            val fire : JsonType2Parser,
                            val water : JsonType2Parser,
                            val grass : JsonType2Parser,
                            val electric : JsonType2Parser,
                            val psychic : JsonType2Parser,
                            val ice : JsonType2Parser,
                            val dragon : JsonType2Parser,
                            val dark : JsonType2Parser,
                            val fairy : JsonType2Parser)

@Serializable
data class JsonType2Parser(val normal : Array<String> = Array(0){""},
                           val fighting : Array<String> = Array(0){""},
                           val flying : Array<String> = Array(0){""},
                           val poison : Array<String> = Array(0){""},
                           val ground : Array<String> = Array(0){""},
                           val rock : Array<String> = Array(0){""},
                           val bug : Array<String> = Array(0){""},
                           val ghost : Array<String> = Array(0){""},
                           val steel : Array<String> = Array(0){""},
                           val fire : Array<String> = Array(0){""},
                           val water : Array<String> = Array(0){""},
                           val grass : Array<String> = Array(0){""},
                           val electric : Array<String> = Array(0){""},
                           val psychic : Array<String> = Array(0){""},
                           val ice : Array<String> = Array(0){""},
                           val dragon : Array<String> = Array(0){""},
                           val dark : Array<String> = Array(0){""},
                           val fairy : Array<String> = Array(0){""}) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as JsonType2Parser

        if (!normal.contentEquals(other.normal)) return false

        return true
    }

    override fun hashCode(): Int {
        return normal.contentHashCode()
    }
}