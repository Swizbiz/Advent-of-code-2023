import java.util.*

private class ClumsyCrucible(input: List<String>) {
    private val grid = Array(input.size) { IntArray(input[0].length) }

    init {
        for (r in grid.indices)
            grid[r] = input[r].map { it.digitToInt() }.toIntArray()
    }

    enum class DirectionLRUP {
        L, R, U, D
    }

    data class Position2D(
            val x: Int,
            val y: Int
    )

    fun Position2D.up() = copy(x = x - 1)
    fun Position2D.down() = copy(x = x + 1)
    fun Position2D.left() = copy(y = y - 1)
    fun Position2D.right() = copy(y = y + 1)

    private fun Position2D.isValid(): Boolean = this.x in grid.indices && this.y in grid[0].indices

    private data class State(
            val position2D: Position2D,
            val directionLRUP: DirectionLRUP,
            val count: Int
    )

    private fun State.adjStates(): List<State> {
        return buildList {
            when (directionLRUP) {
                DirectionLRUP.L -> {
                    if (count < 3 && position2D.left().isValid()) add(State(position2D.left(), DirectionLRUP.L, count + 1))
                    if (position2D.up().isValid()) add(State(position2D.up(), DirectionLRUP.U, 1))
                    if (position2D.down().isValid()) add(State(position2D.down(), DirectionLRUP.D, 1))
                }

                DirectionLRUP.R -> {
                    if (count < 3 && position2D.right().isValid()) add(State(position2D.right(), DirectionLRUP.R, count + 1))
                    if (position2D.up().isValid()) add(State(position2D.up(), DirectionLRUP.U, 1))
                    if (position2D.down().isValid()) add(State(position2D.down(), DirectionLRUP.D, 1))
                }

                DirectionLRUP.U -> {
                    if (count < 3 && position2D.up().isValid()) add(State(position2D.up(), DirectionLRUP.U, count + 1))
                    if (position2D.left().isValid()) add(State(position2D.left(), DirectionLRUP.L, 1))
                    if (position2D.right().isValid()) add(State(position2D.right(), DirectionLRUP.R, 1))
                }

                DirectionLRUP.D -> {
                    if (count < 3 && position2D.down().isValid()) add(State(position2D.down(), DirectionLRUP.D, count + 1))
                    if (position2D.left().isValid()) add(State(position2D.left(), DirectionLRUP.L, 1))
                    if (position2D.right().isValid()) add(State(position2D.right(), DirectionLRUP.R, 1))
                }
            }
        }
    }

    private fun State.adjStatesPart2(): List<State> {
        return buildList {
            when (directionLRUP) {
                DirectionLRUP.L -> {
                    if (count >= 4) {
                        if (position2D.up().isValid()) add(State(position2D.up(), DirectionLRUP.U, 1))
                        if (position2D.down().isValid()) add(State(position2D.down(), DirectionLRUP.D, 1))
                    }
                    if (count < 10 && position2D.left().isValid()) add(State(position2D.left(), DirectionLRUP.L, count + 1))
                }

                DirectionLRUP.R -> {
                    if (count >= 4) {
                        if (position2D.up().isValid()) add(State(position2D.up(), DirectionLRUP.U, 1))
                        if (position2D.down().isValid()) add(State(position2D.down(), DirectionLRUP.D, 1))
                    }
                    if (count < 10 && position2D.right().isValid()) add(State(position2D.right(), DirectionLRUP.R, count + 1))
                }

                DirectionLRUP.U -> {
                    if (count >= 4) {
                        if (position2D.left().isValid()) add(State(position2D.left(), DirectionLRUP.L, 1))
                        if (position2D.right().isValid()) add(State(position2D.right(), DirectionLRUP.R, 1))
                    }
                    if (count < 10 && position2D.up().isValid()) add(State(position2D.up(), DirectionLRUP.U, count + 1))
                }

                DirectionLRUP.D -> {
                    if (count >= 4) {
                        if (position2D.left().isValid()) add(State(position2D.left(), DirectionLRUP.L, 1))
                        if (position2D.right().isValid()) add(State(position2D.right(), DirectionLRUP.R, 1))
                    }
                    if (count < 10 && position2D.down().isValid()) add(State(position2D.down(), DirectionLRUP.D, count + 1))
                }
            }
        }
    }

    private fun shortestPath(part1: Boolean): Int {
        val startPos = Position2D(0, 0)
        val destPos = Position2D(grid.lastIndex, grid[0].lastIndex)

        val pQ = PriorityQueue<Pair<State, Int>>(compareBy { it.second })
        val distMap = hashMapOf<State, Int>()

        val visited = hashSetOf<State>()

        pQ.offer(Pair(State(position2D = startPos, directionLRUP = DirectionLRUP.R, 0), 0))
        pQ.offer(Pair(State(position2D = startPos, directionLRUP = DirectionLRUP.D, 0), 0))

        distMap[State(position2D = startPos, directionLRUP = DirectionLRUP.R, 0)] = 0
        distMap[State(position2D = startPos, directionLRUP = DirectionLRUP.D, 0)] = 0

        while (pQ.isNotEmpty()) {
            val (currState, heatLoss) = pQ.poll()
            visited.add(currState)

            if (part1 && currState.position2D == destPos) {
                return distMap.getValue(currState)
            } else if (currState.position2D == destPos && currState.count >= 4) {
                return distMap.getValue(currState)
            }

            if (distMap.getValue(currState) < heatLoss) continue

            for (adj in if (part1) currState.adjStates() else currState.adjStatesPart2()) {
                if (visited.contains(adj)) continue
                val newHeatLoss =
                        distMap.getOrDefault(currState, Int.MAX_VALUE) + grid[adj.position2D.x][adj.position2D.y]
                if (newHeatLoss < distMap.getOrDefault(adj, Int.MAX_VALUE)) {
                    distMap[adj] = newHeatLoss
                    pQ.offer(Pair(adj, newHeatLoss))
                }
            }
        }
        return -1
    }

    fun solve1() = shortestPath(part1 = true)
    fun solve2() = shortestPath(part1 = false)

}

fun main() {

    fun part1(input: List<String>): Int {
        val clumsyCrucible = ClumsyCrucible(input)
        return clumsyCrucible.solve1()
    }

    fun part2(input: List<String>): Int {
        val clumsyCrucible = ClumsyCrucible(input)
        return clumsyCrucible.solve2()
    }

    val input = readInput("Day17")
    part1(input).println()
    part2(input).println()
}
