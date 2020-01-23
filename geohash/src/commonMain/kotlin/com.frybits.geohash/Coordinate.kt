package com.frybits.geohash

/**
 * Frybits
 * Created by Pablo Baxter (Github: pablobaxter)
 */

class Coordinate(val latitude: Double, val longitude: Double) {

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
