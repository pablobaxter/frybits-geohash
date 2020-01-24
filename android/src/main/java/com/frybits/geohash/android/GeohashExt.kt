package com.frybits.geohash.android

import android.location.Location
import com.frybits.geohash.Geohash
import com.frybits.geohash.MAX_CHAR_PRECISION

/**
 * Frybits
 * Created by Pablo Baxter (Github: pablobaxter)
 */

fun Location.toGeohash(): Geohash = Geohash(latitude, longitude, MAX_CHAR_PRECISION)
