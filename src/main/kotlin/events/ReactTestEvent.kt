package events

import com.jessecorbett.diskord.api.model.Message
import com.jessecorbett.diskord.api.model.Reaction
import com.jessecorbett.diskord.api.websocket.events.MessageReaction

class ReactTestEvent(val messageToReactTo: Message, vararg relevantReactions: Reaction? = emptyArray()) : Reactable {
    override fun onReactAdd(messageReaction: MessageReaction) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onReactRemove(messageReaction: MessageReaction) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun relevantReaction(messageReaction: MessageReaction) {
        //val correctMessage = messageReaction.userId

    }


}