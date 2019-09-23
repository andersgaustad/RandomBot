package commandlogic

import com.jessecorbett.diskord.api.model.Message
import com.jessecorbett.diskord.util.mention
import com.jessecorbett.diskord.util.words
import java.util.concurrent.ThreadLocalRandom

fun roll(message: Message) : String {
    // Wrong format string:
    val wrongFormatString = "Wrong format; Roll command should be '!roll ndX'"

    // Check validity if command:
    val words = message.words
    if (words.size >= 2) {
        // Check if dice format is correct:
        val diceChoices = words[1].split("d")
        // Check that this is now a list of 2 elements, and that all can be casted to ints
        if (diceChoices.size == 2) {
            val numberOfDice = diceChoices[0].toIntOrNull()
            val diceSize =  diceChoices[1].toIntOrNull()

            if (numberOfDice != null && diceSize!= null) {
                // Check number of dice:
                if (numberOfDice > 0) {
                    // Check sides on dice
                    if (diceSize > 0) {
                        // Create a list of dice
                        val dice = Array(numberOfDice) {
                            ThreadLocalRandom.current().nextInt(1, diceSize+1)
                        }

                        val dicesToString = dice.joinToString { i->
                            "[$i] "
                        }.replace(" , ", " ")

                        // Return message
                        return "${message.author.mention} rolled: ${dicesToString}for a total of ${dice.sum()}"

                    } else {
                        return "Cannot roll dice of size $diceSize"


                    }

                } else {
                    return "Cannot roll $numberOfDice dice"


                }

            } else {
                return "Hmmm, I don't think I can roll ${diceChoices[0]}d${diceChoices[1]}..."

            }

        } else {
            return wrongFormatString
        }

    } else {
        return wrongFormatString

    }

}


