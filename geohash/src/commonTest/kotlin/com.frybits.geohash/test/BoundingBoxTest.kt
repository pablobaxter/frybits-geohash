package com.frybits.geohash.test

import com.frybits.geohash.BoundingBox
import com.frybits.geohash.LATITUDE_MAX
import com.frybits.geohash.LATITUDE_MIN
import com.frybits.geohash.LONGITUDE_MAX
import com.frybits.geohash.LONGITUDE_MIN
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Frybits
 * Created by Pablo Baxter (Github: pablobaxter)
 */

class BoundingBoxTest {

    @Test
    fun `Latitude range crossing over poles`() {
        assertFailsWith<IllegalArgumentException>("The southern most latitude must be greater than the northern most latitude") {
            BoundingBox(50.0, 50.0, 30.0, 51.0)
        }
    }

    @Test
    fun `Lat bounds check - min latitude`() {
        assertFailsWith<IllegalArgumentException>("Latitude must be between $LATITUDE_MIN and $LATITUDE_MAX") {
            BoundingBox(-100.0, Random.longitude(), Random.latitude(), Random.longitude())
        }
    }

    @Test
    fun `Lat bounds check - max latitude`() {
        assertFailsWith<IllegalArgumentException>("Latitude must be between $LATITUDE_MIN and $LATITUDE_MAX") {
            BoundingBox(Random.latitude(), Random.longitude(), 180.0, Random.longitude())
        }
    }

    @Test
    fun `Lon bounds check - min longitude`() {
        assertFailsWith<IllegalArgumentException>("Longitude must be between $LONGITUDE_MIN and $LONGITUDE_MAX") {
            BoundingBox(50.0, -181.0, 51.0, Random.longitude())
        }
    }

    @Test
    fun `Lon bounds check - max longitude`() {
        assertFailsWith<IllegalArgumentException>("Longitude must be between $LONGITUDE_MIN and $LONGITUDE_MAX") {
            BoundingBox(50.0, Random.longitude(), 51.0, 181.0)
        }
    }

    @Test
    fun `Check random valid lat-lon within bounding box`() {
        val minLat = Random.nextDouble(-90.0, 0.0)
        val maxLat = Random.nextDouble(0.1, 90.0)
        val minLon = Random.nextDouble(-180.0, 0.0)
        val maxLon = Random.nextDouble(0.1, 180.0)

        val bbox = BoundingBox(minLat, minLon, maxLat, maxLon)

        repeat(REPEAT_TEST_COUNT) {
            val randomValidLat = Random.nextDouble(minLat, maxLat)
            val randomValidLon = Random.nextDouble(minLon, maxLon)

            assertTrue { bbox.contains(randomValidLat, randomValidLon) }
            assertFalse { bbox.contains(minLat - 1, minLon - 1) }
            assertFalse { bbox.contains(maxLat + 1, maxLon + 1) }
        }
    }

    @Test
    fun `Box always intersects self`() {
        val bbox = BoundingBox(40.0, 100.0, 45.0, 125.0)

        // A box always intersects itself
        assertTrue { bbox.intersects(bbox) }
    }

    @Test
    fun `Check intersecting box not crossing 180 meridian`() {
        val bbox = BoundingBox(40.0, 100.0, 45.0, 125.0)
        val testBbox = BoundingBox(35.0, 95.0, 40.0, 115.0)

        assertFalse { bbox.intersects180Meridian }
        assertFalse { testBbox.intersects180Meridian }
        assertTrue { bbox.intersects(testBbox) }
        assertTrue { testBbox.intersects(bbox) }
    }

    @Test
    fun `Check boxes not intersecting via latitude`() {
        val bbox = BoundingBox(40.0, 100.0, 45.0, 125.0)
        val testBbox = BoundingBox(50.0, 100.0, 55.0, 125.0)

        assertFalse { bbox.intersects180Meridian }
        assertFalse { testBbox.intersects180Meridian }
        assertFalse { bbox.intersects(testBbox) }
        assertFalse { testBbox.intersects(bbox) }
    }

    @Test
    fun `Check boxes not intersecting via longitude`() {
        val bbox = BoundingBox(40.0, 100.0, 45.0, 125.0)
        val testBbox = BoundingBox(35.0, 10.0, 55.0, 99.0)

        assertFalse { bbox.intersects180Meridian }
        assertFalse { testBbox.intersects180Meridian }
        assertFalse { bbox.intersects(testBbox) }
        assertFalse { testBbox.intersects(bbox) }
    }

    @Test
    fun `Check boxes intersect, with one crossing 180 meridian`() {
        val bbox = BoundingBox(40.0, 100.0, 45.0, 125.0)
        val testBbox = BoundingBox(35.0, 115.0, 40.0, -180.0)

        assertFalse { bbox.intersects180Meridian }
        assertTrue { testBbox.intersects180Meridian }
        assertTrue { bbox.intersects(testBbox) }
        assertTrue { testBbox.intersects(bbox) }
    }

    @Test
    fun `Check boxes intersect, with both crossing 180 meridian`() {
        val bbox = BoundingBox(40.0, 170.0, 45.0, -170.0)
        val testBbox = BoundingBox(35.0, 165.0, 40.0, -165.0)

        assertTrue { bbox.intersects180Meridian }
        assertTrue { testBbox.intersects180Meridian }
        assertTrue { bbox.intersects(testBbox) }
        assertTrue { testBbox.intersects(bbox) }
    }

    @Test
    fun `Check boxes that are tangent (aka "boop")`() {
        val bbox = BoundingBox(40.0, 170.0, 45.0, -170.0)
        val testBbox = BoundingBox(45.0, -170.0, 50.0, -165.0)

        assertTrue { bbox.intersects180Meridian }
        assertFalse { testBbox.intersects180Meridian }
        assertTrue { bbox.intersects(testBbox) }
        assertTrue { testBbox.intersects(bbox) }
    }
}
