package com.frybits.geohash.test

import com.frybits.geohash.LATITUDE_MAX
import com.frybits.geohash.LATITUDE_MIN
import com.frybits.geohash.LONGITUDE_MAX
import com.frybits.geohash.LONGITUDE_MIN
import com.frybits.geohash.MAX_CHAR_PRECISION
import com.frybits.geohash.internal.LatLonBits
import com.frybits.geohash.internal.toBoundingBox
import com.frybits.geohash.internal.toBoundingBoxAndBits
import com.frybits.geohash.internal.toGeohashString
import com.frybits.geohash.internal.toLatLonBits
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Frybits
 * Created by Pablo Baxter (Github: pablobaxter)
 */

class GeohashUtilsTest {

    @Test
    fun bits_to_boundingbox() {
        val boundingBox1 = toBoundingBox(LatLonBits(TEST_COMBINED_BITS_1))

        assertDoubleEquals(
            boundingBox1.centerCoordinate.latitude,
            TEST_COORD_1.latitude,
            approxLatitudeError(TEST_CHAR_PRECISION_1)
        )
        assertDoubleEquals(
            boundingBox1.centerCoordinate.longitude,
            TEST_COORD_1.longitude,
            approxLongitudeError(TEST_CHAR_PRECISION_1)
        )

        val boundingBox2 = toBoundingBox(LatLonBits(TEST_COMBINED_BITS_2))

        assertDoubleEquals(
            boundingBox2.centerCoordinate.latitude,
            TEST_COORD_2.latitude,
            approxLatitudeError(TEST_CHAR_PRECISION_2)
        )
        assertDoubleEquals(
            boundingBox2.centerCoordinate.longitude,
            TEST_COORD_2.longitude,
            approxLongitudeError(TEST_CHAR_PRECISION_2)
        )

        val boundingBox3 = toBoundingBox(LatLonBits(TEST_COMBINED_BITS_3))

        assertDoubleEquals(
            boundingBox3.centerCoordinate.latitude,
            TEST_COORD_3.latitude,
            approxLatitudeError(TEST_CHAR_PRECISION_3)
        )
        assertDoubleEquals(
            boundingBox3.centerCoordinate.longitude,
            TEST_COORD_3.longitude,
            approxLongitudeError(TEST_CHAR_PRECISION_3)
        )
    }

    @Test
    fun toBoundingBox_validation_check() {
        assertFailsWith<IllegalArgumentException>("Invalid hash bits! Geohash must be between 1 and $MAX_CHAR_PRECISION characters") {
            toBoundingBox(LatLonBits(15L))
        }
    }

    @Test
    fun bits_to_geohash_string() {
        val geohash1 = toGeohashString(
            toLatLonBits(
                TEST_LAT_1,
                TEST_LON_1,
                TEST_GEOHASH_1.length
            )
        )
        assertEquals(TEST_GEOHASH_1, geohash1)

        val geohash2 = toGeohashString(
            toLatLonBits(
                TEST_LAT_2,
                TEST_LON_2,
                TEST_GEOHASH_2.length
            )
        )
        assertEquals(TEST_GEOHASH_2, geohash2)

        val geohash3 = toGeohashString(
            toLatLonBits(
                TEST_LAT_3,
                TEST_LON_3,
                TEST_GEOHASH_3.length
            )
        )
        assertEquals(TEST_GEOHASH_3, geohash3)
    }

    @Test
    fun toGeohashString_validation_check() {
        assertFailsWith<IllegalArgumentException>("Invalid hash bits! Geohash must be between 1 and $MAX_CHAR_PRECISION characters") {
            toGeohashString(LatLonBits(15L))
        }
    }

    @Test
    fun lat_lon_to_bits() {
        val latLonBits1 = toLatLonBits(TEST_COORD_1.latitude, TEST_COORD_1.longitude, TEST_CHAR_PRECISION_1)
        assertEquals(TEST_LAT_BITS_1, latLonBits1.latBits)
        assertEquals(TEST_LON_BITS_1, latLonBits1.lonBits)
        assertEquals(TEST_COMBINED_BITS_1, latLonBits1.combinedBits)

        val latLonBits2 = toLatLonBits(TEST_COORD_2.latitude, TEST_COORD_2.longitude, TEST_CHAR_PRECISION_2)
        assertEquals(TEST_LAT_BITS_2, latLonBits2.latBits)
        assertEquals(TEST_LON_BITS_2, latLonBits2.lonBits)
        assertEquals(TEST_COMBINED_BITS_2, latLonBits2.combinedBits)

        val latLonBits3 = toLatLonBits(TEST_COORD_3.latitude, TEST_COORD_3.longitude, TEST_CHAR_PRECISION_3)
        assertEquals(TEST_LAT_BITS_3, latLonBits3.latBits)
        assertEquals(TEST_LON_BITS_3, latLonBits3.lonBits)
        assertEquals(TEST_COMBINED_BITS_3, latLonBits3.combinedBits)
    }

