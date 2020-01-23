package com.frybits.geohash

import com.frybits.geohash.internal.BITS_PER_CHAR
import com.frybits.geohash.internal.GEOHASH_CHARS
import com.frybits.geohash.internal.LATITUDE_MAX
import com.frybits.geohash.internal.LATITUDE_MIN
import com.frybits.geohash.internal.LONGITUDE_MAX
import com.frybits.geohash.internal.LONGITUDE_MIN
import com.frybits.geohash.internal.LatLonBits
import com.frybits.geohash.internal.MAX_BIT_PRECISION
import com.frybits.geohash.internal.MAX_CHAR_PRECISION
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
 */

class Geohash : Comparable<Geohash> {

    val coordinates: Coordinate
    val charPrecision: Int
    val geohash: String
    val boundingBox: BoundingBox

    internal val latLonBits: LatLonBits

    constructor(latitude: Double, longitude: Double, charPrecision: Int): this(Coordinate(latitude, longitude), charPrecision)

    constructor(coordinates: Coordinate, charPrecision: Int) {
        require(coordinates.latitude in LATITUDE_MIN..LATITUDE_MAX) { "Latitude must be between $LATITUDE_MIN and $LATITUDE_MAX" }
        require(coordinates.longitude in LONGITUDE_MIN..LONGITUDE_MAX) { "Longitude must be between $LONGITUDE_MIN and $LONGITUDE_MAX" }
        require(charPrecision in 1..MAX_CHAR_PRECISION) { "Geohash must be between 1 and $MAX_CHAR_PRECISION characters in precision" }
        this.coordinates = coordinates
        this.charPrecision = charPrecision
        this.latLonBits = toLatLonBits(coordinates.latitude, coordinates.longitude, charPrecision)
        this.geohash = toGeohashString(latLonBits)
        this.boundingBox = toBoundingBox(latLonBits)
    }

    constructor(geohash: String) {
        require(geohash.all { GEOHASH_CHARS.contains(it) }) { "Geohash string invalid" }
        require(geohash.length in 1..MAX_CHAR_PRECISION) { "Geohash must be between 1 and $MAX_CHAR_PRECISION characters" }
        this.geohash = geohash
        this.charPrecision = geohash.length
        val (bbox, bits) = toBoundingBoxAndBits(geohash)
        this.coordinates = bbox.centerCoordinate
        this.latLonBits = bits
        this.boundingBox = bbox
    }

    internal constructor(latLonBits: LatLonBits) {
        this.charPrecision = (latLonBits.combinedBits and 0xF).toInt()
        require(charPrecision in 1..MAX_CHAR_PRECISION) { "Geohash must be between 1 and $MAX_CHAR_PRECISION characters" }

        this.geohash = toGeohashString(latLonBits)
        this.boundingBox = toBoundingBox(latLonBits)
        this.latLonBits = latLonBits
        this.coordinates = boundingBox.centerCoordinate
    }

    operator fun rangeTo(other: Geohash): GeohashRange = GeohashRange(this, other)

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

    operator fun inc(): Geohash {
        val insignificantBits = MAX_BIT_PRECISION - charPrecision * BITS_PER_CHAR
        val bits = ((ord() + 1) shl insignificantBits) or charPrecision.toLong()
        return Geohash(LatLonBits(bits))
    }

    operator fun dec(): Geohash {
        val insignificantBits = MAX_BIT_PRECISION - charPrecision * BITS_PER_CHAR
        val bits = ((ord() - 1) shl insignificantBits) or charPrecision.toLong()
        return Geohash(LatLonBits(bits))
    }

    operator fun plus(steps: Int): Geohash {
        return this + steps.toLong()
    }

    operator fun plus(steps: Long): Geohash {
        val insignificantBits = MAX_BIT_PRECISION - charPrecision * BITS_PER_CHAR
        val bits = ((ord() + steps) shl insignificantBits) or charPrecision.toLong()
        return Geohash(LatLonBits(bits))
    }

    operator fun minus(steps: Int): Geohash {
        return this - steps.toLong()
    }

    operator fun minus(steps: Long): Geohash {
        val insignificantBits = MAX_BIT_PRECISION - charPrecision * BITS_PER_CHAR
        val bits = ((ord() - steps) shl insignificantBits) or charPrecision.toLong()
        return Geohash(LatLonBits(bits))
    }

    @JvmName("stepsBetween")
    infix fun stepsTo(otherGeohash: Geohash): Long {
        require(charPrecision == otherGeohash.charPrecision) { "Geohashes must be of the same precision to compare steps between" }
        return this.ord() - otherGeohash.ord()
    }

    fun contains(geohash: Geohash): Boolean = contains(geohash.geohash)

    fun contains(geohash: String): Boolean {
        if (geohash.length >= charPrecision) {
            return geohash.startsWith(this.geohash)
        }
        return false
    }

    fun contains(coordinates: Coordinate): Boolean = contains(coordinates.latitude, coordinates.longitude)

    fun contains(latitude: Double, longitude: Double): Boolean = boundingBox.contains(latitude, longitude)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Geohash) return false

        return coordinates == other.coordinates &&
                charPrecision == other.charPrecision &&
                geohash == other.geohash &&
                latLonBits == other.latLonBits &&
                boundingBox == other.boundingBox
    }

    override fun hashCode(): Int {
        var result = coordinates.hashCode()
        result = 31 * result + charPrecision
        result = 31 * result + geohash.hashCode()
        result = 31 * result + latLonBits.hashCode()
        result = 31 * result + boundingBox.hashCode()
        return result
    }

    override fun toString(): String {
        return "Geohash(coordinates=$coordinates, charPrecision=$charPrecision, geohash='$geohash', latLonBits=$latLonBits, boundingBox='$boundingBox')"
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

    internal fun ord(): Long {
        val insignificantBits = MAX_BIT_PRECISION - charPrecision * BITS_PER_CHAR
        return latLonBits.combinedBits ushr insignificantBits
    }
}
