package utils

import java.net.HttpURLConnection

fun isConnected(connection : HttpURLConnection) : Boolean =
    connection.responseCode / 100 == 2