package events

import com.jessecorbett.diskord.api.model.Message
import com.jessecorbett.diskord.api.model.User
import com.jessecorbett.diskord.util.mention
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

class DuelGame(participatingUsers : List<User>) {

    private var remainingParticipants : MutableList<User> = participatingUsers.shuffled().toMutableList()

    // Done in one line
    data class DuelPair(val player1 : User, val player2 : User?) {
        fun contains(player : User) = (player == player1 || player == player2)
    }

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

    var active = false

    fun getCurrentPair() = pairs[pairIndex]

    fun createDuelMessage() : String {
        val sb = StringBuilder()
        sb.append("Duel Game: ")
        val currentDuelPair = getCurrentPair()
        if (currentDuelPair.player2 != null) {
            sb.append("${currentDuelPair.player1.mention} :crossed_swords: ${currentDuelPair.player2.mention}")

        } else if (!gameIsWon()) {
            sb.append("${currentDuelPair.player1.mention} has no opponent and automatically qualifies for next round...")

        } else {
            sb.append(":tada: :tada: ${getRemainingPlayer().mention} wins! :tada: :tada:")
        }

        return sb.toString()
    }

    fun declareDuelWinner(winner : User) {
        val currentDuelPair = getCurrentPair()
        if (currentDuelPair.contains(winner)) {
            when (winner) {
                currentDuelPair.player1 -> remainingParticipants.remove(currentDuelPair.player2)
                currentDuelPair.player2 -> remainingParticipants.remove(currentDuelPair.player1)
                else -> {}
            }

            // Iterate, if more duels in this bracket, or move on to the next
            if (pairIndex < pairs.lastIndex) {
                // Iterate
                pairIndex++

            } else {
                // Create new bracket, and reset index
                pairs = createPairs(remainingParticipants)
                pairIndex = 0
            }


        }

    }

    fun countdown() : String {
        val waitingSeconds = ThreadLocalRandom.current().nextDouble(10.0)
        Timer().schedule((waitingSeconds * 1000).toLong()) {
            active = true
        }
        return "Fire!"
    }

    fun deactivate() {
        active = false
    }

    fun gameIsWon() = remainingParticipants.size == 1

    fun getRemainingPlayer() = remainingParticipants[0]
    
}

