package com.frybits.geohash

import kotlin.js.JsName

/**
 * Frybits
 * Created by Pablo Baxter (Github: pablobaxter)
 *
 * 2D representation of an enclosed box projected onto the world map.
 *
 * @constructor Creates new BoundingBox from given SW/NE points
 *
 * @property southWestPoint Southwest point of this [BoundingBox]
 * @property northEastPoint Northeast point of this [BoundingBox]
 *
 * @throws IllegalArgumentException if [minLat] is greater than [maxLat], if any of the latitudes is
 * outside of -90 to 90 degrees, or if any longitude is outside of -180 to 180 degrees.
 */

class BoundingBox(val southWestPoint: Coordinate, val northEastPoint: Coordinate) {

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
    @JsName("boundingBoxLatLons")
    constructor(minLat: Double, minLon: Double, maxLat: Double, maxLon: Double) : this(
        Coordinate(minLat, minLon),
        Coordinate(maxLat, maxLon)
    )

    private val latRange = southWestPoint.latitude..northEastPoint.latitude
    private val lonRange = southWestPoint.longitude..northEastPoint.longitude

    /**
     * @property northWestPoint Northeast point of this [BoundingBox]
     */
    val northWestPoint = Coordinate(northEastPoint.latitude, southWestPoint.longitude)

    /**
     * @property southEastPoint Northeast point of this [BoundingBox]
     */
    val southEastPoint = Coordinate(southWestPoint.latitude, northEastPoint.longitude)

    /**
     * Center point of this box
     */
    val centerCoordinate = Coordinate(
        (northEastPoint.latitude + southWestPoint.latitude) / 2,
        ((northEastPoint.longitude + southWestPoint.longitude) / 2).let { if (it > 180) it - 360 else it }
    )

    /**
     * If this box crosses the 180 meridian
     */
    val intersects180Meridian: Boolean

    init {
        require(southWestPoint.latitude < northEastPoint.latitude) { "The southern most latitude must be greater than the northern most latitude" }
        require(
            southWestPoint.latitude in LATITUDE_MIN..LATITUDE_MAX &&
                    northEastPoint.latitude in LATITUDE_MIN..LATITUDE_MAX
        ) { "Latitude must be between $LATITUDE_MIN and $LATITUDE_MAX" }
        require(
            southWestPoint.longitude in LONGITUDE_MIN..LONGITUDE_MAX &&
                    northEastPoint.longitude in LONGITUDE_MIN..LONGITUDE_MAX
        ) { "Longitude must be between $LONGITUDE_MIN and $LONGITUDE_MAX" }

        this.intersects180Meridian = northEastPoint.longitude < southWestPoint.longitude
    }

    /**
     * Check if this box intersects the other box
     *
     * @param other The other [BoundingBox] to check against
     *
     * @return If this box intersects the other box (this includes tangent boxes), return true else false
     */
    @JsName("intersects")
    fun intersects(other: BoundingBox): Boolean {
        if (other.southWestPoint.latitude > northEastPoint.latitude || other.northEastPoint.latitude < southWestPoint.latitude) return false

        return if (!intersects180Meridian && !other.intersects180Meridian) {
            other.northEastPoint.longitude >= southWestPoint.longitude && other.southWestPoint.longitude <= northEastPoint.longitude
        } else if (intersects180Meridian && !other.intersects180Meridian) {
            northEastPoint.longitude >= other.southWestPoint.longitude || southWestPoint.longitude <= other.northEastPoint.longitude
        } else if (!intersects180Meridian && other.intersects180Meridian) {
            southWestPoint.longitude <= other.northEastPoint.longitude || northEastPoint.longitude >= other.southWestPoint.longitude
        } else {
            true
        }
    }

    /**
     * Checks if given coordinate is within the bounds of this box
     *
     * @param coordinate
     */
    @JsName("containsCoordinate")
    fun contains(coordinate: Coordinate): Boolean = contains(coordinate.latitude, coordinate.longitude)

    /**
     * Checks if given lat/lon is within the bounds of this box
     *
     * @param lat Latitude to test against
     * @param lon Longitude to test against
     */
    @JsName("containsLatLon")
    fun contains(lat: Double, lon: Double): Boolean {
        return lat in latRange && if (intersects180Meridian) {
            lon <= northEastPoint.longitude || lon >= southWestPoint.longitude
        } else {
            lon in lonRange
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BoundingBox) return false

        if (southWestPoint != other.southWestPoint) return false
        if (northEastPoint != other.northEastPoint) return false
        return true
    }

    override fun hashCode(): Int {
        var result = southWestPoint.hashCode()
        result = 31 * result + northEastPoint.hashCode()
        return result
    }

    override fun toString(): String {
        return "BoundingBox(latRange=$latRange, lonRange=$lonRange, centerGeoLatLng=$centerCoordinate, intersects180Meridian=$intersects180Meridian)"
    }
}
