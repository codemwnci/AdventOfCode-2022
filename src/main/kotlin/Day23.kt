import java.io.File

fun main() {
    Day23().puzzle1()
    Day23().puzzle2()
}

class Day23 {
    private val file = File("inputs/day23.txt")
    data class Elf(val id:Int, var x:Int, var y:Int, var proposed:Char? = null) {
        fun isNeighbour(other:Elf) = other.x in x-1..x+1 && other.y in y-1..y+1 && other.id != id
        fun isNNeighbour(other:Elf) = other.y == y-1 && other.x in x-1..x+1
        fun isSNeighbour(other:Elf) = other.y == y+1 && other.x in x-1..x+1
        fun isWNeighbour(other:Elf) = other.x == x-1 && other.y in y-1..y+1
        fun isENeighbour(other:Elf) = other.x == x+1 && other.y in y-1..y+1
        fun proposedPosition() = when(proposed) {
            'N' -> x to y-1
            'S' -> x to y+1
            'W' -> x-1 to y
            'E' -> x+1 to y
            else -> x to y
        }
    }
    private val dirOrder:ArrayDeque<Char> = ArrayDeque(listOf('N', 'S', 'W', 'E'))

    fun puzzle1() {
        val elves = mutableListOf<Elf>()
        var idCount = 0
        file.readLines().forEachIndexed { y , line ->
            line.forEachIndexed { x, c ->
                if (c == '#') elves.add(Elf(idCount++, x, y))
            }
        }

        repeat(10) { round ->
            // make proposals
            elves.forEach { elf ->
                elf.proposed = null
                // only move if there is a neighbour
                if (elves.any { elf.isNeighbour(it) }) {
                    elf.proposed = dirOrder.firstOrNull { dir ->
                        when (dir) {
                            'N' -> !elves.any { elf.isNNeighbour(it) }
                            'S' -> !elves.any { elf.isSNeighbour(it) }
                            'W' -> !elves.any { elf.isWNeighbour(it) }
                            'E' -> !elves.any { elf.isENeighbour(it) }
                            else -> false // should never happen
                        }
                    }
                }
            }

            // remove all that will end up in the same position
            elves.filterNot { elf ->
                // check for any equal proposed positions (but don't test the elf against itself)
                elves.any { elf.id != it.id && elf.proposedPosition() == it.proposedPosition() }
            }.forEach {
                val (x, y) = it.proposedPosition()
                it.x = x
                it.y = y
            }

            dirOrder.addLast(dirOrder.removeFirst())
        }

        val ans = (elves.maxOf { it.x }+1-elves.minOf { it.x }) * (elves.maxOf { it.y }+1-elves.minOf { it.y }) - elves.count()
        println("Part 1 Ans: $ans")
    }

    fun puzzle2() {
        val elves = mutableListOf<Elf>()
        var idCount = 0
        file.readLines().forEachIndexed { y , line ->
            line.forEachIndexed { x, c ->
                if (c == '#') elves.add(Elf(idCount++, x, y))
            }
        }

        repeat(10_000) { round ->
            // make proposals
            elves.forEach { elf ->
                elf.proposed = null
                // only move if there is a neighbour
                if (elves.any { elf.isNeighbour(it) }) {
                    elf.proposed = dirOrder.firstOrNull { dir ->
                        when (dir) {
                            'N' -> !elves.any { elf.isNNeighbour(it) }
                            'S' -> !elves.any { elf.isSNeighbour(it) }
                            'W' -> !elves.any { elf.isWNeighbour(it) }
                            'E' -> !elves.any { elf.isENeighbour(it) }
                            else -> false // should never happen
                        }
                    }
                }
            }

            // remove all that will end up in the same position
            val toProcess = elves.filterNot { elf ->
                // check for any equal proposed positions (but don't test the elf against itself)
                elves.any { elf.id != it.id && elf.proposedPosition() == it.proposedPosition() }
            }
            .filter {
                val (x, y) = it.proposedPosition()
                it.x != x || it.y != y
            }

            if (toProcess.isEmpty()) {
                println("Part 2 Ans: ${round+1}")
                return
            }

            toProcess.forEach {
                val (x, y) = it.proposedPosition()
                it.x = x
                it.y = y
            }

            dirOrder.addLast(dirOrder.removeFirst())
        }
    }
}