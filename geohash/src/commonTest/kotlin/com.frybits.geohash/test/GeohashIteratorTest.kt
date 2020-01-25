package com.frybits.geohash.test

import com.frybits.geohash.GEOHASH_CHARS
import com.frybits.geohash.Geohash
import com.frybits.geohash.internal.GeohashIterator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Frybits
 * Created by Pablo Baxter (Github: pablobaxter)
 */

class GeohashIteratorTest {

    @Test
    fun iterator_a_simple_hash_range() {
        val iterator = GeohashIterator(Geohash(GEOHASH_CHARS.first().toString()), Geohash(GEOHASH_CHARS.last().toString()))
        assertTrue { iterator.hasNext() }
        var count = 0
        while (iterator.hasNext()) {
            assertEquals(GEOHASH_CHARS[count++].toString(), iterator.next().geohash)
        }
        assertFalse { iterator.hasNext() }
        assertEquals(GEOHASH_CHARS.last().toString(), iterator.next().geohash)
    }

    @Test
    fun reversed_hash_iteration() {
        val iterator = GeohashIterator(Geohash(GEOHASH_CHARS.last().toString()), Geohash(GEOHASH_CHARS.first().toString()))
        assertTrue { iterator.hasNext() }
        var count = GEOHASH_CHARS.length - 1
        while (iterator.hasNext()) {
            assertEquals(GEOHASH_CHARS[count--].toString(), iterator.next().geohash)
        }
        assertFalse { iterator.hasNext() }
        assertEquals(GEOHASH_CHARS.first().toString(), iterator.next().geohash)
    }

    @Test
    fun iterating_the_same_hash() {
        val hash = Geohash(GEOHASH_CHARS.first().toString())
        val iterator = GeohashIterator(hash, hash)
        assertFalse { iterator.hasNext() }
        repeat(2) {
            assertEquals(hash, iterator.next())
        }
        assertFalse { iterator.hasNext() }
    }

    @Test
    fun iterating_hash_of_different_length_throws_exception() {
        assertFailsWith<IllegalArgumentException>("Geohashes must be of the same precision to iterate") {
            GeohashIterator(Geohash("00"), Geohash("z"))
        }
    }
}
