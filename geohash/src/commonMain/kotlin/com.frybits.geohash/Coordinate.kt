package com.frybits.geohash

import com.frybits.geohash.internal.LATITUDE_MAX
import com.frybits.geohash.internal.LATITUDE_MIN
import com.frybits.geohash.internal.LONGITUDE_MAX
import com.frybits.geohash.internal.LONGITUDE_MIN

/**
 * Frybits
 * Created by Pablo Baxter (Github: pablobaxter)
 *
 * Lat/Lon Pojo
 *
 * @constructor Creates a coordinate object
 *
 * @property latitude Latitude, guaranteed to be between -90 and 90
 * @property longitude Longitude, guaranteed to be between -180 and 180
 *
 * @throws IllegalArgumentException if [latitude] outside of -90 to 90 degrees, or if any [longitude] is outside of -180 to 180 degrees.
 *
 */

class Coordinate(val latitude: Double, val longitude: Double) {

    init {
        require(latitude in LATITUDE_MIN..LATITUDE_MAX) { "Latitude must be between $LATITUDE_MIN and $LATITUDE_MAX" }
        require(longitude in LONGITUDE_MIN..LONGITUDE_MAX) { "Longitude must be between $LONGITUDE_MIN and $LONGITUDE_MAX" }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Coordinate

        if (latitude != other.latitude) return false
        if (longitude != other.longitude) return false

        return true
    }

    override fun hashCode(): Int {
        var result = latitude.hashCode()
        result = 31 * result + longitude.hashCode()
        return result
    }

    override fun toString(): String {
        return "GeoLatLng(latitude=$latitude, longitude=$longitude)"
    }
}
