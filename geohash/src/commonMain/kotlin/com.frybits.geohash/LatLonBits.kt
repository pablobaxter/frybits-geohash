package com.frybits.geohash

/**
 * Frybits
 * Created by Pablo Baxter (Github: pablobaxter)
 */

// Internal class to hold lat/lon bits without boxing them
internal class LatLonBits {

    val latBits: Long
    val lonBits: Long
    val combinedBits: Long

    constructor(latBits: Long, lonBits: Long, charPrecision: Int) {
        this.latBits = latBits
        this.lonBits = lonBits
        this.combinedBits = recombineBits(latBits, lonBits, charPrecision)
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

// Helper function to recombine the bits
private fun recombineBits(latBits: Long, lonBits: Long, charPrecision: Int): Long {
    require(charPrecision in 1..MAX_CHAR_PRECISION) { "Invalid hash bits! Geohash must be between 1 and $MAX_CHAR_PRECISION characters" }
    val significantBits = charPrecision * BITS_PER_CHAR
    var bits = 0L
    var isEven = true
    var count = 0
    repeat(significantBits) {
        val rightShift = (significantBits - ++count) / 2
        val maskedBit = if (isEven) {
            (lonBits ushr rightShift) and 1
        } else {
            (latBits ushr rightShift) and 1
        }
        bits = (bits shl 1) or maskedBit
        isEven = !isEven
    }
    return (bits shl (MAX_BIT_PRECISION - significantBits)) or charPrecision.toLong()
}

private fun evenOddBitsRightAligned(bits: Long): LongArray {
    val charPrecision = (bits and 0xF).toInt()
    require(charPrecision in 1..MAX_CHAR_PRECISION) { "Invalid hash bits! Geohash must be between 1 and $MAX_CHAR_PRECISION characters" }
    val significantBits = charPrecision * BITS_PER_CHAR
    var count = 0
    var isEven = true
    var latBits = 0L
    var lonBits = 0L
    repeat(significantBits) {
        val rightShift = MAX_BIT_PRECISION - ++count
        val maskedBit = (bits ushr rightShift) and 1
        if (isEven) {
            lonBits = (lonBits shl 1) or maskedBit
        } else {
            latBits = (latBits shl 1) or maskedBit
        }
        isEven = !isEven
    }
    return longArrayOf(latBits, lonBits)
}
