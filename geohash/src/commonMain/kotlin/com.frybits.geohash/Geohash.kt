package com.frybits.geohash

import com.frybits.geohash.internal.BITS_PER_CHAR
import com.frybits.geohash.internal.GEOHASH_CHARS
import com.frybits.geohash.internal.LATITUDE_MAX
import com.frybits.geohash.internal.LATITUDE_MIN
import com.frybits.geohash.internal.LONGITUDE_MAX
import com.frybits.geohash.internal.LONGITUDE_MIN
import com.frybits.geohash.internal.LatLonBits
import com.frybits.geohash.internal.MAX_BIT_PRECISION
import com.frybits.geohash.internal.toBoundingBox
import com.frybits.geohash.internal.toBoundingBoxAndBits
import com.frybits.geohash.internal.toGeohashString
import com.frybits.geohash.internal.toLatLonBits
import kotlin.jvm.JvmName
import kotlin.jvm.JvmOverloads
import kotlin.math.min

/**
 * Frybits
 * Created by Pablo Baxter (Github: pablobaxter)
 *
 * A [Geohash] is a string representation of a latitude/longitude using characters from '0' to '9' and 'b' to 'z' (except 'a', 'i', 'l', 'o').
 * This is done by dividing a mercator projection of the Earth into halves, with each half being represented by a bit (0 or 1), appended left to right.
 * Every 5 bits maps to one of the 32 characters described earlier, with a maximum binary precision of 60 bits (12 characters).
 */

class Geohash : Comparable<Geohash> {

    /**
     * [Coordinate] of this geohash
     */
    val coordinate: Coordinate

    /**
     * Character precision of this geohash
     */
    val charPrecision: Int

    /**
     * String representation of the geohash at the given [charPrecision]
     */
    val geohash: String

    /**
     * The bounding box representing this geohash at the given precision
     */
    val boundingBox: BoundingBox

    // Binary representation of the geohash
    internal val latLonBits: LatLonBits

    /**
     * @param latitude Latitude to be used for generating the geohash
     * @param longitude Longitude to be used for generating the geohash
     * @param charPrecision Character precision for this geohash
     *
     * @throws IllegalArgumentException If [latitude] is not between [LATITUDE_MIN] and [LATITUDE_MAX], if [longitude] is not between
     * [LONGITUDE_MIN] and [LONGITUDE_MAX], or if [charPrecision] is not between 1 and [MAX_CHAR_PRECISION]
     */
    constructor(latitude: Double, longitude: Double, charPrecision: Int): this(Coordinate(latitude, longitude), charPrecision)

    /**
     * @param coordinates
     * @param charPrecision
     *
     * @throws IllegalArgumentException If latitude is not between [LATITUDE_MIN] and [LATITUDE_MAX], if longitude is not between
     * [LONGITUDE_MIN] and [LONGITUDE_MAX], or if [charPrecision] is not between 1 and [MAX_CHAR_PRECISION]
     */
    constructor(coordinates: Coordinate, charPrecision: Int) {
        require(coordinates.latitude in LATITUDE_MIN..LATITUDE_MAX) { "Latitude must be between $LATITUDE_MIN and $LATITUDE_MAX" }
        require(coordinates.longitude in LONGITUDE_MIN..LONGITUDE_MAX) { "Longitude must be between $LONGITUDE_MIN and $LONGITUDE_MAX" }
        require(charPrecision in 1..MAX_CHAR_PRECISION) { "Geohash must be between 1 and $MAX_CHAR_PRECISION characters in precision" }
        this.coordinate = coordinates
        this.charPrecision = charPrecision
        this.latLonBits = toLatLonBits(coordinates.latitude, coordinates.longitude, charPrecision)
        this.geohash = toGeohashString(latLonBits)
        this.boundingBox = toBoundingBox(latLonBits)
    }

    /**
     * @param geohash The string geohash representing a latitude/longitude coordinate
     *
     * @throws IllegalArgumentException If [geohash] string is invalid or if the string length is not between 1 and [MAX_CHAR_PRECISION]
     */
    constructor(geohash: String) {
        require(geohash.all { GEOHASH_CHARS.contains(it, ignoreCase = true) }) { "Geohash string invalid" }
        require(geohash.length in 1..MAX_CHAR_PRECISION) { "Geohash must be between 1 and $MAX_CHAR_PRECISION characters" }
        this.geohash = geohash.toLowerCase()
        this.charPrecision = geohash.length
        val (bbox, bits) = toBoundingBoxAndBits(geohash)
        this.coordinate = bbox.centerCoordinate
        this.latLonBits = bits
        this.boundingBox = bbox
    }

