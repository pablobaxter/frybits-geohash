@file:JvmName("InternalRangeUtils")

package com.frybits.geohash.internal

import com.frybits.geohash.Geohash
import com.frybits.geohash.GeohashRange
import kotlin.jvm.JvmName
import kotlin.jvm.JvmSynthetic

/**
 * Frybits
 * Created by Pablo Baxter (Github: pablobaxter)
 */

@JvmSynthetic
internal fun geohashRange(start: Geohash, endInclusive: Geohash): GeohashRange = GeohashRangeImpl(start, endInclusive)

// Helper to make iterating over a range of geohashes easier
private class GeohashRangeImpl(override val start: Geohash, override val endInclusive: Geohash) : GeohashRange {

    init {
        require(start.charPrecision == endInclusive.charPrecision) { "Geohashes must be of the same precision to get a range" }
    }

    override fun iterator(): Iterator<Geohash> = geoHashIterator(start, endInclusive)
}
