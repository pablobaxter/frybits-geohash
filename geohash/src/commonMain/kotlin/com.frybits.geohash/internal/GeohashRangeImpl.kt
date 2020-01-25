package com.frybits.geohash.internal

import com.frybits.geohash.Geohash
import com.frybits.geohash.GeohashRange

/**
 * Frybits
 * Created by Pablo Baxter (Github: pablobaxter)
 */

// Helper to make iterating over a range of geohashes easier
internal class GeohashRangeImpl(override val start: Geohash, override val endInclusive: Geohash) : GeohashRange {

    init {
        require(start.charPrecision == endInclusive.charPrecision) { "Geohashes must be of the same precision to get a range" }
    }

    override fun iterator(): Iterator<Geohash> = GeohashIterator(start, endInclusive)
}
