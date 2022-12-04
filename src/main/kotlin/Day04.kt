import java.io.File

fun main() {
    Day04().puzzle1()
    Day04().puzzle2()
}

class Day04 {
    private val file = File("inputs/day04.txt")

    private fun IntRange.containsAll(target: IntRange) = this.first <= target.first && this.last >= target.last
    private fun getRangePair() = file.readLines().map { lines ->
        lines.split(",").map { assignments->
            assignments.split("-").let {
                IntRange(it[0].toInt(), it[1].toInt())
            }
        }
    }

    fun puzzle1() {
        println(getRangePair().count { it[0].containsAll(it[1]) || it[1].containsAll(it[0]) })
    }

    fun puzzle2() {
        println(getRangePair().count { it[0].intersect(it[1]).isNotEmpty() })
    }
}