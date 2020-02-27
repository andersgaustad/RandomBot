package commandlogic

import com.jessecorbett.diskord.api.model.Message
import com.jessecorbett.diskord.util.ClientStore
import com.jessecorbett.diskord.util.words
import messageevents.SudokuHandler
import utils.EmojiCompanion
import utils.react

class SudokuCommand(private val sudokuHandler: SudokuHandler, private val clientStore: ClientStore) : Command() {
    override val name: String
        get() = "sudoku"

    override fun parseMessage(message: Message): String {
        return ""
    }

    private val defaultDifficulity = 2

    suspend fun parseSuspendingCommand(message: Message) {
        val words = message.words
        if (words.size == 2) {
            val secondArgument = words[1]
            val difficulityIfPresent = secondArgument.toIntOrNull()

            if (difficulityIfPresent != null) {
                sudokuHandler.createSudokuBoard(message, difficulityIfPresent, clientStore)

            } else if (secondArgument == "reset") {
                sudokuHandler.map.remove(message.author)
                react(EmojiCompanion.white_check_mark, message.id, message.channelId, clientStore)

            }

        } else if (words.size == 1) {
            val sudoku = sudokuHandler.map[message.author]

            if (sudoku == null) {
                sudokuHandler.createSudokuBoard(message, defaultDifficulity, clientStore)
            }

            // Repost after sudoku board is guaranteed to be created
            sudokuHandler.repostSudokuBoard(message, clientStore)

        }

    }



}