    // Internal constructor for generating geohashes from bits
    internal constructor(latLonBits: LatLonBits) {
        this.charPrecision = (latLonBits.combinedBits and 0xF).toInt()
        require(charPrecision in 1..MAX_CHAR_PRECISION) { "Geohash must be between 1 and $MAX_CHAR_PRECISION characters" }

        this.geohash = toGeohashString(latLonBits)
        this.boundingBox = toBoundingBox(latLonBits)
        this.latLonBits = latLonBits
        this.coordinate = boundingBox.centerCoordinate
    }

    /**
     * Creates an iterable [GeohashRange] object from this geohash to the [other]
     */
    operator fun rangeTo(other: Geohash): GeohashRange = GeohashRange(this, other)

    /**
     * Enum class for getting geohash neighbors
     */
    enum class Direction {
        NORTH,
        NORTH_EAST,
        EAST,
        SOUTH_EAST,
        SOUTH,
        SOUTH_WEST,
        WEST,
        NORTH_WEST
    }

    /**
     * Gets this geohash's neighbor at the given [direction]
     *
     * @return The [Geohash] at the given [direction]
     */
    @JvmName("getNeighbor")
    fun neighborAt(direction: Direction): Geohash {
        return when (direction) {
            Direction.NORTH -> Geohash(
                LatLonBits(
                    latBits = latLonBits.latBits + 1,
                    lonBits = latLonBits.lonBits,
                    charPrecision = charPrecision
                )
            )
            Direction.NORTH_EAST -> Geohash(
                LatLonBits(
                    latBits = latLonBits.latBits + 1,
                    lonBits = latLonBits.lonBits + 1,
                    charPrecision = charPrecision
                )
            )
            Direction.EAST -> Geohash(
                LatLonBits(
                    latBits = latLonBits.latBits,
                    lonBits = latLonBits.lonBits + 1,
                    charPrecision = charPrecision
                )
            )
            Direction.SOUTH_EAST -> Geohash(
                LatLonBits(
                    latBits = latLonBits.latBits - 1,
                    lonBits = latLonBits.lonBits + 1,
                    charPrecision = charPrecision
                )
            )
            Direction.SOUTH -> Geohash(
                LatLonBits(
                    latBits = latLonBits.latBits - 1,
                    lonBits = latLonBits.lonBits,
                    charPrecision = charPrecision
                )
            )
            Direction.SOUTH_WEST -> Geohash(
                LatLonBits(
                    latBits = latLonBits.latBits - 1,
                    lonBits = latLonBits.lonBits - 1,
                    charPrecision = charPrecision
                )
            )
            Direction.WEST -> Geohash(
                LatLonBits(
                    latBits = latLonBits.latBits,
                    lonBits = latLonBits.lonBits - 1,
                    charPrecision = charPrecision
                )
            )
            Direction.NORTH_WEST -> Geohash(
                LatLonBits(
                    latBits = latLonBits.latBits + 1,
                    lonBits = latLonBits.lonBits - 1,
                    charPrecision = charPrecision
                )
            )
        }
    }

    /**
     * Gets all geohashes surrounding this one
     *
     * @param includeSelf Whether to include this geohash in the returned list. Defaults to true
     *
     * @return [List] of geohashes surrounding this one
     */
    @JvmOverloads
    @JvmName("getSurroundingGeohashes")
    fun surroundingGeohashes(includeSelf: Boolean = true): List<Geohash> {
        val self = if (includeSelf) listOf(this) else emptyList()

        return listOf(
            neighborAt(Direction.NORTH),
            neighborAt(Direction.NORTH_EAST),
            neighborAt(Direction.EAST),
            neighborAt(Direction.SOUTH_EAST),
            neighborAt(Direction.SOUTH),
            neighborAt(Direction.SOUTH_WEST),
            neighborAt(Direction.WEST),
            neighborAt(Direction.NORTH_WEST)
        ) + self
    }

    /**
     * Steps to the next geohash in the Z-order (It's more like an N-order)
     * If the last geohash in the series is reached, this rolls back to the first
     *
     * @return Next [Geohash]
     */
    operator fun inc(): Geohash {
        val insignificantBits = MAX_BIT_PRECISION - charPrecision * BITS_PER_CHAR
        val bits = ((ord() + 1) shl insignificantBits) or charPrecision.toLong()
        return Geohash(LatLonBits(bits))
    }

