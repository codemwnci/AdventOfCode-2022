import java.io.File
import java.util.PriorityQueue

fun main() {
    Day12().puzzle1()
    Day12().puzzle2()
}

class Day12 {
    private val file = File("inputs/day12.txt")

    data class GridPoint(val x:Int, val y:Int) {
        fun neighbours(xBoundary: Int, yBoundary: Int): List<GridPoint> =
            listOf(GridPoint(x+1, y), GridPoint(x-1, y), GridPoint(x, y+1), GridPoint(x, y-1))
                .filter { it.x in 0 until xBoundary && it.y  in 0 until yBoundary} // exclude points that go outside the boundary
    }

    lateinit var start:GridPoint
    lateinit var end:GridPoint
    private val heightMap = file.readLines().mapIndexed { y, row -> row.toCharArray().mapIndexed { x, col ->
        if (col.isLowerCase()) { col.code - 96 } else {
            if (col == 'S') {
                start = GridPoint(x, y)
                1
            }
            else {
                end = GridPoint(x, y)
                26
            }
        }
    } }

    // need to keep a record of the current distance when added to the queue, in case it is concurrently changed
    data class SearchPoint(val gridPoint: GridPoint, val currentDistance: Int)

    // to find the shortest path, assume a grid of max int values
    // as we inspect each neighbour that we can climb to (no greater than +1 height), set the distance to current distance +1
    // keep going while there are neighbours to traverse
    private fun seekShortest(seekFrom:GridPoint, seekTo:GridPoint):Int {
        val totalDistances = heightMap.map { row -> MutableList(row.size) { Int.MAX_VALUE } }
        totalDistances[start.y][start.x] = 0

        val queue = PriorityQueue<SearchPoint>(compareBy { it.currentDistance })
        queue.add(SearchPoint(seekFrom, 0))

        while(queue.isNotEmpty()) {
            val current = queue.remove()
            current.gridPoint.neighbours(heightMap[0].size, heightMap.size).forEach { neighbour ->
                val neighbourDist = current.currentDistance + 1
                // is climbable, and is a new shortest path
                if (heightMap[neighbour.y][neighbour.x] <= heightMap[current.gridPoint.y][current.gridPoint.x] + 1 &&
                    neighbourDist < totalDistances[neighbour.y][neighbour.x]) {

                    totalDistances[neighbour.y][neighbour.x] = neighbourDist // set the new distance
                    queue.add(SearchPoint(neighbour, totalDistances[neighbour.y][neighbour.x])) // add neighbour to the queue for further seeking
                }
            }
        }

        // by time we get here, if the path was reachable, the end point will have a distance calculated
        return totalDistances[seekTo.y][seekTo.x]
    }


    fun puzzle1() {
        println("Part 1 Ans: ${seekShortest(start, end)}")
    }

    fun puzzle2() {
        val ans = heightMap.flatMapIndexed { y, row -> row.mapIndexed { x, height ->
            if (height == 1) { seekShortest(GridPoint(x, y), end) } else { Int.MAX_VALUE }
        } }.minOrNull()

        println("Part 1 Ans: $ans")
    }
}