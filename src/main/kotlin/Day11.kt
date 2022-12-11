import java.io.File

fun main() {
    Day11().puzzle1()
    Day11().puzzle2()
}

class Day11 {
    private val file = File("inputs/day11.txt")

    data class Monkey(val num:Int, val items:MutableList<Long>, val op:String, val divisorTest:Int, val trueDest:Int, val falseDest:Int, var count:Long = 0)
    private val monkeys = file.readLines().windowed(7, 7, true) {
        Monkey(
            it[0][7].digitToInt(),
            it[1].substringAfter("Starting items: ").split(", ").map { startItem -> startItem.toLong() }.toMutableList(),
            it[2].substringAfter("Operation: new = old "),
            it[3].substringAfter("Test: divisible by ").toInt(),
            it[4].substringAfter("If true: throw to monkey ").toInt(),
            it[5].substringAfter("If false: throw to monkey ").toInt()
        )
    }

    fun puzzle1() {
        repeat(20) {
            monkeys.forEach { monkey ->
                monkey.items.forEach { item ->
                    // first do operation, then divide by 3, then test
                    val opVal:Long = monkey.op.drop(2).let { if (it == "old") item else it.toLong()}
                    val newScore = when(monkey.op.first()) {
                        '*' -> item * opVal
                        '+' -> item + opVal
                        else -> item
                    } / 3

                    val sendTo = if (newScore % monkey.divisorTest == 0L) monkey.trueDest else monkey.falseDest
                    monkeys[sendTo].items.add(newScore)

                    monkey.count++
                }
                monkey.items.clear() // all have been thrown
            }
        }

        val answer = monkeys.sortedByDescending { it.count }.take(2).let { it[0].count * it[1].count }
        println(answer)
    }

    fun puzzle2() {
        repeat(10_000) {
            monkeys.forEach { monkey ->
                // find a number that all divisor tests are still divisible by (product of all divisors)
                // and then make sure the new worry score doesn't go outside this number (using it as a modulus) to prevent it from escalating
                // the product of all divisors will ensure the division test will continue to work for all future tests
                val div = monkeys.map { it.divisorTest }.reduce { acc, d -> acc * d }
                monkey.items.forEach { item ->
                    // first do operation, then divide by 3, then test
                    val opVal = monkey.op.drop(2).let { if (it == "old") item else it.toLong()}
                    val newScore = when(monkey.op.first()) {
                        '*' -> item * opVal
                        '+' -> item + opVal
                        else -> item
                    } % div

                    val sendTo = if (newScore % monkey.divisorTest == 0L) monkey.trueDest else monkey.falseDest
                    monkeys[sendTo].items.add(newScore)

                    monkey.count++
                }
                monkey.items.clear() // all have been thrown
            }
        }

        monkeys.forEach {
            println("Monkey ${it.num} inspected items ${it.count} times")
        }


        val answer = monkeys.sortedByDescending { it.count }.take(2).let { it[0].count * it[1].count }
        println(answer)

    }
}