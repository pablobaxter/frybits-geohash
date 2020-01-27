package com.frybits.geohash.test

import com.frybits.geohash.Direction
import com.frybits.geohash.GEOHASH_CHARS
import com.frybits.geohash.Geohash
import com.frybits.geohash.MAX_CHAR_PRECISION
import com.frybits.geohash.LATITUDE_MAX
import com.frybits.geohash.LATITUDE_MIN
import com.frybits.geohash.LONGITUDE_MAX
import com.frybits.geohash.LONGITUDE_MIN
import com.frybits.geohash.children
import com.frybits.geohash.dec
import com.frybits.geohash.inc
import com.frybits.geohash.minus
import com.frybits.geohash.neighborAt
import com.frybits.geohash.parent
import com.frybits.geohash.plus
import com.frybits.geohash.stepsTo
import com.frybits.geohash.surroundingGeohashes
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Frybits
 * Created by Pablo Baxter (Github: pablobaxter)
 */

class GeohashTest {

    // region Constructor and basic fields tests

    @Test
    fun lat_lon_precision_validation() {
        assertFailsWith<IllegalArgumentException>("Latitude must be between $LATITUDE_MIN and $LATITUDE_MAX") {
            val lat = LATITUDE_MIN - 1
            val lon = Random.longitude()
            val precision = Random.precision()

            Geohash(lat, lon, precision)
        }

        assertFailsWith<IllegalArgumentException>("Longitude must be between $LONGITUDE_MIN and $LONGITUDE_MAX") {
            val lat = Random.latitude()
            val lon = LONGITUDE_MAX + 1
            val precision = Random.precision()

            Geohash(lat, lon, precision)
        }

        assertFailsWith<IllegalArgumentException>("Geohash must be between 1 and $MAX_CHAR_PRECISION characters in precision") {
            val lat = Random.latitude()
            val lon = Random.longitude()
            val precision = 0

            Geohash(lat, lon, precision)
        }
    }

    @Test
    fun geohash_validation() {
        assertFailsWith<IllegalArgumentException>("Geohash string invalid") {
            Geohash("abcdefghijk")
        }

        assertFailsWith<IllegalArgumentException>("Geohash string invalid") {
            Geohash("%@&#(")
        }

        assertFailsWith<IllegalArgumentException>("Geohash must be between 1 and $MAX_CHAR_PRECISION characters") {
            Geohash("hsjkfnqwsxcvbpkjmnbvcxzqwsdfvcx")
        }
    }

    @Test
    fun valid_geohash_uppercase_characters() {
        repeat(REPEAT_TEST_COUNT) {
            Random.geoHash(randomUppercase = true).also { g ->
                assertTrue("Geohash ${g.geohash} failed") {
                    g.geohash.all {
                        GEOHASH_CHARS.contains(
                            it,
                            ignoreCase = false
                        )
                    }
                }
            }
        }
    }

    @Test
    fun hash_length_matches_precision() {
        repeat(REPEAT_TEST_COUNT) {
            val charPrecision = Random.precision()
            val geohash = Geohash(Random.latitude(), Random.longitude(), charPrecision)

            assertEquals(charPrecision, geohash.charPrecision)
            assertEquals(charPrecision, geohash.geohash.length)
        }
    }

    @Test
    fun latLons_produce_expected_geohashes() {
        repeat(REPEAT_TEST_COUNT) {
            val randomPrecision1 = Random.precision(maxCharPrecision = TEST_GEOHASH_1.length)
            val geohash1 = Geohash(TEST_LAT_1, TEST_LON_1, randomPrecision1)
            assertEquals(TEST_GEOHASH_1.substring(0, randomPrecision1), geohash1.geohash)

            val randomPrecision2 = Random.precision(maxCharPrecision = TEST_GEOHASH_2.length)
            val geohash2 = Geohash(TEST_LAT_2, TEST_LON_2, randomPrecision2)
            assertEquals(TEST_GEOHASH_2.substring(0, randomPrecision2), geohash2.geohash)

            val randomPrecision3 = Random.precision(maxCharPrecision = TEST_GEOHASH_3.length)
            val geohash3 = Geohash(TEST_LAT_3, TEST_LON_3, randomPrecision3)
            assertEquals(TEST_GEOHASH_3.substring(0, randomPrecision3), geohash3.geohash)
        }
    }

