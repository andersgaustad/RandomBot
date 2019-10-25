package discordclient

import com.jessecorbett.diskord.api.DiscordUserType
import com.jessecorbett.diskord.api.rest.client.DiscordClient
import kotlinx.coroutines.runBlocking

class DiscordClientWrapper(token : String, userType: DiscordUserType = DiscordUserType.BOT) {
    private val discordClient = DiscordClient(token, userType)

    fun getUser(userId : String) = runBlocking {
        discordClient.getUser(userId)
    }
}