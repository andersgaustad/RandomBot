package events

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