package com.frybits.geohash.internal

/**
 * Frybits
 * Created by Pablo Baxter (Github: pablobaxter)
 */

// Internal class to hold lat/lon bits without boxing them
internal class LatLonBits {

    val latBits: Long // This is the odd bits of the geohash
    val lonBits: Long // This is the even bits of the geohash
    val combinedBits: Long // Combined lat/lon bits, interleaved from left to right with lon bit first

    constructor(latBits: Long, lonBits: Long, charPrecision: Int) {
        this.latBits = latBits
        this.lonBits = lonBits
        this.combinedBits =
            recombineBits(latBits, lonBits, charPrecision)
    }

    constructor(combinedBits: Long) {
        this.combinedBits = combinedBits
        val array = evenOddBitsRightAligned(combinedBits)
        this.latBits = array[0]
        this.lonBits = array[1]
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LatLonBits) return false

        return latBits == other.latBits && lonBits == other.lonBits && combinedBits == other.combinedBits
    }

    override fun hashCode(): Int {
        var result = latBits.hashCode()
        result = 31 * result + lonBits.hashCode()
        result = 31 * result + combinedBits.hashCode()
        return result
    }

    override fun toString(): String {
        return "LatLonBits(latBits=$latBits, lonBits=$lonBits, combinedBits=$combinedBits)"
    }
}
