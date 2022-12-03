import java.io.File

fun main() {
    Day03().puzzle1()
    Day03().puzzle2()
}

class Day03 {
    private val file = File("inputs/day03.txt")

    fun puzzle1() {
        val priorities = file.readLines().map { it.chunked(it.length/2) }.sumOf { compartments ->
            val match = compartments.first().first { c -> compartments.last().contains(c) }
            if (match.isLowerCase()) match.code - 96
            else match.code - 64 + 26
        }
        println(priorities)
    }

    fun puzzle2() {
        val priorities = file.readLines().chunked(3).sumOf { packs ->
            val match = packs.first().first { c -> packs.all { it.contains(c) } }
            if (match.isLowerCase()) match.code - 96
            else match.code - 64 + 26
        }
        println(priorities)
    }
}