import kotlin.math.max
import kotlin.math.min

fun main() {
    fun part1(input: List<String>, expandRatio: Long): Long {
        val galaxies = getGalaxies(input)
        val noGalaxyRows = getNoGalaxyRows(input)
        val noGalaxyColumns = getNoGalaxyColumns(input)
        var sum = 0L
        for (galaxy1WithIndex in galaxies.withIndex()) {
            val index = galaxy1WithIndex.index
            val galaxy1 = galaxy1WithIndex.value
            for (galaxy2 in galaxies.drop(index+1)){
                val distance = getDistance(galaxy1, galaxy2, noGalaxyRows, noGalaxyColumns, expandRatio)
                sum += distance
            }
        }
        return sum
    }

    fun part2(input: List<String>): Long = part1(input, 1000000)

    val input = readInput("Day11")
    part1(input, 2).println()
    part2(input).println()
}

fun getGalaxies(input: List<String>): List<Pair<Int, Int>> {
    val galaxies = mutableListOf<Pair<Int, Int>>()
    for (lineWithIndex in input.withIndex()) {
        val row = lineWithIndex.index
        val line = lineWithIndex.value
        for (charWithIndex in line.withIndex()) {
            val column = charWithIndex.index
            val char = charWithIndex.value
            if (char == '#') {
                galaxies.add(Pair(row, column))
            }
        }
    }
    return galaxies
}

fun getNoGalaxyRows(input: List<String>): List<Int> {
    return input.withIndex().filter{
        !it.value.contains('#')
    }.map{it.index}
}

fun getNoGalaxyColumns(input: List<String>): List<Int> {
    val noGalaxyColumns = mutableListOf<Int>()
    for (column in 0..<input[0].length) {
        val array = mutableListOf<Char>()
        for (element in input) {
            val item = element[column]
            array.add(item)
        }
        if (!array.contains('#')) {
            noGalaxyColumns.add(column)
        }
    }
    return noGalaxyColumns
}

fun getDistance(galaxy1: Pair<Int, Int>, galaxy2: Pair<Int, Int>, noGalaxyRows: List<Int>, noGalaxyColumns: List<Int>, expandRatio: Long) : Long{
    val maxRow = max(galaxy1.first, galaxy2.first)
    val minRow = min(galaxy1.first, galaxy2.first)
    val maxColumn = max(galaxy1.second,galaxy2.second)
    val minColumn = min(galaxy1.second,galaxy2.second)
    val noGalaxyRowCount = noGalaxyRows.count { it in (minRow + 1)..<maxRow }
    val noGalaxyColumnCount = noGalaxyColumns.count {it in (minColumn+1..<maxColumn)}
    return maxRow - minRow + maxColumn - minColumn + (noGalaxyRowCount + noGalaxyColumnCount) * (expandRatio - 1)
}
