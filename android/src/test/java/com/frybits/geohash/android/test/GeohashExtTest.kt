package com.frybits.geohash.android.test

import com.frybits.geohash.Geohash
import org.junit.Test

/**
 * Frybits
 * Created by Pablo Baxter (Github: pablobaxter)
 */

const val TEST_GEOHASH_1 = "9wkrtyvvw3wd"
const val TEST_LAT_1 = 36.51264084
const val TEST_LON_1 = -106.26257842

class GeohashExtTest {

    @Test
    fun testExt() {
        val geohash = Geohash(TEST_GEOHASH_1)
        println(geohash.boundingBox)
    }
}
