private class LensLibrary(val inputs: List<String>) {
    private fun hashAlgo(string: String): Int {
        return string.fold(0) { acc: Int, c: Char ->
            ((acc + c.code) * 17) % 256
        }
    }

    fun part1() = inputs[0].split(",").sumOf { hashAlgo(it) }

    fun part2(): Int {
        val boxMap = hashMapOf<Int, List<Pair<String, Int>>>()
        inputs[0].split(",").forEach { inst ->
            when {
                inst.last() == '-' -> {
                    val boxN = inst.dropLast(1).fold(0) { acc, c -> ((acc + c.code) * 17) % 256 }
                    if (boxMap.contains(boxN)) {
                        val list = boxMap.getValue(boxN).toMutableList()
                        list.removeIf { it.first == inst.dropLast(1) }
                        boxMap[boxN] = list
                    }
                }

                else -> {
                    val (label, value) = inst.split("=")
                    val boxN = label.fold(0) { acc, c -> ((acc + c.code) * 17) % 256 }
                    if (boxMap.contains(boxN)) {
                        val list = boxMap.getValue(boxN).toMutableList()
                        val idx = list.indexOfFirst { it.first == label }
                        if (idx != -1) {
                            list.removeAt(idx)
                            list.add(idx, Pair(label, value.toInt()))
                        } else {
                            list.add(Pair(label, value.toInt()))
                        }
                        boxMap[boxN] = list
                    } else {
                        boxMap[boxN] = listOf(Pair(label, value.toInt()))
                    }
                }
            }
        }

        return boxMap.map { box ->
            box.value.withIndex().map {
                (box.key + 1) * (it.index+1) * (it.value.second)
            }
        }.flatten().sum()
    }
}

fun main() {

    fun part1(input: List<String>): Int {
        val lensLibrary = LensLibrary(inputs = input)
        return lensLibrary.part1()
    }

    fun part2(input: List<String>): Int {
        val lensLibrary = LensLibrary(inputs = input)
        return lensLibrary.part2()
    }

    val input = readInput("Day15")
    part1(input).println()
    part2(input).println()
}
