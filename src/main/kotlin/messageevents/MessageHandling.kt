@file:Suppress("EXPERIMENTAL_API_USAGE")

package messageevents

import com.jessecorbett.diskord.api.model.Message
import com.jessecorbett.diskord.dsl.Bot
import kotlinx.serialization.UnstableDefault

interface MessageHandling {

    @UnstableDefault
    suspend fun onMessageCreated(message : Message, bot : Bot)

}