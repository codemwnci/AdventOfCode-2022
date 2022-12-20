import java.io.File

fun main() {
    Day20().puzzle1()
    Day20().puzzle2()
}

class Day20 {
    private val file = File("inputs/day20.txt")

    data class Encrypted(val num:Long, val origin:Int)
    private fun MutableList<Encrypted>.indexOfOrigin(idx: Int) = this.indexOfFirst { it.origin == idx }
    
    private fun doMix(numList:List<Encrypted>, times:Int):List<Encrypted> {
        val newList = numList.toMutableList()
        repeat(times) {
            numList.forEachIndexed { idx, _ ->
                val currentPos = newList.indexOfOrigin(idx)
                val encryptedNum = newList.removeAt(currentPos)
                val newPos = ((currentPos + encryptedNum.num) % newList.size).let {
                    if (it > 0) it else (numList.size) + it -1 // loop negatives back around to the end of the list
                }

                newList.add(newPos.toInt(), encryptedNum)
            }
        }
        return newList
    }

    fun puzzle1() {
        val numList = file.readLines().mapIndexed { idx, num -> Encrypted(num.toLong(), idx) }
        val newList = doMix(numList, 1)

        val indexOfZero = newList.indexOfFirst { it.num == 0L }
        val ans = newList[(indexOfZero+1000) % newList.size].num + newList[(indexOfZero+2000) % newList.size].num + newList[(indexOfZero+3000) % newList.size].num
        println("Part 1 Answer: $ans")
    }

    fun puzzle2() {
        val numList = file.readLines().mapIndexed { idx, num -> Encrypted(num.toLong()*811589153, idx) }
        val newList = doMix(numList, 10)

        val indexOfZero = newList.indexOfFirst { it.num == 0L }
        val ans = newList[(indexOfZero+1000) % newList.size].num + newList[(indexOfZero+2000) % newList.size].num + newList[(indexOfZero+3000) % newList.size].num
        println("Part 2 Answer: $ans")
    }
}