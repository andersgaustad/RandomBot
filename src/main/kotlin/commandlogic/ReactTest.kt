package commandlogic

import com.jessecorbett.diskord.api.model.Message
import events.ReactTestEvent

class ReactTestCommand : Command() {
    override val name: String
        get() = "react"

    override fun parseMessage(message: Message): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    //fun createReactTestEvent() = ReactTestEvent()
}