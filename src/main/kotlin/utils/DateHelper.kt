package utils

fun getDateEnd(date : Int) : String {
    return when(date) {
        1 -> "st"
        2 -> "nd"
        3 -> "rd"
        else -> "th"
    }
}