    @Test
    fun toLatLonBits_validation() {
        assertFailsWith<IllegalArgumentException>("Latitude must be between $LATITUDE_MIN and $LATITUDE_MAX") {
            toLatLonBits(-91.0, Random.longitude(), Random.precision())
        }

        assertFailsWith<IllegalArgumentException>("Longitude must be between $LONGITUDE_MIN and $LONGITUDE_MAX") {
            toLatLonBits(Random.latitude(), -181.0, Random.precision())
        }

        assertFailsWith<IllegalArgumentException>("Geohash must be between 1 and $MAX_CHAR_PRECISION characters") {
            toLatLonBits(Random.latitude(), Random.longitude(), Int.MAX_VALUE)
        }
    }

    @Test
    fun geohash_to_boundingbox_and_bits() {
        val (bbox1, bits1) = toBoundingBoxAndBits(TEST_BITS_GEOHASH_1)

        assertDoubleEquals(
            TEST_COORD_1.latitude,
            bbox1.centerCoordinate.latitude,
            approxLatitudeError(TEST_CHAR_PRECISION_1)
        )

        assertEquals(TEST_LAT_BITS_1, bits1.latBits)
        assertEquals(TEST_LON_BITS_1, bits1.lonBits)
        assertEquals(TEST_COMBINED_BITS_1, bits1.combinedBits)

        val (bbox2, bits2) = toBoundingBoxAndBits(TEST_BITS_GEOHASH_2)

        assertDoubleEquals(
            TEST_COORD_2.latitude,
            bbox2.centerCoordinate.latitude,
            approxLatitudeError(TEST_CHAR_PRECISION_2)
        )

        assertEquals(TEST_LAT_BITS_2, bits2.latBits)
        assertEquals(TEST_LON_BITS_2, bits2.lonBits)
        assertEquals(TEST_COMBINED_BITS_2, bits2.combinedBits)

        val (bbox3, bits3) = toBoundingBoxAndBits(TEST_BITS_GEOHASH_3)

        assertDoubleEquals(
            TEST_COORD_3.latitude,
            bbox3.centerCoordinate.latitude,
            approxLatitudeError(TEST_CHAR_PRECISION_3)
        )

        assertEquals(TEST_LAT_BITS_3, bits3.latBits)
        assertEquals(TEST_LON_BITS_3, bits3.lonBits)
        assertEquals(TEST_COMBINED_BITS_3, bits3.combinedBits)
    }

    @Test
    fun toBoundingBoxAndBits_validation() {
        assertFailsWith<IllegalArgumentException>("Geohash string invalid") {
            toBoundingBoxAndBits("%^")
        }

        assertFailsWith<IllegalArgumentException>("Geohash must be between 1 and $MAX_CHAR_PRECISION characters") {
            toBoundingBoxAndBits("")
        }
    }

    @Test
    fun valid_random_latLon_to_bits_to_hash_to_bits() {
        repeat(REPEAT_TEST_COUNT) {
            val lat = Random.latitude()
            val lon = Random.longitude()
            val precision = Random.precision()

            val coordsToLatLonBits = toLatLonBits(lat, lon, precision)
            val geohashString = toGeohashString(coordsToLatLonBits)

            val (coords, bits) = toBoundingBoxAndBits(geohashString)
            val coords2 = toBoundingBox(coordsToLatLonBits)

            assertDoubleEquals(
                lat,
                coords.centerCoordinate.latitude,
                approxLatitudeError(precision),
                "Precision: $precision"
            )
            assertDoubleEquals(
                lon,
                coords.centerCoordinate.longitude,
                approxLongitudeError(precision),
                "Precision: $precision"
            )
            assertDoubleEquals(
                lat,
                coords2.centerCoordinate.latitude,
                approxLatitudeError(precision),
                "Precision: $precision"
            )
            assertDoubleEquals(
                lon,
                coords2.centerCoordinate.longitude,
                approxLongitudeError(precision),
                "Precision: $precision"
            )
            assertEquals(coordsToLatLonBits, bits, "Precision: $precision")
        }
    }
}
