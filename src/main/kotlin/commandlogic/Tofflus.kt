package commandlogic

import com.jessecorbett.diskord.api.model.Message
import utils.getDateEnd
import java.time.LocalDate

class Tofflus : Command() {
    override val name: String
        get() = "tofflus"

    override fun parseMessage(message: Message): String {
        val sb = StringBuilder()

        val date = LocalDate.now()
        val dayOfMonth = date.dayOfMonth
        val month = date.monthValue

        val december = 12

        // Checking if it is december
        if (month == december) {
            // It is december
            if (dayOfMonth in 1..24) {
                // It is soon christmas!
                if (dayOfMonth == 24) {
                    sb.append(":sparkles: :sparkles: Merry Christmas! :sparkles: :sparkles:\n")

                } else {
                    sb.append("Today is the $dayOfMonth${getDateEnd(dayOfMonth)} of December, and there are ${24-dayOfMonth} days until Christmas\n")
                }

                sb.append("https://tv.nrk.no/serie/jul-i-skomakergata/sesong/1/episode/$dayOfMonth/avspiller")

            } else {
                sb.append("Christmas has passed, try again next year!")
            }

        } else {
            sb.append("It is not December yet!\n")
            sb.append("There are still ${december-month} months until December")

        }

        return sb.toString()
    }
}