import java.util.*

private class Day20System(val broadcaster: List<String>, val modules: Map<String, Module>) {
    data class Module(var name: String, var type: Type, val outputs: List<String>) {
        enum class Type {
            FlipFlop,
            Conjunction;

            companion object {
                fun from(str: Char) = if (str == '%') FlipFlop else Conjunction
            }
        }

        companion object {
            fun parse(input: String): Pair<String, Module> {
                val (moduleStr, outputStr) = input.split(" -> ")
                val type = Type.from(moduleStr.first())
                val name = moduleStr.drop(1)
                val outputs = outputStr.split(", ")
                return name to Module(name, type, outputs)
            }
        }

        var memory: MutableMap<String, Boolean> = mutableMapOf()
        var on = false

        fun addInput(input: String) {
            memory[input] = PULSE_LOW
        }

        fun route(signal: Signal): Signal? {
            return if (type == Type.FlipFlop) {
                if (signal.pulse == PULSE_HIGH) return null
                on = !on
                Signal(on, name, outputs)
            } else {
                memory[signal.from] = signal.pulse
                val outputPulse = if (memory.values.all { it == PULSE_HIGH }) PULSE_LOW else PULSE_HIGH
                Signal(outputPulse, name, outputs)
            }
        }
    }

    data class Signal(val pulse: Boolean, val from: String,  val to:List<String>)

    companion object {
        const val PULSE_HIGH = true
        const val PULSE_LOW = false

        fun parse(input: List<String>): Day20System {
            val broadcasterIndex = input.indexOfFirst { it.startsWith("broadcaster") }
            val broadcaster = input[broadcasterIndex].substringAfter("-> ").split(", ")
            val modules = input.filterIndexed { i, _ -> i != broadcasterIndex }.associate {
                Module.parse(it)
            }
            modules.forEach { (k, v) ->
                for (output in v.outputs) {
                    if (modules[output] != null) modules.getValue(output).addInput(k)
                }
            }

            return Day20System(broadcaster, modules)
        }
    }

    fun outputPulse(pulse: Boolean = PULSE_LOW, times: Int = 1000): Long {
        var highPulse = 0L
        var lowPulse = 0L
        var attempt = 0
        while (attempt < times) {
            if (pulse == PULSE_HIGH) highPulse++ else lowPulse++
            val queue = LinkedList<Signal>()
            queue.add(Signal(pulse, "button", broadcaster))
            while (queue.isNotEmpty()) {
                val signal = queue.pop()
                for (input in signal.to) {
                    if (signal.pulse == PULSE_HIGH) highPulse++
                    else lowPulse++

                    if (!modules.contains(input)) continue
                    val next = modules.getValue(input).route(signal) ?: continue
                    queue.add(next)
                }
            }
            attempt++
        }

        return highPulse * lowPulse
    }

    fun leastButtonPress(pulse: Boolean = PULSE_LOW, destination: String): Long {
        var attempt = 0L
        var needPulse = PULSE_HIGH
        var conjunctionsToDestination = modules.filter { it.value.outputs.contains(destination) }.values.map { it.name }.toMutableList()
        while (conjunctionsToDestination.size == 1) {
            needPulse = !needPulse
            conjunctionsToDestination = modules.getValue(conjunctionsToDestination.first()).memory.keys.toMutableList()
        }
        val conjunctionsToDestinationCycle = conjunctionsToDestination.associateWith { -1L }.toMutableMap()
        while (conjunctionsToDestinationCycle.any { it.value == -1L }) {
            attempt++
            val queue = LinkedList<Signal>()
            queue.add(Signal(pulse, "button", broadcaster))
            while (queue.isNotEmpty()) {
                val signal = queue.pop()
                for (input in signal.to) {
                    if (input in conjunctionsToDestinationCycle && signal.pulse == needPulse) conjunctionsToDestinationCycle[input] = attempt
                    if (!modules.contains(input)) continue

                    val next = modules.getValue(input).route(signal) ?: continue
                    queue.add(next)
                }
            }
        }

        return conjunctionsToDestinationCycle.values.reduce { acc, cycles -> lcm(acc, cycles) }
    }
    private tailrec fun gcd(x: Long, y: Long): Long = if (y == 0L) x else gcd(y, x % y)
    private fun lcm(x: Long, y: Long) = x * y / gcd(x, y)

}

fun main() {
    val input = readInput("Day20")

    part1(input).println()
    part2(input).println()
}

private fun part1(input: List<String>): Long {
    val system = Day20System.parse(input)
    return system.outputPulse()
}

private fun part2(input: List<String>): Long {
    val system = Day20System.parse(input)
    return system.leastButtonPress(destination = "rx")
}
