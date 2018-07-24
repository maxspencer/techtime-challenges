package techtime

import org.junit.Assert.*
import org.junit.Test

class SequenceScoreKtTest {

    @Test
    fun trace() {
        assertEquals("bbfdaaedab", trace(12))
    }

    @Test
    fun levenshtein() {
        assertEquals(3, levenshtein("kitten", "sitting"))
    }

    @Test
    fun affinity() {

    }

    @Test
    fun posdist() {
        val seq = listOf(1111, 12, 54, 89, 91, 1112)
        assertEquals(1, posdist(12, 54, seq))
        assertEquals(2, posdist(12, 89, seq))
    }

    @Test
    fun pairScore() {
    }

    @Test
    fun score() {
        val seq = listOf(56, 44, 7, 18, 6, 61, 98, 4, 21, 64, 58, 81, 78, 22, 93, 55, 79, 24, 86, 13, 51, 57, 90, 54, 92, 67, 8, 88, 3, 36, 84, 99, 77, 1, 37, 38, 14, 29, 50, 60, 35, 42, 70, 73, 27, 53, 46, 83, 23, 52, 94, 63, 96, 33, 12, 31, 91, 69, 0, 28, 80, 30, 40, 41, 34, 75, 71, 89, 87, 65, 9, 62, 82, 11, 48, 17, 49, 39, 16, 5, 74, 25, 95, 2, 85, 10, 76, 32, 59, 66, 43, 45, 47, 20, 15, 72, 68, 97, 19, 26)
        assertEquals(2121.5, score(seq), 0.001)
    }

    @Test
    fun scoreFast() {
        val seq = listOf(56, 44, 7, 18, 6, 61, 98, 4, 21, 64, 58, 81, 78, 22, 93, 55, 79, 24, 86, 13, 51, 57, 90, 54, 92, 67, 8, 88, 3, 36, 84, 99, 77, 1, 37, 38, 14, 29, 50, 60, 35, 42, 70, 73, 27, 53, 46, 83, 23, 52, 94, 63, 96, 33, 12, 31, 91, 69, 0, 28, 80, 30, 40, 41, 34, 75, 71, 89, 87, 65, 9, 62, 82, 11, 48, 17, 49, 39, 16, 5, 74, 25, 95, 2, 85, 10, 76, 32, 59, 66, 43, 45, 47, 20, 15, 72, 68, 97, 19, 26)
        assertEquals(2121.5, scoreFast(seq), 0.001)
        //val shuffled = seq.shuffled()
        //assertEquals(score(shuffled), scoreFast(shuffled), 0.001)
    }
}