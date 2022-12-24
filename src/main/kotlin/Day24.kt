import java.io.File

fun main() {
    Day24().puzzle1()
    Day24().puzzle2()
}

class Day24 {
    data class Blizzard(val pos:Point, val direction:Char)
    data class Point(var x:Int, var y:Int) {
        operator fun plus(other: Point) = Point(x + other.x, y + other.y)
        operator fun rem(boundary: Point) = Point(x % boundary.x, y % boundary.y)
        fun neighbours() = listOf(Point(x+1, y), Point(x-1, y), Point(x, y+1), Point(x, y-1))
    }

    private val map = File("inputs/day24.txt").readLines().map { line -> line.map { c -> c } }
    private val bounds = Point(map[0].size, map.size)
    private val blizzards = map.flatMapIndexed { y, yList -> yList.mapIndexed { x, c ->
        when(c) {
            '>','<','^','v' -> Blizzard(Point(x, y), c)
            else -> null
        }
    } }.filterNotNull()

    private fun moveBlizzards(toMove:List<Blizzard>) =
        toMove.map { blizzard ->
            val step = when (blizzard.direction) {
                '>' -> Point(1, 0)
                '<' -> Point(-1, 0)
                'v' -> Point(0, 1)
                '^' -> Point(0, -1)
                else -> null
            }!!

            var next = blizzard.pos
            while (true) {
                next += step
                next %= bounds
                if (next.x < 0) next.x = bounds.x - 1
                if (next.y < 0) next.y = bounds.y - 1

                if (map[next.y][next.x]=='#') continue
                else break
            }
            Blizzard(next, blizzard.direction)
        }

    private fun findShortestPath(start:Point, end:Point, blizzardStartState:List<Blizzard>): Pair<Int, List<Blizzard>> {
        var locations = mutableMapOf(start to 0)
        var blizzardState = blizzardStartState

        while (!locations.containsKey(end)) {
            blizzardState = moveBlizzards(blizzardState)
            val updatedLocations = mutableMapOf<Point, Int>()

            locations.forEach { (pos:Point, len:Int) ->
                (pos.neighbours() + pos).forEach { moveTo -> // add pos to neighbours, because Stay is a potentially valid next move
                    if (moveTo.x in 0 until bounds.x && moveTo.y in 0 until bounds.y && // check is in bounds, and not a wall, also check it is not in the blizzard
                        blizzardState.none { moveTo == it.pos } && map[moveTo.y][moveTo.x]!='#') {
                        updatedLocations.merge(moveTo, len + 1) { a, b -> a } // if two could be at the same spot, just take one - doesnt matter which
                    }
                }
            }
            locations = updatedLocations
        }

        return locations[end]!! to blizzardState
    }

    fun puzzle1() {
        val (ans, _) = findShortestPath(Point(1, 0), Point(map[0].indices.last - 1, map.indices.last), blizzards)
        println("Part 1 Ans: $ans")
    }

    fun puzzle2() {
        val start = Point(1, 0)
        val end = Point(map[0].indices.last - 1, map.indices.last)
        val (p1, blizzardToContinue) = findShortestPath(start, end, blizzards)
        val (p2, blizzardToContinue2) = findShortestPath(end, start, blizzardToContinue)
        val (p3, _) = findShortestPath(start, end, blizzardToContinue2)

        println("Part 2 Ans: ${p1+p2+p3}")
    }
}