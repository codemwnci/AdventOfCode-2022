import java.io.File

// Part 1 was easy enough.
// Part 2 was a little hacky using the solution to part 1 as a starting point though.
// Part 2 works, because I know that the human score is on the right hand side, so can do b-a, rather than a-b when evaluating "root"
// However, a better way to achieve this would be to rather than use a stack to wait until we have the numeric value
// of each algebraic symbol (monkey name), we could instead use recursion, and then find out if the left hand side or the right hand side
// of the root operation stay the same when the human number changes (and therefore programatically know which number to subtract from). This
// may also be quicker because when goal seeking we only have to re-evaluate the side with the human number in it (because the other
// side will re-evaluate the same each time).
// Possibly the most elegant solution of all though, would be to take the algebraic equation and simplify it, so that the
// human score was the only value on the left-hand side. That approach was nowhere near as easy as a goal-seek algorithm though, and
// goal-seek was pretty quick to execute, and therefore the elegant solution was not worth it.

fun main() {
    Day21().puzzle1()
    Day21().puzzle2()
}

class Day21 {
    private val file = File("inputs/day21.txt")

    fun puzzle1() {
        val numbers = mutableMapOf<String, Long>()
        val q = ArrayDeque<Pair<String, String>>()

        file.readLines().forEach{ line ->
            val parts = line.split(": ")
            val monkey = parts[0]
            if (parts[1].toIntOrNull() != null) {
                numbers[monkey] = parts[1].toLong()
            }
            else {
                q.add(parts[0] to parts[1])
            }
        }

        while(q.isNotEmpty()) {
            val instr = q.removeLast()
            Regex("(\\w+) (.) (\\w+)").matchEntire(instr.second)!!.destructured.let { (m1, op, m2) ->
                if (numbers.containsKey(m1) && numbers.containsKey(m2)) {
                    val a = numbers[m1]!!
                    val b = numbers[m2]!!
                    when(op) {
                        "+" -> numbers[instr.first] = a + b
                        "-" -> numbers[instr.first] = a - b
                        "*" -> numbers[instr.first] = a * b
                        "/" -> numbers[instr.first] = a / b
                    }
                } else q.addFirst(instr) // if we can't find both algebraic values, add it to the bottom of the stack and process later
            }
        }

        println("Part 1 Ans: ${numbers["root"]}")

    }

    private val input = file.readLines()
    private fun passEqualityCheck(humanNum:Double): Double {

        val numbers = mutableMapOf<String, Double>()
        val q = ArrayDeque<Pair<String, String>>()

        input.forEach { line ->
            val parts = line.split(": ")
            val monkey = parts[0]
            if (parts[1].toIntOrNull() != null) {
                numbers[monkey] = if (monkey != "humn") parts[1].toDouble() else humanNum
            } else {
                q.add(parts[0] to parts[1])
            }
        }

        while (q.isNotEmpty()) {
            val instr = q.removeLast()
            Regex("(\\w+) (.) (\\w+)").matchEntire(instr.second)!!.destructured.let { (m1, op1, m2) ->
                val op = if (instr.first == "root") "=" else op1 // cater for special case where root op is an "=" not what it says in the file

                if (numbers.containsKey(m1) && numbers.containsKey(m2)) {
                    val a = numbers[m1]!!
                    val b = numbers[m2]!!
                    when (op) {
                        "=" -> numbers[instr.first] = b - a
                        "+" -> numbers[instr.first] = a + b
                        "-" -> numbers[instr.first] = a - b
                        "*" -> numbers[instr.first] = a * b
                        "/" -> numbers[instr.first] = a / b
                    }
                } else q.addFirst(instr)
            }
        }

        return numbers["root"]!!
    }

    // use goal seek
    fun puzzle2() {
        var low = 0.0
        var high = 1e15 // 1*10^15 is next largest round number after the answer to part 1. Slightly quicker than Double.MAX_VALUE
        var humanNum = -1.0
        while (low < high) {
            humanNum = (low + high) / 2.0
            val check = passEqualityCheck(humanNum)
            if (check == 0.0) break
            else if (check < 0) low = humanNum
            else high = humanNum
        }
        println("Part 2 Ans: ${humanNum.toLong()}")
    }
}