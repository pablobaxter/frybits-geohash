package com.frybits.geohash

/**
 *
 * Frybits
 * Created by Pablo Baxter (Github: pablobaxter)
 *
 * 2D representation of an enclosed box projected onto the world map.
 *
 * @property minLat Min (southern-most) latitude point of the box
 * @property minLon Min (western-most) longitude point of the box
 * @property maxLat Max (northern-most) latitude point of the box
 * @property maxLon Max (eastern-most) longitude point of the box
 *
 * @constructor Creates new BoundingBox from lat/lon ranges
 *
 * @throws IllegalArgumentException if [minLat] is greater than [maxLat], if any of the latitudes is
 * outside of -90 to 90 degrees, or if any longitude is outside of -180 to 180 degrees.
 */
class BoundingBox(val minLat: Double, val minLon: Double, val maxLat: Double, val maxLon: Double) {

    private val latRange = minLat..maxLat
    private val lonRange = minLon..maxLon

    /**
     * Center latitude of this box
     */
    val centerLat = (maxLat + minLat) / 2

    /**
     * Center longitude of this box
     */
    val centerLon = ((maxLon + minLon) / 2).let {
        // Ensures longitude is between -180 and 180
        if (it > 180) return@let it - 360
        return@let it
    }

    /**
     * If this box crosses the 180 meridian
     */
    val intersects180Meridian: Boolean

    init {
        require(minLat < maxLat) { "The southern most latitude must be greater than the northern most latitude" }
        require(
            minLat in LATITUDE_MIN..LATITUDE_MAX &&
                    maxLat in LATITUDE_MIN..LATITUDE_MAX
        ) { "Latitude must be between $LATITUDE_MIN and $LATITUDE_MAX" }
        require(
            minLon in LONGITUDE_MIN..LONGITUDE_MAX &&
                    maxLon in LONGITUDE_MIN..LONGITUDE_MAX
        ) { "Longitude must be between $LONGITUDE_MIN and $LONGITUDE_MAX" }

        this.intersects180Meridian = maxLon < minLon
    }

    /**
     * Check if this box intersects the other box
     *
     * @param other The other [BoundingBox] to check against
     *
     * @return If this box intersects the other box (this includes tangent boxes), return true else false
     */
    fun intersects(other: BoundingBox): Boolean {
        if (other.minLat > maxLat || other.maxLat < minLat) return false

        return if (!intersects180Meridian && !other.intersects180Meridian) {
            other.maxLon >= minLon && other.minLon <= maxLon
        } else if (intersects180Meridian && !other.intersects180Meridian) {
            maxLon >= other.minLon || minLon <= other.maxLon
        } else if (!intersects180Meridian && other.intersects180Meridian) {
            minLon <= other.maxLon || maxLon >= other.minLon
        } else {
            true
        }
    }

    /**
     * Checks if given lat/lon is within the bounds of this box
     *
     * @param lat Latitude to test against
     * @param lon Longitude to test against
     */
    fun contains(lat: Double, lon: Double): Boolean {
        return lat in latRange && if (intersects180Meridian) {
            lon <= maxLon || lon >= minLon
        } else {
            lon in lonRange
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BoundingBox) return false

        if (minLat != other.minLat) return false
        if (minLon != other.minLon) return false
        if (maxLat != other.maxLat) return false
        if (maxLon != other.maxLon) return false
        return true
    }

    override fun hashCode(): Int {
        var result = minLat.hashCode()
        result = 31 * result + minLon.hashCode()
        result = 31 * result + maxLat.hashCode()
        result = 31 * result + maxLon.hashCode()
        return result
    }

    override fun toString(): String {
        return "BoundingBox(latRange=$latRange, lonRange=$lonRange, centerLat=$centerLat, centerLon=$centerLon, intersects180Meridian=$intersects180Meridian)"
    }
}
