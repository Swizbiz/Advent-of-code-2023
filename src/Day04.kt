private data class Game(
        val game: Int,
        val winningNumbers: List<Int>,
        val numbers: List<Int>
)

fun main() {

    fun List<String>.parse(): List<Game> {
        return this.map {
            it.split(":", "|").map { it.trim() }
        }.map { games ->
            Game(
                    game = games[0].filter { it.isDigit() }.toInt(),
                    winningNumbers = games[1].split(" ").filter { it.isNotEmpty() }.map { it.trim() }.map { it.toInt() },
                    numbers = games[2].split(" ").filter { it.isNotEmpty() }.map { it.trim() }.map { it.toInt() },
            )
        }
    }

    fun part1(lines: List<String>): Int {
        return lines.parse().map { game ->
            game.winningNumbers
                    .intersect(game.numbers.toSet())
                    .takeIf { it.isNotEmpty() }
                    ?.drop(1)
                    ?.fold(1) { old, _ -> old * 2 } ?: 0

        }.sum()
    }

    fun part2(lines: List<String>): Int {
        return lines.parse().let { games ->
            val copies = games.associate { it.game to 1 }.toMutableMap()
            games.forEach {
                val winningNum = it.winningNumbers.intersect(it.numbers.toSet()).size
                for (i in it.game + 1..it.game + winningNum) {
                    copies[i] = copies[i]!! + copies[it.game]!!
                }
            }
            copies.values.sum()
        }
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part2(testInput) == 30)

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}
