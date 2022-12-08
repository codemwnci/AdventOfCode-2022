import java.io.File

fun main() {
    Day08().puzzle1()
    Day08().puzzle2()
}

class Day08 {
    private val file = File("inputs/day08.txt")

    fun puzzle1() {
        val treeGrid = file.readLines().map { it.toList().map { it.digitToInt() } }

        val numVisible = (treeGrid.size*2 + treeGrid[0].size*2 - 4) +
            (1 until treeGrid.size-1).sumOf { y ->
                (1 until treeGrid[y].size-1).count { x ->
                    (0 until x).all { leftX -> treeGrid[y][leftX] < treeGrid[y][x] } ||
                    (0 until y).all { upY -> treeGrid[upY][x] < treeGrid[y][x] } ||
                    (x+1 until treeGrid[y].size).all { rightX -> treeGrid[y][rightX] < treeGrid[y][x] } ||
                    (y+1 until treeGrid.size).all { downY -> treeGrid[downY][x] < treeGrid[y][x] }
            }
        }

        println("Part 1 Ans: $numVisible")
    }

    fun puzzle2() {
        val treeGrid = file.readLines().map { it.toList().map { it.digitToInt() } }

        val bestScore =
            (1 until treeGrid.size-1).maxOf { y ->
                (1 until treeGrid[y].size-1).maxOf { x ->
                    // minOf deals with adding the tree that it is blocked by, but not adding an additional +1 if at the edge of the map
                    minOf((x-1 downTo 0).takeWhile { leftX -> treeGrid[y][leftX] < treeGrid[y][x] }.count() + 1, (x-1 downTo 0).count()) *
                    minOf((y-1 downTo 0).takeWhile { upY -> treeGrid[upY][x] < treeGrid[y][x] }.count() + 1, (y-1 downTo 0).count()) *
                    minOf((x+1 until treeGrid[y].size).takeWhile { rightX -> treeGrid[y][rightX] < treeGrid[y][x] }.count() + 1, (x+1 until treeGrid[y].size).count()) *
                    minOf((y+1 until treeGrid.size).takeWhile { downY -> treeGrid[downY][x] < treeGrid[y][x] }.count() + 1, (y+1 until treeGrid.size).count())
                }
            }
        println("Part 2 Ans: $bestScore")
    }
}