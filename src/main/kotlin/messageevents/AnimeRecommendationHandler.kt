package messageevents

import com.jessecorbett.diskord.api.model.Message
import com.jessecorbett.diskord.api.model.User

/**
The purpose of this class is to recommend an anime to a user based on the user's answers to a series of question.
 In practice, the user goes through a flowchart which is constructed using the "The ultimate anime recommendation flowchart" by reddit user u/lukeatlook
 Disclaimer: I do not own this, and all credits go to u/lukeatlook
 The flowchart may be found at https://imgur.com/q9Xjv4p, and Luke's profile may be found at https://www.reddit.com/user/lukeatlook
 */

class AnimeRecommendationHandler : MessageHandling {
    override suspend fun onMessageCreated(message: Message) {
        val user = message.author
        val currentNode = map[user]

        // Check if we hit a valid node
        if (currentNode != null) {
            val content = message.content
            // If user has typed number
            val number = content.toIntOrNull()
            if (number != null) {
                // Subtract 1 as list is 0-indexed
                // Let kotlin handle exceptions
                val nextNode = currentNode.getChild(number-1)

                // Display next question
                //TODO

                // Advance through tree
                map[user] = nextNode



            }
        }
    }

    val map = mutableMapOf<User, Node>()

    val root = createTree()

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
        storyNotImportant.attach(hundredsOfEpisodes)


        val onePiece = HyperlinkLeafNode("One Piece", "https://en.wikipedia.org/wiki/One_Piece")
        hundredsOfEpisodes.attach(onePiece)


        return root
    }
}