package com.frybits.geohash.internal

import com.frybits.geohash.Geohash
import com.frybits.geohash.dec
import com.frybits.geohash.inc

/**
 * Frybits
 * Created by Pablo Baxter (Github: pablobaxter)
 */

internal class GeohashIterator(
    private var curr: Geohash,
    private val endInclusive: Geohash
) : Iterator<Geohash> {

    init {
        require(curr.charPrecision == endInclusive.charPrecision) { "Geohashes must be of the same precision to iterate" }
    }

    private val reversed = endInclusive < curr

    // If the curr is >= the end hash, we've reached last
    private var reachedLast = if (reversed) curr <= endInclusive else curr >= endInclusive

    // hasNext as long as we have not reached the last hash
    override fun hasNext(): Boolean = !reachedLast

    // This will always return the last curr hash if hasNext() is false, otherwise the hash would roll over and reiterate
    override fun next(): Geohash {
        val h = curr
        if (!reachedLast) {
            if (reversed) curr-- else curr++ // Hold the current hash, and step the pointer
            // Sets the flag if it has reached endInclusive
            reachedLast = curr == endInclusive
        }
        return h
    }
}
