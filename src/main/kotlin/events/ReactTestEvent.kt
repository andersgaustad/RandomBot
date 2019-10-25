package events

import com.jessecorbett.diskord.api.model.Emoji
import com.jessecorbett.diskord.api.model.Message
import com.jessecorbett.diskord.api.model.User
import com.jessecorbett.diskord.api.websocket.events.MessageReaction
import core.DCW

class ReactTestEvent(private val messageToReactTo: Message?, private val emojiToReact: Emoji? = null) : Reactable {
    override fun onReactAdd(messageReaction: MessageReaction) {
        if (relevantReaction(messageReaction)) {
            listOfJoiningUsers.add(DCW.getUser(messageReaction.userId))
        }
    }

    override fun onReactRemove(messageReaction: MessageReaction) {
        if (relevantReaction(messageReaction)) {
            listOfJoiningUsers.remove(DCW.getUser(messageReaction.userId))
        }
    }

    val listOfJoiningUsers = mutableSetOf<User>()

    private fun relevantReaction(messageReaction: MessageReaction) : Boolean {
        return if (messageToReactTo != null) {
            val correctMessage = messageReaction.messageId == messageToReactTo.id
            if (emojiToReact == null) {
                correctMessage

            } else {
                correctMessage && messageReaction.emoji == emojiToReact
            }

        } else {
            false
        }


    }

}