@file:JvmName("GeohashAndroidUtils")

package com.frybits.geohash

import android.location.Location

/**
 * Frybits
 * Created by Pablo Baxter (Github: pablobaxter)
 */

/**
 * Get a [Geohash] from the given [Location]
 *
 * @param precision Character precision of the geohash
 */
@JvmOverloads
@JvmName("geohashFromLocation")
fun Location.toGeohash(precision: Int = MAX_CHAR_PRECISION): Geohash = Geohash(latitude, longitude, precision)

@JvmOverloads
@JvmName("locationFromCoordinate")
fun Coordinate.toLocation(geohash:String = "geohash"): Location = Location(geohash).apply {
    latitude = this@toLocation.latitude
    longitude = this@toLocation.longitude
}

val Geohash.location: Location
    get() = coordinate.toLocation(geohash)

