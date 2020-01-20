package events

import java.util.concurrent.ThreadLocalRandom
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

}

fun createSudokuGame(n: Int, cluesToRemove: Int) : Sudoku {
    val dimensions = n*n

    // Place random numbers (17 are required for unique solution


    do {
        // Get base thet may or may not have a solution
        val base = createBaseBoard()


    }
}

fun createBaseBoard(dimensions: Int) : Matrix<Int> {
    val gridSolution = Matrix<Int>(dimensions, dimensions) { _, _ -> 0 } // Magic

    var placed = 0
    while (placed < 18) {
        val r = ThreadLocalRandom.current().nextInt(0, dimensions-1)
        val c = ThreadLocalRandom.current().nextInt(0, dimensions-1)
        val t = ThreadLocalRandom.current().nextInt(1, dimensions)

        if (gridSolution[r, c] == 0) {
            gridSolution[r, c] = t
            placed++
        }
    }

    return gridSolution
}

fun solve(grid: Matrix<Int>) : Matrix<Int>? {
    // Check if grid is solved
    grid.forEachIndexed { x, y, t ->
        // Check if valid, return null if not
        fun validLine(index: Int, isRow: Boolean) : Boolean {
            val array = Array(grid.array.size) {
                if (isRow) {
                    grid.array[it][index]

                } else {
                    grid.array[index][it]
                }
            }

            return noDuplicates(array)

        }


        fun isValidRow(rowIndex: Int) = validLine(rowIndex, true)

        fun isValidColumn(columnIndex: Int) = validLine(columnIndex, false)

        fun isValidBox(rowIndex: Int, columnIndex: Int) : Boolean {
            val n = sqrt(grid.array.size.toDouble()).toInt()

            val xShift = (rowIndex % n) * n
            val yShift = (columnIndex % n) * n

            val array = IntArray(grid.array.size)

            for (i in 0 until n) {
                for( j in 0 until n) {
                    array[i*n + j] = grid[i + xShift, j + yShift]
                }
            }

            return noDuplicates(array.toTypedArray())
        }

        // End of local functions

        // Check constraints
        if (isValidRow(x) && isValidColumn(y) && isValidBox(x, y)) {

            // If t == 0 it is not solved yet
            if (t == 0) {
                // Depth first search
                // TODO Continue

            }

        } else {
            return null
        }
    }
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

