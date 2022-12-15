
import java.io.File
import kotlin.math.abs

fun main() {
    Day15().puzzle1()
    Day15().puzzle2()
}

fun calcDistance(from: Point, to: Point): Int = abs(from.x - to.x) + abs(from.y - to.y)
data class Point(var x:Int, var y:Int) {
    fun neighbours(xBoundary: Int, yBoundary: Int): List<Point> =
        (-1 .. 1).map { yOffset ->  (-1 .. 1).map { xOffset -> Point(x+xOffset, y+yOffset)  } }.flatten()
            .filter { it.x in 0 until xBoundary && it.y  in 0 until yBoundary} // exclude points that go outside the boundary
}

class Day15 {
    private val file = File("inputs/day15.txt")

    data class PointRange(val sensor:Point, val beacon:Point) {
        fun noBeaconRange(row:Int): List<Point> {
            val distanceFromSensorToBeacon = calcDistance(sensor, beacon)
            val xRange = sensor.x - distanceFromSensorToBeacon .. sensor.x + distanceFromSensorToBeacon

            return xRange.filter {
                val distanceFromSensorToRow = calcDistance(sensor, Point(it, row))
                distanceFromSensorToBeacon >= distanceFromSensorToRow
            }.map{
                Point(it, row)
            }
        }
    }

    // signal is a beacon or a sensor
    private val signals = file.readLines().map {
        val part = it.split(":")
        val sensor = Point(part[0].substringAfter("x=").takeWhile { it != ',' }.toInt(), part[0].substringAfter("y=").toInt())
        val beacon = Point(part[1].substringAfter("x=").takeWhile { it != ',' }.toInt(), part[1].substringAfter("y=").toInt())

        PointRange(sensor, beacon)
    }

    fun puzzle1() {
        println(signals.map { it.noBeaconRange(2_000_000).filter { p -> it.beacon != p } }.flatten().toSet().count())
    }

    fun puzzle2() {
        val bounds = 4_000_000L
        var distressBeacon = 0L
        signals.forEach {
            val scanningDistance = calcDistance(it.sensor, it.beacon) + 1
            for (horizontal in 0..scanningDistance) {
                val vertical = scanningDistance - horizontal
                val p = Point(it.sensor.x + horizontal, it.sensor.y + vertical)
                p.neighbours(4_000_000, 4_000_000).forEach {
                    if (canDistressBeaconBeAt(it)) {
                        distressBeacon = (it.x * bounds) + it.y
                    }
                }
            }
        }
        println(distressBeacon) //12625383204261
    }

    private fun canDistressBeaconBeAt(pos: Point): Boolean = signals.all { (sensor, beacon) ->
        when (pos) {
            beacon -> false
            else -> calcDistance(pos, sensor) > calcDistance(sensor, beacon)
        }
    }
}