package commandlogic

import utils.isConnected
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ThreadLocalRandom
import javax.net.ssl.HttpsURLConnection

fun getRandomGuardDialog () : String {
    val stringPrefix = "<td>\""

    val address = "https://en.uesp.net/wiki/Skyrim:Guard_Dialogue"

    val url = URL(address)

    if (isConnected(url.openConnection() as HttpURLConnection)) {
        val rawtext = url.readText().split("\n")
        val choices = ArrayList<String>()

        for (line in rawtext) {
            if (line.contains(stringPrefix) && !line.contains("href")) {
                choices.add(line.split("\"")[1])
            }
        }

        return if (choices.isNotEmpty()) {
            val index = ThreadLocalRandom.current().nextInt(0, choices.size)
            choices[index]

        } else {
            "Could not find anything cool to reply with"
        }

    } else {
        return "Could not connect to $address"
    }

}



