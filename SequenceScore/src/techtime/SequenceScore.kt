package techtime

import java.security.MessageDigest
import javax.xml.bind.DatatypeConverter
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

// Definitions from the challenge description:
fun trace(n: Int): String {
    val digest = MessageDigest.getInstance("SHA-1")
    val inputBytes = n.toString().toByteArray()
    val outputBytes = digest.digest(inputBytes)
    return DatatypeConverter.printHexBinary(outputBytes).filter { it.isLetter() }.toLowerCase()
}

fun levenshtein(a: String, b: String): Int = LevenshteinDistance.distance(a, b).toInt()

fun affinity(n: Int, m: Int): Int = levenshtein(trace(n), trace(m))

fun posdist(n: Int, m: Int, sequence: List<Int>): Int = abs(sequence.indexOf(n) - sequence.indexOf(m))

fun pairScore(n: Int, m: Int, sequence: List<Int>): Double = affinity(n, m).toDouble() / posdist(n, m, sequence).toDouble()

fun score(sequence: List<Int>): Double {
    var total = 0.0
    var pairs = 0
    (0..99).forEach { m ->
        (0 until m).forEach { n ->
            if (posdist(n, m, sequence) <= 3) {
                val ps = pairScore(n, m, sequence)
                total += ps
                pairs += 1
            }
        }
    }
    return total
}

// This computes the same results as score, but faster and it works for sequences of any length. Working for sequences
// of any length is especially helpful for the greedy algorithm below and in the `improveWithSwaps` step (see below).
// The inner loop of `score` runs approximately 5000 times, whereas the inner loop of `scoreFast` runs approximately
// 300 times (for a sequence of 100 numbers).
fun scoreFast(sequence: List<Int>): Double {
    var total = 0.0
    // `x..y` is the range of numbers x to y, inclusive, whereas `x until y` includes x but not y.
    (0 until sequence.size).forEach { j ->
        // The definition of score says only to include pairScores where posdist(n, m, sequence) <= 3
        (max(0, j-3) until j).forEach { i ->
            // Note `n` and `m` are obtained from the positions `i` and `j`.
            val n = sequence[i]
            val m = sequence[j]
            total += pairScore(n, m, sequence)
        }
    }
    return total
}

// Like `greedyNnWithStart`, but trying every possible start and seeing which one yields the best result.
fun greedyNn(): Pair<Double, List<Int>> {
    var bestScore = -1.0
    var bestSequence: List<Int>? = null
    (0..99).forEach {
        println("Greedy nearest neighbour starting from $it...")
        val (score, seq) = greedyNnWithStart(it)
        if (score > bestScore) {
            bestScore = score
            bestSequence = seq
        }
    }
    return Pair(bestScore, bestSequence!!)
}

// Grow the sequence in a simple greedy fashion, starting with a given number.
fun greedyNnWithStart(start: Int): Pair<Double, List<Int>> {
    // Begin by removing `start` from `remaining`:
    val remaining: MutableList<Int> = (0..99).toMutableList()
    remaining.remove(start)
    val seq: MutableList<Int> = mutableListOf(start)

    // Each iteration of this outer loop removes one number from `remaining` and adds it to `seq`:
    while (remaining.isNotEmpty()) {
        var topScore = -1.0
        var topScorer = -1
        // This loop finds which of the remaining numbers yields the highest score for the sequence so far:
        remaining.forEach {
            val score = scoreFast(seq + it)
            if (score > topScore) {
                topScorer = it
                topScore = score
            }
        }
        seq.add(topScorer)
        remaining.remove(topScorer)
    }
    return Pair(scoreFast(seq), seq.toList())
}

