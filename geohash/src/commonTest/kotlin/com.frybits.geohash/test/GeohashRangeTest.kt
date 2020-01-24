package com.frybits.geohash.test

import com.frybits.geohash.Geohash
import kotlin.test.Test
import kotlin.test.assertFailsWith

/**
 * Frybits
 * Created by Pablo Baxter (Github: pablobaxter)
 */

class GeohashRangeTest {

    @Test
    fun geohashRange_for_geohashes_of_differing_precision() {
        assertFailsWith<IllegalArgumentException>("Geohashes must be of the same precision to get a range") {
            Geohash("0")..Geohash("zz")
        }
    }
}