    @Test
    fun geohashes_produce_expected_latLons() {
        repeat(REPEAT_TEST_COUNT) {
            val randomPrecision1 = Random.precision(maxCharPrecision = TEST_GEOHASH_1.length)
            val geohash1 = Geohash(TEST_GEOHASH_1.substring(0, randomPrecision1))
            assertDoubleEquals(TEST_LAT_1, geohash1.coordinate.latitude, approxLatitudeError(randomPrecision1))
            assertDoubleEquals(TEST_LON_1, geohash1.coordinate.longitude, approxLongitudeError(randomPrecision1))

            val randomPrecision2 = Random.precision(maxCharPrecision = TEST_GEOHASH_2.length)
            val geohash2 = Geohash(TEST_GEOHASH_2.substring(0, randomPrecision2))
            assertDoubleEquals(TEST_LAT_2, geohash2.coordinate.latitude, approxLatitudeError(randomPrecision2))
            assertDoubleEquals(TEST_LON_2, geohash2.coordinate.longitude, approxLongitudeError(randomPrecision2))

            val randomPrecision3 = Random.precision(maxCharPrecision = TEST_GEOHASH_3.length)
            val geohash3 = Geohash(TEST_GEOHASH_3.substring(0, randomPrecision3))
            assertDoubleEquals(TEST_LAT_3, geohash3.coordinate.latitude, approxLatitudeError(randomPrecision3))
            assertDoubleEquals(TEST_LON_3, geohash3.coordinate.longitude, approxLongitudeError(randomPrecision3))
        }
    }

    @Test
    fun geohashes_cycled_between_hash_and_coords_remain_the_same() {
        var lat = 0.0
        var lon = 0.0
        var hash = TEST_GEOHASH_1
        repeat(REPEAT_TEST_COUNT) {
            val geohash = Geohash(hash)
            lat = geohash.coordinate.latitude
            lon = geohash.coordinate.longitude
            hash = Geohash(lat, lon, geohash.charPrecision).geohash
        }

        assertDoubleEquals(TEST_LAT_1, lat, approxLatitudeError(TEST_GEOHASH_1.length))
        assertDoubleEquals(TEST_LON_1, lon, approxLatitudeError(TEST_GEOHASH_1.length))
        assertEquals(TEST_GEOHASH_1, hash)

        hash = TEST_GEOHASH_2
        repeat(REPEAT_TEST_COUNT) {
            val geohash = Geohash(hash)
            lat = geohash.coordinate.latitude
            lon = geohash.coordinate.longitude
            hash = Geohash(lat, lon, geohash.charPrecision).geohash
        }

        assertDoubleEquals(TEST_LAT_2, lat, approxLatitudeError(TEST_GEOHASH_2.length))
        assertDoubleEquals(TEST_LON_2, lon, approxLatitudeError(TEST_GEOHASH_2.length))
        assertEquals(TEST_GEOHASH_2, hash)

        hash = TEST_GEOHASH_3
        repeat(REPEAT_TEST_COUNT) {
            val geohash = Geohash(hash)
            lat = geohash.coordinate.latitude
            lon = geohash.coordinate.longitude
            hash = Geohash(lat, lon, geohash.charPrecision).geohash
        }

        assertDoubleEquals(TEST_LAT_3, lat, approxLatitudeError(TEST_GEOHASH_3.length))
        assertDoubleEquals(TEST_LON_3, lon, approxLatitudeError(TEST_GEOHASH_3.length))
        assertEquals(TEST_GEOHASH_3, hash)
    }

    // endregion

    // region Neighbors tests

    @Test
    fun expected_surrounding_neighbors_are_produced() {
        val centerHash = "9u5tc2rep"
        val surroundingHashes = listOf(
            "9u5tc2rer",
            "9u5tc2rg2",
            "9u5tc2rg0",
            "9u5tc2rfb",
            "9u5tc2rdz",
            "9u5tc2rdy",
            "9u5tc2ren",
            "9u5tc2req",
            centerHash
        )
        Geohash(centerHash).surroundingGeohashes().forEachIndexed { i, h ->
            assertEquals(surroundingHashes[i], h.geohash)
        }
    }