    /**
     * Steps to the previous geohash in the Z-order (It's more like an N-order)
     * If the first geohash in the series is reached, this rolls to the last
     *
     * @return Previous [Geohash]
     */
    operator fun dec(): Geohash {
        val insignificantBits = MAX_BIT_PRECISION - charPrecision * BITS_PER_CHAR
        val bits = ((ord() - 1) shl insignificantBits) or charPrecision.toLong()
        return Geohash(LatLonBits(bits))
    }

    /**
     * Steps forward n times in the geohash Z-order at this geohash's precision
     */
    operator fun plus(steps: Int): Geohash {
        return this + steps.toLong()
    }

    /**
     * Steps forward n times in the geohash Z-order at this geohash's precision
     */
    operator fun plus(steps: Long): Geohash {
        val insignificantBits = MAX_BIT_PRECISION - charPrecision * BITS_PER_CHAR
        val bits = ((ord() + steps) shl insignificantBits) or charPrecision.toLong()
        return Geohash(LatLonBits(bits))
    }

    /**
     * Steps backward n times in the geohash Z-order at this geohash's precision
     */
    operator fun minus(steps: Int): Geohash {
        return this - steps.toLong()
    }

    /**
     * Steps backward n times in the geohash Z-order at this geohash's precision
     */
    operator fun minus(steps: Long): Geohash {
        val insignificantBits = MAX_BIT_PRECISION - charPrecision * BITS_PER_CHAR
        val bits = ((ord() - steps) shl insignificantBits) or charPrecision.toLong()
        return Geohash(LatLonBits(bits))
    }

    /**
     * Gives the number of steps needed to reach the given geohash from this geohash
     * A negative number indicates that the other geohash is before this one.
     *
     * @throws IllegalArgumentException if the geohashes are of different precision
     */
    @JvmName("stepsBetween")
    infix fun stepsTo(otherGeohash: Geohash): Long {
        require(charPrecision == otherGeohash.charPrecision) { "Geohashes must be of the same precision to compare steps between" }
        return otherGeohash.ord() - this.ord()
    }

    /**
     * Checks if the given [geohash] is in this geohash. All geohashes contain themselves
     */
    fun contains(geohash: Geohash): Boolean = contains(geohash.geohash)

    /**
     * Checks if the given [geohash] string is in this geohash. All geohashes contain themselves
     */
    fun contains(geohash: String): Boolean {
        if (geohash.length >= charPrecision) {
            return geohash.startsWith(this.geohash, ignoreCase = true)
        }
        return false
    }

    /**
     * Checks if the given [coordinate] is in this geohash
     */
    fun contains(coordinate: Coordinate): Boolean = contains(coordinate.latitude, coordinate.longitude)

    /**
     * Checks if the given [latitude]/[longitude] is in this geohash
     */
    fun contains(latitude: Double, longitude: Double): Boolean = boundingBox.contains(latitude, longitude)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Geohash) return false

        return coordinate == other.coordinate &&
                charPrecision == other.charPrecision &&
                geohash == other.geohash &&
                latLonBits == other.latLonBits &&
                boundingBox == other.boundingBox
    }

    override fun hashCode(): Int {
        var result = coordinate.hashCode()
        result = 31 * result + charPrecision
        result = 31 * result + geohash.hashCode()
        result = 31 * result + latLonBits.hashCode()
        result = 31 * result + boundingBox.hashCode()
        return result
    }

    override fun toString(): String {
        return "Geohash(coordinates=$coordinate, charPrecision=$charPrecision, geohash='$geohash', latLonBits=$latLonBits, boundingBox='$boundingBox')"
    }

    override fun compareTo(other: Geohash): Int {
        // Short-circuit to compare the binaries. If they are equal, geohashes are equal
        if (charPrecision == other.charPrecision) return ord().compareTo(other.ord())

        val insignificantBits = min(
            MAX_BIT_PRECISION - charPrecision * BITS_PER_CHAR,
            MAX_BIT_PRECISION - other.charPrecision * BITS_PER_CHAR
        )

        val otherOrd = other.latLonBits.combinedBits ushr insignificantBits
        val ord = latLonBits.combinedBits ushr insignificantBits
        return if (otherOrd != ord) {
            ord.compareTo(otherOrd)
        } else {
            charPrecision.compareTo(other.charPrecision)
        }
    }

    // The Z-order position of this geohash at the given precision
    internal fun ord(): Long {
        val insignificantBits = MAX_BIT_PRECISION - charPrecision * BITS_PER_CHAR
        return latLonBits.combinedBits ushr insignificantBits
    }
}
