package events

import com.jessecorbett.diskord.api.model.Message
import com.jessecorbett.diskord.api.model.User

class DuelGame(participatingUsers : List<User>) {

    private var remainingParticipants : MutableList<User> = participatingUsers.shuffled().toMutableList()

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
    var pairIndex = 0

    fun declareDuelWinner(winner : User) {
        val currentDuelPair = pairs[pairIndex]
        when(winner) {
            currentDuelPair.player1 -> remainingParticipants.remove(currentDuelPair.player2)
            currentDuelPair.player2 -> remainingParticipants.remove(currentDuelPair.player1)
            else -> {}
        }
    }

    fun gameIsWon() = remainingParticipants.size == 1

    fun getRemainingPlayer() = remainingParticipants[0]
    
}

