fun main() {
    fun part1(input: List<String>): Int {
        val timeList = input[0].split(":")[1].split(" ").filter { it.isNotEmpty() }
        val distanceList = input[1].split(":")[1].split(" ").filter { it.isNotEmpty() }
        var sum = 0
        for (game in timeList.indices) {
            var wins = 0
            val raceTime = timeList[game].toInt()
            for (chargingTime in 0..raceTime) {
                if ((raceTime - chargingTime) * chargingTime > distanceList[game].toInt())
                    wins++
            }
            if (sum == 0)
                sum = wins
            else if (wins != 0)
                sum *= wins
        }
        return sum
    }

    fun part2(input: List<String>): Int {
        val time = input[0].split(":")[1].split(" ").joinToString("").trim()
        val distance = input[1].split(":")[1].split(" ").joinToString("").trim()
        var wins = 0
            for (chargingTime in 0..time.toInt()) {
                if ((time.toLong() - chargingTime) * chargingTime > distance.toLong())
                    wins++
            }
            wins.println()
        return wins
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    check(part1(testInput) == 288)
    check(part2(testInput) == 71503)

    val input = readInput("Day06")
    part1(input).println()
    part2(input).println()
}
