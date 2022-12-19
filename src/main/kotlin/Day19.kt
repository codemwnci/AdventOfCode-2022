import java.io.File
import java.util.*

// Needed some help with this one, but got there in the end
fun main() {
    Day19().puzzle1()
    Day19().puzzle2()
}

class Day19 {
    data class RobotCost(val ore:Int=0, val clay:Int=0, val obsidian: Int=0)
    data class Blueprint(val id: Int, val oreBot:RobotCost, val clayBot:RobotCost, val obsBot:RobotCost, val geodeBot:RobotCost)

    private val regex = Regex("Blueprint (\\d+):\\s+Each ore robot costs (\\d+) ore.\\s+Each clay robot costs (\\d+) ore.\\s+Each obsidian robot costs (\\d+) ore and (\\d+) clay.\\s+Each geode robot costs (\\d+) ore and (\\d+) obsidian.")
    private val blueprints = File("inputs/day19.txt").readLines().map { line ->
        regex.matchEntire(line)!!.groupValues.let {
            Blueprint(it[1].toInt(), RobotCost(ore=it[2].toInt()), RobotCost(ore=it[3].toInt()),RobotCost(ore=it[4].toInt(), clay=it[5].toInt()), RobotCost(ore=it[6].toInt(), obsidian = it[7].toInt()))
        }
    }

    enum class Robot { ORE, CLAY, OBSIDIAN, GEODE, NONE }
    data class State(val blueprint: Blueprint, val time: Int = 0, val ore: Int = 0, val clay: Int = 0, val obsidian: Int = 0, val geode: Int = 0,
                     val oreBot: Int = 1, val clayBot: Int = 0, val obsidianBot: Int = 0, val geodeBot: Int = 0): Comparable<State> {

        private val zeroCostRobot = RobotCost()

        private fun createNewStateIfAffordable(robot:Robot, newOre:Int, newClay:Int, newObs:Int, newGeode:Int): State? {
            val botCost = when(robot) {
                Robot.ORE -> blueprint.oreBot
                Robot.CLAY -> blueprint.clayBot
                Robot.OBSIDIAN -> blueprint.obsBot
                Robot.GEODE -> blueprint.geodeBot
                Robot.NONE -> zeroCostRobot
            }

            // if the bot is not affordable (before mining, so don't include new materials), return null
            val unaffordable = !(botCost.ore <= ore && botCost.clay <= clay && botCost.obsidian <= obsidian)

            return if (unaffordable) null else copy(
                time = time + 1,
                ore = ore + newOre - botCost.ore,
                clay = clay + newClay - botCost.clay,
                obsidian = obsidian + newObs - botCost.obsidian,
                geode = geode + newGeode,
                oreBot = oreBot + if (robot == Robot.ORE) 1 else 0,
                clayBot =  clayBot + if (robot == Robot.CLAY) 1 else 0,
                obsidianBot = obsidianBot + if (robot == Robot.OBSIDIAN) 1 else 0,
                geodeBot = geodeBot + if (robot == Robot.GEODE) 1 else 0
            )
        }

        fun nextStates():List<State> {
            val newOre = oreBot
            val newClay = clayBot
            val newObsidian = obsidianBot
            val newGeode = geodeBot
            return listOfNotNull( // ignore the nulls
                createNewStateIfAffordable(Robot.GEODE, newOre, newClay, newObsidian, newGeode),
                createNewStateIfAffordable(Robot.OBSIDIAN, newOre, newClay, newObsidian, newGeode),
                createNewStateIfAffordable(Robot.CLAY, newOre, newClay, newObsidian, newGeode),
                createNewStateIfAffordable(Robot.ORE, newOre, newClay, newObsidian, newGeode),
                createNewStateIfAffordable(Robot.NONE, newOre, newClay, newObsidian, newGeode)
            )
        }

        override fun compareTo(other: State): Int {
            return -1 * geodeBot.compareTo(other.geodeBot).let {
                if (it != 0) it else obsidianBot.compareTo(other.obsidianBot).let {
                    if (it != 0) it else clayBot.compareTo(other.clayBot).let {
                        if (it != 0) it else oreBot.compareTo(other.oreBot)
                    }
                }
            }
        }
    }

    fun puzzle1() {
        val ans =  blueprints.sumOf { blueprint ->
            val initial = State(blueprint)
            val seen = mutableSetOf(initial)
            val queue = PriorityQueue<State>().also { it.add(initial) }
            val max = mutableMapOf(0 to 0).withDefault { 0 }
            while (queue.isNotEmpty()) {
                val current = queue.remove()
                if (max.getValue(current.time) > current.geode) continue
                if (current.time >= 24) {
                    max[current.time] = max.getValue(current.time).coerceAtLeast(current.geode)
                    continue
                }
                max[current.time] = max.getValue(current.time).coerceAtLeast(current.geode)
                current.nextStates().filter { it.time < 25 }.filter { seen.add(it) }.toCollection(queue)
            }
            blueprint.id * max[24]!!
        }

        println("Part 1 Ans: $ans")
    }

    fun puzzle2() {
        val ans = blueprints.take(3).map { blueprint ->
            val initial = State(blueprint)
            val seen = mutableSetOf(initial)
            val queue = PriorityQueue<State>().also { it.add(initial) }
            val max = mutableMapOf(0 to 0).withDefault { 0 }
            while (queue.isNotEmpty()) {
                val current = queue.remove()
                if (max.getValue(current.time) > current.geode) continue
                if (current.time >= 32) {
                    max[current.time] = max.getValue(current.time).coerceAtLeast(current.geode)
                    continue
                }
                max[current.time] = max.getValue(current.time).coerceAtLeast(current.geode)
                current.nextStates().filter { it.time < 33 }.filter { seen.add(it) }.toCollection(queue)
            }

            max[32]!!
        }.fold(1L) { acc, i -> acc * i }

        println("Part 2 Ans: $ans")
    }
}