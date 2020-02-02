@file:JvmName("InternalGeohashUtils")

package com.frybits.geohash.internal

import com.frybits.geohash.BITS_PER_CHAR
import com.frybits.geohash.BoundingBox
import com.frybits.geohash.GEOHASH_CHARS
import com.frybits.geohash.LATITUDE_MAX
import com.frybits.geohash.LATITUDE_MIN
import com.frybits.geohash.LONGITUDE_MAX
import com.frybits.geohash.LONGITUDE_MIN
import com.frybits.geohash.MAX_BIT_PRECISION
import com.frybits.geohash.MAX_CHAR_PRECISION
import kotlin.jvm.JvmName
import kotlin.jvm.JvmSynthetic
import kotlin.math.pow

/**
 * Frybits
 * Created by Pablo Baxter (Github: pablobaxter)
 */

// Array to iterate a masking bit through all 5 bits of the geohash character key
private val BIT_MASKS = intArrayOf(16, 8, 4, 2, 1)

// Makes the INT:CHAR map into a CHAR:INT map for decoding geohash strings
private val GEOHASH_CHARS_DECODER = GEOHASH_CHARS
    .mapIndexed { index, c -> c to index }
    .associate { return@associate it }

@JvmSynthetic
// LatLonBits -> BoundingBox
internal fun toBoundingBox(latLonBits: LatLonBits): BoundingBox {
    val charPrecision = (latLonBits.combinedBits and 0xF).toInt() // Last 4 bits are used for holding character precision
    require(charPrecision in 1..MAX_CHAR_PRECISION) { "Invalid hash bits! Geohash must be between 1 and $MAX_CHAR_PRECISION characters" }
    val latRange = doubleArrayOf(
        LATITUDE_MIN,
        LATITUDE_MAX
    )
    val lonRange = doubleArrayOf(
        LONGITUDE_MIN,
        LONGITUDE_MAX
    )

    // Get the number of significant bits based off charPrecision. Max significantBis == 60
    val significantBits = charPrecision * BITS_PER_CHAR

    // Bits are read left to right, leftmost bit is always the even one
    var isEven = true

    // Iterate each significant bit
    repeat(significantBits) {
        val mask = 1L shl (MAX_BIT_PRECISION - (it + 1)) // Create the bit mask for the current significantBit
        val bit = (latLonBits.combinedBits and mask) != 0L // Is the bit set?

        // Geohashing algorithm
        if (isEven) {
            val mid = lonRange.average()
            if (bit) {
                lonRange[0] = mid
            } else {
                lonRange[1] = mid
            }
        } else {
            val mid = latRange.average()
            if (bit) {
                latRange[0] = mid
            } else {
                latRange[1] = mid
            }
        }
        isEven = !isEven
    }
    // At this point, the range arrays contain the min/max lat/lon range for the given bits
    return BoundingBox(latRange[0], lonRange[0], latRange[1], lonRange[1])
}

@JvmSynthetic
// LatLonBits -> Geohash string
internal fun toGeohashString(latLonBits: LatLonBits): String {
    val charPrecision = (latLonBits.combinedBits and 0xF).toInt() // Last 4 bits are used for holding character precision
    require(charPrecision in 1..MAX_CHAR_PRECISION) { "Invalid hash bits! Geohash must be between 1 and $MAX_CHAR_PRECISION characters" }
    var geoHashBits = latLonBits.combinedBits
    return buildString {
        repeat(charPrecision) {
            // Get the leftmost 5 bits as an INT
            val charPosition = (geoHashBits ushr (MAX_BIT_PRECISION - BITS_PER_CHAR)).toInt()
            // Append the character at the given position
            append(GEOHASH_CHARS[charPosition])
            geoHashBits = geoHashBits shl BITS_PER_CHAR // Push the bits out the left side
        }
    }
}

@JvmSynthetic
// Lat/Lon and precision -> LatLonBits
internal fun toLatLonBits(latitude: Double, longitude: Double, charPrecision: Int): LatLonBits {
    require(latitude in LATITUDE_MIN..LATITUDE_MAX) { "Latitude must be between $LATITUDE_MIN and $LATITUDE_MAX" }
    require(longitude in LONGITUDE_MIN..LONGITUDE_MAX) { "Longitude must be between $LONGITUDE_MIN and $LONGITUDE_MAX" }
    require(charPrecision in 1..MAX_CHAR_PRECISION) { "Geohash must be between 1 and $MAX_CHAR_PRECISION characters" }
    val latRange = doubleArrayOf(
        LATITUDE_MIN,
        LATITUDE_MAX
    )
    val lonRange = doubleArrayOf(
        LONGITUDE_MIN,
        LONGITUDE_MAX
    )

    // Number of bits to encode (max 60)
    val significantBits = charPrecision * BITS_PER_CHAR

    // First bit is always the even one
    var isEven = true
    var latBits = 0L // Temp holder for lat bits
    var lonBits = 0L // Temp holder for lon bits

    // Geohashing encoding algorithm
    repeat(significantBits) {
        if (isEven) {
            val mid = lonRange.average()
            if (longitude >= mid) {
                lonRange[0] = mid
                lonBits = (lonBits shl 1) or 1
            } else {
                lonRange[1] = mid
                lonBits = lonBits shl 1
            }
        } else {
            val mid = latRange.average()
            if (latitude >= mid) {
                latRange[0] = mid
                latBits = (latBits shl 1) or 1
            } else {
                latRange[1] = mid
                latBits = latBits shl 1
            }
        }
        isEven = !isEven
    }

    return latLonBits(latBits, lonBits, charPrecision)
}

