package messageevents

import com.jessecorbett.diskord.api.model.Message
import com.jessecorbett.diskord.api.model.User
import com.jessecorbett.diskord.dsl.Bot
import kotlinx.serialization.UnstableDefault
import utils.sendMessage

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
                if (nextNode !is LeafNode) {
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



    private val map = mutableMapOf<User, Node>()

    private val root = AnimeTree().createTree()

    fun beginAnimeRecommendation(user : User) : String {
        // Set user node to root
        map[user] = root

        return "Hi ${user.username}, do you want me to recommend an anime? If so, please answer the following statements"
    }

    fun getRootDisplayMessage() = root.getDisplayMessage()

    fun getAllAnimes() : Set<LeafNode> {
        // Recursive function diving to find all nodes
        fun recursiveAddChildren(node: Node) : Set<LeafNode> {
            return if (node is LeafNode) {
                setOf(node)

            } else {
                val parent = mutableSetOf<LeafNode>()
                node.children.forEach {
                    parent.addAll(recursiveAddChildren(it))
                }
                parent.toSet()
            }

        }

        return recursiveAddChildren(root)
    }


}

// Tree data
interface TreeCreating {
    fun createTree() : Node
}

class AnimeTree : TreeCreating {
    override fun createTree(): Node {
        // Root
        val root = Node("I am looking for something...", arrayListOf("Dynamic", "Funny", "Calm", "Moving", "Mature", "Intense", "Scary"))


        val action = ActionTree().createTree()
        val comedy = ComedyTree().createTree()
        val sliceOfLife = SliceOfLifeTree().createTree()
        val drama = DramaTree().createTree()
        val thrillerAndHorror = ThrillerHorrorTree().createTree()


        val solString = "Slice of life"

        val solOrComedy = Node("$solString or comedy?", arrayListOf(solString, "Comedy"))
        solOrComedy.attach(sliceOfLife, comedy)

        val solOrDrama = Node("$solString or drama?", arrayListOf(solString, "Drama"))
        solOrDrama.attach(sliceOfLife, drama)

        root.attach(action, comedy, solOrComedy, solOrDrama, drama, thrillerAndHorror.children[1], thrillerAndHorror.children[0])


        return root
    }

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


        val notReallySupernaturalWayPoint = createWayPointNode(
            LeafNode("Black Lagoon", "https://myanimelist.net/anime/889/Black_Lagoon"),
            LeafNode("Phantom", "https://myanimelist.net/anime/5682/Phantom__Requiem_for_the_Phantom")

        )
        val darkerThanBlack =
            LeafNode("Darker than Black", "https://myanimelist.net/anime/2025/Darker_than_Black__Kuro_no_Keiyakusha")
        // Tengen node already created
        val medievalFantasyWayPoint = createWayPointNode(

            LeafNode(
                    "Rage of Bahamut: Genesis",
                    "https://myanimelist.net/anime/21843/Shingeki_no_Bahamut__Genesis"
            ),
            LeafNode("Slayers", "https://myanimelist.net/anime/534/Slayers")

        )
        // fma node already created
        val urbanFantasyWayPoint = createWayPointNode(
            LeafNode("Zetsuen no Tempest", "https://myanimelist.net/anime/14075/Zetsuen_no_Tempest"),
            LeafNode("Hitman Reborn", "https://myanimelist.net/anime/1604/Katekyo_Hitman_Reborn"),
            LeafNode("Shakugan no Shana", "https://myanimelist.net/anime/355/Shakugan_no_Shana")

        )
        val kenichi = LeafNode("Kenichi", "https://myanimelist.net/anime/1559/Shijou_Saikyou_no_Deshi_Kenichi")
        val yuYuHakusho = LeafNode("Yu Yu Hakusho", "https://myanimelist.net/anime/392/Yuu%E2%98%86Yuu%E2%98%86Hakusho")
        val magicalGirlsWayPoint = createWayPointNode(
            LeafNode("Sailor Moon", "https://myanimelist.net/anime/530/Bishoujo_Senshi_Sailor_Moon"),
            LeafNode("Cardcaptor Sakura", "https://myanimelist.net/anime/232/Cardcaptor_Sakura"),
            LeafNode("Lyrical Nanoha", "https://myanimelist.net/anime/76/Mahou_Shoujo_Lyrical_Nanoha?q=lyrical%20n")

        )
        val scienceFiction = Node("Type of scifi?", arrayListOf("Shootout", "Mecha"))
        val samuraiWayPoint = createWayPointNode(samuraiChamploo, rurouniKenshin)

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


