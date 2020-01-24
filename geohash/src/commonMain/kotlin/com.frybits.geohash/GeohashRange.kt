package com.frybits.geohash

/**
 * Frybits
 * Created by Pablo Baxter (Github: pablobaxter)
 */

class GeohashRange(override val start: Geohash, override val endInclusive: Geohash) : Iterable<Geohash>, ClosedRange<Geohash> {

    init {
        require(start.charPrecision == endInclusive.charPrecision) { "Geohashes must be of the same precision to get a range" }
    }

    override fun iterator(): Iterator<Geohash> = GeohashIterator(start, endInclusive)
}

private class GeohashIterator(private var curr: Geohash, private val endInclusive: Geohash) : Iterator<Geohash> {

    private var reachedLast = false

    override fun hasNext(): Boolean = !reachedLast

    override fun next(): Geohash {
        reachedLast = curr == endInclusive
        return curr++
    }
}
