import java.io.File

fun main() {
    Day17().puzzle1()
    Day17().puzzle2()
}

class Day17 {

    data class Point(val x:Int, val y:Long)
    class JetPattern(private val pattern:String) {
        private var cursor:Int = -1
        fun length() = pattern.length
        fun nextJet():Char {
            cursor = (cursor + 1) % pattern.length
            return pattern[cursor]
        }
    }

    private val jetPattern = JetPattern(File("inputs/day17.txt").readText())
    private val rocksAtRest = mutableSetOf<Point>()
    private val shapes = listOf(
        listOf(Point(1, 1), Point(2, 1), Point(3, 1), Point(4, 1)),
        listOf(Point(2, 3), Point(1, 2), Point(2, 2), Point(3, 2), Point(2, 1)),
        listOf(Point(3, 3), Point(3, 2), Point(1, 1), Point(2, 1), Point(3, 1)),
        listOf(Point(1, 4), Point(1, 3), Point(1, 2), Point(1, 1)),
        listOf(Point(1, 2), Point(2, 2), Point(1, 1), Point(2, 1))
    )

    private fun isValid(shape:List<Point>) = shape.none { rocksAtRest.contains(it) || it.x < 1 || it.x > 7 || it.y < 1 }

    fun puzzle1() {
        var top = 0L
        repeat(2022) {
            var shape = shapes[it % 5].map {
                // always starts at xOffset = 2, and we will offset Y to be the highest current point
                Point(it.x + 2, it.y + top + 3)
            }

            while (true) {
                val maybeShape = if (jetPattern.nextJet() == '>') { shape.map { Point(it.x+1, it.y) } } else { shape.map { Point(it.x-1, it.y) }}

                shape = if (isValid(maybeShape)) maybeShape else shape

                // now move down
                val maybeShape2 = shape.map { Point(it.x, it.y - 1) }
                if (isValid(maybeShape2)) {
                    shape = maybeShape2
                }
                else {
                    // come to rest if we get here
                    // add all parts of the shape to the Set
                    shape.forEach {
                        rocksAtRest.add(it)
                        top = maxOf(top, it.y)
                    }
                    break
                }
            }
        }

        println("Part 1 Ans: $top")

    }

    fun puzzle2() {

        // with 5 shapes and pattern.length input (10092), the whole sequence repeats itself. We need to calculate
        // the height of 1 sequence, the number of sequences, and the remaining rocks (and the height that will add)
        //
        // So, we need to know the height after 5 wind sequences. This is not == number of rockfalls however, as 1 rockfall will result in multiple
        // iterations of the jet cycle. Once we know the cycle length, we can multiply the cycle height by the
        // number of cycles in 1 billion. We should also track the height at each stage of the cycle, so the remaining rocks
        // (modulus of 1 billion) can simply be added on
        fun solve(): Long {
            var top = 0L
            val heights = mutableListOf<Long>()
            var cycleSyncNum: Int? = null
            var ogCoord: Point? = null
            var ogRockNum = -1
            val seenCycles = mutableSetOf<Int>()
            var time = 0
            var rockNum = 0

            repeat(100_000) {
                var shape = shapes[rockNum % 5].map {
                    // always starts at xOffset = 2, and we will offset Y to be the highest current point
                    Point(it.x + 2, it.y + top + 3)
                }

                while (true) {
                    time++
                    val maybeShape = if (jetPattern.nextJet() == '>') { shape.map { Point(it.x+1, it.y) } } else { shape.map { Point(it.x-1, it.y) }}
                    shape = if (isValid(maybeShape)) maybeShape else shape

                    // now move down
                    val maybeShape2 = shape.map { Point(it.x, it.y - 1) }
                    if (isValid(maybeShape2)) {
                        shape = maybeShape2
                    } else {
                        // come to rest if we get here
                        // add all parts of the shape to the Set
                        shape.forEach {
                            rocksAtRest.add(it)
                            top = maxOf(top, it.y)
                        }
                        break
                    }
                }

                if (rockNum % shapes.size == shapes.lastIndex) { // last shape
                    if (cycleSyncNum == null) {
                        val windNum = time % jetPattern.length()
                        if (!seenCycles.add(windNum)) {
                            cycleSyncNum = windNum
                            ogCoord = shape[0].copy()
                            ogRockNum = rockNum
                        }
                    }
                    else if (time % jetPattern.length() == cycleSyncNum) {
                        val cycleHeight = shape[0].y - ogCoord!!.y
                        val cycleLength = rockNum - ogRockNum
                        val rocksToJumpOver = 1_000_000_000_000 - ogRockNum
                        val cyclesToJump = rocksToJumpOver / cycleLength
                        val remaining = (rocksToJumpOver % cycleLength).toInt()
                        return (cyclesToJump * cycleHeight) + heights[remaining - 1]
                    }
                }

                if (ogCoord != null) { heights.add(top) }

                rockNum += 1
            }
            return 0L // should never get here
        }

        println("Part 2 Ans: ${solve()}")
    }
}