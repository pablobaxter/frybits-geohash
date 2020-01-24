package com.frybits.geohash.test

import com.frybits.geohash.internal.LatLonBits
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Frybits
 * Created by Pablo Baxter (Github: pablobaxter)
 */

class LatLonBitsTest {

    @Test
    fun recombine_bits() {
        val test1 = LatLonBits(
            TEST_LAT_BITS_1,
            TEST_LON_BITS_1,
            TEST_CHAR_PRECISION_1
        )
        assertEquals(TEST_COMBINED_BITS_1, test1.combinedBits)

        val test2 = LatLonBits(
            TEST_LAT_BITS_2,
            TEST_LON_BITS_2,
            TEST_CHAR_PRECISION_2
        )
        assertEquals(TEST_COMBINED_BITS_2, test2.combinedBits)

        val test3 = LatLonBits(
            TEST_LAT_BITS_3,
            TEST_LON_BITS_3,
            TEST_CHAR_PRECISION_3
        )
        assertEquals(TEST_COMBINED_BITS_3, test3.combinedBits)
    }

    @Test
    fun split_combined_bits() {
        val test1 = LatLonBits(TEST_COMBINED_BITS_1)
        assertEquals(TEST_LAT_BITS_1, test1.latBits)
        assertEquals(TEST_LON_BITS_1, test1.lonBits)

        val test2 = LatLonBits(TEST_COMBINED_BITS_2)
        assertEquals(TEST_LAT_BITS_2, test2.latBits)
        assertEquals(TEST_LON_BITS_2, test2.lonBits)

        val test3 = LatLonBits(TEST_COMBINED_BITS_3)
        assertEquals(TEST_LAT_BITS_3, test3.latBits)
        assertEquals(TEST_LON_BITS_3, test3.lonBits)
    }

    @Test
    fun compare_LatLonBits() {
        val latLonBits1 = LatLonBits(
            TEST_LAT_BITS_1,
            TEST_LON_BITS_1,
            TEST_CHAR_PRECISION_1
        )
        val combinedBits1 = LatLonBits(TEST_COMBINED_BITS_1)

        assertEquals(latLonBits1, combinedBits1)

        val latLonBits2 = LatLonBits(
            TEST_LAT_BITS_2,
            TEST_LON_BITS_2,
            TEST_CHAR_PRECISION_2
        )
        val combinedBits2 = LatLonBits(TEST_COMBINED_BITS_2)

        assertEquals(latLonBits2, combinedBits2)

        val latLonBits3 = LatLonBits(
            TEST_LAT_BITS_3,
            TEST_LON_BITS_3,
            TEST_CHAR_PRECISION_3
        )
        val combinedBits3 = LatLonBits(TEST_COMBINED_BITS_3)

        assertEquals(latLonBits3, combinedBits3)
    }

    // TODO Test invalid LatLonBits
}
