import java.io.File

import java.util.ArrayDeque

fun main() {
    Day05().puzzle1()
    Day05().puzzle2()
}

class Day05 {
    private val file = File("inputs/day05.txt")
    data class Instruction(val count:Int, val from:Int, val to:Int)

    private val stacks:MutableList<ArrayDeque<Char>>
    private val instructions:List<Instruction>

    init {
        val inputStrings = file.readLines()
        val stacksInput = inputStrings.takeWhile { it.isNotEmpty() }
        val instructionsInput = inputStrings.takeLast(inputStrings.size - stacksInput.size - 1)

        stacks = stacksInput.dropLast(1).foldRight(mutableListOf<ArrayDeque<Char>>()) { line, stacks ->
            line.chunked(4).map { it[1] }.mapIndexed { idx, crate ->
                if (stacks.getOrNull(idx) == null) stacks.add(idx, ArrayDeque<Char>())
                if (crate != ' ') stacks[idx].push(crate)
            }
            stacks
        }

        instructions = instructionsInput.map {
            it.split(" ").let {
                Instruction(it[1].toInt(), it[3].toInt(), it[5].toInt())
            }
        }
    }


    fun puzzle1() {
        instructions.forEach {
            for (i in 1..it.count) {
                stacks[it.to-1].push(stacks[it.from-1].pop())
            }
        }

        println("Answer: ")
        stacks.forEach { print(it.peek()) }
    }

    fun puzzle2() {
        instructions.forEach {
            val tmpQ = ArrayDeque<Char>()
            for (i in 1..it.count) {
                tmpQ.push(stacks[it.from-1].pop())
            }
            for (i in 1..it.count) {
                stacks[it.to-1].push(tmpQ.pop())
            }
        }

        println("\nAnswer: ")
        stacks.forEach { print(it.peek()) }
    }
}