    @Test
    fun neighbors_start_at_North_and_iterate_clockwise() {
        val centerHash = Geohash("9u5tC2rep")
        centerHash.surroundingGeohashes(includeSelf = false).forEachIndexed { i, h ->
            when (i) {
                0 -> assertEquals(centerHash.neighborAt(Direction.NORTH), h)
                1 -> assertEquals(centerHash.neighborAt(Direction.NORTH_EAST), h)
                2 -> assertEquals(centerHash.neighborAt(Direction.EAST), h)
                3 -> assertEquals(centerHash.neighborAt(Direction.SOUTH_EAST), h)
                4 -> assertEquals(centerHash.neighborAt(Direction.SOUTH), h)
                5 -> assertEquals(centerHash.neighborAt(Direction.SOUTH_WEST), h)
                6 -> assertEquals(centerHash.neighborAt(Direction.WEST), h)
                7 -> assertEquals(centerHash.neighborAt(Direction.NORTH_WEST), h)
                else -> fail("Unexpected hash included: $h")
            }
        }
    }

    @Test
    fun test_surrounding_geohashes_at_180_meridian() {
        val centerHash = "8h0jb0"
        val surroundingHashes = listOf(
            "8h0jb1",
            "8h0jb3",
            "8h0jb2",
            "8h0j8r",
            "8h0j8p",
            "xupvxz",
            "xupvzb",
            "xupvzc",
            centerHash
        )
        Geohash(centerHash).surroundingGeohashes().forEachIndexed { i, h ->
            assertEquals(surroundingHashes[i], h.geohash)
        }
    }

    @Test
    fun neighbors_edge_case() {
        val centerHash = "zzzzzzzzzzz"
        val surroundingHashes = listOf(
            "pbpbpbpbpbp",
            "00000000000",
            "bpbpbpbpbpb",
            "bpbpbpbpbp8",
            "zzzzzzzzzzx",
            "zzzzzzzzzzw",
            "zzzzzzzzzzy",
            "pbpbpbpbpbn",
            centerHash
        )
        Geohash(centerHash).surroundingGeohashes().forEachIndexed { i, h ->
            assertEquals(surroundingHashes[i], h.geohash)
        }
    }

    // endregion

    @Test
    fun incrementing_geohashes() {
        var geohash = Geohash("0")
        repeat(32) {
            // Let's iterate through all hashes
            assertEquals(GEOHASH_CHARS[it].toString(), geohash++.geohash)
        }
    }

    @Test
    fun decrementing_geohashes() {
        var geohash = Geohash("Z")
        repeat(32) {
            // Let's iterate through all hashes
            assertEquals(GEOHASH_CHARS[GEOHASH_CHARS.length - it - 1].toString(), geohash--.geohash)
        }
    }

    @Test
    fun incrementing_geohashes_by_random_number() {
        val geohash = Geohash("0")
        repeat(REPEAT_TEST_COUNT) {
            val random = Random.nextInt(0, 32)
            assertEquals(GEOHASH_CHARS[random].toString(), (geohash + random).geohash)
        }
    }

    @Test
    fun decrementing_geohashes_by_random_number() {
        val geohash = Geohash("Z")
        repeat(REPEAT_TEST_COUNT) {
            val random = Random.nextInt(0, 32)
            assertEquals(GEOHASH_CHARS[GEOHASH_CHARS.length - random - 1].toString(), (geohash - random).geohash)
        }
    }

    @Test
    fun steps_between_geohashes() {
        val geohash = Geohash("0")
        repeat(REPEAT_TEST_COUNT) {
            val random = Random.nextLong(0, 32)
            val testHash = geohash + random
            assertEquals(random, geohash stepsTo testHash)
        }
    }

    @Test
    fun geohash_contains_string_checks() {
        repeat(REPEAT_TEST_COUNT) {
            val testHash = Random.geoHash(Random.precision(2, 10))
            var hash = testHash.geohash
            while (hash.length <= MAX_CHAR_PRECISION) {
                assertTrue { testHash.contains(hash) }
                hash += GEOHASH_CHARS.random()
            }
        }
    }

