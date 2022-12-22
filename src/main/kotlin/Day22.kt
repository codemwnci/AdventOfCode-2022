import java.io.File

fun main() {
    Day22().puzzle1()
    Day22().puzzle2()
}

// Part 1 was pretty straight forward
// Part 2, I am sure there is a more elegant solution. It is also hard coded for the layout of the cube, so the real input
// will work, but the test input will not because it has a different cube shape. It works though!

class Day22 {
    private val file = File("inputs/day22.txt")
    private class Facing(var dir:Int = 0) {
        fun turnClockwise() {
            dir = (dir + 1) % 4
        }
        fun turnAntiClockwise() {
            dir -= 1
            if (dir < 0) dir = 3
        }
        fun directionPoint() = when (dir) {
            0 -> Point(1, 0)
            1 -> Point(0,1)
            2 -> Point(-1, 0)
            3 -> Point(0, -1)
            else -> Point(0,0)
        }
    }
    private data class Point(var x:Int, var y:Int) {
        operator fun plus(other:Point) = Point(x+other.x, y+other.y)
    }
    private sealed interface Instruction {
        data class Steps(val steps:Int) : Instruction
        data class Rotate(val leftOrRight:Char) : Instruction
    }

    private val RIGHT:Int = 0
    private val DOWN:Int = 1
    private val LEFT:Int = 2
    private val UP:Int = 3

    fun puzzle1() {
        val lines = file.readLines()

        val route = mutableListOf<Instruction>()
        var numString = ""
        lines.last().forEach {
            when {
                it.isDigit() -> numString += it
                it.isLetter() -> {
                    if (numString.isNotEmpty()) {
                        route.add(Instruction.Steps(numString.toInt()))
                        numString = ""
                    }
                    route.add(Instruction.Rotate(it))
                }
            }
        }
        if (numString.isNotEmpty()) {
            route.add(Instruction.Steps(numString.toInt()))
            numString = ""
        }

        val maxWidth = lines.dropLast(2).maxOf { it.length } + 1

        // pad all around the map, will help with index out of bounds (because we are searching for ' ' as a wrap around
        // but also, as the map is index 1 based, we don't need to do any extra translation if we pad by 1 on x & y axis
        val map = lines.toMutableList().also{ it.add(0, "")}.dropLast(1).map { it.padEnd(maxWidth, ' ') }.map { " $it" }
        val pos = Point(map[1].indexOf('.'), 1)
        val facing = Facing()

        // walk route
        route.forEach {
            when (it) {
                is Instruction.Rotate -> if (it.leftOrRight == 'R') facing.turnClockwise() else facing.turnAntiClockwise()
                is Instruction.Steps -> {
                    for (i in 1..it.steps) {
                        val nextPos = pos + facing.directionPoint()

                        if (map[nextPos.y][nextPos.x] == '#') {
                            break
                        }
                        else if (map[nextPos.y][nextPos.x] == ' ') {
                            // need to wrap around
                            when (facing.dir) {
                                0 -> {
                                    val nextWrappedWall = map[pos.y].indexOf('#')
                                    val nextOpenSpace = map[pos.y].indexOf(".")
                                    if (nextWrappedWall == -1 || nextWrappedWall > nextOpenSpace) {
                                        pos.x = nextOpenSpace
                                    }
                                    else break
                                }
                                1 -> {
                                    val nextWrappedWall = map.indexOfFirst { it[pos.x] == '#' }
                                    val nextOpenSpace = map.indexOfFirst { it[pos.x] == '.' }
                                    if (nextWrappedWall == -1 || nextWrappedWall > nextOpenSpace) {
                                        pos.y = nextOpenSpace
                                    }
                                    else break
                                }
                                2 -> {
                                    val nextWrappedWall = map[pos.y].lastIndexOf('#')
                                    val nextOpenSpace = map[pos.y].lastIndexOf(".")
                                    if (nextWrappedWall == -1 || nextWrappedWall < nextOpenSpace) {
                                        pos.x = nextOpenSpace
                                    }
                                    else break
                                }
                                3 -> {
                                    val nextWrappedWall = map.indexOfLast { it[pos.x] == '#' }
                                    val nextOpenSpace = map.indexOfLast { it[pos.x] == '.' }
                                    if (nextWrappedWall == -1 || nextWrappedWall < nextOpenSpace) {
                                        pos.y = nextOpenSpace
                                    }
                                    else break
                                }
                            }
                        }
                        else {
                            // just move if needed
                            pos.x = nextPos.x
                            pos.y = nextPos.y
                        }
                    }
                }
            }
        }

        val ans = (1000 * (pos.y)) + (4 * (pos.x)) + facing.dir
        println("Part 1 Ans: $ans")
    }

