package json

import kotlinx.serialization.Serializable

@Serializable
data class JsonPokemonObject(val gen1 : JsonType1Object,
                             val gen2 : JsonType1Object,
                             val gen3 : JsonType1Object,
                             val gen4 : JsonType1Object,
                             val gen5 : JsonType1Object)

@Serializable
data class JsonType1Object(val normal : JsonType2Object,
                           val fighting : JsonType2Object,
                           val flying : JsonType2Object,
                           val poison : JsonType2Object,
                           val ground : JsonType2Object,
                           val rock : JsonType2Object,
                           val bug : JsonType2Object,
                           val ghost : JsonType2Object,
                           val steel : JsonType2Object,
                           val fire : JsonType2Object,
                           val water : JsonType2Object,
                           val grass : JsonType2Object,
                           val electric : JsonType2Object,
                           val psychic : JsonType2Object,
                           val ice : JsonType2Object,
                           val dragon : JsonType2Object,
                           val dark : JsonType2Object,
                           val fairy : JsonType2Object)

@Serializable
data class JsonType2Object(val normal : Array<String> = Array(0){""},
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

        other as JsonType2Object

        if (!normal.contentEquals(other.normal)) return false

        return true
    }

    override fun hashCode(): Int {
        return normal.contentHashCode()
    }
}