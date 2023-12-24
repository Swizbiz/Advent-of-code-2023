fun main() {
    fun arrangements(springs: String, contNumbs: List<Int>, cache: MutableMap<Pair<String, List<Int>>, Long> = mutableMapOf()): Long {

        if (cache.contains(Pair(springs, contNumbs))) {
            return cache[Pair(springs, contNumbs)]!!
        }

        var total = 0L


        if (contNumbs.isEmpty())
            return if (springs.contains('#')) 0 else 1

        if (springs.isEmpty())
            return if (contNumbs.isEmpty()) 1 else 0

        if (springs.first() in "?#" && contNumbs[0] <= springs.length && "." !in springs.substring(0, contNumbs[0])
                && (contNumbs[0] == springs.length || springs[contNumbs[0]] != '#')
        ) {
            total += arrangements(springs.drop(contNumbs[0] + 1), contNumbs.drop(1), cache)
        }

        if (springs.first() in "?.")
            total += arrangements(springs.drop(1), contNumbs, cache)


        cache[Pair(springs, contNumbs)] = total

        return total
    }

    fun part1(lines: List<String>): Long {
        return lines.sumOf {
            val (springs, contSpring) = it.split(" ").map {
                it.trim()
            }
            arrangements(
                    springs = springs,
                    contNumbs = contSpring.split(",").map { it.toInt() }
            )
        }
    }

    fun part2(lines: List<String>): Long {
        return lines.sumOf {
            val (springs, contSpring) = it.split(" ").map {
                it.trim()
            }
            arrangements(
                    springs = List(5) { springs }.joinToString("?"),
                    contNumbs = List(5) { contSpring.split(",").map { it.toInt() } }.flatten()
            )
        }
    }

    val input = readInput("Day12")
    part1(input).println()
    part2(input).println()
}
