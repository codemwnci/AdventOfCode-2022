import java.io.File

fun main() {
    Day01().puzzle1()
    Day01().puzzle2()
}

class Day01 {
    private val file = File("inputs/day01.txt")

    fun <T> List<T>.split(predicate: (T) -> Boolean): List<List<T>> =
        fold(mutableListOf(mutableListOf<T>())) { acc, t ->
            if (predicate(t)) acc.add(mutableListOf())
            else acc.last().add(t)
            acc
        }

    private fun getCalorieList(): List<Int> = file.readLines().split { it.isBlank() }.map { subList -> subList.sumOf { it.toInt() } }

    fun puzzle1() {
        val list = getCalorieList()
        // sorting is less efficient, as we only need to find the max value
        val max = list.foldIndexed(Pair(0,0)) { idx, res, x ->
            if (res.second < x) Pair(idx, x)
            else res
        }

        println("Elf ${max.first} has ${max.second} calories")

        // NOTE: If we didn't want the index, we could have just done
        // println(list.maxOrNull())
    }

    fun puzzle2() {
        val list = getCalorieList().toMutableList()

        // only need to know the top3, we don't need to know the index, so just sort and take top 3
        val top3 = list.sortedDescending().take(3).sum()
        println("Top3 Elves are carrying $top3 Calories")
    }
}