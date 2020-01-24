package com.frybits.geohash.test

import com.frybits.geohash.BoundingBox
import com.frybits.geohash.internal.LATITUDE_MAX
import com.frybits.geohash.internal.LATITUDE_MIN
import com.frybits.geohash.internal.LONGITUDE_MAX
import com.frybits.geohash.internal.LONGITUDE_MIN
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
    fun latitude_range_crossing_over_poles() {
        assertFailsWith<IllegalArgumentException>("The southern most latitude must be greater than the northern most latitude") {
            BoundingBox(50.0, 50.0, 30.0, 51.0)
        }
    }

    @Test
    fun lat_bounds_check_min_latitude() {
        assertFailsWith<IllegalArgumentException>("Latitude must be between $LATITUDE_MIN and $LATITUDE_MAX") {
            BoundingBox(-100.0, Random.longitude(), Random.latitude(), Random.longitude())
        }
    }

    @Test
    fun lat_bounds_check_max_latitude() {
        assertFailsWith<IllegalArgumentException>("Latitude must be between $LATITUDE_MIN and $LATITUDE_MAX") {
            BoundingBox(Random.latitude(), Random.longitude(), 180.0, Random.longitude())
        }
    }

    @Test
    fun lon_bounds_check_min_longitude() {
        assertFailsWith<IllegalArgumentException>("Longitude must be between $LONGITUDE_MIN and $LONGITUDE_MAX") {
            BoundingBox(50.0, -181.0, 51.0, Random.longitude())
        }
    }

    @Test
    fun lon_bounds_check_max_longitude() {
        assertFailsWith<IllegalArgumentException>("Longitude must be between $LONGITUDE_MIN and $LONGITUDE_MAX") {
            BoundingBox(50.0, Random.longitude(), 51.0, 181.0)
        }
    }

    @Test
    fun check_random_valid_latLon_within_bounding_box() {
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
    fun box_always_intersects_self() {
        val bbox = BoundingBox(40.0, 100.0, 45.0, 125.0)

        // A box always intersects itself
        assertTrue { bbox.intersects(bbox) }
    }

    @Test
    fun check_intersecting_box_not_crossing_180_meridian() {
        val bbox = BoundingBox(40.0, 100.0, 45.0, 125.0)
        val testBbox = BoundingBox(35.0, 95.0, 40.0, 115.0)

        assertFalse { bbox.intersects180Meridian }
        assertFalse { testBbox.intersects180Meridian }
        assertTrue { bbox.intersects(testBbox) }
        assertTrue { testBbox.intersects(bbox) }
    }

    @Test
    fun check_boxes_not_intersecting_via_latitude() {
        val bbox = BoundingBox(40.0, 100.0, 45.0, 125.0)
        val testBbox = BoundingBox(50.0, 100.0, 55.0, 125.0)

        assertFalse { bbox.intersects180Meridian }
        assertFalse { testBbox.intersects180Meridian }
        assertFalse { bbox.intersects(testBbox) }
        assertFalse { testBbox.intersects(bbox) }
    }

    @Test
    fun check_boxes_not_intersecting_via_longitude() {
        val bbox = BoundingBox(40.0, 100.0, 45.0, 125.0)
        val testBbox = BoundingBox(35.0, 10.0, 55.0, 99.0)

        assertFalse { bbox.intersects180Meridian }
        assertFalse { testBbox.intersects180Meridian }
        assertFalse { bbox.intersects(testBbox) }
        assertFalse { testBbox.intersects(bbox) }
    }

    @Test
    fun check_boxes_intersect_with_one_crossing_180_meridian() {
        val bbox = BoundingBox(40.0, 100.0, 45.0, 125.0)
        val testBbox = BoundingBox(35.0, 115.0, 40.0, -180.0)

        assertFalse { bbox.intersects180Meridian }
        assertTrue { testBbox.intersects180Meridian }
        assertTrue { bbox.intersects(testBbox) }
        assertTrue { testBbox.intersects(bbox) }
    }

    @Test
    fun check_boxes_intersect_with_both_crossing_180_meridian() {
        val bbox = BoundingBox(40.0, 170.0, 45.0, -170.0)
        val testBbox = BoundingBox(35.0, 165.0, 40.0, -165.0)

        assertTrue { bbox.intersects180Meridian }
        assertTrue { testBbox.intersects180Meridian }
        assertTrue { bbox.intersects(testBbox) }
        assertTrue { testBbox.intersects(bbox) }
    }

    @Test
    fun check_boxes_that_are_tangent_aka_BOOP() {
        val bbox = BoundingBox(40.0, 170.0, 45.0, -170.0)
        val testBbox = BoundingBox(45.0, -170.0, 50.0, -165.0)

        assertTrue { bbox.intersects180Meridian }
        assertFalse { testBbox.intersects180Meridian }
        assertTrue { bbox.intersects(testBbox) }
        assertTrue { testBbox.intersects(bbox) }
    }
}
