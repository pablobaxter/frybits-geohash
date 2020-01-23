package com.frybits.geohash.test

import com.frybits.geohash.toBoundingBox
import com.frybits.geohash.toBoundingBoxAndBits
import com.frybits.geohash.toGeohashString
import com.frybits.geohash.toLatLonBits
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Frybits
 * Created by Pablo Baxter (Github: pablobaxter)
 */

class GeohashUtilsTest {

    @Test
    fun `Lat-Lon to bits to geohash string`() {
        val geohash1 = toGeohashString(toLatLonBits(TEST_LAT_1, TEST_LON_1, TEST_GEOHASH_1.length))
        assertEquals(TEST_GEOHASH_1, geohash1)

        val geohash2 = toGeohashString(toLatLonBits(TEST_LAT_2, TEST_LON_2, TEST_GEOHASH_2.length))
        assertEquals(TEST_GEOHASH_2, geohash2)

        val geohash3 = toGeohashString(toLatLonBits(TEST_LAT_3, TEST_LON_3, TEST_GEOHASH_3.length))
        assertEquals(TEST_GEOHASH_3, geohash3)
    }

    @Test
    fun `Valid random lat-lon to bits to hash to bits`() {
        repeat(100000) {
            val lat = Random.latitude()
            val lon = Random.longitude()
            val precision = Random.precision()

            val coordsToLatLonBits = toLatLonBits(lat, lon, precision)
            val geohashString = toGeohashString(coordsToLatLonBits)

            val (coords, bits) = toBoundingBoxAndBits(geohashString)
            val coords2 = toBoundingBox(coordsToLatLonBits)

            assertDoubleEquals(lat, coords.centerLat, approxLatitudeError(precision), "Precision: $precision")
            assertDoubleEquals(lon, coords.centerLon, approxLongitudeError(precision), "Precision: $precision")
            assertDoubleEquals(lat, coords2.centerLat, approxLatitudeError(precision), "Precision: $precision")
            assertDoubleEquals(lon, coords2.centerLon, approxLongitudeError(precision), "Precision: $precision")
            assertEquals(coordsToLatLonBits, bits, "Precision: $precision")
        }
    }

    // TODO Test invalid geohash utils
}
