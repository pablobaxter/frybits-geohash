package com.frybits.geohash.internal

import com.frybits.geohash.BITS_PER_CHAR
import com.frybits.geohash.MAX_BIT_PRECISION
import com.frybits.geohash.MAX_CHAR_PRECISION

/**
 * Frybits
 * Created by Pablo Baxter (Github: pablobaxter)
 */

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
