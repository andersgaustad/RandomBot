package commandlogic

import com.jessecorbett.diskord.api.model.Message

class DuelGameCommand : Command() {
    val duelGameCreatedMessage = "Duel Game created!\\nReact with :gun: to join"

    override val name: String
        get() = "duel"

    override fun parseMessage(message: Message): String {
        return duelGameCreatedMessage
    }
}