import java.util.Comparator
import kotlin.system.measureTimeMillis

private enum class TYPE(val value:Int){
    FIVE_KIND(7),
    FOUR_KIND(6),
    FULL_HOUSE(5),
    THREE_KIND(4),
    TWO_PAIR(3),
    ONE_PAIR(2),
    HIGH_CARD(1)
}

private data class CamelCard(
        val card: String,
        val type: TYPE,
        val bid:Int
)

fun main() {

    fun getTypePart1(card: String): TYPE {
        val map = mutableMapOf<Char, Int>()
        card.forEach {
            if (map.containsKey(it))
                map[it] = map[it]!! + 1
            else
                map[it] = 1
        }
        return when {
            map.size == 1 && map.all { it.value == 5 } -> TYPE.FIVE_KIND
            map.size == 2 && map.any { it.value == 4 } -> TYPE.FOUR_KIND
            map.size == 2 && map.any { it.value == 3 } && map.any { it.value == 2 } -> TYPE.FULL_HOUSE
            map.size == 3 && map.any { it.value == 3 } && map.filter { it.value == 1 }.count() == 2 -> TYPE.THREE_KIND
            map.size == 3 && map.filter { it.value == 2 }.count() == 2 -> TYPE.TWO_PAIR
            map.size == 4 && map.filter { it.value == 2 }.count() == 1 -> TYPE.ONE_PAIR
            map.size == 5 -> TYPE.HIGH_CARD
            else -> error("Unknown type")
        }
    }

    fun getTypePart2(card: String): TYPE {
        val map = mutableMapOf<Char, Int>()
        card.forEach {
            if (map.containsKey(it))
                map[it] = map[it]!! + 1
            else
                map[it] = 1
        }
        return when {
            map.size == 1 -> TYPE.FIVE_KIND
            map.size == 2 && map.any { it.value == 4 } -> {
                if (map.containsKey('J'))
                    TYPE.FIVE_KIND
                else
                    TYPE.FOUR_KIND
            }
            map.size == 2 && map.any { it.value == 3 } && map.any { it.value == 2 } -> {
                if (map.containsKey('J'))
                    TYPE.FIVE_KIND
                else
                    TYPE.FULL_HOUSE
            }
            map.size == 3 && map.any { it.value == 3 } && map.filter { it.value == 1 }.count() == 2 -> {
                if (map.containsKey('J'))
                    TYPE.FOUR_KIND
                else
                    TYPE.THREE_KIND
            }
            // Two pair - 22334 - where two cards share one label, two other cards share a second label, and the remaining card has a third label
            map.size == 3 && map.filter { it.value == 2 }.count() == 2 -> {
                if (map.containsKey('J')){
                    if (map['J'] == 1) {
                        TYPE.FULL_HOUSE
                    } else {
                        TYPE.FOUR_KIND
                    }
                } else {
                    TYPE.TWO_PAIR
                }
            }
            map.size == 4 && map.filter { it.value == 2 }.count() == 1 -> {
                if (map.containsKey('J'))
                    TYPE.THREE_KIND
                else
                    TYPE.ONE_PAIR
            }
            map.size == 5 -> {
                if (map.containsKey('J'))
                    TYPE.ONE_PAIR
                else
                    TYPE.HIGH_CARD
            }
            else -> error("Unknown type")
        }
    }

    fun sortRankOfSameType(cards: List<CamelCard>, part1:Boolean): List<CamelCard> {
        val strengthMap = mapOf(
                'A' to 22, 'K' to 21, 'Q' to 20, 'J' to if (part1) 19 else 9,
                'T' to 18, '9' to 17, '8' to 16, '7' to 15,
                '6' to 14, '5' to 13, '4' to 12, '3' to 11,
                '2' to 10
        )
        val comparator = Comparator<CamelCard> { o1, o2 ->
            for (i in 0..4){
                val c1 = strengthMap[o1.card[i]] ?: -1
                val c2 = strengthMap[o2.card[i]] ?: -1

                if (c1 > c2) return@Comparator 1
                if (c1 < c2) return@Comparator -1
            }
            0
        }
        return cards.sortedWith(comparator).reversed()
    }

    fun solve(lines: List<String>, part1:Boolean = true): Int {
        val strongestRank = lines.size
        return lines.map {
            val (card, bid) = it.split(" ").map { it.trim() }
            CamelCard(
                    card = it,
                    type = if (part1) getTypePart1(card) else getTypePart2(card),
                    bid = bid.toInt()
            )
        }.groupBy {
            it.type
        }.toSortedMap(compareByDescending { it.value })
                .let {
                    var currentRank = 0
                    var result = 0
                    it.onEachIndexed { index, entry ->
                        if (index == 0){
                            currentRank = strongestRank
                        }
                        if (entry.value.size == 1){
                            result += currentRank * entry.value.first().bid
                            currentRank--
                        } else {
                            val sortedList = sortRankOfSameType(entry.value, part1)
                            sortedList.forEach {
                                result += currentRank * it.bid
                                currentRank--
                            }
                        }
                    }
                    result
                }
    }

    val input = readInput("Day07")
    measureTimeMillis { solve(input, part1 = true).println() }
    measureTimeMillis { solve(input, part1 = false).println() }
}
