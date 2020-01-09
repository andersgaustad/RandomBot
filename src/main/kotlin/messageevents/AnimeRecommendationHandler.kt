package messageevents

import com.jessecorbett.diskord.api.model.Message
import com.jessecorbett.diskord.api.model.User
import com.jessecorbett.diskord.dsl.Bot
import com.jessecorbett.diskord.util.ClientStore
import com.jessecorbett.diskord.util.sendMessage
import kotlinx.serialization.UnstableDefault

/**
The purpose of this class is to recommend an anime to a user based on the user's answers to a series of question.
 In practice, the user goes through a flowchart which is constructed using the "The ultimate anime recommendation flowchart" by reddit user u/lukeatlook
 Disclaimer: I do not own this, and all credits go to u/lukeatlook
 The flowchart may be found at https://imgur.com/q9Xjv4p, and Luke's profile may be found at https://www.reddit.com/user/lukeatlook
 */

@Suppress("EXPERIMENTAL_API_USAGE")
class AnimeRecommendationHandler : MessageHandling {
    @UnstableDefault
    override suspend fun onMessageCreated(message : Message, bot : Bot) {
        val user = message.author
        val currentNode = map[user]

        // Check if we hit a valid node
        if (currentNode != null) {
            val content = message.content
            // If user has typed number
            val possibleNumber = content.toIntOrNull()

            // Parse possible number
            // Local function
            suspend fun chooseOption(index : Int) {
                // Let kotlin handle exceptions
                val nextNode = currentNode.getChild(index)

                // Display next question
                sendMessage(nextNode.getDisplayMessage(), message.channelId, bot.clientStore)

                // Advance through tree if not final
                if (!nextNode.isLeafNode()) {
                    map[user] = nextNode

                } else {
                    // We hit a leaf node. No more nodes should be fetched
                    // Clean up for user
                    map.remove(user)
                }
            }
            // End of local function

            // Check if message is number
            if (possibleNumber != null) {
                // User gives number as 1-indexed, not 0-indexed
                // Need to subtract 1
                chooseOption(possibleNumber-1)

            } else {
                // Check if option is written as string
                // Format options to non case sensitive
                val options = currentNode.options
                val formattedOptions = ArrayList<String>(options.size)
                options.forEach { formattedOptions.add(it.toLowerCase()) }

                    // Check if option is present. This returns -1 if it is not
                    val indexIfPresent = formattedOptions.indexOf(content)

                    // If option was found, pass option index
                    if (indexIfPresent != -1) {
                        chooseOption(indexIfPresent)
                    }
            }
        }


    }




    // Top 10 workarounds
    // #1
    private suspend fun sendMessage(text: String, channelId: String, clientStore: ClientStore) = clientStore.channels[channelId].sendMessage(text)

    private val map = mutableMapOf<User, Node>()

    private val root = createTree()

    fun beginAnimeRecommendation(user : User) : String {
        // Set user node to root
        map[user] = root

        return "Hi ${user.username}, do you want me to recommend an anime? If so, please answer the following statements"
    }

    fun getRootDisplayMessage() = root.getDisplayMessage()

    private fun createTree() : Node {
        // Root
        val root = Node("I am looking for something...", arrayListOf("Dynamic", "Funny", "Calm", "Moving", "Mature", "Intense", "Scary"))

        // Action/adventure
        val actionAdventure = Node("What kind of action?", arrayListOf("Fights", "Sports", "PURE FUN!"))
        root.attach(actionAdventure)


        val fights = Node("Finished story?", arrayListOf("It doesn't matter", "Yes, I want it to have a conclusive finale"))
        actionAdventure.attach(fights)


        val storyNotImportant = Node("How many episodes?", arrayListOf("Hundreds!", "A few"))
        fights.attach(storyNotImportant)


        val hundredsOfEpisodes = Node("Pick yout poison:", arrayListOf("Pirates", "Ninjas", "Spirit Samurai", "Mages", "Magical Girls", "Mechas", "Unfinished Greatness", "THE classic"))
        val aFew = Node("Setting?", arrayListOf("Trapped in a video game", "Fantasy", "Big city life"))

        storyNotImportant.apply {
            attach(hundredsOfEpisodes)
            attach(aFew)
        }


        val onePiece = HyperlinkLeafNode("One Piece", "https://en.wikipedia.org/wiki/One_Piece")
        val naruto = HyperlinkLeafNode("Naruto", "https://en.wikipedia.org/wiki/Naruto")
        val bleach = HyperlinkLeafNode("Bleach", "https://en.wikipedia.org/wiki/Bleach_(TV_series)")
        val fairyTail = HyperlinkLeafNode("Fairy Tail", "https://en.wikipedia.org/wiki/Fairy_Tail")
        val prettyCure = HyperlinkLeafNode("Pretty Cure", "https://en.wikipedia.org/wiki/Pretty_Cure")
        val gundam = HyperlinkLeafNode("Gundam", "https://en.wikipedia.org/wiki/Pretty_Cure")
        val hunterXHunter = HyperlinkLeafNode("Hunter X Hunter (2011)", "https://en.wikipedia.org/wiki/Hunter_%C3%97_Hunter_(2011_TV_series)")
        val dragonBall = HyperlinkLeafNode("Dragon Ball", "https://en.wikipedia.org/wiki/Dragon_Ball")

        hundredsOfEpisodes.apply {
            attach(onePiece)
            attach(naruto)
            attach(bleach)
            attach(fairyTail)
            attach(prettyCure)
            attach(gundam)
            attach(hunterXHunter)
            attach(dragonBall)
        }


        val trappedInAVideoGame = Node("Choose your MMO", arrayListOf("The dumb popular one", "The boring smart one"))
        val fantasy = Node("Theme?", arrayListOf("Mankind's last stand", "Arabian nights (djinns)", "Dragons, adventure, and romance", "Medieval knights", "Anime Hogwarts", "Futuristic Hogwarts", "Spirit weapons"))

        // TODO("Continue here")


        return root
    }
}
