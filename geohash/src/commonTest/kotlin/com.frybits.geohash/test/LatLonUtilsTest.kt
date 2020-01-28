package com.frybits.geohash.test

import com.frybits.geohash.MAX_CHAR_PRECISION
import com.frybits.geohash.internal.evenOddBitsRightAligned
import com.frybits.geohash.internal.latLonBits
import com.frybits.geohash.internal.recombineBits
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Frybits
 * Created by Pablo Baxter (Github: pablobaxter)
 */

class LatLonUtilsTest {

    @Test
    fun recombine_bits() {
        val test1 = recombineBits(
            TEST_LAT_BITS_1,
            TEST_LON_BITS_1,
            TEST_CHAR_PRECISION_1
        )
        assertEquals(TEST_COMBINED_BITS_1, test1)

        val test2 = recombineBits(
            TEST_LAT_BITS_2,
            TEST_LON_BITS_2,
            TEST_CHAR_PRECISION_2
        )
        assertEquals(TEST_COMBINED_BITS_2, test2)

        val test3 = recombineBits(
            TEST_LAT_BITS_3,
            TEST_LON_BITS_3,
            TEST_CHAR_PRECISION_3
        )
        assertEquals(TEST_COMBINED_BITS_3, test3)
    }

    @Test
    fun split_combined_bits() {
        val test1 = evenOddBitsRightAligned(TEST_COMBINED_BITS_1)
        assertEquals(TEST_LAT_BITS_1, test1[0])
        assertEquals(TEST_LON_BITS_1, test1[1])

        val test2 = evenOddBitsRightAligned(TEST_COMBINED_BITS_2)
        assertEquals(TEST_LAT_BITS_2, test2[0])
        assertEquals(TEST_LON_BITS_2, test2[1])

        val test3 = evenOddBitsRightAligned(TEST_COMBINED_BITS_3)
        assertEquals(TEST_LAT_BITS_3, test3[0])
        assertEquals(TEST_LON_BITS_3, test3[1])
    }

    @Test
    fun LatLonBits_validation() {
        assertFailsWith<IllegalArgumentException>("Invalid hash bits! Geohash must be between 1 and $MAX_CHAR_PRECISION characters") {
            recombineBits(Random.nextLong(), Random.nextLong(), 13)
        }

        assertFailsWith<IllegalArgumentException>("Invalid hash bits! Geohash must be between 1 and $MAX_CHAR_PRECISION characters") {
            evenOddBitsRightAligned(13L)
        }
    }

    @Test
    fun compare_LatLonBits() {
        val latLonBits1 = latLonBits(
            TEST_LAT_BITS_1,
            TEST_LON_BITS_1,
            TEST_CHAR_PRECISION_1
        )
        val combinedBits1 = latLonBits(TEST_COMBINED_BITS_1)

        assertEquals(latLonBits1, combinedBits1)

        val latLonBits2 = latLonBits(
            TEST_LAT_BITS_2,
            TEST_LON_BITS_2,
            TEST_CHAR_PRECISION_2
        )
        val combinedBits2 = latLonBits(TEST_COMBINED_BITS_2)

        assertEquals(latLonBits2, combinedBits2)

        val latLonBits3 = latLonBits(
            TEST_LAT_BITS_3,
            TEST_LON_BITS_3,
            TEST_CHAR_PRECISION_3
        )
        val combinedBits3 = latLonBits(TEST_COMBINED_BITS_3)

        assertEquals(latLonBits3, combinedBits3)
    }
}
