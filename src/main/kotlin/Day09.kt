import java.io.File
import kotlin.math.absoluteValue

fun main() {
    Day09().puzzle1()
    Day09().puzzle2()
}

class Day09 {
    private val file = File("inputs/day09.txt")

    data class Point(var x:Int = 0, var y:Int = 0)

    fun puzzle1() {
        val visited = mutableSetOf<Point>()
        val head = Point()
        val tail = Point()

        visited.add(tail.copy()) // starting point

        file.readLines().forEach { line ->
            line.split(" ").let {
                val step = when(it.first()) {   //it.first is the Direction
                    "R" -> Pair(1, 0)
                    "U" -> Pair(0,1)
                    "L" -> Pair(-1, 0)
                    "D" -> Pair(0,-1)
                    else -> Pair(0,0)
                }

                repeat(it.last().toInt()) {    //it.last is the number of steps in the given direction
                    head.x += step.first
                    head.y += step.second

                    // if on the same horizontal, or vertical - check they have not parted by more than 2 steps
                    if (head.y == tail.y) tail.x += ((head.x - tail.x) / 2) // integer division means this will only occur in multiples of 2, left or right
                    else if (head.x == tail.x) tail.y += ((head.y - tail.y) /2)
                    else {
                        if ((head.x - tail.x).absoluteValue > 1 || (head.y - tail.y).absoluteValue > 1) {
                            // they are not on the same x or y, and more than 1 space away, therefore it needs to move diagonally 1
                            tail.x += if (head.x > tail.x) 1 else -1
                            tail.y += if (head.y > tail.y) 1 else -1
                        }
                    }
                    visited.add(tail.copy())
                }
            }
        }
        println("Part 1 Ans: ${visited.size}")
    }

    fun puzzle2() {
        val visited = mutableSetOf<Point>()
        val snake = List(10) { Point() }

        visited.add(Point()) // starting point

        file.readLines().forEach { line ->
            line.split(" ").let {
                val step = when (it.first()) {
                    "R" -> Pair(1, 0)
                    "U" -> Pair(0, 1)
                    "L" -> Pair(-1, 0)
                    "D" -> Pair(0, -1)
                    else -> Pair(0, 0)
                }

                repeat(it.last().toInt()) {
                    snake.first().x += step.first
                    snake.first().y += step.second

                    snake.windowed(2, 1).forEach {  snakePart ->
                        val head = snakePart[0] // not the actual head, but the head of the part we are comparing
                        val tail = snakePart[1] // not the actual tail, but the tail of the part we are comparing
                        // if on the same horizontal, or vertical - check they have not parted by more than 2 steps
                        if (head.y == tail.y) tail.x += ((head.x - tail.x) / 2) // integer division means this will only occur in multiples of 2, left or right
                        else if (head.x == tail.x) tail.y += ((head.y - tail.y) /2)
                        else {
                            if ((head.x - tail.x).absoluteValue > 1 || (head.y - tail.y).absoluteValue > 1) {
                                // they are not on the same x or y, and more than 1 space away, therefore it needs to move diagonally 1
                                tail.x += if (head.x > tail.x) 1 else -1
                                tail.y += if (head.y > tail.y) 1 else -1
                            }
                        }
                    }
                    visited.add(snake.last().copy())
                }
            }
        }
        println("Part 2 Ans: ${visited.size}")
    }
}