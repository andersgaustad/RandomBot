package json

import kotlinx.serialization.Serializable

@Serializable
data class JsonBaconObject(val skyrim : Array<String> = Array<String>(0){""}, val oblivion : Array<String> = Array<String>(0){""}) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as JsonBaconObject

        if (!skyrim.contentEquals(other.skyrim)) return false
        if (!oblivion.contentEquals(other.oblivion)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = skyrim.contentHashCode()
        result = 31 * result + oblivion.contentHashCode()
        return result
    }

}

@Serializable
data class JSONTest(val a: Int, val b: String ="42")