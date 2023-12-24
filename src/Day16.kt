data class Position2D(
        val x: Int,
        val y: Int
)

fun Position2D.north() = copy(x = x - 1)
fun Position2D.south() = copy(x = x + 1)
fun Position2D.east() = copy(y = y + 1)
fun Position2D.west() = copy(y = y - 1)

private class BeamOfLight(val input: List<String>) {

    private val grid = List(input.size) { mutableListOf<Tile>() }

    init {
        for (row in input.indices) {
            for (col in input[0].indices) {
                grid[row].add(Tile(position = Position2D(row, col), type = input[row][col]))
            }
        }
    }

    enum class Direction {
        N, S, W, E
    }

    private data class Tile(
            val position: Position2D,
            val type: Char = Char.MAX_VALUE
    ) {
        var energy = 0
    }


    private fun List<List<Tile>>.deepCopy():List<List<Tile>> {
        val newGrid = List(input.size) { mutableListOf<Tile>() }
        for (r in this.indices){
            for (c in this[0].indices){
                newGrid[r].add(Tile(position = grid[r][c].position, type = grid[r][c].type))
            }
        }
        return newGrid
    }


    private fun Position2D.isValid(): Boolean {
        if (this.x in grid.indices && this.y in grid[0].indices)
            return true
        return false
    }

    private fun walkOfBeam(position: Position2D, direction: Direction, grid:List<List<Tile>>) {
        val queue = ArrayDeque<Pair<Position2D,Direction>>()
        val path = hashSetOf<Pair<Position2D,Position2D>>()
        when(direction){
            Direction.W -> {
                when(grid[position.x][position.y].type){
                    '.' -> queue.add(Pair(position,Direction.E))
                    '-' -> queue.add(Pair(position,Direction.E))
                    '|' -> {
                        if (position.north().isValid()) queue.add(Pair(position,Direction.N))
                        if (position.south().isValid()) queue.add(Pair(position,Direction.S))
                    }
                    '/' -> if (position.north().isValid()) queue.add(Pair(position,Direction.N))
                    '\\' -> if (position.south().isValid()) queue.add(Pair(position,Direction.S))
                }
            }
            Direction.E -> {
                when(grid[position.x][position.y].type){
                    '.' -> queue.add(Pair(position,Direction.W))
                    '-' -> queue.add(Pair(position,Direction.W))
                    '|' -> {
                        if (position.north().isValid()) queue.add(Pair(position,Direction.N))
                        if (position.south().isValid()) queue.add(Pair(position,Direction.S))
                    }
                    '/' -> if (position.south().isValid()) queue.add(Pair(position,Direction.S))
                    '\\' -> if (position.north().isValid()) queue.add(Pair(position,Direction.N))
                }
            }
            Direction.N -> {
                when(grid[position.x][position.y].type){
                    '.' -> queue.add(Pair(position,Direction.S))
                    '|' -> queue.add(Pair(position,Direction.S))
                    '-' -> {
                        if (position.west().isValid())
                            queue.add(Pair(position,Direction.W))
                        if (position.east().isValid())
                            queue.add(Pair(position,Direction.E))
                    }
                    '/' -> if (position.west().isValid()) queue.add(Pair(position,Direction.W))
                    '\\' -> if (position.east().isValid()) queue.add(Pair(position,Direction.E))
                }
            }
            Direction.S -> {
                when(grid[position.x][position.y].type){
                    '.' -> queue.add(Pair(position,Direction.N))
                    '|' -> queue.add(Pair(position,Direction.N))
                    '-' -> {
                        if (position.west().isValid())
                            queue.add(Pair(position,Direction.W))
                        if (position.east().isValid())
                            queue.add(Pair(position,Direction.E))
                    }
                    '/' -> if (position.east().isValid()) queue.add(Pair(position,Direction.E))
                    '\\' -> if (position.west().isValid()) queue.add(Pair(position,Direction.W))
                }
            }
        }
        while (queue.isNotEmpty()){
            val (current, currDirection) = queue.removeFirst()
            grid[current.x][current.y].energy++
            when(currDirection){
                Direction.E -> {
                    val next = current.east()
                    if (!next.isValid()) continue
                    if (path.contains(Pair(current,next)) || path.contains(Pair(next,current)))
                        continue
                    path.add(Pair(current,next))
                    when(grid[next.x][next.y].type){
                        '.' -> queue.add(Pair(next,Direction.E))
                        '-' -> queue.add(Pair(next,Direction.E))
                        '|' -> {
                            queue.add(Pair(next,Direction.N))
                            queue.add(Pair(next,Direction.S))
                        }
                        '/' -> queue.add(Pair(next,Direction.N))
                        '\\' -> queue.add(Pair(next,Direction.S))
                    }
                }
                Direction.S -> {
                    val next = current.south()
                    if (!next.isValid()) continue
                    if (path.contains(Pair(current,next)) || path.contains(Pair(next,current)))
                        continue
                    path.add(Pair(current,next))
                    when(grid[next.x][next.y].type){
                        '.' -> queue.add(Pair(next,Direction.S))
                        '|' -> queue.add(Pair(next,Direction.S))
                        '-' -> {
                            queue.add(Pair(next,Direction.E))
                            queue.add(Pair(next,Direction.W))
                        }
                        '/' -> queue.add(Pair(next,Direction.W))
                        '\\' -> queue.add(Pair(next,Direction.E))
                    }
                }
                Direction.W -> {
                    val next = current.west()
                    if (!next.isValid()) continue
                    if (path.contains(Pair(current,next)) || path.contains(Pair(next,current)))
                        continue
                    path.add(Pair(current,next))
                    when(grid[next.x][next.y].type){
                        '.' -> queue.add(Pair(next,Direction.W))
                        '-' -> queue.add(Pair(next,Direction.W))
                        '|' -> {
                            queue.add(Pair(next,Direction.N))
                            queue.add(Pair(next,Direction.S))
                        }
                        '/' -> queue.add(Pair(next,Direction.S))
                        '\\' -> queue.add(Pair(next,Direction.N))
                    }
                }
                Direction.N -> {
                    val next = current.north()
                    if (!next.isValid()) continue
                    if (path.contains(Pair(current,next)) || path.contains(Pair(next,current)))
                        continue
                    path.add(Pair(current,next))
                    when(grid[next.x][next.y].type){
                        '.' -> queue.add(Pair(next,Direction.N))
                        '|' -> queue.add(Pair(next,Direction.N))
                        '-' -> {
                            queue.add(Pair(next,Direction.E))
                            queue.add(Pair(next,Direction.W))
                        }
                        '/' -> queue.add(Pair(next,Direction.E))
                        '\\' -> queue.add(Pair(next,Direction.W))
                    }
                }
            }
        }
    }


