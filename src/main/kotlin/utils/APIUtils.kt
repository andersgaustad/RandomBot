package utils

import com.jessecorbett.diskord.util.ClientStore
import com.jessecorbett.diskord.util.sendMessage

suspend fun sendMessage(text: String, channelId: String, clientStore: ClientStore) = clientStore.channels[channelId].sendMessage(text)