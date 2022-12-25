import java.io.File
import kotlin.math.abs
import kotlin.math.log
import kotlin.math.pow

fun main() {
    Day25().puzzle1()
}

class Day25 {
    private fun pow(num:Int, toPower: Int): Long = num.toDouble().pow(toPower.toDouble()).toLong()

    fun puzzle1() {
        val map = mapOf('0' to 0, '1' to 1, '2' to 2, '-' to -1, '=' to -2)
        var decimal = File("inputs/day25.txt").readLines().sumOf { line ->
            var index = 0
            line.foldRight(0L) { c, acc -> acc + map[c]!! * (Math.pow(5.0, index++.toDouble())).toLong()}
        }

        println("Decimal: $decimal")

        var snafu = ""
        for (power in log(decimal.toDouble(), 5.0).toInt() downTo 0) {
            listOf(-2, -1, 0, 1, 2)
                .minByOrNull { abs(decimal - pow(5, power) * it) }
                .also {
                    decimal -= pow(5, power) * it!!
                    snafu += "=-012"[it + 2]
                }
        }
        println("Snafu: $snafu")
    }
}