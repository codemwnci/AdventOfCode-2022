import java.io.File

fun main() {
    Day06().puzzle1()
    Day06().puzzle2()
}

class Day06 {
    private val file = File("inputs/day06.txt")

    private fun findFirstMarker(uniqueLength:Int) = file.readText().windowed(uniqueLength, 1).takeWhile {
        it.toList().distinct().count() != uniqueLength
    }.size + uniqueLength

    fun puzzle1() {
        println(findFirstMarker(4))
    }

    fun puzzle2() {
        println(findFirstMarker(14))
    }
}