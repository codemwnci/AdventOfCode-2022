import java.io.File
import kotlin.math.abs

// tried 2 approaches to the second part of the puzzle
// couldn't get the first approach to get the right number for the real input (fine for the sample)

fun main() {
    Day18().puzzle1()
    //Day18().puzzle2()
    Day18().puzzle2Alt()
}

class Day18 {
    private val file = File("inputs/day18.txt")

    data class Point3D(val x:Int, val y:Int, val z:Int) {
        // if 1 away on one of the three planes
        fun isNeighbour(other: Point3D) = abs(x - other.x) + abs(y-other.y) + abs(z-other.z) == 1

        fun isEnclosed(grid3d:List<Point3D>) = // NOTE: This function does not cater for irregular shape circumstances
            grid3d.any { it.x - x > 0 && it.y-y == 0 && it.z-z == 0 } && grid3d.any { it.x - x < 0 && it.y-y == 0 && it.z-z == 0 } && // enclosed on x plane
            grid3d.any { it.x - x == 0 && it.y-y > 0 && it.z-z == 0 } && grid3d.any { it.x - x == 0 && it.y-y < 0 && it.z-z == 0 } && // enclosed on y plane
            grid3d.any { it.x - x == 0 && it.y-y == 0 && it.z-z > 0 } && grid3d.any { it.x - x == 0 && it.y-y == 0 && it.z-z < 0 }    // enclosed on z plane

        fun neighbours() = listOf(Point3D(x+1, y, z), Point3D(x-1, y, z), Point3D(x, y+1, z), Point3D(x, y-1, z), Point3D(x, y, z+1), Point3D(x, y, z-1))
    }

    private val gridPoints = file.readLines().map { line ->
        line.split(",").let {  Point3D(it[0].toInt(), it[1].toInt(), it[2].toInt()) }
    }

    fun puzzle1() {
        // calculate the number of neighbours for each in a 3D space and subtract from 6, then sum
        val sum = gridPoints.sumOf { p -> 6 - gridPoints.count { it.isNeighbour(p) } }
        println("Part 1 Ans: $sum")
    }

    // NOTE: This approach doesn't work! the isEnclosed function does not cater for the usecase
    // where the 3D shape is irregular, i.e. whilst there are points either side of the x, y, or z plane
    // this doesn't mean there isn't open air in between
    fun puzzle2() {
        // fill in all the airholes, and then recalculate the number of faces
        val gridSet = gridPoints.toMutableSet()
        val airpockets = mutableSetOf<Point3D>()

        for (x in (gridPoints.minOf { it.x }) .. gridPoints.maxOf { it.x }) {
            for (y in (gridPoints.minOf { it.y }) .. gridPoints.maxOf { it.y }) {
                for (z in (gridPoints.minOf { it.z }) .. gridPoints.maxOf { it.z }) {
                    val p = Point3D(x, y, z)
                    if (p !in gridSet && p !in airpockets) {
                        if (p.isEnclosed(gridPoints)) airpockets.add(p)
                    }
                }
            }
        }

        gridSet.addAll(airpockets)

        val sum = gridSet.sumOf { p -> 6 - gridSet.count { it.isNeighbour(p) } }
        println("Part 2 Ans: $sum")
    }

    // This approach DOES work. It simulates water coming from the origin, and fills each point, and then
    // attempts the same on all in-boundary neighbouring points (except if it encounters the shape). This prevents the search from
    // going _inside_ the shape
    fun puzzle2Alt(): MutableList<Point3D> {
        // check the area OUTSIDE (no need to go more than 1 item beyond the bounds)
        // count the number of these points that are touching part of the 3d shape
        // the breadth-first-search effectively simulates water filling the space around the 3d shape

        val minx = gridPoints.minOf { it.x } - 1
        val maxx = gridPoints.maxOf { it.x } + 1
        val miny = gridPoints.minOf { it.y } - 1
        val maxy = gridPoints.maxOf { it.y } + 1
        val minz = gridPoints.minOf { it.z } - 1
        val maxz = gridPoints.maxOf { it.z } + 1

        val gridSet = gridPoints.toMutableSet()
        var numOutside = 0L
        val q = ArrayDeque<Point3D>()
        val visited = mutableListOf<Point3D>()
        q.add(Point3D(0,0,0))
        visited.add(Point3D(0,0,0))
        while(q.isNotEmpty()) {
            val p = q.removeFirst()
            numOutside += gridPoints.sumOf { if (p.isNeighbour(it)) 1L else 0 }
            p.neighbours().forEach {
                if(it.x in minx..maxx && it.y in miny..maxy && it.z in minz..maxz && it !in gridSet && !visited.contains(it)) {
                    q.add(it)
                    visited.add(it)
                }
            }
        }
        println("Part 2 Ans: ${numOutside}")

        return visited
    }
}