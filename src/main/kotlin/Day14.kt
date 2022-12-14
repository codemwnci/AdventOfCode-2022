import java.io.File

fun main() {
    Day14().puzzle1()
    Day14().puzzle2()
}

class Day14 {
    private val file = File("inputs/day14.txt")

    data class Point(var x:Int, var y:Int)

    fun puzzle1() {
        val lines = file.readLines().map { line ->
            line.split(" -> ").map { coord ->
                coord.split(",").let { Point(it[0].toInt(), it[1].toInt()) }
            }
        }

        val maxX = lines.flatten().maxOf { it.x }
        val maxY = lines.flatten().maxOf { it.y }

        val grid = (0 .. maxY+1).map { (0 .. maxX+1).map { '.' }.toMutableList()  } // draw the grid with nothing in it
        lines.forEach {
            it.windowed(2, 1) {
                val y1 = minOf(it[0].y, it[1].y)
                val y2 = maxOf(it[0].y, it[1].y)
                val x1 = minOf(it[0].x, it[1].x)
                val x2 = maxOf(it[0].x, it[1].x)
                (y1 .. y2).forEach { y ->
                    (x1 .. x2).forEach { x ->
                        grid[y][x] = '#'
                    }
                }
            }
        }

        val sand = Point(500, 0)
        while (sand.x in 0 .. maxX && sand.y in 0 .. maxY) {
            if (grid[sand.y+1][sand.x] == '.') {
                sand.y++
            }
            else if (grid[sand.y+1][sand.x-1] == '.') {
                sand.y++
                sand.x--
            }
            else if (grid[sand.y+1][sand.x+1] == '.') {
                sand.y++
                sand.x++
            }
            else {
                // come to rest
                grid[sand.y][sand.x] = 'o'
                // create a new piece of sand
                sand.x = 500
                sand.y = 0
            }
        }

        println("Part 1 Ans: ${grid.flatten().count { it == 'o' } }")
    }

    fun puzzle2() {

        val lines = file.readLines().map { line ->
            line.split(" -> ").map { coord ->
                coord.split(",").let { Point(it[0].toInt(), it[1].toInt()) }
            }
        }

        val maxX = lines.flatten().maxOf { it.x }
        val maxY = lines.flatten().maxOf { it.y }

        val grid = (0 .. maxY+2).map { (0 .. maxX+maxY).map { '.' }.toMutableList()  } // draw the grid with nothing in it
        lines.forEach {
            it.windowed(2, 1) {
                val y1 = minOf(it[0].y, it[1].y)
                val y2 = maxOf(it[0].y, it[1].y)
                val x1 = minOf(it[0].x, it[1].x)
                val x2 = maxOf(it[0].x, it[1].x)
                (y1 .. y2).forEach { y ->
                    (x1 .. x2).forEach { x ->
                        grid[y][x] = '#'
                    }
                }
            }
        }

        val sand = Point(500, 0)
        while (grid[0][500] != 'o') {
            if (sand.y+1 == maxY+2) {
                // hit rock bottom
                // come to rest
                grid[sand.y][sand.x] = 'o'
                // create a new piece of sand
                sand.x = 500
                sand.y = 0
            }
            else if (grid[sand.y+1][sand.x] == '.') {
                sand.y++
            }
            else if (grid[sand.y+1][sand.x-1] == '.') {
                sand.y++
                sand.x--
            }
            else if (grid[sand.y+1][sand.x+1] == '.') {
                sand.y++
                sand.x++
            }
            else {
                // come to rest
                grid[sand.y][sand.x] = 'o'
                // create a new piece of sand
                sand.x = 500
                sand.y = 0
            }
        }

        println("Part 2 Ans: ${grid.flatten().count { it == 'o' } }")
    }
}