        val shootOutWayPoint = WayPointNode(
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

class ComedyTree : TreeCreating {
    override fun createTree(): Node {
        val comedy = Node("What kind of comedy?", arrayListOf("Daily life", "Romance comedy", "Parody"))

        val dailyLife = Node("Setting?", arrayListOf("School", "Work"))
        val lightHeartedOption = "Lighthearted casual comedy with some romance and supernatural stuff"
        val romanceComedy = Node("Theme?", arrayListOf("For starters", "Anti-romance", "Drawing girly manga", "Meta-humor", "College-aged cast", "Girl-orineted (shoujo)", "Sitcom", "Harem", "Incest", "Gay couples", "Old classic", "Sports", "Everybody wins", lightHeartedOption, "Delusional supernerds", "Slice of life turned drama"))
        val parody = Node("Making fun of...", arrayListOf("Anime cliches", "Action series", "Romance comedies", "Society"))

        comedy.attach(dailyLife, romanceComedy, parody)


        val school = Node("About?", arrayListOf("Teaching", "Delinquents", "nursing a demon baby", "*Ordinary* school life", "Discount fight club", "Boys being boys", "Dirty jokes", lightHeartedOption))
        val work = Node("at...", arrayListOf("Animation studio", "Restaurant", "Disneyland", "McDonald's", "School"))

        dailyLife.attach(school, work)

        val greatTeacherOnizuka = LeafNode("Great Teacher Onizuka", "https://myanimelist.net/anime/245/Great_Teacher_Onizuka")
        val schoolOrTeachingWaypointNode = createWayPointNode(
            LeafNode("Sayonara Zetsubou Sensei", "https://myanimelist.net/anime/2605/Sayonara_Zetsubou_Sensei"),
            greatTeacherOnizuka
        )
        val beelzebub = LeafNode("Beelzebub", "https://myanimelist.net/anime/9513/Beelzebub")
        val delinquentsWaypointNode = createWayPointNode(
            greatTeacherOnizuka,
            LeafNode("Cromartie High", "https://myanimelist.net/anime/114/Sakigake_Cromartie_Koukou"),
            beelzebub
        )
        // Beelzebub already created
        val schoolLifeWaypointNode = createWayPointNode(
            LeafNode("Nichijou", "https://myanimelist.net/anime/10165/Nichijou"),
            LeafNode("Azumanga Daioh", "https://myanimelist.net/anime/66/Azumanga_Daioh"),
            LeafNode("D-Frag!", "https://myanimelist.net/anime/20031/D-Frag")
        )
        val benTo = LeafNode("Ben-To", "https://myanimelist.net/anime/10396/Ben-To")
        val dailyLifeOfHighschoolBoys = LeafNode("Daily Lives of High School Boys", "https://myanimelist.net/anime/11843/Danshi_Koukousei_no_Nichijou")
        val seitokaiYakuindomo = LeafNode("Seitokai Yakuindomo", "https://myanimelist.net/anime/8675/Seitokai_Yakuindomo")
        val longWordWaypointNode = createWayPointNode(
            LeafNode("Yamada-kun and the Seven Witches", "https://myanimelist.net/anime/28677/Yamada-kun_to_7-nin_no_Majo_TV"),
            LeafNode("Witch Craft Works", "https://myanimelist.net/anime/21085/Witch_Craft_Works")
        )

        school.attach(schoolOrTeachingWaypointNode, delinquentsWaypointNode, beelzebub, schoolLifeWaypointNode, benTo, dailyLifeOfHighschoolBoys, seitokaiYakuindomo, longWordWaypointNode)


        val shirobako = LeafNode("Shirobako", "https://myanimelist.net/anime/25835/Shirobako")
        val working = LeafNode("Working!", "https://myanimelist.net/anime/6956/Working")
        val amagiBrilliantPark = LeafNode("Amagi Brilliant Park", "https://myanimelist.net/anime/22147/Amagi_Brilliant_Park")
        val devilIsAPartTimer = LeafNode("The Devil is a Part-Timer", "https://myanimelist.net/anime/15809/Hataraku_Maou-sama")
        // Teaching node already created

        work.attach(shirobako, working, amagiBrilliantPark, devilIsAPartTimer)


        val toradora = LeafNode("Toradora!", "https://myanimelist.net/anime/4224/Toradora")
        val oregairu = LeafNode("Oregairu", "https://myanimelist.net/anime/14813/Yahari_Ore_no_Seishun_Love_Comedy_wa_Machigatteiru")
        val gekkanShoujoNozakikun = LeafNode("Gekkan Shoujo Nozaki-kun", "https://myanimelist.net/anime/23289/Gekkan_Shoujo_Nozaki-kun")
        val saekano = LeafNode("Saekano", "https://myanimelist.net/anime/23277/Saenai_Heroine_no_Sodatekata")
        val goldenTime = LeafNode("Golden Time", "https://myanimelist.net/anime/17895/Golden_Time")
        val girlOriented = Node("Another theme?", arrayListOf("Playful love", "Reverse harem", "Breaking the loner", "Tall girl, short boy", "Weirdo girl, cool boy", "Idols", "Gentle giant", "'Too busy for romance'", "Fine arts students", "Life as a shrine deity"))
        val schoolRumble = LeafNode("School Rumble", "https://myanimelist.net/anime/24/School_Rumble")
        val nisekoi = LeafNode("Nisekoi", "https://myanimelist.net/anime/18897/Nisekoi")
        val oreImo = LeafNode("OreImo", "https://myanimelist.net/anime/8769/Ore_no_Imouto_ga_Konnani_Kawaii_Wake_ga_Nai")
        val gayCouples = Node("Boys or girls?", arrayListOf("Boys", "Girls"))
        val maisonIkkoku = LeafNode("Maison Ikkoku", "https://myanimelist.net/anime/1453/Maison_Ikkoku")
        val crossGame = LeafNode("Cross Game", "https://myanimelist.net/anime/5941/Cross_Game")
        val amagamiSS = LeafNode("Amagami SS", "https://myanimelist.net/anime/8676/Amagami_SS")
        // Long word option
        val chuunibyou = LeafNode("Chuunibyou", "https://myanimelist.net/anime/14741/Chuunibyou_demo_Koi_ga_Shitai")
        val kokoroConnect = LeafNode("Kokoro Connect", "https://myanimelist.net/anime/11887/Kokoro_Connect")

        romanceComedy.attach(toradora, oregairu, gekkanShoujoNozakikun, saekano, goldenTime, girlOriented, schoolRumble, nisekoi, oreImo, gayCouples, maisonIkkoku, crossGame, amagamiSS, longWordWaypointNode, chuunibyou, kokoroConnect)


        val maidSama = LeafNode("Maid Sama", "https://myanimelist.net/anime/7054/Kaichou_wa_Maid-sama")
        val ouranKoukouHostClub = LeafNode("Ouran Koukou Host Club", "https://myanimelist.net/anime/853/Ouran_Koukou_Host_Club")
        val wallflower = LeafNode("The Wallflower", "https://myanimelist.net/anime/1562/Yamato_Nadeshiko_Shichihenge%E2%99%A5")
        val lovelyComplex = LeafNode("Lovely Complex", "https://myanimelist.net/anime/2034/Lovely%E2%98%85Complex")
        val kimiNiTodoke = LeafNode("Kimi ni Todoke", "https://myanimelist.net/anime/6045/Kimi_ni_Todoke")
        val skipBeat = LeafNode("Skip Beat", "https://myanimelist.net/anime/4722/Skip_Beat")
        val oreMonogatari = LeafNode("Ore Monogatari", "https://myanimelist.net/anime/28297/Ore_Monogatari")
        val mlm = LeafNode("My Little Monster", "https://myanimelist.net/anime/14227/Tonari_no_Kaibutsu-kun")
        val honeyAndClover = LeafNode("Honey and Clover", "https://myanimelist.net/anime/16/Hachimitsu_to_Clover")
        val kamisamaKiss = LeafNode("Kamisama Kiss", "https://myanimelist.net/anime/14713/Kamisama_Hajimemashita")

        girlOriented.attach(maidSama, ouranKoukouHostClub, wallflower, lovelyComplex, kimiNiTodoke, skipBeat, oreMonogatari, mlm, honeyAndClover, kamisamaKiss)


        val junjouRomantica = LeafNode("Junjou Romantica", "https://myanimelist.net/anime/3092/Junjou_Romantica")
        val sakuraTrick = LeafNode("Sakura Trick", "https://myanimelist.net/anime/20047/Sakura_Trick")

        gayCouples.attach(junjouRomantica, sakuraTrick)


        val animeClichesWaypointNode = createWayPointNode(
            LeafNode("The Melancholy of Haruhi Suzumiya", "https://myanimelist.net/anime/849/Suzumiya_Haruhi_no_Yuuutsu"),
            LeafNode("Lucky Star", "https://myanimelist.net/anime/1887/Lucky%E2%98%86Star"),
            LeafNode("Binbougami ga!", "https://myanimelist.net/anime/13535/Binbougami_ga")
        )
        val gintama = LeafNode("Gintama","https://myanimelist.net/anime/918/Gintama")
        val romanceComediesWaypointNode = createWayPointNode(
            gekkanShoujoNozakikun,
            saekano,
            LeafNode("The World God Only Knows", "https://myanimelist.net/anime/8525/Kami_nomi_zo_Shiru_Sekai")
        )
        val humanityHasDeclined = LeafNode("Humanity Has Declined", "https://myanimelist.net/anime/10357/Jinrui_wa_Suitai_Shimashita")

        parody.attach(animeClichesWaypointNode, gintama, romanceComediesWaypointNode, humanityHasDeclined)


        return comedy
    }
}

class SliceOfLifeTree : TreeCreating {
    override fun createTree(): Node {
        val sliceOfLife = Node("'Calm', or 'calm before the storm'", arrayListOf("I want to chill", "I was born to feel"))

        val chill = Node("With whom?", arrayListOf("Random people", "Cute girls doing cute things", "Jesus & Buddha"))
        val cutePTSDOption = "Cute PTSD"
        val bornToFeel = Node("Feel how?", arrayListOf(cutePTSDOption, "Warm and bittersweet", "Heavy cringe", "I've got tissues"))

        sliceOfLife.attach(chill, bornToFeel)


        val chillWithRandos = Node("Setting?", arrayListOf("At work", "School friends drawing manga", "School mystery club", "Road trip adventure", "Adult women"))
        val cuteGirls = Node("What's cute?", arrayListOf("Wannabe popstars", "Life away from the city", "Tank drifting", cutePTSDOption))
        val saintOniisan = LeafNode("Saint Oniisan", "https://myanimelist.net/anime/15775/Saint%E2%98%86Oniisan")

        chill.attach(chillWithRandos, cuteGirls, saintOniisan)


        val chillAtWork = Node("Where?", arrayListOf("Idyllic Venice", "Life in a band", "Rural Japan", "Shopping Alley", "Spirit hunting", "Astronauts"))
        val bakuman = LeafNode("Bakuman", "https://myanimelist.net/anime/7674/Bakuman")
        val roadTrip = Node("Through", arrayListOf("Medieval Europe and its economy", "Rural Japan and spirit stuff", "Philosophy and a talking motorcycle"))
        val princessJellyfish = LeafNode("Princess Jellyfish", "https://myanimelist.net/anime/8129/Kuragehime")

        chillWithRandos.attach(chillAtWork, bakuman, roadTrip, princessJellyfish)


        val aria = LeafNode("Aria ", "https://myanimelist.net/anime/477/Aria_The_Animation")
        val beck = LeafNode("Beck", "https://myanimelist.net/anime/57/Beck")
        val barakamon = LeafNode("Barakamon", "https://myanimelist.net/anime/22789/Barakamon")
        val tamakoMarket = LeafNode("Tamako Market", "https://myanimelist.net/anime/16417/Tamako_Market")
        val kyoukaiNoKanata = LeafNode("Kyoukai no Kanata", "https://myanimelist.net/anime/18153/Kyoukai_no_Kanata")
        val spaceBrothers = LeafNode("Space Brothers", "https://myanimelist.net/anime/12431/Uchuu_Kyoudai")

        chillAtWork.attach(aria, beck, barakamon, tamakoMarket, kyoukaiNoKanata, spaceBrothers)


        val spiceandWolf = LeafNode("Spice & Wolf", "https://myanimelist.net/anime/2966/Ookami_to_Koushinryou")
        val mushishi = LeafNode("Mushishi", "https://myanimelist.net/anime/457/Mushishi")
        val kinosJourney = LeafNode("Kino's Journey", "https://myanimelist.net/anime/486/Kino_no_Tabi__The_Beautiful_World")

        roadTrip.attach(spiceandWolf, mushishi, kinosJourney)


        val popstars = Node("Pro or amateur?", arrayListOf("Big stage", "School life"))
        val nonNonBiyori = LeafNode("Non Non Biyori", "https://myanimelist.net/anime/17549/Non_Non_Biyori")
        val girlsAndPanzer = LeafNode("Girls & Panzer", "https://myanimelist.net/anime/14131/Girls___Panzer")
        val ptsd = Node("Serving as...", arrayListOf("Magical girls", "Soldiers"))

        cuteGirls.attach(popstars, nonNonBiyori, girlsAndPanzer, ptsd)


        val bigStageWaypointNode = createWayPointNode(
            LeafNode("The iDOLM@STER", "https://myanimelist.net/anime/10278/The_iDOLMSTER"),
            LeafNode("Aikatsu!", "https://myanimelist.net/anime/15061/Aikatsu")
        )
        val schoolClubWaypointNode = createWayPointNode(
            LeafNode("Love Live!", "https://myanimelist.net/anime/15051/Love_Live_School_Idol_Project"),
            LeafNode("K-On!", "https://myanimelist.net/anime/5680/K-On")
        )

        popstars.attach(bigStageWaypointNode, schoolClubWaypointNode)


        val yukiYunaIsAHero = LeafNode("Yuki Yuna is a Hero", "https://myanimelist.net/anime/25519/Yuuki_Yuuna_wa_Yuusha_de_Aru")
        val soRaNoWoTo = LeafNode("So Ra No Wo To", "https://myanimelist.net/anime/6802/So_Ra_No_Wo_To")

        ptsd.attach(yukiYunaIsAHero, soRaNoWoTo)


        // ptsd node already created
        val bittersweet = Node("Premise?", arrayListOf("Unexpected child", "Unexpected relocation", "Talent vs hard work"))
        val cringe = Node("Who is 'the hero'?", arrayListOf("Awkward teenage girl", "Shut-in loser"))
        val sad = Node("Feels?", arrayListOf("And laughs", "Overcoming grief", "Hardships of life and death"))

        bornToFeel.attach(ptsd, bittersweet, cringe, sad)


        val usagiDrop = LeafNode("Usagi Drop", "https://myanimelist.net/anime/10162/Usagi_Drop")
        val hanasakuIroha = LeafNode("Hanasaku Iroha", "https://myanimelist.net/anime/9289/Hanasaku_Iroha")
        val sakuraSouNoPetNaKanojo = LeafNode("Sakura-sou no Pet na Kanojo", "https://myanimelist.net/anime/13759/Sakura-sou_no_Pet_na_Kanojo")

        bittersweet.attach(usagiDrop, hanasakuIroha, sakuraSouNoPetNaKanojo)


        val wataMote = LeafNode("Watamote", "https://myanimelist.net/anime/16742/Watashi_ga_Motenai_no_wa_Dou_Kangaetemo_Omaera_ga_Warui")
        val welcomeToTheNHK = LeafNode("Welcome to the NHK", "https://myanimelist.net/anime/1210/NHK_ni_Youkoso")

        cringe.attach(wataMote, welcomeToTheNHK)


        val angelBeats = LeafNode("Angel Beats!", "https://myanimelist.net/anime/6547/Angel_Beats")
        val typeOfOvercomingGrief = Node("Individual development or group relationships?", arrayListOf("Young pianist struggle after losing his mother", "Kids reuniting years after the death of a childhood friend"))
        val hardships = Node("Are you patient?", arrayListOf("Nope", "Just make it short", "Bring it on"))

        sad.attach(angelBeats, typeOfOvercomingGrief, hardships)


        val yourLieInApril = LeafNode("Your Lie in April", "https://myanimelist.net/anime/23273/Shigatsu_wa_Kimi_no_Uso")
        val anohana = LeafNode("AnoHana", "https://myanimelist.net/anime/9989/Ano_Hi_Mita_Hana_no_Namae_wo_Bokutachi_wa_Mada_Shiranai")

        typeOfOvercomingGrief.attach(yourLieInApril, anohana)


        val natsumesBookOfFriends = LeafNode("Natsume's Book of Friends", "https://myanimelist.net/anime/4081/Natsume_Yuujinchou")
        val haibaneRenmei = LeafNode("Haibane Renmei", "https://myanimelist.net/anime/387/Haibane_Renmei")
        val clannad = LeafNode("Clannad -> Clannad After Story", "https://myanimelist.net/anime/2167/Clannad\nhttps://myanimelist.net/anime/4181/Clannad__After_Story")

        hardships.attach(natsumesBookOfFriends, haibaneRenmei, clannad)

        return sliceOfLife
    }

}

class DramaTree : TreeCreating {
    override fun createTree(): Node {
        val drama = Node("Genre?", arrayListOf("Romance", "Fantasy", "Post-apocalyptic rural dystopia", "Science fiction", "Time travel", "Psychological"))

        val romance = Node("Theme?", arrayListOf("Mystery (supernatural)", "Fantasy", "1980 classic", "Comedy drama with musicians", "Swan lake magical girl", "Love triangle", "Incest", "Homosexuality", "Adult life struggle", "Realism"))
        val fantasy = Node("Action-packed?", arrayListOf("Yes", "No"))
        val shinsekaiYori = LeafNode("Shinsekai yori", "https://myanimelist.net/anime/13125/Shinsekai_yori")
        val sciFi = Node("Type?", arrayListOf("Existential", "Realistic", "Action", "Space opera"))
        val nowAndThen = LeafNode("Now and Then, Here and There", "https://myanimelist.net/anime/160/Ima_Soko_ni_Iru_Boku")
        val cyberpunkOption = "Cyberpunk"
        val romanceOption = "Romance"
        val mysteryOption = "Mystery"
        val psychological = Node("Theme?", arrayListOf(cyberpunkOption, romanceOption, mysteryOption, "Artsy wild ride", "Supernatural", "Game of souls", "Reflection on life choices"))

        drama.attach(romance, fantasy, shinsekaiYori, sciFi, nowAndThen, psychological)


        val bakemonogatari = LeafNode("Bakemonogatari", "https://myanimelist.net/anime/5081/Bakemonogatari")
        val nagiNoAsukara = LeafNode("Nagi no Asukara", "https://myanimelist.net/anime/16067/Nagi_no_Asu_kara")
        val roseOfVersailles = LeafNode("Rose of Versailles", "https://imgur.com/q9Xjv4p")
        val musiciansWaypointNode = createWayPointNode(
            LeafNode("Nodame Cantabile", "https://myanimelist.net/anime/1698/Nodame_Cantabile"),
            LeafNode("Nana", "https://myanimelist.net/anime/877/Nana")
        )
        val princessTutu = LeafNode("Princess Tutu", "https://myanimelist.net/anime/721/Princess_Tutu")
        val loveTriangleWaypointNode = createWayPointNode(
            LeafNode("ef: A Tale of Melodies", "https://myanimelist.net/anime/4789/ef__A_Tale_of_Melodies"),
            LeafNode("True Tears", "https://myanimelist.net/anime/2129/True_Tears"),
            LeafNode("White Album 2", "https://myanimelist.net/anime/18245/White_Album_2"),
            LeafNode("Touch", "https://myanimelist.net/anime/1065/Touch")
        )
        val koiKaze = LeafNode("Koi Kaze", "https://myanimelist.net/anime/634/Koi_Kaze")
        val oniisamaE = LeafNode("Oniisama e", "https://myanimelist.net/anime/795/Oniisama_e")
        val rec = LeafNode("Rec", "https://myanimelist.net/anime/710/Rec")
        val akuNoHana = LeafNode("Aku no Hana", "https://myanimelist.net/anime/16201/Aku_no_Hana")

        romance.attach(bakemonogatari, nagiNoAsukara, roseOfVersailles, musiciansWaypointNode, princessTutu, loveTriangleWaypointNode, koiKaze, oniisamaE, rec, akuNoHana)


        val seireiNoMoribito = LeafNode("Seirei no Moribito", "https://myanimelist.net/anime/1827/Seirei_no_Moribito")
        val beastPlayerErin = LeafNode("Beast Player Erin", "https://myanimelist.net/anime/5420/Kemono_no_Souja_Erin")

        fantasy.attach(seireiNoMoribito, beastPlayerErin)


        val existential = Node("Theme?", arrayListOf(cyberpunkOption, romanceOption, mysteryOption))
        val planetes = LeafNode("Planetes", "https://myanimelist.net/anime/329/Planetes")
        val wolfsRain = LeafNode("Wolf's Rain", "https://myanimelist.net/anime/202/Wolfs_Rain")
        val spaceOpera = Node("How epic?", arrayListOf("Starfleet", "Starship"))

        sciFi.attach(existential, planetes, wolfsRain, spaceOpera)


        val cyberpunkWaypointNode = createWayPointNode(
            LeafNode("Ghost in the Shell: Stand Alone Complex", "https://myanimelist.net/anime/467/Koukaku_Kidoutai__Stand_Alone_Complex"),
            LeafNode("Technolyze", "https://myanimelist.net/anime/26/Texhnolyze")
        )
        val kaiba = LeafNode("Kaiba", "https://myanimelist.net/anime/3701/Kaiba")
        val mysteryWaypointNode = createWayPointNode(
            LeafNode("Ergo Proxy", "https://myanimelist.net/anime/790/Ergo_Proxy"),
            LeafNode("Serial Experiments Lain", "https://myanimelist.net/anime/339/Serial_Experiments_Lain")
        )

        existential.attach(cyberpunkWaypointNode, kaiba, mysteryWaypointNode)


        val legendOfTheGalacticHeroes = LeafNode("Legend of the Galactic Heroes", "https://myanimelist.net/anime/820/Ginga_Eiyuu_Densetsu")
        val spaceBattleshipYamato2199 = LeafNode("Space Battleship Yamato 2199", "https://myanimelist.net/anime/12029/Uchuu_Senkan_Yamato_2199")

        spaceOpera.attach(legendOfTheGalacticHeroes, spaceBattleshipYamato2199)


        // Cyberpunk, romance, and mystery nodes already created
        val wildRide = Node("Hop in:", arrayListOf("Gender-bending feminist classic", "Mystery overloaded with symbolism", "Lesbian Bear Storm"))
        val xxxHOLIC = LeafNode("xxxHOLiC", "https://myanimelist.net/manga/10/xxxHOLiC")
        val deathParade = LeafNode("Death Parade", "https://myanimelist.net/anime/28223/Death_Parade")
        val tatamiGalaxy = LeafNode("Tatami Galaxy", "https://myanimelist.net/anime/7785/Yojouhan_Shinwa_Taikei")

        psychological.attach(cyberpunkWaypointNode, kaiba, mysteryWaypointNode, wildRide, xxxHOLIC, deathParade, tatamiGalaxy)


        val revolutionaryGirlUtena = LeafNode("Revolutionary Girl Utena", "https://myanimelist.net/anime/440/Shoujo_Kakumei_Utena")
        val penguindrum = LeafNode("Mawaru Penguindrum", "https://myanimelist.net/anime/10721/Mawaru_Penguindrum")
        val yuriKumaArashi = LeafNode("Yuri Kuma Arashi", "https://myanimelist.net/anime/26165/Yuri_Kuma_Arashi")

        wildRide.attach(revolutionaryGirlUtena, penguindrum, yuriKumaArashi)


        return drama
    }

}

class ThrillerHorrorTree : TreeCreating {
    override fun createTree(): Node {
        val thrillerAndHorror = Node("Tension: Graphic or implied", arrayListOf("Horror", "Thriller"))

        val horror = Node("What's scary?", arrayListOf("Vampires", "Vampires with guns", "Aliens", "Mystery", "Sci-Fi", "Ghosts", "Superpowers", "War against the undead", "The Ring", "Demons", "People are"))
        val thriller = Node("What kind of thriller?", arrayListOf("Crime drama", "Science fiction", "Hunger games", "Gambling", "Proper drama", "I'm ready"))

        thrillerAndHorror.attach(horror, thriller)


        val vampiresWayPointNode = createWayPointNode(
            LeafNode("Shiki", "https://myanimelist.net/anime/7724/Shiki"),
            LeafNode("Blood+", "https://myanimelist.net/anime/150/Blood_")
        )
        val hellsingUltimate = LeafNode("Hellsing Ultimate", "https://myanimelist.net/anime/777/Hellsing_Ultimate")
        val parasyte = LeafNode("Parasyte", "https://myanimelist.net/anime/22535/Kiseijuu__Sei_no_Kakuritsu")
        val mysteryWayPointNode = createWayPointNode(
            LeafNode("Another", "https://myanimelist.net/anime/11111/Another"),
            LeafNode("Higurashi", "https://myanimelist.net/anime/934/Higurashi_no_Naku_Koro_ni")
        )
        val deadmanWonderland = LeafNode("Deadman Wonderland", "https://myanimelist.net/anime/6880/Deadman_Wonderland")
        val ghostWayPointNode = createWayPointNode(
            LeafNode("Ghost Hunt", "https://myanimelist.net/anime/1571/Ghost_Hunt"),
            LeafNode("Dusk Maiden", "https://myanimelist.net/anime/12445/Tasogare_Otome_x_Amnesia")
        )
        val elfenLied = LeafNode("Elfen Lied", "https://myanimelist.net/anime/226/Elfen_Lied")
        val tokyoGhoul = LeafNode("Tokyo Ghoul", "https://myanimelist.net/anime/22319/Tokyo_Ghoul")
        val jigokuShoujo = LeafNode("Jigoku Shoujo", "https://myanimelist.net/anime/228/Jigoku_Shoujo")
        val mononoke = LeafNode("Mononoke", "https://myanimelist.net/anime/2246/Mononoke")
        val peopleAreScary = Node("Setting?", arrayListOf("Germany", "Dark fantasy"))

        horror.attach(vampiresWayPointNode, hellsingUltimate, parasyte, mysteryWayPointNode, deadmanWonderland, ghostWayPointNode, elfenLied, tokyoGhoul, jigokuShoujo, mononoke, peopleAreScary)


        val monster = LeafNode("Monster", "https://myanimelist.net/anime/19/Monster")
        val berserk = LeafNode("Berserk\n(Read the manga)", "https://myanimelist.net/anime/32379/Berserk")

        peopleAreScary.attach(monster, berserk)


        val crimeDrama = Node("With", arrayListOf("Psychology of the psychopath", "Surreal investigation", "Grim Reaper", "Terrorists", "Unusual time composition"))
        val scienceFiction = Node("Contemporary or futuristic?", arrayListOf("Time travel experiment", "Cyberpunk police"))
        val hungerGames = Node("With the support of...", arrayListOf("Overly Attached Girlfriend 2.0 (with an axe)", "Bombs", "Legendary heroes"))
        val gamblingWayPointNode = createWayPointNode(
            LeafNode("Kaiji", "https://myanimelist.net/anime/3002/Gyakkyou_Burai_Kaiji__Ultimate_Survivor"),
            LeafNode("Akagi", "https://myanimelist.net/anime/658/Touhai_Densetsu_Akagi__Yami_ni_Maiorita_Tensai")
        )
        val drama = Node("Premise", arrayListOf("Count of Monte Cristo", "Juvenile detention center in the 50's"))
        val ready = Node("No you're not", arrayListOf("Mechas", "Magical Girls"))

        thriller.attach(crimeDrama, scienceFiction, hungerGames, gamblingWayPointNode, drama, ready)


        // Monster node already created
        val paranoiaAgent = LeafNode("Paranoia Agent", "https://myanimelist.net/anime/323/Mousou_Dairinin")
        val deathNote = LeafNode("Death Note", "https://myanimelist.net/anime/1535/Death_Note")
        val terroists = Node("As?", arrayListOf("Protagonists", "Antagonists"))
        val karaNoKyoukai = LeafNode("Kara no Kyoukai", "https://myanimelist.net/anime/2593/Kara_no_Kyoukai_1__Fukan_Fuukei")

        crimeDrama.attach(monster, paranoiaAgent, deathNote, terroists, karaNoKyoukai)


        val zankyouNoTerror = LeafNode("Zankyou no Terror", "https://myanimelist.net/anime/23283/Zankyou_no_Terror")
        val higashiNoEden = LeafNode("Higashi no Eden", "https://myanimelist.net/anime/5630/Higashi_no_Eden")

        terroists.attach(zankyouNoTerror, higashiNoEden)


        val steinsGate = LeafNode("Steins;Gate", "https://myanimelist.net/anime/9253/Steins_Gate")
        val psychoPass = LeafNode("Psycho-Pass", "https://myanimelist.net/anime/13601/Psycho-Pass")

        scienceFiction.attach(steinsGate, psychoPass)


        val miraiNikki = LeafNode("Mirai Nikki", "https://myanimelist.net/anime/10620/Mirai_Nikki")
        val btooom = LeafNode("Btooom!", "https://myanimelist.net/anime/14345/Btooom")
        val fateZero = LeafNode("Fate/Zero", "https://myanimelist.net/anime/10087/Fate_Zero")

        hungerGames.attach(miraiNikki, btooom, fateZero)


        val gankutsuou = LeafNode("Gankutsuou", "https://myanimelist.net/anime/239/Gankutsuou")
        val rainbow = LeafNode("Rainbow", "https://myanimelist.net/anime/6114/Rainbow__Nisha_Rokubou_no_Shichinin")

        drama.attach(gankutsuou, rainbow)


        val nge = LeafNode("Neon Genesis Evangelion -> End of Evangelion", "https://myanimelist.net/anime/30/Neon_Genesis_Evangelion\nhttps://myanimelist.net/anime/32/Neon_Genesis_Evangelion__The_End_of_Evangelion")
        val puellaMagiMadokaMagica = LeafNode("Puella Magi Madoka Magica", "https://myanimelist.net/anime/9756/Mahou_Shoujo_Madoka%E2%98%85Magica")

        ready.attach(nge, puellaMagiMadokaMagica)


        return thrillerAndHorror
    }

}







