import java.io.File

fun main() {
    Day02().puzzle1()
    Day02().puzzle2()
}

// There may be more elegant solutions here with a set of maps and a generic function
// but the following works as expected

class Day02 {
    private val file = File("inputs/day02.txt")

    fun puzzle1() {
        val total = file.readLines().map {
            val round = it.split(" ")
            val handScore = when(round.last()) {
                "X" -> 1
                "Y" -> 2
                "Z" -> 3
                else -> 0
            }
            val winScore = when(round.first()) {
                "A" -> when(round.last()) {
                    "X" -> 3
                    "Y" -> 6
                    else ->0
                }
                "B" -> when(round.last())  {
                    "Y" -> 3
                    "Z" -> 6
                    else ->0
                }
                "C" ->  when(round.last())  {
                    "X" -> 6
                    "Z" -> 3
                    else ->0
                }
                else -> 0
            }

            handScore + winScore
        }.sum()

        println(total)
    }

    fun puzzle2() {
        val total = file.readLines().map {
            val round = it.split(" ")
            val handScore = when(round.first()) {
                "A" -> when(round.last()) {
                    "X" -> 3 // rock+lose = scissors(3)
                    "Y" -> 1 // rock+draw = rock(1)
                    "Z" -> 2 // rock+win = paper(2)
                    else -> 0
                }
                "B" -> when(round.last()) {
                    "X" -> 1 // paper+lose = rock(1)
                    "Y" -> 2 // paper+draw = paper(2)
                    "Z" -> 3 // paper+win = scissors(3)
                    else -> 0
                }
                "C" -> when(round.last()) {
                    "X" -> 2 // scissors+lose = paper(2)
                    "Y" -> 3 // scissors+draw = scissors(3)
                    "Z" -> 1 // scissors+win = rock(1)
                    else -> 0
                }
                else -> 0
            }
            val winScore = when(round.last()) {
                "X" -> 0
                "Y" -> 3
                "Z" -> 6
                else -> 0
            }
            handScore + winScore
        }.sum()

        println(total)
    }
}