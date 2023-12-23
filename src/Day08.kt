import println
import readInput

tailrec fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)

fun lcm(a: Long, b: Long): Long = a / gcd(a, b) * b

const val START = "AAA"
const val FINISH = "ZZZ"
fun main() {

    fun List<String>.networkMap() = this.drop(2).associate { string ->
        val (node, rs) = string.split("=").map { it.trim() }
        node to rs.replace("(", "").replace(")", "")
                .split(",").map { it.trim() }
    }

    fun part1(lines: List<String>): Int {
        return lines.networkMap().let { networkMap ->
            val instruction = lines[0]
            var steps = 0
            var currentPos = START
            while (currentPos != FINISH) {
                val dir = instruction[(steps % instruction.length)]
                currentPos = if (dir == 'L') {
                    networkMap[currentPos]!![0]
                } else {
                    networkMap[currentPos]!![1]
                }
                steps++
            }
            steps
        }
    }

    fun part2(lines: List<String>): Long {
        return lines.networkMap().let { networkMap ->
            networkMap.keys.filter { it.endsWith("A") }
                    .map {
                        val instruction = lines[0]
                        var currPos = it
                        var steps = 0L
                        while (!currPos.endsWith("Z")) {
                            val dir = instruction[((steps % instruction.length).toInt())]
                            currPos = if (dir == 'L') {
                                networkMap[currPos]!![0]
                            } else {
                                networkMap[currPos]!![1]
                            }
                            steps++
                        }
                        steps
                    }.reduce(::lcm)
        }
    }

    val input = readInput("Day08")
    part1(input).println()
    part2(input).println()
}
