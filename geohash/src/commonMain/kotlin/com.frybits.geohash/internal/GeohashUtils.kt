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

/**
 * Frybits
 * Created by Pablo Baxter (Github: pablobaxter)
 */

private val BIT_MASKS = intArrayOf(16, 8, 4, 2, 1)
private val GEOHASH_CHARS_DECODER = GEOHASH_CHARS
    .mapIndexed { index, c -> c to index }
    .associate { return@associate it }

internal fun toBoundingBox(latLonBits: LatLonBits): BoundingBox {
    val charPrecision = (latLonBits.combinedBits and 0xF).toInt()
    require(charPrecision in 1..MAX_CHAR_PRECISION) { "Invalid hash bits! Geohash must be between 1 and $MAX_CHAR_PRECISION characters" }
    val latRange = doubleArrayOf(
        LATITUDE_MIN,
        LATITUDE_MAX
    )
    val lonRange = doubleArrayOf(
        LONGITUDE_MIN,
        LONGITUDE_MAX
    )
    val significantBits = charPrecision * BITS_PER_CHAR

    var isEven = true

    repeat(significantBits) {
        val mask = 1L shl (MAX_BIT_PRECISION - (it + 1))
        val bit = (latLonBits.combinedBits and mask) != 0L
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
    return BoundingBox(latRange[0], lonRange[0], latRange[1], lonRange[1])
}

internal fun toGeohashString(latLonBits: LatLonBits): String {
    val charPrecision = (latLonBits.combinedBits and 0xF).toInt()
    require(charPrecision in 1..MAX_CHAR_PRECISION) { "Invalid hash bits! Geohash must be between 1 and $MAX_CHAR_PRECISION characters" }
    var geoHashBits = latLonBits.combinedBits
    return buildString {
        repeat(charPrecision) {
            // Get the leftmost 5 bits as an INT
            val charPosition = (geoHashBits ushr (MAX_BIT_PRECISION - BITS_PER_CHAR)).toInt()
            append(GEOHASH_CHARS[charPosition])
            geoHashBits = geoHashBits shl BITS_PER_CHAR
        }
    }
}

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
    val significantBits = charPrecision * BITS_PER_CHAR

    var isEven = true
    var latBits = 0L
    var lonBits = 0L

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

    return LatLonBits(latBits, lonBits, charPrecision)
}

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
    geohashString.toLowerCase().forEach { c ->
        val d = GEOHASH_CHARS_DECODER.getValue(c)
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
            LatLonBits(latBits, lonBits, geohashString.length)
}
