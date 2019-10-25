package events

import com.jessecorbett.diskord.api.websocket.events.MessageReaction

interface Reactable {
    fun onReactAdd(messageReaction: MessageReaction)
    fun onReactRemove(messageReaction: MessageReaction)
}