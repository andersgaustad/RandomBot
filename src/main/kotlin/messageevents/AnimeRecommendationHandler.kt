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

    private val root = setUpTree()

    fun beginAnimeRecommendation(user : User) : String {
        // Set user node to root
        map[user] = root

        return "Hi ${user.username}, do you want me to recommend an anime? If so, please answer the following statements"
    }

    fun getRootDisplayMessage() = root.getDisplayMessage()

    private fun setUpTree() : Node {
        // Root
        val root = Node("I am looking for something...", arrayListOf("Dynamic", "Funny", "Calm", "Moving", "Mature", "Intense", "Scary"))

        root.attach(ActionTree().createTree())

        return root
    }
}

// Tree data
interface TreeCreating {
    fun createTree() : Node
}

class ActionTree : TreeCreating {
    override fun createTree(): Node {
        // Action/adventure
        val actionAdventure = Node("What kind of action?", arrayListOf("Fights", "Sports", "PURE FUN!"))


        val fights =
            Node("Finished story?", arrayListOf("It doesn't matter", "Yes, I want it to have a conclusive finale"))
        val sports = Node("Oldschool or newschool?", arrayListOf("Fresh classic", "Classics", "Fresh stuff"))
        val pureFun = Node(
            "Ready for the joyride?",
            arrayListOf(
                "Hell yeah",
                "HELL YEAH!",
                "Eyegasm",
                "Eargasm",
                "Foodgasm",
                "Cartoon",
                "Tribute to superheroes",
                "Zombie apocalypse",
                "How stupid can it get?",
                "Oldschool comic style"
            )
        )
        actionAdventure.attach(fights, sports, pureFun)


        val storyNotImportant = Node("How many episodes?", arrayListOf("Hundreds!", "A few"))
        val storyImportant = Node("Action or adventure?", arrayListOf("Road trip adventure", "Full blown action"))

        fights.attach(storyNotImportant, storyImportant)


        val hundredsOfEpisodes = Node(
            "Pick your poison:",
            arrayListOf(
                "Pirates",
                "Ninjas",
                "Spirit Samurai",
                "Mages",
                "Magical Girls",
                "Mechas",
                "Unfinished Greatness",
                "THE classic"
            )
        )
        val aFew = Node("Setting?", arrayListOf("Trapped in a video game", "Space", "Fantasy", "Big city life"))

        storyNotImportant.attach(hundredsOfEpisodes, aFew)


        val onePiece = LeafNode("One Piece", "https://myanimelist.net/anime/21/One_Piece")
        val naruto = LeafNode("Naruto", "https://myanimelist.net/anime/20/Naruto")
        val bleach = LeafNode("Bleach", "https://myanimelist.net/anime/269/Bleach")
        val fairyTail = LeafNode("Fairy Tail", "https://myanimelist.net/anime/6702/Fairy_Tail")
        val prettyCure = LeafNode("Pretty Cure", "https://myanimelist.net/anime/603/Futari_wa_Precure")
        val gundam = LeafNode("Gundam", "https://myanimelist.net/anime/80/Mobile_Suit_Gundam")
        val hunterXHunter =
            LeafNode("Hunter X Hunter (2011)", "https://myanimelist.net/anime/11061/Hunter_x_Hunter_2011")
        val dragonBall = LeafNode("Dragon Ball", "https://myanimelist.net/anime/223/Dragon_Ball")

        hundredsOfEpisodes.attach(onePiece, naruto, bleach, fairyTail, prettyCure, gundam, hunterXHunter, dragonBall)


        val trappedInAVideoGame = Node("Choose your MMO", arrayListOf("The dumb popular one", "The boring smart one"))
        val knightsOfSidonia = LeafNode("Knights of Sidonia", "https://myanimelist.net/anime/19775/Sidonia_no_Kishi")
        val fantasy = Node(
            "Theme?",
            arrayListOf(
                "Mankind's last stand",
                "Arabian nights (djinns)",
                "Dragons, adventure, and romance",
                "Medieval knights",
                "Anime Hogwarts",
                "Futuristic Hogwarts",
                "Spirit weapons"
            )
        )
        val bigCityLife = Node(
            "What drives your plot?",
            arrayListOf("Gang wars", "X-Men schoolgirls", "Life in a spirit town", "Spirit hunting", "Demon hunting")
        )

        aFew.attach(trappedInAVideoGame, knightsOfSidonia, fantasy, bigCityLife)


        val sao = LeafNode("Sword Art Online", "https://myanimelist.net/anime/11757/Sword_Art_Online")
        val logHorizon = LeafNode("Log Horizon", "https://myanimelist.net/anime/17265/Log_Horizon")

        trappedInAVideoGame.attach(sao, logHorizon)


        val attackOnTitan = LeafNode("Attack on Titan", "https://myanimelist.net/anime/16498/Shingeki_no_Kyojin")
        val magiLabyrinthOfMagic =
            LeafNode("Magi: The Labyrinth of Magic", "https://myanimelist.net/anime/14513/Magi__The_Labyrinth_of_Magic")
        val akatsukiNoYona = LeafNode("Akatsuko no Yona", "https://myanimelist.net/anime/25013/Akatsuki_no_Yona")
        val nanatsuNoTaizai = LeafNode("Nanatsu no Taizai", "https://myanimelist.net/anime/23755/Nanatsu_no_Taizai")
        val littleWitchAcademica =
            LeafNode("Little Witch Academica", "https://myanimelist.net/anime/33489/Little_Witch_Academia_TV")
        val mahoukaKoukounoRettousei =
            LeafNode("Mahouka Koukou no Rettousei", "https://myanimelist.net/anime/20785/Mahouka_Koukou_no_Rettousei")
        val soulEater = LeafNode("Soul Eater", "https://myanimelist.net/anime/3588/Soul_Eater")

        fantasy.attach(
            attackOnTitan,
            magiLabyrinthOfMagic,
            akatsukiNoYona,
            nanatsuNoTaizai,
            littleWitchAcademica,
            mahoukaKoukounoRettousei,
            soulEater
        )


        val durarara = LeafNode("Durarara!!", "https://myanimelist.net/anime/6746/Durarara")
        val aCertainScientificRailgun =
            LeafNode("A Certain Scientific Railgun", "https://myanimelist.net/anime/6213/Toaru_Kagaku_no_Railgun")
        val yozakuraQuartet = LeafNode(
            "Yozakura Quartet: Hana no Uta",
            "https://myanimelist.net/anime/18497/Yozakura_Quartet__Hana_no_Uta"
        )
        val noragami = LeafNode("Noragami", "https://myanimelist.net/anime/20507/Noragami")
        val blueExorcist = LeafNode("Blue Exorcist", "https://myanimelist.net/anime/9919/Ao_no_Exorcist")

        bigCityLife.attach(durarara, aCertainScientificRailgun, yozakuraQuartet, noragami, blueExorcist)


        val roadTripAdventure = Node(
            "Theme?",
            arrayListOf(
                "Journey into manhood through the mecha genre",
                "Steampunk scientific magic",
                "Space opera jazz",
                "'30s in Chicago",
                "Talking (&) Swords",
                "Aerial combat",
                "Hip-hop",
                "Historical",
                "Spirited Away into a fantasy drama"
            )
        )
        val fullBlownAction = Node(
            "Supernatural?",
            arrayListOf(
                "Not really",
                "Superpowers",
                "Journey into manhood through the mecha genre",
                "Medieval fantasy",
                "Steampunk scientific magic",
                "Urban fantasy",
                "Martial arts",
                "Spirit Hunting",
                "Magical girls",
                "Science fiction",
                "Samurai"
            )
        )

        storyImportant.attach(roadTripAdventure, fullBlownAction)


        val tengenToppaGurrenLagann =
            LeafNode("Tengen Toppa Gurren Lagann", "https://myanimelist.net/anime/2001/Tengen_Toppa_Gurren_Lagann")
        val fmab = LeafNode(
            "Full Metal Alchemist: Brotherhood",
            "https://myanimelist.net/anime/5114/Fullmetal_Alchemist__Brotherhood"
        )
        val cowboyBebop = LeafNode("Cowboy Bebop", "https://myanimelist.net/anime/1/Cowboy_Bebop")
        val baccano = LeafNode("Baccano!", "https://myanimelist.net/anime/2251/Baccano")
        val katanagatari = LeafNode("Katanagatari", "https://myanimelist.net/anime/6594/Katanagatari")
        val lastExile = LeafNode("Last Exile", "https://myanimelist.net/anime/97/Last_Exile")
        val samuraiChamploo = LeafNode("Samurai Champloo", "https://myanimelist.net/anime/205/Samurai_Champloo")
        val rurouniKenshin =
            LeafNode("Rurouni Kenshin", "https://myanimelist.net/anime/45/Rurouni_Kenshin__Meiji_Kenkaku_Romantan")
        val twelveKingdoms = LeafNode("Twelve Kingdoms,", "https://myanimelist.net/anime/153/Juuni_Kokuki")


        roadTripAdventure.attach(
            tengenToppaGurrenLagann,
            fmab,
            cowboyBebop,
            baccano,
            katanagatari,
            lastExile,
            samuraiChamploo,
            rurouniKenshin,
            twelveKingdoms
        )


        val notReallySupernaturalWayPoint = createWaypointNode(
            arrayListOf(
                LeafNode("Black Lagoon", "https://myanimelist.net/anime/889/Black_Lagoon"),
                LeafNode("Phantom", "https://myanimelist.net/anime/5682/Phantom__Requiem_for_the_Phantom")
            )
        )
        val darkerThanBlack =
            LeafNode("Darker than Black", "https://myanimelist.net/anime/2025/Darker_than_Black__Kuro_no_Keiyakusha")
        // Tengen node already created
        val medievalFantasyWayPoint = createWaypointNode(
            arrayListOf(
                LeafNode(
                    "Rage of Bahamut: Genesis",
                    "https://myanimelist.net/anime/21843/Shingeki_no_Bahamut__Genesis"
                ),
                LeafNode("Slayers", "https://myanimelist.net/anime/534/Slayers")
            )
        )
        // fma node already created
        val urbanFantasyWayPoint = createWaypointNode(
            arrayListOf(
                LeafNode("Zetsuen no Tempest", "https://myanimelist.net/anime/14075/Zetsuen_no_Tempest"),
                LeafNode("Hitman Reborn", "https://myanimelist.net/anime/1604/Katekyo_Hitman_Reborn"),
                LeafNode("Shakugan no Shana", "https://myanimelist.net/anime/355/Shakugan_no_Shana")
            )
        )
        val kenichi = LeafNode("Kenichi", "https://myanimelist.net/anime/1559/Shijou_Saikyou_no_Deshi_Kenichi")
        val yuYuHakusho = LeafNode("Yu Yu Hakusho", "https://myanimelist.net/anime/392/Yuu%E2%98%86Yuu%E2%98%86Hakusho")
        val magicalGirlsWayPoint = createWaypointNode(
            arrayListOf(
                LeafNode("Sailor Moon", "https://myanimelist.net/anime/530/Bishoujo_Senshi_Sailor_Moon"),
                LeafNode("Cardcaptor Sakura", "https://myanimelist.net/anime/232/Cardcaptor_Sakura"),
                LeafNode("Lyrical Nanoha", "https://myanimelist.net/anime/76/Mahou_Shoujo_Lyrical_Nanoha?q=lyrical%20n")
            )
        )
        val scienceFiction = Node("Type of scifi?", arrayListOf("Shootout", "Mecha"))
        val samuraiWayPoint = createWaypointNode(arrayListOf(samuraiChamploo, rurouniKenshin))

        fullBlownAction.attach(
            notReallySupernaturalWayPoint,
            darkerThanBlack,
            tengenToppaGurrenLagann,
            medievalFantasyWayPoint,
            fmab,
            urbanFantasyWayPoint,
            kenichi,
            yuYuHakusho,
            magicalGirlsWayPoint,
            scienceFiction,
            samuraiWayPoint
        )


        val shootOutWayPoint = WaypointNode(
            arrayListOf(
                LeafNode("Gungrave", "https://myanimelist.net/anime/267/Gungrave"),
                LeafNode("Trigun", "https://myanimelist.net/anime/6/Trigun"),
                LeafNode("Outlaw Star", "https://myanimelist.net/anime/400/Seihou_Bukyou_Outlaw_Star")
            )
        )
        val mechaFilter = Node(
            "What sounds interesting?",
            arrayListOf("Road trip", "Neo-noir", "Mindgames", "Pacific Rim", "Comedy", "Romance", "A sci-fi serenade")
        )

        scienceFiction.attach(shootOutWayPoint, mechaFilter)


        val eurekaSeven = LeafNode("Eureka Seven", "https://myanimelist.net/anime/237/Koukyoushihen_Eureka_Seven")
        val bigO = LeafNode("Big O", "https://myanimelist.net/anime/567/The_Big_O")
        val codeGeass = LeafNode("Code Geass", "https://myanimelist.net/anime/1575/Code_Geass__Hangyaku_no_Lelouch")
        val gunBuster = LeafNode("Gunbuster", "https://myanimelist.net/anime/949/Top_wo_Nerae_Gunbuster")
        val fullMetalPanic = LeafNode("Full Metal Panic", "https://myanimelist.net/anime/71/Full_Metal_Panic")
        val visionOfEscaflowne =
            LeafNode("Vision of Escaflowne", "https://myanimelist.net/anime/182/Tenkuu_no_Escaflowne")
        val macross = LeafNode("Macross", "https://myanimelist.net/anime/1088/Macross")

        mechaFilter.attach(eurekaSeven, bigO, codeGeass, gunBuster, fullMetalPanic, visionOfEscaflowne, macross)


        val sportQuery = "What kind of sport are you looking for?"
        val pingPong = LeafNode("Ping Pong", "https://myanimelist.net/anime/22135/Ping_Pong_the_Animation")
        val classicsFilter =
            Node(sportQuery, arrayListOf("Boxing", "Racing", "Basketball", "Baseball", "Baseball + gambling", "Go"))
        val freshFilter = Node(
            sportQuery,
            arrayListOf(
                "Basketball",
                "Baseball",
                "Cycling",
                "Tennis",
                "Volleyball",
                "Swimming",
                "American football",
                "Poetry card game"
            )
        )

        sports.attach(pingPong, classicsFilter, freshFilter)


        val hajimeNoIppo = LeafNode("Hajime no Ippo", "https://myanimelist.net/anime/263/Hajime_no_Ippo")
        val initialD = LeafNode("Initial D", "https://myanimelist.net/anime/185/Initial_D_First_Stage")
        val slamDunk = LeafNode("Slam Dunk", "https://myanimelist.net/anime/170/Slam_Dunk")
        val major = LeafNode("Major", "https://myanimelist.net/anime/627/Major_S1")
        val oneOuts = LeafNode("One Outs", "https://myanimelist.net/anime/5040/One_Outs")
        val hikaruNoGo = LeafNode("Hikaru no Go", "https://myanimelist.net/anime/135/Hikaru_no_Go")

        classicsFilter.attach(hajimeNoIppo, initialD, slamDunk, major, oneOuts, hikaruNoGo)


        val kurokoNoBasket = LeafNode("Kuroko no Basket", "https://myanimelist.net/anime/11771/Kuroko_no_Basket")
        val aceOfDiamond = LeafNode("Ace of Diamond", "https://myanimelist.net/anime/18689/Diamond_no_Ace")
        val yowamushiPedal = LeafNode("Yowamushi Pedal", "https://myanimelist.net/anime/18179/Yowamushi_Pedal")
        val babySteps = LeafNode("Baby Steps", "https://myanimelist.net/anime/21185/Baby_Steps")
        val haikyuu = LeafNode("Haikyuu!!", "https://myanimelist.net/anime/20583/Haikyuu")
        val free = LeafNode("Free!", "https://myanimelist.net/anime/18507/Free")
        val eyeshield21 = LeafNode("Eyeshield 21", "https://myanimelist.net/anime/15/Eyeshield_21")
        val chihayafuru = LeafNode("Chihayafuru", "https://myanimelist.net/anime/10800/Chihayafuru")

        freshFilter.attach(
            kurokoNoBasket,
            aceOfDiamond,
            yowamushiPedal,
            babySteps,
            haikyuu,
            free,
            eyeshield21,
            chihayafuru
        )


        val flcl = LeafNode("FLCL", "https://myanimelist.net/anime/227/FLCL")
        val killLaKill = LeafNode("Kill la Kill", "https://myanimelist.net/anime/18679/Kill_la_Kill")
        val noGameNoLife = LeafNode("No Game No Life", "https://myanimelist.net/anime/19815/No_Game_No_Life")
        val symphogear = LeafNode("Symphogear", "https://myanimelist.net/anime/11751/Senki_Zesshou_Symphogear")
        val shokugekiNoSouma = LeafNode("Shokugeki no Souma", "https://myanimelist.net/anime/28171/Shokugeki_no_Souma")
        val pantyStocking =
            LeafNode("Panty & Stocking", "https://myanimelist.net/anime/8795/Panty___Stocking_with_Garterbelt")
        val samuraiFlamenco = LeafNode("Samurai Flamenco", "https://myanimelist.net/anime/19365/Samurai_Flamenco")
        val highschoolOfTheDead =
            LeafNode("Highschool of the Dead", "https://myanimelist.net/anime/8074/Highschool_of_the_Dead")
        val crossAnge =
            LeafNode("Cross Ange", "https://myanimelist.net/anime/25731/Cross_Ange__Tenshi_to_Ryuu_no_Rondo")
        val jjba =
            LeafNode("JoJo's Bizarre Adventure", "https://myanimelist.net/anime/14719/JoJo_no_Kimyou_na_Bouken_TV")

        pureFun.attach(
            flcl,
            killLaKill,
            noGameNoLife,
            symphogear,
            shokugekiNoSouma,
            pantyStocking,
            samuraiFlamenco,
            highschoolOfTheDead,
            crossAnge,
            jjba
        )

        return actionAdventure
    }
}



