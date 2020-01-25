package com.frybits.geohash.test

import com.frybits.geohash.Coordinate
import com.frybits.geohash.LATITUDE_MAX
import com.frybits.geohash.LATITUDE_MIN
import com.frybits.geohash.LONGITUDE_MAX
import com.frybits.geohash.LONGITUDE_MIN
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertFailsWith

/**
 * Frybits
 * Created by Pablo Baxter (Github: pablobaxter)
 */

class CoordinateTest {

    @Test
    fun validation_checks() {
        assertFailsWith<IllegalArgumentException>("Latitude must be between $LATITUDE_MIN and $LATITUDE_MAX") {
            val lat = LATITUDE_MIN - 1
            val lon = Random.longitude()

            Coordinate(lat, lon)
        }

        assertFailsWith<IllegalArgumentException>("Longitude must be between $LONGITUDE_MIN and $LONGITUDE_MAX") {
            val lat = Random.latitude()
            val lon = LONGITUDE_MAX + 1

            Coordinate(lat, lon)
        }
    }
}
