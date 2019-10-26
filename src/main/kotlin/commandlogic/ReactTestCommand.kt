package commandlogic

import com.jessecorbett.diskord.api.model.Message
import events.ReactTestEvent

class ReactTestCommand : Command() {
    override val name: String
        get() = "react"

    override fun parseMessage(message: Message): String {
        return "React with :fire: to test"
    }

}