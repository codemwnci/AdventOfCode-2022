import java.io.File

fun main() {
    Day10().puzzle1()
    Day10().puzzle2()
}

class Day10 {
    private val file = File("inputs/day10.txt")
    class SigPr(val cycles:Int, val toAdd:Int)

    fun puzzle1() {

        var currentCycle = 0
        var currX = 1

        val totalSignal = file.readLines().sumOf { line ->
            line.split(" ").let { ins ->
                val res = when(ins[0]) {
                    "noop" -> SigPr(1, 0)
                    "addx" -> SigPr(2, ins[1].toInt())
                    else -> SigPr(0,0)
                }

                var toReturn = 0
                // add cycles
                repeat(res.cycles) {
                    currentCycle++
                    if ( (currentCycle - 20) % 40 == 0) toReturn = currX * currentCycle
                }

                currX += res.toAdd

                toReturn
            }
        }

        println(totalSignal)
    }

    fun puzzle2() {
        var currentCycle = 0
        var currX = 1

        file.readLines().forEach { line ->
            line.split(" ").let { ins ->
                val res = when (ins[0]) {
                    "noop" -> SigPr(1, 0)
                    "addx" -> SigPr(2, ins[1].toInt())
                    else -> SigPr(0, 0)
                }

                repeat(res.cycles) {
                    if (currentCycle % 40 == 0) println("")
                    print( if (currentCycle % 40 in (currX-1)..(currX+1)) "#" else ".")
                    currentCycle++
                }
                currX += res.toAdd
            }
        }
    }
}