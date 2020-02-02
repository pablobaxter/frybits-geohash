package com.frybits.geohash.test

import com.frybits.geohash.BoundingBox
import com.frybits.geohash.BoxQuery
import com.frybits.geohash.Geohash
import kotlin.test.Test

/**
 * Frybits
 * Created by Pablo Baxter (Github: pablobaxter)
 */

class GeohashQueryTest {

    @Test
    fun blah() {
        val hash = Geohash("x8zp042p8j81")
        val hash2 = Geohash("8j8jb42h210p")

        val boundingBox = BoundingBox(
            hash.coordinate.latitude,
            hash.coordinate.longitude,
            hash2.coordinate.latitude,
            hash2.coordinate.longitude
        )
        val query = BoxQuery(boundingBox).geohashes()
        println(query.size)
        query.forEach {
            println(it.geohash)
        }
    }
}