// Repeatedly trying all possible two-way swaps until no more improvements can be found.
fun improveWithSwaps(result: Pair<Double, List<Int>>): Pair<Double, List<Int>> {
    val workingSeq = result.second.toMutableList()
    var workingScore = result.first
    var improved: Boolean
    do {
        improved = false
        for (i in (0..99)) {
            for (j in (0..99)) {
                if (i == j) continue
                // We only need to compare the scores of the slices of the list that would be affected by the swap:
                val iMin = max(0, i-3)
                val iMax = min(99, i+3)
                val jMin = max(0, j-3)
                val jMax = min(99, j+3)
                val iSlice = workingSeq.subList(iMin, iMax+1)
                val jSlice = workingSeq.subList(jMin, jMax+1)
                // Combined score of the above slices:
                val currentScore = scoreFast(iSlice) + scoreFast(jSlice)

                // Put `j`th number in the middle of `iSlice`
                val iSwappedSlice = iSlice.toMutableList()
                iSwappedSlice[i-iMin] = workingSeq[j]
                // Put the `i`th number in the middle of `jSlice`
                val jSwappedSlice = jSlice.toMutableList()
                jSwappedSlice[j-jMin] = workingSeq[i]

                // Combined score of the slices after swap:
                val potentialScore = scoreFast(iSwappedSlice) + scoreFast(jSwappedSlice)

                if (potentialScore > currentScore) {
                    // Hooray we found an improvement:
                    val temp = workingSeq[i]
                    workingSeq[i] = workingSeq[j]
                    workingSeq[j] = temp
                    val newScore = scoreFast(workingSeq)
                    assert(newScore > workingScore)
                    println("Improve score from $workingScore to $newScore by swapping positions $i and $j")
                    //println(workingSeq.joinToString(", "))
                    workingScore = newScore

                    // This `improved` makes the outer loop keep going until a whole iteration is passed with no
                    // improvements found and then you may as well stop. Like when you're playing solitaire and you get
                    // all the way through the deck without making a move.
                    improved = true
                }
            }
        }
    } while (improved)
    return Pair(scoreFast(workingSeq), workingSeq.toList())
}

fun main(args: Array<String>) {
    val greedyNnResult = greedyNn()
    println("Greedy nearest neigbour result:")
    println(greedyNnResult.second.joinToString(", "))
    println(greedyNnResult.first)
    val improvedResult = improveWithSwaps(greedyNnResult)
    println("Swap-improved result:")
    println(improvedResult.second.joinToString(", "))
    println(improvedResult.first)
}

// Greedy NN result:
// 2286.500000000001
// 34, 59, 17, 8, 14, 67, 92, 80, 78, 75, 84, 19, 35, 3, 26, 91, 27, 69, 38, 23, 90, 86, 1, 37, 13, 2, 49, 70, 95, 33, 81, 94, 25, 39, 20, 42, 62, 6, 88, 73, 24, 97, 44, 52, 51, 96, 61, 43, 9, 68, 65, 98, 22, 50, 66, 30, 0, 56, 77, 31, 60, 40, 29, 71, 55, 28, 82, 57, 87, 21, 76, 32, 79, 54, 36, 53, 83, 47, 74, 99, 5, 11, 72, 18, 64, 10, 93, 85, 15, 16, 7, 46, 89, 48, 41, 63, 45, 58, 12, 4

// With improvements:
// 2365.3
// 72, 78, 17, 84, 41, 0, 92, 38, 45, 63, 93, 19, 79, 3, 48, 91, 27, 69, 58, 23, 90, 86, 1, 60, 15, 77, 56, 70, 95, 33, 81, 94, 25, 39, 20, 42, 62, 6, 12, 73, 24, 97, 74, 52, 51, 96, 61, 43, 9, 44, 85, 80, 49, 37, 65, 89, 35, 34, 2, 31, 98, 8, 29, 71, 55, 28, 82, 57, 87, 21, 76, 32, 13, 54, 36, 53, 59, 47, 26, 18, 5, 11, 88, 99, 64, 10, 22, 75, 50, 40, 7, 46, 30, 67, 68, 66, 14, 16, 83, 4
