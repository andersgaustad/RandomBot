package commandlogic

import com.jessecorbett.diskord.api.model.Message
import com.jessecorbett.diskord.util.words
import messageevents.AnimeRecommendationHandler

class AnimeRecommendationCommand(private val animeRecommendationHandler: AnimeRecommendationHandler) : Command() {
    override val name: String
        get() = "animerec"

    override fun parseMessage(message: Message): String {
        val words = message.words
        return if (words.size == 1) {
            animeRecommendationHandler.beginAnimeRecommendation(message.author)

        } else {
            when(words[1]) {
                "random" -> animeRecommendationHandler.getAllAnimes().random().getDisplayMessage()
                else -> ""
            }
        }

    }
}