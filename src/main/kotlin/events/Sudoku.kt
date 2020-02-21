package events

import java.util.concurrent.ThreadLocalRandom
import kotlin.math.pow
import kotlin.math.sqrt

class Sudoku(val grid: Matrix<Int>, val solution: Matrix<Int>) {
    override fun toString(): String {
        val sb = StringBuilder()
        val special = sqrt(grid.array.size.toDouble()).toInt()


        fun shouldInsertVerticalBorder(index: Int) = (index % (special) == special-1) && index != grid.array.size-1

        fun shouldInsertHorizontalBorder(x: Int, y: Int) = shouldInsertVerticalBorder(x) && y == grid.array.size-1


        fun createHorizontalBorder() : String {
            val horizontalSb = StringBuilder()
            for (i: Int in 0 until special) {
                for (j : Int in 0 until special) {
                    horizontalSb.append("-")
                }
                horizontalSb.append("+")
            }
            return horizontalSb.toString().substring(0, special-1)
        }

        grid.forEachIndexed { x, y, t ->
            sb.append(t.toString())
            when {
                shouldInsertHorizontalBorder(x, y) -> sb.append(createHorizontalBorder())
                shouldInsertVerticalBorder(y) -> sb.append("|")
                else -> sb.append(t.toString())
            }
        }

        return sb.toString()
    }

    fun isSolved() = grid == solution

}

class Matrix<T>(val x: Int, val y: Int, val array: Array<Array<T>>) {
    companion object {
        inline operator fun <reified T> invoke() = Matrix(0, 0, Array(0) { emptyArray<T>()})

        inline operator fun <reified T> invoke(x: Int, y: Int) = Matrix(x, y, Array(x) { arrayOfNulls<T>(y)})

        inline operator fun <reified T> invoke(x: Int, y: Int, operator: (Int, Int) -> (T)): Matrix<T> {
            val array = Array(x) {outer ->
                Array(y) { inner ->
                    operator(outer, inner)
                }
            }
            return Matrix(x, y, array)

        }
    }

    operator fun get(x: Int, y: Int) = array[x][y]

    operator fun set(x: Int, y: Int, t: T) {
        array[x][y] = t
    }

    inline fun forEach(operation: (T) -> Unit) {
        array.forEach {
            it.forEach {t ->
                operation.invoke(t)
            }
        }
    }

