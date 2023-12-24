import kotlin.math.min

private class Day21Map(val map: List<String>) {
    enum class Direction {
        U, L, D, R;

        fun move(pair: Pair<Int, Int>) = when (this) {
            U -> Pair(pair.first - 1, pair.second)
            L -> Pair(pair.first, pair.second - 1)
            D -> Pair(pair.first + 1, pair.second)
            R -> Pair(pair.first, pair.second + 1)
        }
    }

    val startPoint =
            map.mapIndexed { i, s -> if (s.contains('S')) i to s.indexOf('S') else -1 to -1 }.find { it.first != -1 }!!

    val lastRow = map.lastIndex
    val lastCol = map.first().lastIndex
    private fun isPositionGarden(position: Pair<Int, Int>) =
            isValidPosition(position) && map[position.first][position.second] != '#'

    private fun isValidPosition(position: Pair<Int, Int>) =
            position.first in 0..lastRow && position.second in 0..lastCol

    fun countPossibleGarden(steps: Int, repeat: Boolean = false): Long {
        var currentPositions = mutableSetOf(startPoint)
        if (!repeat) {
            for (step in 1..steps) {
                val newPositions = mutableSetOf<Pair<Int, Int>>()
                for (position in currentPositions) {
                    newPositions.addAll(Direction.entries.map { it.move(position) }.filter { isPositionGarden(it) })
                }
                currentPositions = newPositions
            }
            return currentPositions.count().toLong()
        }

        // assert quadratic formula as pattern
        val mod = steps % map.size
        val cycle = steps / map.size
        val countsByStep = mutableMapOf<Int, Long>()
        var step = 0
        while (step <= min(mod + map.size * 2, steps)) {
            step++
            val newPositions = mutableSetOf<Pair<Int, Int>>()
            for (position in currentPositions) {
                newPositions.addAll(Direction.entries.map { it.move(position) }
                        .filter { isPositionGarden(moveToInBound(it)) })
            }
            countsByStep[step] = newPositions.count().toLong()
            currentPositions = newPositions
        }
        if (step == steps) return countsByStep.getValue(step)

        val y1 = countsByStep.getValue(mod)
        val y2 = countsByStep.getValue(mod + map.size)
        val y3 = countsByStep.getValue(mod + map.size * 2)

        return y1 + cycle.toLong() * (y2 - y1) + cycle.toLong() * (cycle - 1) / 2 * ((y3 - y2) - (y2 - y1))
    }

    private fun moveToInBound(point: Pair<Int, Int>): Pair<Int, Int> {
        val newRow =
                if (point.first >= 0) point.first % (lastRow + 1) else ((lastRow + 1) - (-point.first % (lastRow + 1))) % (lastRow + 1)
        val newCol =
                if (point.second >= 0) point.second % (lastCol + 1) else ((lastCol + 1) - (-point.second % (lastCol + 1))) % (lastCol + 1)
        return newRow to newCol
    }

}

fun main() {
    fun part1(input: List<String>): Long {
        val system = Day21Map(input)
        return system.countPossibleGarden(64)
    }

    fun part2(input: List<String>): Long {
        val system = Day21Map(input)
        return system.countPossibleGarden(26501365, true)
    }

    val input = readInput("Day21")
    part1(input).println()
    part2(input).println()
}
