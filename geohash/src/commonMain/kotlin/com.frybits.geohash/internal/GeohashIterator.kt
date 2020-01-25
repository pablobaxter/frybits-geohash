package com.frybits.geohash.internal

import com.frybits.geohash.Geohash
import com.frybits.geohash.inc

/**
 * Frybits
 * Created by Pablo Baxter (Github: pablobaxter)
 */

internal class GeohashIterator(private var curr: Geohash, private val endInclusive: Geohash) : Iterator<Geohash> {

    private var reachedLast = false

    override fun hasNext(): Boolean = !reachedLast

    override fun next(): Geohash {
        reachedLast = curr == endInclusive
        return curr++
    }
}
