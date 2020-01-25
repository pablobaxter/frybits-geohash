@file:JvmName("GeohashConstants")
package com.frybits.geohash

import kotlin.jvm.JvmName

/**
 * Frybits
 * Created by Pablo Baxter (Github: pablobaxter)
 *
 * Constants used for Geohashes
 */

const val MAX_CHAR_PRECISION = 12

internal const val LATITUDE_MIN = -90.0
internal const val LATITUDE_MAX = 90.0
internal const val LONGITUDE_MIN = -180.0
internal const val LONGITUDE_MAX = 180.0
internal const val GEOHASH_CHARS = "0123456789bcdefghjkmnpqrstuvwxyz"
internal const val MAX_BIT_PRECISION = 64
internal const val BITS_PER_CHAR = 5
