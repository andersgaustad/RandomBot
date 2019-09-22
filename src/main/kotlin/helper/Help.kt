package helper

import java.io.File

fun help(path : String = "src/main/resources/documentation/Help.txt") : String
    = File(path).readText()