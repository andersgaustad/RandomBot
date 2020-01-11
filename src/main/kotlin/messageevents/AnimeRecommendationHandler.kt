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
                    val indexIfPresent = formattedOptions.indexOf(content.toLowerCase())

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
        val storyImportant = Node("Action or adventure?", arrayListOf("Road trip adventure", "Full blown action"))

        fights.attach(storyNotImportant, storyImportant)


        val hundredsOfEpisodes = Node("Pick your poison:", arrayListOf("Pirates", "Ninjas", "Spirit Samurai", "Mages", "Magical Girls", "Mechas", "Unfinished Greatness", "THE classic"))
        val aFew = Node("Setting?", arrayListOf("Trapped in a video game", "Fantasy", "Big city life"))

        storyNotImportant.attach(hundredsOfEpisodes, aFew)


        val onePiece = LeafNode("One Piece", "https://myanimelist.net/anime/21/One_Piece")
        val naruto = LeafNode("Naruto", "https://myanimelist.net/anime/20/Naruto")
        val bleach = LeafNode("Bleach", "https://myanimelist.net/anime/269/Bleach")
        val fairyTail = LeafNode("Fairy Tail", "https://myanimelist.net/anime/6702/Fairy_Tail")
        val prettyCure = LeafNode("Pretty Cure", "https://myanimelist.net/anime/603/Futari_wa_Precure")
        val gundam = LeafNode("Gundam", "https://myanimelist.net/anime/80/Mobile_Suit_Gundam")
        val hunterXHunter = LeafNode("Hunter X Hunter (2011)", "https://myanimelist.net/anime/11061/Hunter_x_Hunter_2011")
        val dragonBall = LeafNode("Dragon Ball", "https://myanimelist.net/anime/223/Dragon_Ball")

        hundredsOfEpisodes.attach(onePiece, naruto, bleach, fairyTail, prettyCure, gundam, hunterXHunter, dragonBall)


        val trappedInAVideoGame = Node("Choose your MMO", arrayListOf("The dumb popular one", "The boring smart one"))
        val fantasy = Node("Theme?", arrayListOf("Mankind's last stand", "Arabian nights (djinns)", "Dragons, adventure, and romance", "Medieval knights", "Anime Hogwarts", "Futuristic Hogwarts", "Spirit weapons"))
        val bigCityLife = Node("What drives your plot?", arrayListOf("Gang wars", "X-Men schoolgirls", "Life in a spirit town", "Spirit hunting", "Demon hunting"))

        aFew.attach(trappedInAVideoGame, fantasy, bigCityLife)


        val sao = LeafNode("Sword Art Online", "https://myanimelist.net/anime/11757/Sword_Art_Online")
        val logHorizon = LeafNode("Log Horizon", "https://myanimelist.net/anime/17265/Log_Horizon")

        trappedInAVideoGame.attach(sao, logHorizon)


        val attackOnTitan = LeafNode("Attack on Titan", "https://myanimelist.net/anime/16498/Shingeki_no_Kyojin")
        val magiLabyrinthOfMagic = LeafNode("Magi: The Labyrinth of Magic", "https://myanimelist.net/anime/14513/Magi__The_Labyrinth_of_Magic")
        val akatsukiNoYona = LeafNode("Akatsuko no Yona", "https://myanimelist.net/anime/25013/Akatsuki_no_Yona")
        val nanatsuNoTaizai = LeafNode("Nanatsu no Taizai", "https://myanimelist.net/anime/23755/Nanatsu_no_Taizai")
        val littleWitchAcademica = LeafNode("Little Witch Academica", "https://myanimelist.net/anime/33489/Little_Witch_Academia_TV")
        val mahoukaKoukounoRettousei = LeafNode("Mahouka Koukou no Rettousei", "https://myanimelist.net/anime/20785/Mahouka_Koukou_no_Rettousei")
        val soulEater = LeafNode("Soul Eater", "https://myanimelist.net/anime/3588/Soul_Eater")

        fantasy.attach(attackOnTitan, magiLabyrinthOfMagic, akatsukiNoYona, nanatsuNoTaizai, littleWitchAcademica, mahoukaKoukounoRettousei, soulEater)


        val durarara = LeafNode("Durarara!!", "https://myanimelist.net/anime/6746/Durarara")
        val aCertainScientificRailgun = LeafNode("A Certain Scientific Railgun", "https://myanimelist.net/anime/6213/Toaru_Kagaku_no_Railgun")
        val yozakuraQuartet = LeafNode("Yozakura Quartet: Hana no Uta", "https://myanimelist.net/anime/18497/Yozakura_Quartet__Hana_no_Uta")
        val noragami = LeafNode("Noragami", "https://myanimelist.net/anime/20507/Noragami")
        val blueExorcist = LeafNode("Blue Exorcist", "https://myanimelist.net/anime/9919/Ao_no_Exorcist")

        bigCityLife.attach(durarara, aCertainScientificRailgun, yozakuraQuartet, noragami, blueExorcist)


        val roadTripAdventure = Node("Theme?", arrayListOf("Journey into manhood through the mecha genre", "Steampunk scientific magic", "Space opera jazz", "'30s in Chicago", "Talking (&) Swords", "Aerial combat", "Hip-hop", "Historical", "Spirited Away into a fantasy drama"))
        val fullBlownAction = Node("Supernatural?", arrayListOf("Not really", "Superpowers", "Journey into manhood through the mecha genre", "Medieval fantasy", "Steampunk scientific magic", "Urban fantasy", "Martial arts", "Spirit Hunting", "Magical girls", "Science fiction", "Samurai"))

        storyImportant.attach(roadTripAdventure, fullBlownAction)


        val tengenToppaGurrenLagann = LeafNode("Tengen Toppa Gurren Lagann", "https://myanimelist.net/anime/2001/Tengen_Toppa_Gurren_Lagann")
        val fmab = LeafNode("Full Metal Alchemist: Brotherhood", "https://myanimelist.net/anime/5114/Fullmetal_Alchemist__Brotherhood")
        val cowboyBebop = LeafNode("Cowboy Bebop", "https://myanimelist.net/anime/1/Cowboy_Bebop")
        val baccano = LeafNode("Baccano!", "https://myanimelist.net/anime/2251/Baccano")
        val katanagatari = LeafNode("Katanagatari", "https://myanimelist.net/anime/6594/Katanagatari")
        val lastExile = LeafNode("Last Exile", "https://myanimelist.net/anime/97/Last_Exile")
        val samuraiChamploo = LeafNode("Samurai Champloo", "https://myanimelist.net/anime/205/Samurai_Champloo")
        val rurouniKenshin = LeafNode("Rurouni Kenshin", "https://myanimelist.net/anime/45/Rurouni_Kenshin__Meiji_Kenkaku_Romantan")

        roadTripAdventure.attach(tengenToppaGurrenLagann, fmab, cowboyBebop, baccano, katanagatari, lastExile, samuraiChamploo, rurouniKenshin)

        // TODO Continue here






        return root
    }
}