    inline fun forEachIndexed(operation: (x: Int, y: Int, T) -> Unit) {
        array.forEachIndexed { rowIndex, p ->
            p.forEachIndexed { columnIndex, t ->
                operation.invoke(rowIndex, columnIndex, t)
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other is Matrix<*>) {
            val myRowDimensions = array.size
            val myColumnDimensions = array[0].size

            val otherArray = other.array
            val otherRowDimensions = otherArray.size
            val otherColumnDimensions = otherArray[0].size

            if (myRowDimensions == otherRowDimensions && myColumnDimensions == otherColumnDimensions) {
                forEachIndexed { x, y, _ ->
                    val myElement = this[x, y]
                    val otherElement = other[x, y]

                    if (myElement != otherElement) {
                        return false
                    }
                }
                return true

            } else {
                return super.equals(other)
            }

        } else {
            return super.equals(other)
        }
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        result = 31 * result + array.contentDeepHashCode()
        return result
    }



}

fun createSudokuGame(n: Int, cluesToRemove: Int) : Sudoku {
    val dimensions = n.toDouble().pow(2).toInt()

    // Place random numbers (17 are required for unique solution
    do {
        // Get base that may or may not have a solution
        val base = createBaseBoard(dimensions)

        // If no solution, this is null
        val solution = solve(base)

        val hasSolution = solution != null

        if (hasSolution) {
            val array = solution!!.array
            val playerBoard = Matrix(array.size, array.size, array)

            for (i in 0 until cluesToRemove) {
                do {
                    val randomX = ThreadLocalRandom.current().nextInt(dimensions)
                    val randomY = ThreadLocalRandom.current().nextInt(dimensions)

                    val value = playerBoard[randomX, randomY]

                    val retry = value == 0

                    if (!retry) {
                        playerBoard[randomX, randomY] = 0

                    }
                } while (retry)
            }

            if (hasSolution) {
                return Sudoku(playerBoard, solution)
            }

        }

    } while (true)

}

fun createBaseBoard(dimensions: Int) : Matrix<Int> {
    val gridSolution = Matrix(dimensions, dimensions) { _, _ -> 0 } // Magic

    var placed = 0
    while (placed < 18) {
        val r = ThreadLocalRandom.current().nextInt(0, dimensions-1)
        val c = ThreadLocalRandom.current().nextInt(0, dimensions-1)
        val t = ThreadLocalRandom.current().nextInt(1, dimensions)

        if (gridSolution[r, c] == 0) {
            gridSolution[r, c] = t
            val debugBool = isValidPlacement(gridSolution, r, c)
            if (isValidPlacement(gridSolution, r, c)) {
                placed++

            } else {
                gridSolution[r, c] = 0
            }



        }
    }

    return gridSolution
}

fun noDuplicates(arrayOfElements : Array<Int>) : Boolean {
    val discovered = mutableSetOf<Int>()

    arrayOfElements.forEach {
        if (discovered.contains(it) && it != 0) {
            return false

        } else {
            discovered.add(it)
        }
    }

    // if loop completes, no duplicates are found
    return true
}

fun isValidPlacement(grid: Matrix<Int>, rowIndex: Int, columnIndex: Int) = isValidRow(grid, rowIndex) && isValidColumn(grid, columnIndex) && isValidBox(grid, rowIndex, columnIndex)

fun getLine(grid: Matrix<Int>, index: Int, isRow: Boolean) : Array<Int> {
    return Array(grid.array.size) {
        if (isRow) {
            grid.array[index][it]

        } else {
            grid.array[it][index]
        }
    }
}

fun getBox(grid: Matrix<Int>, rowIndex: Int, columnIndex: Int) : Array<Int> {
    val n = sqrt(grid.array.size.toDouble()).toInt()

    val xShift = (rowIndex / n)
    val yShift = (columnIndex / n)

    val array = IntArray(grid.array.size)

    for (i in 0 until n) {
        for( j in 0 until n) {
            array[i*n + j] = grid[i + n*xShift, j + n*yShift]
        }
    }

    return array.toTypedArray()
}


fun validLine(grid: Matrix<Int>, index: Int, isRow: Boolean) = noDuplicates(getLine(grid, index, isRow))

fun isValidRow(grid: Matrix<Int>, rowIndex: Int) = validLine(grid, rowIndex, true)

fun isValidColumn(grid: Matrix<Int>, columnIndex: Int) = validLine(grid, columnIndex, false)

fun isValidBox(grid: Matrix<Int>, rowIndex: Int, columnIndex: Int) = noDuplicates(getBox(grid, rowIndex, columnIndex))

fun solve(grid: Matrix<Int>) : Matrix<Int>? {
    // Check if grid is solved
    grid.forEachIndexed { x, y, t ->
        // If t == 0 it is not solved yet
        if (t == 0) {
            // Depth first search
            for (i in 1..9) {
                val array = grid.array
                val newBoard = Matrix(array.size, array.size, array)
                newBoard[x, y] = i


                val debugRow = getLine(newBoard, x, true).toIntArray()
                val debugCol = getLine(newBoard, y, false).toIntArray()
                val debugBox = getBox(newBoard, x, y).toIntArray()

                val debugValidRow = isValidRow(newBoard, x)
                val debugValidCol = isValidColumn(newBoard, y)
                val debugValidBox = isValidBox(newBoard, x, y)

                val debugValid = isValidPlacement(newBoard, x, y)

                if (isValidPlacement(newBoard, x, y)) {
                    // Only return when not null
                    val subsolution = solve(newBoard)
                    if (subsolution != null) {
                        return subsolution
                    }

                    //solve(newBoard).let { return it }
                }

            }

            // If current board has no solutions, return null
            return null

        }
    }



    // If iterating through all spaces results in no empty spaces we have found a final solution
    return grid
}
