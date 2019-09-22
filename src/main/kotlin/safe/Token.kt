package safe

import java.io.File

private val BOT_TOKEN = readToken()

fun getToken(): String = BOT_TOKEN

private fun readToken(path: String="src/main/resources/safe/TokenKey.txt"): String
        = File(path).readLines()[0]