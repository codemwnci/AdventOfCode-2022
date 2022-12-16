import java.io.File
import java.util.*

// This one was pretty tricky - tried a few different approaches
// This one is the cleanest approach, with a lot of influence from other people's solutions
// My recursive solution (depth first search) even with a few optimisations was too inefficient

fun main() {
    Day16().puzzle1()
    Day16().puzzle2()
}

class Day16 {
    private val file = File("inputs/day16.txt")
    data class Node(val name:String, val flow: Int = 0, val exits:List<String>)

    val nodes = file.readLines().map {
        Node(
            it.substring(6, 8),
            it.substringAfter("=").takeWhile { c-> c != ';' }.toInt(),
            it.substringAfter("valve").drop(1).split(",").map { it.trim() }
        )
    }.sortedByDescending { it.flow }

    // rather than recalculate a brute force path, just know for each node, the shortest path to any node with a
    // valve that has a flow, that we can then lookup when carrying out the search
    // this creates a start node mapped to an end node mapped to a distance
    val distances = nodes.associateWith { from ->
        nodes.filter { it.flow > 0 }.associateWith { to ->
            shortestDistance(from, to)
        }
    }

    private fun shortestDistance(from: Node, to: Node): Int {

        data class Dist(val node:Node, var distance: Int = Int.MAX_VALUE)
        val all = nodes.map { Dist(it) }.associateBy { it.node.name }
        val d1 = all[from.name]!!
        val dTarget = all[to.name]!!

        val queue = PriorityQueue<Dist>(compareBy { it.distance })
        d1.distance = 0
        queue.add(d1)

        while (true) {
            val d = queue.remove()
            if (d == dTarget) {
                return dTarget.distance
            }
            d.node.exits.forEach {
                val dx = all[it]!!
                if (dx.distance == Int.MAX_VALUE) {
                    dx.distance = d.distance + 1
                    queue.add(dx)
                }
            }
        }
    }
    fun solve(timeLeft:Int, withElephants:Boolean):Int {
        val start = nodes.first { it.name == "AA" }
        var maxFlowReleased = 0


        fun search(pressureTally:Int, pos:Node, visited:List<Node>, timeSpent:Int, shouldSpawnElephants:Boolean) {
            maxFlowReleased = maxOf(pressureTally, maxFlowReleased)

            if (!shouldSpawnElephants) {
                val stillToVisit = (nodes - visited)
                var index = 0
                val possibleFlowRelease = (timeLeft - timeSpent downTo 0 step 2).sumOf {
                    stillToVisit[index++].flow * it
                }
                if (possibleFlowRelease + pressureTally < maxFlowReleased) return
            }

            distances.getValue(pos).forEach { (maybeTarget, distance) ->
                val newMinute = timeSpent + distance + 1
                if (newMinute < timeLeft && maybeTarget !in visited) {
                    search(pressureTally + (timeLeft - newMinute) * maybeTarget.flow,
                        maybeTarget, visited + maybeTarget, newMinute, shouldSpawnElephants)
                }
            }

            if (shouldSpawnElephants) {
                // don't want the elephant spawning their own elephants
                search(pressureTally, start, visited,0,false)
            }
        }

        search(0, start, emptyList(), 0, withElephants)

        return maxFlowReleased
    }

    fun puzzle1() = println("Part 1 Ans: " + solve(30, false))
    fun puzzle2() = println("Part 2 Ans: " + solve(26, true))
}