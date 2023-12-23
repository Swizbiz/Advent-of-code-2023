import java.util.ArrayDeque

enum class Direction(val modifier: Pair<Int, Int>) {
    Left(-1 to 0), Up(0 to -1), Right(1 to 0), Down(0 to 1);

    fun opposite() = when (this) {
        Left -> Right
        Up -> Down
        Right -> Left
        Down -> Up
    }

    fun turnRight() = when (this) {
        Left -> Up
        Up -> Right
        Right -> Down
        Down -> Left
    }

    fun turnLeft() = turnRight().opposite()
}

enum class Pipe(val sign: Char, val allowedDirections: Set<Direction>) {
    Vertical('|', setOf(Direction.Up, Direction.Down)),
    Horizontal('-', setOf(Direction.Left, Direction.Right)),
    NorthEast('L', setOf(Direction.Up, Direction.Right)),
    NorthWest('J', setOf(Direction.Up, Direction.Left)),
    SouthEast('F', setOf(Direction.Down, Direction.Right)),
    SouthWest('7', setOf(Direction.Down, Direction.Left)),
    Ground('.', emptySet()), Starting('S', Direction.entries.toSet());

    fun checkRight(direction: Direction): Set<Pair<Int, Int>> = when (this) {
        Vertical -> setOf(direction.turnRight().modifier)
        Horizontal -> setOf(direction.turnRight().modifier)
        NorthEast -> if (direction == Direction.Left) setOf(Direction.Right.modifier + Direction.Up.modifier)
        else setOf(Direction.Down.modifier, Direction.Left.modifier, Direction.Down.modifier + Direction.Left.modifier)

        NorthWest -> if (direction == Direction.Down) setOf(Direction.Left.modifier + Direction.Up.modifier)
        else setOf(
                Direction.Down.modifier, Direction.Right.modifier, Direction.Down.modifier + Direction.Right.modifier
        )

        SouthEast -> if (direction == Direction.Up) setOf(Direction.Right.modifier + Direction.Down.modifier)
        else setOf(Direction.Up.modifier, Direction.Left.modifier, Direction.Up.modifier + Direction.Left.modifier)

        SouthWest -> if (direction == Direction.Right) setOf(Direction.Left.modifier + Direction.Down.modifier)
        else setOf(Direction.Up.modifier, Direction.Right.modifier, Direction.Up.modifier + Direction.Right.modifier)

        Ground -> setOf()
        Starting -> setOf()
    }

    fun checkLeft(direction: Direction): Set<Pair<Int, Int>> = when (this) {
        Vertical -> setOf(direction.turnLeft().modifier)
        Horizontal -> setOf(direction.turnLeft().modifier)

        NorthEast -> if (direction == Direction.Down) setOf(Direction.Right.modifier + Direction.Up.modifier)
        else setOf(Direction.Down.modifier, Direction.Left.modifier, Direction.Down.modifier + Direction.Left.modifier)

        NorthWest -> if (direction == Direction.Right) setOf(Direction.Left.modifier + Direction.Up.modifier)
        else setOf(
                Direction.Down.modifier, Direction.Right.modifier, Direction.Down.modifier + Direction.Right.modifier
        )

        SouthEast -> if (direction == Direction.Left) setOf(Direction.Right.modifier + Direction.Down.modifier)
        else setOf(Direction.Up.modifier, Direction.Left.modifier, Direction.Up.modifier + Direction.Left.modifier)

        SouthWest -> if (direction == Direction.Up) setOf(Direction.Left.modifier + Direction.Down.modifier)
        else setOf(Direction.Up.modifier, Direction.Right.modifier, Direction.Up.modifier + Direction.Right.modifier)

        Ground -> setOf()
        Starting -> setOf()
    }

    companion object {
        fun fromSign(sigh: Char): Pipe {
            return Pipe.entries.find { it.sign == sigh }!!
        }
    }
}

operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>) = (this.first + other.first) to (this.second + other.second)

fun main() {
    val start = System.nanoTime()
    val exerciseData = readInput("Day10")

    val pipesMap = exerciseData.map { it.toCharArray().map { Pipe.fromSign(it) } }

    var rightCounter = 0
    var leftCounter = 0

    val loopPositions = navigatePipes(pipesMap) { direction, pipe, _ ->
        if (pipe.allowedDirections.contains(direction.turnRight())) rightCounter++
        if (pipe.allowedDirections.contains(direction.turnLeft())) leftCounter++
    }


    val internalPoints = mutableSetOf<Pair<Int, Int>>()
    navigatePipes(pipesMap) { direction, pipe, position ->
        val insideModifiers = if (rightCounter > leftCounter) pipe.checkRight(direction) else pipe.checkLeft(direction)
        val insidePositions = insideModifiers.map { it + position }

        val positionsQueue = ArrayDeque<Pair<Int, Int>>()
        positionsQueue.addAll(insidePositions)

        while (positionsQueue.isNotEmpty()) {
            val currPosition = positionsQueue.removeFirst()

            if (internalPoints.contains(currPosition)) continue
            if (loopPositions.contains(currPosition)) continue

            internalPoints.add(currPosition)

            val adjacentPositions = Direction.entries.map { it.modifier + currPosition }

            positionsQueue.addAll(adjacentPositions)
        }
    }

    println(internalPoints.size)
    println("Part 2: ${System.nanoTime() - start} nanos")
}

private fun navigatePipes(
        pipesMap: List<List<Pipe>>,
        action: (Direction, Pipe, Pair<Int, Int>) -> Unit
): Set<Pair<Int, Int>> {
    val initialPosition = getInitialPosition(pipesMap)

    val positionsQueue = ArrayDeque<Pair<Int, Int>>()
    positionsQueue.add(initialPosition)

    val cache = mutableSetOf(initialPosition)

    while (positionsQueue.isNotEmpty()) {
        val currPosition = positionsQueue.removeFirst()

        val currPipe = pipesMap[currPosition.second][currPosition.first]

        currPipe.allowedDirections
                .asSequence()
                .map { it to currPosition + it.modifier }
                .filter { isIndexContainedInMap(pipesMap, it.second) }
                .map { it to pipesMap[it.second.second][it.second.first] }
                .filter { it.second.allowedDirections.contains(it.first.first.opposite()) }
                .filterNot { cache.contains(it.first.second) }
                .firstOrNull()
                ?.let {
                    action(it.first.first, it.second, it.first.second)

                    positionsQueue.add(it.first.second)
                    cache.add(it.first.second)
                }
    }

    return cache
}

private fun getInitialPosition(pipesMap: List<List<Pipe>>): Pair<Int, Int> {
    val startingPointY = pipesMap.indexOfFirst { it.contains(Pipe.Starting) }
    val startingPointX = pipesMap[startingPointY].indexOf(Pipe.Starting)

    val initialPosition = startingPointX to startingPointY
    return initialPosition
}

private fun isIndexContainedInMap(
        pipesMap: List<List<Pipe>>,
        it: Pair<Int, Int>
) = (0..pipesMap.first().size).contains(it.first) && (0..pipesMap.size).contains(it.second)