@JvmSynthetic
// Geohash string -> BoundingBox & LatLonBits
// Instead of creating 2 separate functions to iterate through the geohash, this just kills 2 birds with one stone
internal fun toBoundingBoxAndBits(geohashString: String): Pair<BoundingBox, LatLonBits> {
    require(geohashString.all { GEOHASH_CHARS.contains(it, ignoreCase = true) }) { "Geohash string invalid" }
    require(geohashString.length in 1..MAX_CHAR_PRECISION) { "Geohash must be between 1 and $MAX_CHAR_PRECISION characters" }

    var isEven = true
    val latRange = doubleArrayOf(
        LATITUDE_MIN,
        LATITUDE_MAX
    )
    val lonRange = doubleArrayOf(
        LONGITUDE_MIN,
        LONGITUDE_MAX
    )
    var latBits = 0L
    var lonBits = 0L

    // Iterate through all the characters of the geohash. Make them lowercase if they aren't already
    geohashString.toLowerCase().forEach { c ->
        // Get the position of this char
        val d = GEOHASH_CHARS_DECODER.getValue(c)

        // This iterates through each bit of the position Int. Assumes 2^5 - 1 as max value
        BIT_MASKS.forEach { mask ->
            val bit = (d and mask) != 0
            if (isEven) {
                val mid = lonRange.average()
                if (bit) {
                    lonRange[0] = mid
                    lonBits = (lonBits shl 1) or 1
                } else {
                    lonRange[1] = mid
                    lonBits = lonBits shl 1
                }
            } else {
                val mid = latRange.average()
                if (bit) {
                    latRange[0] = mid
                    latBits = (latBits shl 1) or 1
                } else {
                    latRange[1] = mid
                    latBits = latBits shl 1
                }
            }
            isEven = !isEven
        }
    }
    return BoundingBox(latRange[0], lonRange[0], latRange[1], lonRange[1]) to
            latLonBits(latBits, lonBits, geohashString.length)
}

// This gives approximate lat/lon error given the precision
@JvmSynthetic
internal fun approxLatitudeError(charPrecision: Int) = 90.0 / 2.0.pow((charPrecision * BITS_PER_CHAR) / 2)

@JvmSynthetic
internal fun approxLongitudeError(charPrecision: Int): Double = 180.0 / 2.0.pow(((charPrecision * BITS_PER_CHAR) + 1) / 2)

// Credit to kungfoo's geohash-java library (https://github.com/kungfoo/geohash-java) for these 4 functions
// From [GeoHashSizeTable]
@JvmSynthetic
internal fun maxLatSizeAt(charPrecision: Int): Double = 180.0 / 2.0.pow((charPrecision * BITS_PER_CHAR) / 2)

// From [GeoHashSizeTable]
@JvmSynthetic
internal fun maxLonSizeAt(charPrecision: Int): Double = 360.0 / 2.0.pow(((charPrecision * BITS_PER_CHAR) + 1) / 2)

// From [BoundingBox]
@JvmSynthetic
internal fun BoundingBox.latSize(): Double = northEastPoint.latitude - southEastPoint.latitude

// From [BoundingBox]
@JvmSynthetic
internal fun BoundingBox.lonSize(): Double {
    if (northEastPoint.longitude == LONGITUDE_MAX || northWestPoint.longitude == LONGITUDE_MIN) return 360.0
    val delta = northEastPoint.longitude - northWestPoint.longitude
    return if (delta < 0) delta + 360.0 else delta
}

@JvmSynthetic
internal fun BoundingBox.maxCharsToCover(): Int {
    val latDelta = latSize()
    val lonDelta = lonSize()
    repeat(MAX_CHAR_PRECISION) { count ->
        if (maxLatSizeAt(MAX_CHAR_PRECISION - count) >= latDelta && maxLonSizeAt(MAX_CHAR_PRECISION - count) >= lonDelta) {
            return MAX_CHAR_PRECISION - count
        }
    }
    return 0
}

@JvmSynthetic
internal fun BoundingBox.encompassesCompletely(other: BoundingBox): Boolean {
    return contains(other.northWestPoint) && contains(other.northEastPoint)
            && contains(other.southEastPoint) && contains(other.southWestPoint)
}