    @Test
    fun geohash_contains_geohash_test() {
        repeat(REPEAT_TEST_COUNT) {
            val testHash = Random.geoHash(Random.precision(2, 10))
            var hash = testHash.geohash
            while (hash.length <= MAX_CHAR_PRECISION) {
                assertTrue { testHash.contains(Geohash(hash)) }
                hash += GEOHASH_CHARS.random()
            }
        }
    }

    @Test
    fun geohash_not_contains_string_checks() {
        repeat(REPEAT_TEST_COUNT) {
            var testHash = Random.geoHash(Random.precision(2, 10))
            var hash = testHash++.geohash
            while (hash.length <= MAX_CHAR_PRECISION) {
                assertFalse { testHash.contains(hash) }
                hash += GEOHASH_CHARS.random()
            }
        }
    }

    @Test
    fun geohash_not_contains_geohash_test() {
        repeat(REPEAT_TEST_COUNT) {
            var testHash = Random.geoHash(Random.precision(2, 10))
            var hash = testHash++.geohash
            while (hash.length <= MAX_CHAR_PRECISION) {
                assertFalse { testHash.contains(Geohash(hash)) }
                hash += GEOHASH_CHARS.random()
            }
        }
    }

    @Test
    fun geohash_comparison_same_precision() {
        repeat(REPEAT_TEST_COUNT) {
            val randomGeohash = Random.geoHash()

            assertTrue {
                if (randomGeohash.isLast()) { // We got the very last geohash for this precision.
                    return@assertTrue randomGeohash - 1 < randomGeohash // Compare to the hash less than the random one
                } else {
                    return@assertTrue randomGeohash + 1 > randomGeohash // Compare to the hash greater than the random one
                }
            }
        }
    }

    @Test
    fun geohash_comparison_different_precision() {
        repeat(REPEAT_TEST_COUNT) {
            val randomGeohash1 = Random.geoHash(Random.precision(maxCharPrecision = 9))
            val randomGeohash2 = Geohash(randomGeohash1.geohash + GEOHASH_CHARS.random())

            assertTrue { randomGeohash1 <= randomGeohash2 }
            assertTrue { randomGeohash2 >= randomGeohash1 }
        }
    }

    @Test
    fun geohash_comparison_precise_hash_greater_than_imprecise_hash() {
        val geohashZ = Geohash("zzzZzz")
        val geohash00 = Geohash("00")

        assertTrue { geohashZ > geohash00 }
        assertTrue { geohash00 < geohashZ }
    }

    @Test
    fun geohash_comparison_edge_case_with_0_geohash() {
        val geohash1 = Geohash("0")
        val geohash2 = Geohash("0000000")

        assertNotEquals(geohash1, geohash2) // Geohashes are not equal
        assertTrue { geohash1 < geohash2 } // Geohash1 is less precise, therefore less than
        // Geohash2 is more precise, therefore greater than
        assertTrue { geohash2 > geohash1 }
    }

    @Test
    fun geohash_parent_check() {
        repeat(REPEAT_TEST_COUNT) {
            var testHash: Geohash? = Random.geoHash(MAX_CHAR_PRECISION)
            while (testHash != null) {
                assertEquals(testHash.geohash.dropLast(1).ifEmpty { null }, testHash.parent?.geohash)
                testHash = testHash.parent
            }
        }
    }

    @Test
    fun geohash_children_check() {
        repeat(REPEAT_TEST_COUNT) {
            val testHash = Random.geoHash(Random.precision(11))
            testHash.children().forEachIndexed { i, g ->
                assertEquals(testHash.geohash + GEOHASH_CHARS[i], g.geohash, "Test hash: ${testHash.geohash}")
            }
        }
    }

    @Test
    fun geohash_children_validation() {
        repeat(REPEAT_TEST_COUNT) {
            val testHash = Random.geoHash(MAX_CHAR_PRECISION)
            assertTrue { testHash.children().isEmpty() }
        }
    }
}
