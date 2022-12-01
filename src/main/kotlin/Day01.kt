import java.io.File

fun main() {
    Day01().puzzle1()
    Day01().puzzle2()
}

class Day01 {
    private val file = File("inputs/day01.txt")

    private fun getCalorieList(): MutableList<Int> {
        val list = mutableListOf(0)
        file.readLines().forEach {
            if (it.isEmpty()) list.add(0)                          // add new item to tail if we find a blank line
            else list[list.lastIndex] = list.last() + it.toInt()   // otherwise incremement the tail with the read value
        }
        return list
    }


    fun puzzle1() {
        val list = getCalorieList()
        // sorting is less efficient, as we only need to find the max value
        val max = list.foldIndexed(Pair(0,0)) { idx, res, x ->
            if (res.second < x) Pair(idx, x)
            else res
        }

        println("Elf ${max.first} has ${max.second} calories")
    }

    fun puzzle2() {
        val list = getCalorieList()

        // only need to know the top3, we don't need to know the index, so just sort and take top 3
        list.sortDescending()
        val top3 = list.take(3).reduce { sum, x -> sum + x} // slightly more elegant/functional than //val top3 = list[0]+list[1]+list[2]
        println("Top3 Elves are carrying $top3 Calories")
    }
}