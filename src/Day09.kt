fun main() {

    fun solve(lines: List<String>, part1: Boolean): Long {
        return lines.sumOf { line ->
            generateSequence(line.split(" ").map { it.trim().toLong() }) { list ->
                list.windowed(2) { it[1] - it[0] }
            }.takeWhile { list ->
                list.any { it != 0L }
            }.map {
                if (part1) it.last() else it.first()
            }.toList().reversed()
                    .fold(0L) { acc, l -> if (part1) acc + l else l - acc }
        }
    }

    val input = readInput("Day09")
    solve(input, part1 = true).println()
    solve(input, part1 = false).println()
}
