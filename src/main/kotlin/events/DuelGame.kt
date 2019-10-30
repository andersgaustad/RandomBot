package events

import com.jessecorbett.diskord.api.model.Message
import com.jessecorbett.diskord.api.model.User

class DuelGame(private val messageRoot : Message?,  participatingUsers : List<User>) {

    private val remainingParticipants = participatingUsers.shuffled()

    // Done in one line
    data class DuelPair(val player1 : User, val player2 : User?)

    fun createPairs(remainingParticipants : List<User>) : List<DuelPair> {
        val pairs = ArrayList<DuelPair>(remainingParticipants.size / 2 + 1)
        val indexes = remainingParticipants.indices
        for (i in indexes) {
            if (i != indexes.last) {
                pairs.add(DuelPair(remainingParticipants[i], remainingParticipants[i+1]))

            } else {
                pairs.add(DuelPair(remainingParticipants[i], null))
            }
        }

        return pairs
    }

    var pairs = createPairs(remainingParticipants)

    fun gameIsWon() = remainingParticipants.size == 1

    fun getRemainingPlayer() = remainingParticipants[0]
    
}

