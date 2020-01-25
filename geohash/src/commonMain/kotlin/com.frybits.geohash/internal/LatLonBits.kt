@file:JvmName("InternalBitsUtils")

package com.frybits.geohash.internal

import com.frybits.geohash.BITS_PER_CHAR
import com.frybits.geohash.MAX_BIT_PRECISION
import com.frybits.geohash.MAX_CHAR_PRECISION
import kotlin.jvm.JvmName
import kotlin.jvm.JvmSynthetic

/**
 * Frybits
 * Created by Pablo Baxter (Github: pablobaxter)
 */

@JvmSynthetic
internal fun latLonBits(latBits: Long, lonBits: Long, charPrecision: Int): LatLonBits =
    LatLonBitsImpl(latBits, lonBits, charPrecision)

@JvmSynthetic
internal fun latLonBits(combinedBits: Long): LatLonBits = LatLonBitsImpl(combinedBits)

internal interface LatLonBits {

    val latBits: Long // This is the odd bits of the geohash
    val lonBits: Long // This is the even bits of the geohash
    val combinedBits: Long // Combined lat/lon bits, interleaved from left to right with lon bit first
}

// Internal class to hold lat/lon bits without boxing them
private class LatLonBitsImpl : LatLonBits {

    override val latBits: Long // This is the odd bits of the geohash
    override val lonBits: Long // This is the even bits of the geohash
    override val combinedBits: Long // Combined lat/lon bits, interleaved from left to right with lon bit first

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

@JvmSynthetic
// Interleaves the latbits and lonbits, starting with lonbit
internal fun recombineBits(latBits: Long, lonBits: Long, charPrecision: Int): Long {
    require(charPrecision in 1..MAX_CHAR_PRECISION) { "Invalid hash bits! Geohash must be between 1 and $MAX_CHAR_PRECISION characters" }

    // How many bits we should encode
    val significantBits = charPrecision * BITS_PER_CHAR
    var bits = 0L // Bits container
    var isEven = true

    // Geohashing encoding algorithm
    repeat(significantBits) {
        val rightShift = (significantBits - (it + 1)) / 2
        val maskedBit = if (isEven) {
            (lonBits ushr rightShift) and 1
        } else {
            (latBits ushr rightShift) and 1
        }
        bits = (bits shl 1) or maskedBit
        isEven = !isEven
    }

    // Left align the bits, and encode the precision at the last 4 bits. Last 4 bits are never used for lat/lon encoding
    return (bits shl (MAX_BIT_PRECISION - significantBits)) or charPrecision.toLong()
}

@JvmSynthetic
// Split the combined bits into latbits and lonbits
// Storing the lat/lon bits separately takes one step away each time we search for a neighbor
internal fun evenOddBitsRightAligned(bits: Long): LongArray {
    val charPrecision = (bits and 0xF).toInt() // charPrecision encoded to the bits
    require(charPrecision in 1..MAX_CHAR_PRECISION) { "Invalid hash bits! Geohash must be between 1 and $MAX_CHAR_PRECISION characters" }
    val significantBits = charPrecision * BITS_PER_CHAR
    var isEven = true
    var latBits = 0L
    var lonBits = 0L
    repeat(significantBits) {
        val rightShift = MAX_BIT_PRECISION - (it + 1)
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
