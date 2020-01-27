package com.frybits.geohash.test

import android.location.Location
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.frybits.geohash.Geohash
import com.frybits.geohash.location
import com.frybits.geohash.toGeohash
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.pow

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

// This gives approximate lat/lon error given the precision
fun approxLatitudeError(precision: Int) = 90.0 / 2.0.pow(approxCoordinateBits(precision))
fun approxLongitudeError(precision: Int): Double {
    val bits = approxCoordinateBits(precision)
    // When precision is odd, there is one extra longitude bit, otherwise it has equal latitude and longitude bits
    return 180.0 / 2.0.pow(if (precision % 2 == 0) bits else bits + 1)
}

// Gives number of bits used for either latitude or longitude
private fun approxCoordinateBits(precision: Int): Int = (precision * 5 / 2)


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

    @Test
    fun geohash_to_location_extension() {
        val geohash1 = Geohash(TEST_GEOHASH_1)
        val location1 = Location(geohash1.geohash).apply {
            latitude = TEST_LAT_1
            longitude = TEST_LON_1
        }
        assertEquals(location1.provider, geohash1.location.provider)
        assertEquals(location1.latitude, geohash1.location.latitude, approxLatitudeError(geohash1.charPrecision))
        assertEquals(location1.longitude, geohash1.location.longitude, approxLongitudeError(geohash1.charPrecision))

        val geohash2 = Geohash(TEST_GEOHASH_2)
        val location2 = Location(geohash2.geohash).apply {
            latitude = TEST_LAT_2
            longitude = TEST_LON_2
        }
        assertEquals(location2.provider, geohash2.location.provider)
        assertEquals(location2.latitude, geohash2.location.latitude, approxLatitudeError(geohash2.charPrecision))
        assertEquals(location2.longitude, geohash2.location.longitude, approxLongitudeError(geohash2.charPrecision))

        val geohash3 = Geohash(TEST_GEOHASH_3)
        val location3 = Location(geohash3.geohash).apply {
            latitude = TEST_LAT_3
            longitude = TEST_LON_3
        }
        assertEquals(location3.provider, geohash3.location.provider)
        assertEquals(location3.latitude, geohash3.location.latitude, approxLatitudeError(geohash3.charPrecision))
        assertEquals(location3.longitude, geohash3.location.longitude, approxLongitudeError(geohash3.charPrecision))
    }
}
