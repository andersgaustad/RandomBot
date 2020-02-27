package messageevents

import com.jessecorbett.diskord.api.model.Message
import com.jessecorbett.diskord.api.model.User
import com.jessecorbett.diskord.dsl.Bot
import com.jessecorbett.diskord.util.ClientStore
import com.jessecorbett.diskord.util.mention
import events.Sudoku
import events.createSudokuGame
import kotlinx.serialization.UnstableDefault
import utils.EmojiCompanion
import utils.deleteMessage
import utils.react
import utils.sendMessage

@Suppress("EXPERIMENTAL_API_USAGE")
class SudokuHandler : MessageHandling {
    val map = mutableMapOf<User, SudokuDataObject>()

    @UnstableDefault
    override suspend fun onMessageCreated(message: Message, bot: Bot) {
        fun checkIfSudokuInput(trimmed: String) : Boolean {
            // Input should after trim be on form:
            // [x][y]=t


            // Will only work on sudokus with one digit numbers
            if (trimmed.length != 8) {
                return false
            }

            val intCheckerArray = arrayOf(trimmed[1], trimmed[4], trimmed[7])
            val leftBracketArray = arrayOf(trimmed[0], trimmed[3])
            val rightBracketArray = arrayOf(trimmed[2], trimmed[5])
            val equalSign = trimmed[6] == '='

            // Kotlin magic
            return intCheckerArray.all { it.toString().toIntOrNull() != null } && leftBracketArray.all { it == '[' } && rightBracketArray.all { it == ']' } && equalSign

        }

        // Check if user has live sudoku
        val user = message.author
        val sudokuData = map[user]
        if (sudokuData != null) {
            val trimmed = message.content.replace(" ", "")
            // Check if input is sudoku
            if (checkIfSudokuInput(trimmed)) {
                val rowIndex = trimmed[1].toString().toInt()-1
                val columnIndex = trimmed[4].toString().toInt()-1
                val value = trimmed[7].toString().toInt()

                // Handle insertion
                val wasCorrectGuess = handleSudokuNumberInsertion(message.author, rowIndex, columnIndex, value)

                // React with feedback
                reactOnInput(message, bot.clientStore, wasCorrectGuess)

                // If correct, repost sudoku
                if (wasCorrectGuess) {
                    repostSudokuBoard(message, bot.clientStore)
                }

                // Should also check if sudoku now is solved
                if (sudokuData.sudoku.isSolved()) {
                    sendMessage("Sudoku solved!", message.id, bot.clientStore)
                    // Flush user data from map
                    map.remove(message.author)
                }

            }

        }

    }

    suspend fun createSudokuBoard(message: Message, difficultyFactor: Int, clientStore: ClientStore) {
        val user = message.author

        // Give feedback; sudoku creation may take some time
        sendMessage("Creating sudoku for ${user.mention} with difficulity $difficultyFactor, please wait", message.channelId, clientStore)

        val cluesToRemove = 10 + 9 * difficultyFactor // Might tweak this later (For difficulties 1-5?)

        val sudoku = createSudokuGame(3, cluesToRemove)
        val boardString = "Board of ${user.username}:\n$sudoku"
        val sudokuBoard = sendMessage(boardString, message.channelId, clientStore)
        map[user] = SudokuDataObject(sudoku, sudokuBoard)
    }

    suspend fun repostSudokuBoard(message: Message, clientStore: ClientStore) {
        val user = message.author
        val sudokuData = map[user]

        if (sudokuData != null) {
            val resentSudoku = sendMessage(sudokuData.sudoku.toString(), message.channelId, clientStore)
            if (sudokuData.sudokuBoard != null) {
                deleteMessage(sudokuData.sudokuBoard!!.id, message.channelId, clientStore)
                sudokuData.sudokuBoard = resentSudoku // This should work as sudokuboard is mutable
            }
        }

    }

    fun handleSudokuNumberInsertion(user: User, rowIndex: Int, columnIndex: Int, input: Int) : Boolean {
        val sudokuData = map[user]
        try {
            return if (sudokuData != null) {
                val userGrid = sudokuData.sudoku.grid
                val solution = sudokuData.sudoku.solution
                val currentUserFieldValue = userGrid[rowIndex, columnIndex]
                val correctValue = solution[rowIndex, columnIndex]

                // If space is empty and guess is correct, update value and return true
                if (correctValue == input) {
                    userGrid[rowIndex, columnIndex] = input
                    true

                } else {
                    false
                }

            } else {
                false
            }

        } catch(e: Exception) {
            return false
        }

    }

    private suspend fun reactOnInput(message: Message, clientStore: ClientStore, correctGuess: Boolean) {
        val reaction = if (correctGuess) {
            // White check mark
            EmojiCompanion.white_check_mark

        } else {
            // Red cross
            EmojiCompanion.x

        }

        react(reaction, message.id, message.channelId, clientStore)
    }

}

data class SudokuDataObject(val sudoku: Sudoku, var sudokuBoard: Message?)