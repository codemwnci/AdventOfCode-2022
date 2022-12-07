import java.io.File

import java.util.ArrayDeque

fun main() {
    Day07().puzzle1()
    Day07().puzzle2()
}

class Day07 {
    private val file = File("inputs/day07.txt")

    class Files(val size:Int, val name:String)
    class Dir(val name:String) {
        val subDirs= mutableListOf<Dir>()
        val files = mutableListOf<Files>()

        fun dirSize():Int = files.fold(0){ acc, f ->  acc + f.size} + subDirs.fold(0) {acc, dir -> acc + dir.dirSize() }
    }
    private val dirStack = ArrayDeque<Dir>()

    init {
        dirStack.push(Dir("/"))
        file.readLines().forEach {
            if (it.startsWith("dir")) {
                dirStack.peek().subDirs.add(Dir(it.substringAfter(" ")))
            }
            if (it.startsWith("$ cd")) {
                val dirName = it.drop(2).substringAfter(" ")
                if (dirName == "..") {
                    dirStack.pop()
                } else {
                    if (dirName != "/") dirStack.push(dirStack.peek().subDirs.find { it.name == dirName })
                }
            }
            if (it[0].isDigit()) {
                dirStack.peek().files.add(Files(it.substringBefore(" ").toInt(), it.substringAfter(" ")))
            }
        }
    }

    fun puzzle1() {
        fun findSubDirSize(dir: Dir, maxSize: Int): Int =
            dir.subDirs.fold( if (dir.dirSize() <= maxSize) { dir.dirSize() } else { 0 } ) { s, d -> s + findSubDirSize(d, maxSize) }

        println("Puzzle 1 Ans: " + findSubDirSize(dirStack.last, 100_000))
    }


    fun puzzle2() {
        data class DirSize(val name:String, val size:Int)

        val spaceNeeded = 30_000_000 - (70_000_000 - dirStack.last.dirSize())

        fun filterOnSize(spaceNeeded:Int, dir:Dir): MutableList<DirSize> {
            val list = mutableListOf<DirSize>()
            if (dir.dirSize() >= spaceNeeded) list.add(DirSize(dir.name, dir.dirSize()))
            dir.subDirs.forEach {
                list.addAll(filterOnSize(spaceNeeded, it))
            }
            return list
        }

        println("Puzzle 2 Ans: " + filterOnSize(spaceNeeded, dirStack.last).minOf { it.size })
    }
}