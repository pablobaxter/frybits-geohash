package com.frybits.geohash

import com.frybits.geohash.internal.GeohashIterator

/**
 * Frybits
 * Created by Pablo Baxter (Github: pablobaxter)
 */

class GeohashRange(override val start: Geohash, override val endInclusive: Geohash) : Iterable<Geohash>,
    ClosedRange<Geohash> {

    init {
        require(start.charPrecision == endInclusive.charPrecision) { "Geohashes must be of the same precision to get a range" }
    }

    override fun iterator(): Iterator<Geohash> = GeohashIterator(start, endInclusive)
}
