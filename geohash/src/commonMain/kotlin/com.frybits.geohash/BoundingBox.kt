package com.frybits.geohash

import com.frybits.geohash.internal.LATITUDE_MAX
import com.frybits.geohash.internal.LATITUDE_MIN
import com.frybits.geohash.internal.LONGITUDE_MAX
import com.frybits.geohash.internal.LONGITUDE_MIN

/**
 *
 * Frybits
 * Created by Pablo Baxter (Github: pablobaxter)
 *
 * 2D representation of an enclosed box projected onto the world map.
 *
 * @property southWest Southwest point of this [BoundingBox]
 * @property northEast Northeast ponit of this [BoundingBox]
 */
class BoundingBox(val southWest: Coordinate, val northEast: Coordinate) {

    /**
     * @param minLat Min (southern-most) latitude point of the box
     * @param minLon Min (western-most) longitude point of the box
     * @param maxLat Max (northern-most) latitude point of the box
     * @param maxLon Max (eastern-most) longitude point of the box
     *
     * @constructor Creates new BoundingBox from lat/lon ranges
     *
     * @throws IllegalArgumentException if [minLat] is greater than [maxLat], if any of the latitudes is
     * outside of -90 to 90 degrees, or if any longitude is outside of -180 to 180 degrees.
     */
    constructor(minLat: Double, minLon: Double, maxLat: Double, maxLon: Double) : this(
        Coordinate(minLat, minLon),
        Coordinate(maxLat, maxLon)
    )

    private val latRange = southWest.latitude..northEast.latitude
    private val lonRange = southWest.longitude..northEast.longitude

    /**
     * Center point of this box
     */
    val centerCoordinate = Coordinate(
        (northEast.latitude + southWest.latitude) / 2,
        ((northEast.longitude + southWest.longitude) / 2).let { if (it > 180) it - 360 else it }
    )

    /**
     * If this box crosses the 180 meridian
     */
    val intersects180Meridian: Boolean

    init {
        require(southWest.latitude < northEast.latitude) { "The southern most latitude must be greater than the northern most latitude" }
        require(
            southWest.latitude in LATITUDE_MIN..LATITUDE_MAX &&
                    northEast.latitude in LATITUDE_MIN..LATITUDE_MAX
        ) { "Latitude must be between $LATITUDE_MIN and $LATITUDE_MAX" }
        require(
            southWest.longitude in LONGITUDE_MIN..LONGITUDE_MAX &&
                    northEast.longitude in LONGITUDE_MIN..LONGITUDE_MAX
        ) { "Longitude must be between $LONGITUDE_MIN and $LONGITUDE_MAX" }

        this.intersects180Meridian = northEast.longitude < southWest.longitude
    }

    /**
     * Check if this box intersects the other box
     *
     * @param other The other [BoundingBox] to check against
     *
     * @return If this box intersects the other box (this includes tangent boxes), return true else false
     */
    fun intersects(other: BoundingBox): Boolean {
        if (other.southWest.latitude > northEast.latitude || other.northEast.latitude < southWest.latitude) return false

        return if (!intersects180Meridian && !other.intersects180Meridian) {
            other.northEast.longitude >= southWest.longitude && other.southWest.longitude <= northEast.longitude
        } else if (intersects180Meridian && !other.intersects180Meridian) {
            northEast.longitude >= other.southWest.longitude || southWest.longitude <= other.northEast.longitude
        } else if (!intersects180Meridian && other.intersects180Meridian) {
            southWest.longitude <= other.northEast.longitude || northEast.longitude >= other.southWest.longitude
        } else {
            true
        }
    }

    /**
     * Checks if given coordinate is within the bounds of this box
     *
     * @param coordinates
     */
    fun contains(coordinates: Coordinate): Boolean = contains(coordinates.latitude, coordinates.longitude)

    /**
     * Checks if given lat/lon is within the bounds of this box
     *
     * @param lat Latitude to test against
     * @param lon Longitude to test against
     */
    fun contains(lat: Double, lon: Double): Boolean {
        return lat in latRange && if (intersects180Meridian) {
            lon <= northEast.longitude || lon >= southWest.longitude
        } else {
            lon in lonRange
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BoundingBox) return false

        if (southWest != other.southWest) return false
        if (northEast != other.northEast) return false
        return true
    }

    override fun hashCode(): Int {
        var result = southWest.hashCode()
        result = 31 * result + northEast.hashCode()
        return result
    }

    override fun toString(): String {
        return "BoundingBox(latRange=$latRange, lonRange=$lonRange, centerGeoLatLng=$centerCoordinate, intersects180Meridian=$intersects180Meridian)"
    }
}
