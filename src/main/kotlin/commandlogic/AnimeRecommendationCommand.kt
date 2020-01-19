package commandlogic

import com.jessecorbett.diskord.api.model.Message
import messageevents.AnimeRecommendationHandler

class AnimeRecommendationCommand(private val animeRecommendationHandler: AnimeRecommendationHandler) : Command() {
    override val name: String
        get() = "animerec"

    override fun parseMessage(message: Message): String {
        // Need to construct the whole tree. May be easier to just take the existing from bot if possible later
        return animeRecommendationHandler.beginAnimeRecommendation(message.author)
    }
}