    fun solve1():Int{
        walkOfBeam(
                position = Position2D(0,0), direction = Direction.W, grid = grid)
        return grid.sumOf { it.count { it.energy > 0 } }
    }

    fun solve2():Int {
        val conf = mutableListOf<Int>()
        for (row in grid.indices){
            val newG = grid.deepCopy()
            walkOfBeam(position = Position2D(row,0), direction = Direction.W, grid = newG)
            conf.add(newG.map { it.count { it.energy > 0 } }.sum())
        }

        for (row in grid.indices){
            val newG = grid.deepCopy()
            walkOfBeam(position = Position2D(row,grid[0].size-1), direction = Direction.E, grid = newG)
            conf.add(newG.sumOf { it.count { it.energy > 0 } })
        }

        for (col in grid[0].indices){
            val newG = grid.deepCopy()
            walkOfBeam(position = Position2D(0,col), direction = Direction.N, grid = newG)
            conf.add(newG.sumOf { it.count { it.energy > 0 } })
        }

        for (col in grid[0].indices){
            val newG = grid.deepCopy()
            walkOfBeam(position = Position2D(grid.size-1,col), direction = Direction.N, grid = newG)
            conf.add(newG.sumOf { it.count { it.energy > 0 } })
        }

        return conf.max()
    }

}

fun main() {

    fun part1(input: List<String>): Int {
        val beamOfLight = BeamOfLight(input = input)
        return beamOfLight.solve1()
    }

    fun part2(input: List<String>): Int {
        val beamOfLight = BeamOfLight(input = input)
        return beamOfLight.solve2()
    }

val input = readInput("Day16")
    part1(input).println()
    part2(input).println()
}
