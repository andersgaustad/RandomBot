package commandlogic

import com.jessecorbett.diskord.api.model.Message
import kotlinx.serialization.ImplicitReflectionSerializer

class Ping : Command() {
    override val name: String
        get() = "ping"

    @ImplicitReflectionSerializer
    override fun parseMessage(message: Message): String {
        return "pong!"
    }
}