    fun puzzle2() {

        // Consider a cube, laid out as (which is my input layout)
        //    [1][2]
        //    [3]
        // [5][4]
        // [6]

        // this is the logic for the wrap, rather than jumping as per part 1 instructions
        fun moveAlongCubeFace(pos:Point, facing: Facing): Pair<Point, Facing> {
            if (pos.x == 50 && pos.y in 0..49 && facing.dir == LEFT) return Point(0, 149-pos.y) to Facing(RIGHT) // Face 1 moving Left to 5
            if (pos.x in 50..99 && pos.y == 0 && facing.dir == UP) return Point(0, 150+pos.x-50) to Facing(RIGHT) // Face 1 moving Up to 6
            // Face 1 Right carries on as normal
            // Face 1 Down carries on as normal
            if (pos.x == 149 && pos.y in 0..49 && facing.dir == RIGHT) return Point(99, 149-pos.y) to Facing(LEFT) // Face 2 moving right 4
            if (pos.x in 100..149 && pos.y == 49 && facing.dir == DOWN) return Point(99, 50+(pos.x-100)) to Facing(LEFT) // Face 2 moving down to 3
            if (pos.x in 100..149 && pos.y == 0 && facing.dir == UP) return Point(pos.x-100, 199) to Facing(UP) // Face 2 moving up to 6
            // Face 2 Left carries on as normal
            if (pos.x == 50 && pos.y in 50..99 && facing.dir == LEFT) return Point(pos.y-50, 100) to Facing(DOWN) // Face 3 moving Left to 5
            if (pos.x == 99 && pos.y in 50..99 && facing.dir == RIGHT) return Point(pos.y+50, 49) to Facing(UP) // Face 3 moving right to 2
            // Face 3 Up carries on as normal
            // Face 3 Down carries on as normal
            if (pos.x == 99 && pos.y in 100..149 && facing.dir == RIGHT) return Point(149, 149-pos.y) to Facing(LEFT) // Face 4 moving right to 2
            if (pos.x in 50..99 && pos.y == 149 && facing.dir == DOWN) return Point(49, 100+pos.x) to Facing(LEFT) // Face 4 moving down to 6
            // Face 4 Up carries on as normal
            // Face 4 Left carries on as normal
            if (pos.x in 0..49 && pos.y == 100 && facing.dir == UP) return Point(50, 50+pos.x) to Facing(RIGHT) // Face 5 moving up to 3
            if (pos.x == 0 && pos.y in 100..149 && facing.dir == LEFT) return Point(50, 149-pos.y) to Facing(RIGHT) // Face 5 moving left to 1
            // Face 5 Right carries on as normal
            // Face 5 Down carries on as normal
            if (pos.x == 0 && pos.y in 150..199 && facing.dir == LEFT) return Point(50+pos.y-150, 0) to Facing(DOWN) // Face 6 moving left to 1
            if (pos.x == 49 && pos.y in 150..199 && facing.dir == RIGHT) return Point(50+pos.y-150, 149) to Facing(UP) // Face 6 moving right to 4
            if (pos.x in 0..49 && pos.y == 199 && facing.dir == DOWN) return Point(100+pos.x, 0) to Facing(DOWN) // Face 6 moving down to 2

            // if we get here, just move one step as normal
            return (pos + facing.directionPoint() to facing)
        }

        val lines = file.readLines()
        val route = mutableListOf<Instruction>()
        var numString = ""
        lines.last().forEach {
            when {
                it.isDigit() -> numString += it
                it.isLetter() -> {
                    if (numString.isNotEmpty()) {
                        route.add(Instruction.Steps(numString.toInt()))
                        numString = ""
                    }
                    route.add(Instruction.Rotate(it))
                }
            }
        }
        if (numString.isNotEmpty()) {
            route.add(Instruction.Steps(numString.toInt()))
            numString = ""
        }

        // don't need the padding this time round
        val map = lines.dropLast(2)
        val pos = Point(map[0].indexOf('.'), 0)
        var facing = Facing()

        // walk route
        route.forEach {
            when (it) {
                is Instruction.Rotate -> if (it.leftOrRight == 'R') facing.turnClockwise() else facing.turnAntiClockwise()
                is Instruction.Steps -> {
                    for (i in 1..it.steps) {
                        val (nextPos, newDir) = moveAlongCubeFace(pos, facing)
                        if (map[nextPos.y][nextPos.x] == '#') {
                            break
                        }
                        else {
                            // just move if needed
                            pos.x = nextPos.x
                            pos.y = nextPos.y
                            facing = newDir
                        }
                    }
                }
            }
        }

        val ans = (1000 * (pos.y+1)) + (4 * (pos.x+1)) + facing.dir
        println("Part 2 Ans: $ans")
    }
}