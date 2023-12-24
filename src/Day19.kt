private class Aplenty(val input: List<String>) {
    private val rules = hashMapOf<String, List<String>>()

    init {
        input.takeWhile { it.isNotEmpty() }.map {
            val wf = it.substringBefore("{")
            val rules = it.substringAfter("{").substringBefore("}").split(",")
            Pair(wf, rules)
        }.associateTo(rules) { it.first to it.second }
    }

    fun solvePart1(): Int {
        return input.takeLastWhile { it.isNotEmpty() }
                .map {
                    val ratingsMap = it.substringAfter("{").substringBefore("}")
                            .split(",").map {
                                Pair(it.substringBefore("="), it.substringAfter("=").toInt())
                            }.associate { it.first to it.second }
                    var wf = "in"
                    var accepted: Boolean? = null
                    while (true) {
                        for (inst in rules.getValue(wf)) {
                            val br = inst.split(":")
                            when (br.size) {
                                1 -> {
                                    if (br.first() == "A") {
                                        accepted = true
                                        break
                                    }
                                    if (br.first() == "R") {
                                        accepted = false
                                        break
                                    }
                                    wf = br.first()
                                }

                                else -> {
                                    val cG = br[0].contains(">")
                                    if (cG) {
                                        val (r, v) = br[0].split(">")
                                        if (ratingsMap.getValue(r) > v.toInt()) {
                                            wf = br[1]
                                            if (wf == "A") accepted = true
                                            if (wf == "R") accepted = false
                                            break
                                        }
                                    } else {
                                        val (r, v) = br[0].split("<")
                                        if (ratingsMap.getValue(r) < v.toInt()) {
                                            wf = br[1]
                                            if (wf == "A") accepted = true
                                            if (wf == "R") accepted = false
                                            break
                                        }
                                    }
                                }
                            }
                        }
                        if (accepted != null)
                            break
                    }
                    Pair(ratingsMap, accepted)
                }.filter { it.second == true }
                .sumOf { it.first.values.sum() }
    }

    fun solvePart2(): Long {
        data class State(val wf: String, val xR: Pair<Long,Long>, val mR: Pair<Long,Long>, val aR: Pair<Long,Long>, val sR: Pair<Long,Long>)

        fun newRange(op: String, n: Long, lo: Long, hi: Long): Pair<Long, Long> {
            return when (op) {
                ">" -> Pair(maxOf(lo, n + 1),hi)
                "<" -> Pair(lo, minOf(hi, n - 1))
                ">=" -> Pair(maxOf(lo, n),hi)
                "<=" -> Pair(lo,minOf(hi, n))
                else -> throw AssertionError()
            }
        }

        var total = 0L
        val queue = ArrayDeque<State>()
        queue.addLast(State(wf = "in", xR = Pair(1,4000), mR = Pair(1,4000), aR = Pair(1,4000), sR = Pair(1,4000)))
        while (queue.isNotEmpty()) {
            val (wf, xR, mR, aR, sR) = queue.removeLast()
            val (xl, xh) = xR
            val (ml, mh) = mR
            val (al, ah) = aR
            val (sl, sh) = sR

            when(wf){
                "A" -> {
                    total += (xh - xl + 1) * (mh - ml + 1) * (ah - al + 1) * (sh - sl + 1)
                    continue
                }
                "R" -> continue
                else ->{
                    var xrr = xR
                    var mrr = mR
                    var arr = aR
                    var srr = sR
                    for (inst in rules.getValue(wf)) {
                        val br = inst.split(":")
                        when (br.size) {
                            1 -> queue.addLast(State(br.first(), xrr, mrr, arr, srr))
                            else -> {
                                val condn = br[0]
                                val op = if (condn.contains(">")) ">" else "<"
                                val (rating, num) = condn.split(op)
                                val newOp = if (op == ">") "<=" else ">="
                                when (rating) {
                                    "x" -> {
                                        queue.addLast(State(br[1], newRange(op, num.toLong(), xrr.first, xrr.second), mrr, arr, srr))
                                        xrr = newRange(newOp, num.toLong(), xrr.first, xrr.second)
                                    }
                                    "m" -> {
                                        queue.addLast(State(br[1], xrr, newRange(op, num.toLong(), mrr.first,mrr.second), arr, srr))
                                        mrr = newRange(newOp, num.toLong(), mrr.first, mrr.second)
                                    }
                                    "a" -> {
                                        queue.addLast(State(br[1], xrr, mrr, newRange(op, num.toLong(), arr.first,arr.second), srr))
                                        arr = newRange(newOp, num.toLong(), arr.first, arr.second)
                                    }
                                    "s" -> {
                                        queue.addLast(State(br[1], xrr, mrr, arr, newRange(op, num.toLong(),srr.first,srr.second)))
                                        srr = newRange(newOp, num.toLong(), srr.first, srr.second)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        return total
    }
}

fun main() {

    fun part1(input: List<String>): Int {
        val aplenty = Aplenty(input)
        return aplenty.solvePart1()
    }

    fun part2(input: List<String>): Long {
        val aplenty = Aplenty(input)
        return aplenty.solvePart2()
    }

    val input = readInput("Day19")
    part1(input).println()
    part2(input).println()
}
