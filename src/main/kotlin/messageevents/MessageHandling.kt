package messageevents

import com.jessecorbett.diskord.api.model.Message
import com.jessecorbett.diskord.dsl.Bot

interface MessageHandling {

    suspend fun onMessageCreated(message : Message)

}