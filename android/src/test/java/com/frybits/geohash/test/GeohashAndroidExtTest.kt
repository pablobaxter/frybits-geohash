package com.frybits.geohash.test

import android.location.Location
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.frybits.geohash.toGeohash
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Frybits
 * Created by Pablo Baxter (Github: pablobaxter)
 */

// For geohashes
const val TEST_GEOHASH_1 = "9wkrtyvvw3wd"
const val TEST_LAT_1 = 36.51264084
const val TEST_LON_1 = -106.26257842

const val TEST_GEOHASH_2 = "w18md"
const val TEST_LAT_2 = 9.426
const val TEST_LON_2 = 90.461

const val TEST_GEOHASH_3 = "000000z"
const val TEST_LAT_3 = -89.9952
const val TEST_LON_3 = -179.9897

@RunWith(AndroidJUnit4::class)
class GeohashExtTest {

    @Test
    fun location_extension() {
        val location1 = Location("geohash").apply {
            latitude = TEST_LAT_1
            longitude = TEST_LON_1
        }
        assertEquals(TEST_GEOHASH_1, location1.toGeohash(TEST_GEOHASH_1.length).geohash)

        val location2 = Location("geohash").apply {
            latitude = TEST_LAT_2
            longitude = TEST_LON_2
        }
        assertEquals(TEST_GEOHASH_2, location2.toGeohash(TEST_GEOHASH_2.length).geohash)

        val location3 = Location("geohash").apply {
            latitude = TEST_LAT_3
            longitude = TEST_LON_3
        }
        assertEquals(TEST_GEOHASH_3, location3.toGeohash(TEST_GEOHASH_3.length).geohash)
    }
}
