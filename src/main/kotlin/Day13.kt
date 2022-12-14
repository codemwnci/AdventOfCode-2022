import java.io.File

// build system was screwing up (because I'm not using Gradle), so couldn't get JSON working

fun main() {
    Day13().puzzle1()
    Day13().puzzle2()
}

class Day13 {
    private val file = File("inputs/day13.txt")

    sealed interface IntOrList:Comparable<IntOrList> {
        override fun compareTo(other: IntOrList):Int {
            return when (this) {
                is MaybeInt -> when (other) {
                    is MaybeInt -> this.value.compareTo(other.value)
                    is MaybeList -> MaybeList(listOf(this)).compareTo(other) // promote this MaybeInt to MaybeList and compare again
                }
                is MaybeList -> when (other) {
                    is MaybeInt -> this.compareTo(MaybeList(listOf(other))) // promote other MaybeInt to MaybeList and compare again
                    is MaybeList -> {
                        val thisItr = this.value.iterator()
                        val otherItr = other.value.iterator()
                        while(thisItr.hasNext() && otherItr.hasNext()) {
                            val compare = thisItr.next().compareTo(otherItr.next())
                            if (compare != 0) return compare
                        }
                        this.value.size.compareTo(other.value.size)
                    }
                }
            }
        }
    }
    data class MaybeInt(val value:Int): IntOrList
    data class MaybeList(val value:List<IntOrList>): IntOrList


    private fun parseIntOrList(line: String): IntOrList {
        fun recursiveParseIntOrList(line: String): Pair<IntOrList, String> {
            // deal with numbers first
            if (line[0] != '[') {
                val num = line.takeWhile { it.isDigit() } // this is quicker than searching for other terminating items , such as , ] etc
                return MaybeInt(num.toInt()) to line.substring(num.length)
            }

            var curr = line.substring(1)
            val list = mutableListOf<IntOrList>()
            while (curr.isNotEmpty()) {
                if (curr[0] == ']') return MaybeList(list) to curr.substring(1)
                else if (curr[0] == ',') curr = curr.substring(1)

                val (intOrList, tail) = recursiveParseIntOrList(curr)
                list.add(intOrList)
                curr = tail
            }
            return MaybeList(list) to ""
        }

        // return the result of the recursive parse
        return recursiveParseIntOrList(line).first
    }

    fun puzzle1() {
        val pairs = file.readLines().windowed(2, 3).map {
            val first = parseIntOrList(it[0])
            val second = parseIntOrList(it[1])
            first to second
        }

        val ans = pairs.withIndex().filter { it.value.first < it.value.second }.sumOf { it.index + 1 }
        println("Part 1 Ans: $ans")
    }


    fun puzzle2() {
        val packets = file.readLines().filter { it.isNotEmpty() }.map { parseIntOrList(it) }.toMutableList()
        packets.add(parseIntOrList("[[2]]"))
        packets.add(parseIntOrList("[[6]]"))
        packets.sort()

        val ans = (packets.indexOf(parseIntOrList("[[2]]")) + 1) * (packets.indexOf(parseIntOrList("[[6]]")) + 1)
        println("Part 2 Ans: $ans")
    }
}