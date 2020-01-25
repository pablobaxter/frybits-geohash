@file:JvmName("GeohashUtils")

package com.frybits.geohash

import com.frybits.geohash.internal.LatLonBits
import kotlin.js.JsName
import kotlin.jvm.JvmName
import kotlin.jvm.JvmOverloads

/**
 * Frybits
 * Created by Pablo Baxter (Github: pablobaxter)
 *
 * Set of extension functions for Geohash class
 */

/**
 * Gets this geohash's neighbor at the given [direction]
 *
 * @return The [Geohash] at the given [direction]
 */

@JsName("getNeighbor")
@JvmName("getNeighbor")
fun Geohash.neighborAt(direction: Direction): Geohash {
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
@JsName("getSurroundingGeohashes")
@JvmOverloads
@JvmName("getSurroundingGeohashes")
fun Geohash.surroundingGeohashes(includeSelf: Boolean = true): List<Geohash> {
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
operator fun Geohash.inc(): Geohash {
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
operator fun Geohash.dec(): Geohash {
    val insignificantBits = MAX_BIT_PRECISION - charPrecision * BITS_PER_CHAR
    val bits = ((ord() - 1) shl insignificantBits) or charPrecision.toLong()
    return Geohash(LatLonBits(bits))
}

/**
 * Steps forward n times in the geohash Z-order at this geohash's precision
 */
operator fun Geohash.plus(steps: Int): Geohash {
    return this + steps.toLong()
}

/**
 * Steps forward n times in the geohash Z-order at this geohash's precision
 */
operator fun Geohash.plus(steps: Long): Geohash {
    val insignificantBits = MAX_BIT_PRECISION - charPrecision * BITS_PER_CHAR
    val bits = ((ord() + steps) shl insignificantBits) or charPrecision.toLong()
    return Geohash(LatLonBits(bits))
}

/**
 * Steps backward n times in the geohash Z-order at this geohash's precision
 */
operator fun Geohash.minus(steps: Int): Geohash {
    return this - steps.toLong()
}

/**
 * Steps backward n times in the geohash Z-order at this geohash's precision
 */
operator fun Geohash.minus(steps: Long): Geohash {
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
@JsName("stepsBetween")
@JvmName("stepsBetween")
infix fun Geohash.stepsTo(otherGeohash: Geohash): Long {
    require(charPrecision == otherGeohash.charPrecision) { "Geohashes must be of the same precision to compare steps between" }
    return otherGeohash.ord() - this.ord()
}