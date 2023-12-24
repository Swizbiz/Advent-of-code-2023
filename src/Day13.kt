fun main() {

    fun Array<CharArray>.transpose(): Array<CharArray> {
        val newGrid = Array(this[0].size) { CharArray(this.size) }
        for (row in indices)
            for (col in this[0].indices)
                newGrid[col][row] = this[row][col]
        return newGrid
    }

    fun Array<CharArray>.invalids(pairs: List<Pair<Int, Int>>): List<Pair<Pair<Int, Int>, Pair<Int, Int>>> {
        val invalid = mutableListOf<Pair<Pair<Int, Int>, Pair<Int, Int>>>()
        for ((_, pair) in pairs.withIndex()) {
            var nsRow = pair.first
            var neRow = pair.second
            while (nsRow >= 0 && neRow < this.size) {
                if (this[nsRow].contentEquals(this[neRow])) {
                    nsRow--
                    neRow++
                    continue
                }
                if (!this[nsRow].contentEquals(this[neRow])) break
            }
            if (nsRow + 1 > 0 && neRow - 1 == this.size - 1) continue
            if (nsRow + 1 == 0 && neRow - 1 < this.size - 1) continue
            if (nsRow == 0 && neRow == this.size - 1) continue
            val dIdx = this[nsRow].indices.filter { this[nsRow][it] != this[neRow][it] }
            if (dIdx.size == 1)
                invalid.add(Pair(Pair(nsRow, dIdx.first()), Pair(pair.first, pair.second)))
        }
        return invalid.toList()
    }

    fun Array<CharArray>.smudgeList(): List<Pair<Pair<Int, Int>, Pair<Int, Int>>> {
        val pairs = mutableListOf<Pair<Int, Int>>()
        val smudges = mutableListOf<Pair<Pair<Int, Int>, Pair<Int, Int>>>()
        for (row in indices) {
            if (row + 1 > this.size - 1) break
            if (this[row].contentEquals(this[row + 1])) pairs.add(Pair(row, row + 1))
            val dIdx = this[row].indices.filter { this[row][it] != this[row + 1][it] }
            if (dIdx.size == 1) smudges.add(Pair(Pair(row, dIdx.first()), Pair(row, row + 1)))
        }
        return smudges + invalids(pairs)
    }

    fun Array<CharArray>.deepCopy(): Array<CharArray> {
        val nArr = Array(this.size) { CharArray(this[0].size) }
        for (row in indices) {
            for (col in this[0].indices)
                nArr[row][col] = this[row][col]
        }
        return nArr
    }

    fun Array<CharArray>.findMirror(smudgeList: List<Pair<Pair<Int, Int>, Pair<Int, Int>>>): Int {
        smudgeList.forEach { p ->
            val matrix = this.deepCopy()
            if (matrix[p.first.first][p.first.second] == '.') matrix[p.first.first][p.first.second] = '#'
            else matrix[p.first.first][p.first.second] = '.'

            var nsRow = p.second.first
            var neRow = p.second.second

            while (nsRow >= 0 && neRow < matrix.size) {
                if (matrix[nsRow].contentEquals(matrix[neRow])) {
                    nsRow--
                    neRow++
                    continue
                }
                if (!matrix[nsRow].contentEquals(matrix[neRow])) break
            }
            if (nsRow + 1 > 0 && neRow - 1 == matrix.size - 1) return p.second.first
            if (nsRow + 1 == 0 && neRow - 1 < matrix.size - 1) return p.second.first
            if (nsRow == 0 && neRow == matrix.size - 1) return p.second.first
        }
        return -1
    }

    fun Array<CharArray>.findMirror(): Int {
        val pairs = mutableListOf<Pair<Int, Int>>()
        for (row in indices)
            if (row + 1 < this.size && this[row].contentEquals(this[row + 1]))
                pairs.add(Pair(row, row + 1))

        for ((_, pair) in pairs.withIndex()) {
            var nsRow = pair.first
            var neRow = pair.second
            while (nsRow >= 0 && neRow < this.size) {
                if (this[nsRow].contentEquals(this[neRow])) {
                    nsRow--
                    neRow++
                    continue
                }
                if (!this[nsRow].contentEquals(this[neRow])) break
            }
            if (nsRow + 1 > 0 && neRow - 1 == this.size - 1) return pair.first
            if (nsRow + 1 == 0 && neRow - 1 < this.size - 1) return pair.first
            if (nsRow == 0 && neRow == this.size - 1) return pair.first
        }
        return -1
    }


    fun part1(input: List<String>): Int {
        return input.fold(listOf(emptyList<String>())) { acc, s ->
            if (s.isEmpty()) {
                acc.plusElement(emptyList())
            } else {
                acc.dropLast(1).plusElement(acc.last() + listOf(s))
            }
        }.map { list ->
            val grid = Array(list.size) { CharArray(list[0].length) }
            for (row in list.indices)
                for (col in list[0].indices)
                    grid[row][col] = list[row][col]

            val vM = grid.transpose().findMirror()
            if (vM != -1) {
                vM + 1
            } else {
                val hM = (grid.findMirror() + 1) * 100
                hM
            }
        }.sum()
    }

    fun part2(input: List<String>): Int {
        return input.fold(listOf(emptyList<String>())) { acc, s ->
            if (s.isEmpty()) {
                acc.plusElement(emptyList())
            } else {
                acc.dropLast(1).plusElement(acc.last() + listOf(s))
            }
        }.map { list ->
            val grid1 = Array(list.size) { CharArray(list[0].length) }
            val grid2 = Array(list.size) { CharArray(list[0].length) }
            for (row in list.indices) {
                for (col in list[0].indices) {
                    grid1[row][col] = list[row][col]
                    grid2[row][col] = list[row][col]
                }
            }
            val transpose = grid1.transpose()
            val vM = transpose.findMirror(transpose.smudgeList())
            if (vM != -1) {
                vM + 1
            } else {
                val s = grid2.smudgeList()
                val hM = (grid2.findMirror(s) + 1) * 100
                hM
            }
        }.sum()
    }

    val input = readInput("Day13")
    part1(input).println()
    part2(input).println()
}
