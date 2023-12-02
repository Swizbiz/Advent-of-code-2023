fun main() {
    fun isGameImpossible(cubes: List<String>): Boolean {
        cubes.forEach { cube ->
            when {
                cube.contains("red") && cube.replace("red", "").trim().toInt() > 12 -> return true
                cube.contains("green") && cube.replace("green", "").trim().toInt() > 13 -> return true
                cube.contains("blue") && cube.replace("blue", "").trim().toInt() > 14 -> return true
            }
        }
        return false
    }

    fun part1(input: List<String>): Int {
        var sum = 0
        input.forEachIndexed { index, string ->
            val games = string.split(";")
            games.forEach { game ->
                val cubes = if (game.startsWith("Game"))
                    game.split(":")[1].split(",")
                else
                    game.split(",")

                if (isGameImpossible(cubes))
                    return@forEachIndexed
            }
            sum += index + 1
        }
        return sum
    }

    fun part2(input: List<String>): Int {
        var sum = 0
        input.forEach { string ->
            val games = string.split(";")
            var maxRed = 0
            var maxGreen = 0
            var maxBlue = 0
            games.forEach { game ->
                val cubes = if (game.startsWith("Game"))
                    game.split(":")[1].split(",")
                else
                    game.split(",")

                cubes.forEach { cube ->
                    when {
                        cube.contains("red") ->
                            maxRed = maxOf(maxRed, cube.replace("red", "").trim().toInt())
                        cube.contains("green") ->
                            maxGreen = maxOf(maxGreen, cube.replace("green", "").trim().toInt())
                        cube.contains("blue") ->
                            maxBlue = maxOf(maxBlue, cube.replace("blue", "").trim().toInt())
                    }
                }
            }
            sum += maxRed * maxGreen * maxBlue
        }
        return sum
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 8)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}
