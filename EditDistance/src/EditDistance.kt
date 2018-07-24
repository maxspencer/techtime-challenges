import java.util.*

fun main(args: Array<String>) {
    println(PascaleDistance.distance("Luc", "Lake"))
}

val PascaleDistance = EditDistanceMetric(
        { if (it.isVowel()) 0.5f else 1.0f },
        { if (it.isVowel()) 0.5f else 1.0f },
        { a, b -> if (a.isVowel() || b.isVowel()) 0.5f else 1.0f }
)

val LevensteinDistance = EditDistanceMetric(
        { 1.0f },
        { 1.0f },
        { _, _ -> 1.0f }
)

fun add(a: Int, b: Int): Int {
    return a + b
}

fun <R, S, T> partial(binaryFunction: (R, S) -> T, a: R): (S) -> T {
    return { b: S ->
        binaryFunction(a, b)
    }
}

val addOne = partial(::add, 1)

val shoulbBeThree = addOne(2)

private fun Char.isVowel(): Boolean = this == 'a' || this == 'e' || this == 'i' || this == 'o' || this == 'u'

class EditDistanceMetric(
        private val additionCostFunction: (Char) -> Float,
        private val deletionCostFunction: (Char) -> Float,
        private val substitutionCostFunction: (Char, Char) -> Float
) {
    fun distance(a: String, b: String): Float {
        val dm: Array<Array<Float>> = Array(b.length + 1) { row ->
            Array(a.length + 1) { col ->
                when {
                    row == 0 -> col.toFloat()
                    col == 0 -> row.toFloat()
                    else -> -1.0f
                }
            }
        }
        for (y in 1..b.length) {
            for (x in 1..a.length) {
                dm[y][x] = minOf(
                        dm[y-1][x-1] + substitutionCostFunction(a[x-1], b[y-1]),
                        dm[y-1][x] + additionCostFunction(b[y-1]),
                        dm[y][x-1] + deletionCostFunction(a[x-1])
                )
            }
        }
        return dm[b.length][a.length]
    }

    fun distance2(a: String, b: String): Float {
        val results: PriorityQueue<PartialResult> = PriorityQueue()
        
        return results.peek().cost
    }
}

class PartialResult(val x: Int, val y: Int, val cost: Float): Comparable<PartialResult> {
    override fun compareTo(other: PartialResult): Int {
        return when {
            cost > other.cost -> 1
            cost < other.cost -> -1
            else -> 0
        }
    }

}
