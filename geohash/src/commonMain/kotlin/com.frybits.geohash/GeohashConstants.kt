@file:JvmName("GeohashConstants")

package com.frybits.geohash

import kotlin.jvm.JvmName

/**
 * Frybits
 * Created by Pablo Baxter (Github: pablobaxter)
 *
 * Constants used for Geohashes
 */

// Max number of character precision. 12 * 5 bits = 60
const val MAX_CHAR_PRECISION = 12

internal const val LATITUDE_MIN = -90.0
internal const val LATITUDE_MAX = 90.0
internal const val LONGITUDE_MIN = -180.0
internal const val LONGITUDE_MAX = 180.0
// Only characters allowed for the geohash
internal const val GEOHASH_CHARS = "0123456789bcdefghjkmnpqrstuvwxyz"
// Geohash bits is 60 bits long, with 4 bits for encoding char precision Int
internal const val MAX_BIT_PRECISION = 64
// 2^5 - 1 == 31, which is the last index of GEOHASH_CHARS
internal const val BITS_PER_CHAR = 5
