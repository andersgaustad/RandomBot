package messageevents

import com.jessecorbett.diskord.api.model.Message
import com.jessecorbett.diskord.api.model.User
import com.jessecorbett.diskord.dsl.Bot
import events.Sudoku
import events.createSudokuGame
import kotlinx.serialization.UnstableDefault
import utils.deleteMessage
import utils.sendMessage

@Suppress("EXPERIMENTAL_API_USAGE")
class SudokuHandler : MessageHandling {
    private val map = mutableMapOf<User, SudokuDataObject>()

    @UnstableDefault
    override suspend fun onMessageCreated(message: Message, bot: Bot) {

    }

    suspend fun createSudokuBoard(message: Message, difficultyFactor: Int, bot: Bot) {
        val user = message.author
        val cluesToRemove = 10 + 9 * difficultyFactor // Might tweak this later (For difficulties 1-5?)

        val sudoku = createSudokuGame(3, cluesToRemove)
        val boardString = "Board of ${user.username}:\n$sudoku"
        val sudokuBoard = sendMessage(boardString, message.channelId, bot.clientStore)
        map[user] = SudokuDataObject(sudoku, sudokuBoard)
    }

    suspend fun repostSudokuBoard(message: Message, bot: Bot) {
        val user = message.author
        val sudokuData = map[user]

        if (sudokuData != null) {
            val resentSudoku = sendMessage(sudokuData.sudoku.toString(), message.channelId, bot.clientStore)
            if (sudokuData.sudokuBoard != null) {
                deleteMessage(sudokuData.sudokuBoard!!.id, message.channelId, bot.clientStore)
                sudokuData.sudokuBoard = resentSudoku // This should work as sudokuboard is mutable
            }
        }

    }

    fun handleSudokuNumberInsertion(user: User, rowIndex: Int, columnIndex: Int, input: Int) : Boolean {
        val sudokuData = map[user]

        return if (sudokuData != null) {
            val userGrid = sudokuData.sudoku.grid
            val solution = sudokuData.sudoku.solution
            val currentUserFieldValue = userGrid[rowIndex, columnIndex]
            val correctValue = solution[rowIndex, columnIndex]

            // If space is empty and guess is correct, update value and return true
            if (currentUserFieldValue != 0 && correctValue == input) {
                userGrid[rowIndex, columnIndex] = input
                true

            } else {
                false
            }

        } else {
            false
        }
    }

}

data class SudokuDataObject(val sudoku: Sudoku, var sudokuBoard: Message?)