package com.frybits.geohash.test

import com.frybits.geohash.GEOHASH_CHARS
import com.frybits.geohash.Geohash
import com.frybits.geohash.MAX_CHAR_PRECISION
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Frybits
 * Created by Pablo Baxter (Github: pablobaxter)
 */

class GeohashRangeTest {

    @Test
    fun iterating_using_range() {
        val startChar = GEOHASH_CHARS.first()
        val endChar = GEOHASH_CHARS.last()

        (Geohash(startChar.toString())..Geohash(endChar.toString())).forEachIndexed { i, g ->
            assertEquals(GEOHASH_CHARS[i].toString(), g.geohash)
        }
    }

    @Test
    fun range_contains_checks() {
        // Essentially range through all geohashes
        val range = (Geohash("000000000000")..Geohash("zzzzzzzzzzzz"))
        repeat(REPEAT_TEST_COUNT) {
            val testHash = Random.geoHash(MAX_CHAR_PRECISION)
            assertTrue(testHash.geohash) { range.contains(testHash) }
        }
    }

    @Test
    fun range_validate_empty() {
        val range = (Geohash("zzz")..Geohash("000"))
        assertTrue { range.isEmpty() }
        repeat(REPEAT_TEST_COUNT) {
            val testHash = Random.geoHash(Random.precision(maxCharPrecision = 3))
            assertFalse(testHash.geohash) { range.contains(testHash) }
        }
    }

    @Test
    fun range_for_geohashes_of_differing_precision() {
        assertFailsWith<IllegalArgumentException>("Geohashes must be of the same precision to get a range") {
            Geohash("0")..Geohash("zz")
        }
    }
}
