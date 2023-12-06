fun main() {

    val extractMap: (List<String>, String) -> List<Pair<LongRange, LongRange>> = { list, map ->
        list.dropWhile { !it.contains(map) }.takeWhile { it.isNotEmpty() }.drop(1)
                .map {
                    val (dest, src, range) = it.split(" ").map { it.trim().toLong() }
                    Pair(src..<src + range, dest..<dest + range)
                }
    }

    val getCorrespondingX: (List<Pair<LongRange, LongRange>>, Long) -> Long = { lPair, categ ->
        var dcat = Long.MAX_VALUE
        lPair.forEach {
            if (categ >= it.first.first && categ <= it.first.last) {
                val index = categ - it.first.first
                dcat = it.second.first + index
            }
        }
        if (dcat == Long.MAX_VALUE) {
            dcat = categ
        }
        dcat
    }


    fun solve(lines: List<String>, part1:Boolean = true): Long {
        val seedToSoil = extractMap(lines, "seed-to-soil")
        val soilToFer = extractMap(lines, "soil-to-fertilizer")
        val fertToWater = extractMap(lines, "fertilizer-to-water")
        val waterToLight = extractMap(lines, "water-to-light")
        val lightToTemp = extractMap(lines, "light-to-temperature")
        val tempToHumidity = extractMap(lines, "temperature-to-humidity")
        val humidityToLocation = extractMap(lines, "humidity-to-location")


        fun location(seed: Long): Long {
            val soil = getCorrespondingX(seedToSoil, seed)
            val fert = getCorrespondingX(soilToFer, soil)
            val water = getCorrespondingX(fertToWater, fert)
            val light = getCorrespondingX(waterToLight, water)
            val temp = getCorrespondingX(lightToTemp, light)
            val humidity = getCorrespondingX(tempToHumidity, temp)

            return getCorrespondingX(humidityToLocation, humidity)
        }

        var minLoc = Long.MAX_VALUE
        if (part1) {
            val seeds = lines.first().substringAfter(":").trim().split(" ").map { it.trim().toLong() }
            seeds.forEach { seed ->
                val location = location(seed)
                if (minLoc > location)
                    minLoc = location
            }
        } else {
            val seedsRange = lines.first()
                    .substringAfter(":")
                    .trim()
                    .split(" ")
                    .map { it.trim().toLong() }
                    .windowed(2, 2).map {
                        it.first()..<it.first() + it.last()
                    }
            seedsRange.forEach {
                for (seed in it.first..it.last) {
                    val location = location(seed)
                    if (minLoc > location)
                        minLoc = location
                }
            }
        }

        return minLoc
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")

    val input = readInput("Day05")
    solve(input, true).println()
    solve(input, false).println()
}
