@file:JvmName("GeohashUtils")

package com.frybits.geohash

import com.frybits.geohash.internal.latLonBits
import kotlin.js.JsName
import kotlin.jvm.JvmName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmSynthetic

/**
 * Frybits
 * Created by Pablo Baxter (Github: pablobaxter)
 *
 * Set of extension functions for Geohash class
 */

/**
 * Get a [Geohash] from the given [Coordinate]
 *
 * @param precision Character precision of the geohash
 */
@JvmOverloads
@JsName("geohashFromCoordinate")
@JvmName("geohashFromCoordinate")
fun Coordinate.toGeohash(precision: Int = MAX_CHAR_PRECISION): Geohash = Geohash(latitude, longitude, precision)

/**
 * Get a [Geohash] from the given [BoundingBox]
 *
 * @param precision Character precision of the geohash
 */
@JvmOverloads
@JsName("geohashFromBoundingBox")
@JvmName("geohashFromBoundingBox")
fun BoundingBox.toGeohash(precision: Int = MAX_CHAR_PRECISION): Geohash = Geohash(centerCoordinate, precision)

/**
 * Get a [Geohash] from the given [String]
 */
@JsName("geohashFromString")
@JvmName("geohashFromString")
fun String.toGeohash(): Geohash = Geohash(this)

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
            latLonBits(
                latBits = latLonBits.latBits + 1,
                lonBits = latLonBits.lonBits,
                charPrecision = charPrecision
            )
        )
        Direction.NORTH_EAST -> Geohash(
            latLonBits(
                latBits = latLonBits.latBits + 1,
                lonBits = latLonBits.lonBits + 1,
                charPrecision = charPrecision
            )
        )
        Direction.EAST -> Geohash(
            latLonBits(
                latBits = latLonBits.latBits,
                lonBits = latLonBits.lonBits + 1,
                charPrecision = charPrecision
            )
        )
        Direction.SOUTH_EAST -> Geohash(
            latLonBits(
                latBits = latLonBits.latBits - 1,
                lonBits = latLonBits.lonBits + 1,
                charPrecision = charPrecision
            )
        )
        Direction.SOUTH -> Geohash(
            latLonBits(
                latBits = latLonBits.latBits - 1,
                lonBits = latLonBits.lonBits,
                charPrecision = charPrecision
            )
        )
        Direction.SOUTH_WEST -> Geohash(
            latLonBits(
                latBits = latLonBits.latBits - 1,
                lonBits = latLonBits.lonBits - 1,
                charPrecision = charPrecision
            )
        )
        Direction.WEST -> Geohash(
            latLonBits(
                latBits = latLonBits.latBits,
                lonBits = latLonBits.lonBits - 1,
                charPrecision = charPrecision
            )
        )
        Direction.NORTH_WEST -> Geohash(
            latLonBits(
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
 * Gets the [Geohash] containing this [Geohash]. Ex. '13bcd' has the parent '13bc'
 * Will return [null] if this [Geohash] has no parent (single character geohash)
 */
val Geohash.parent: Geohash?
    get() {
        return if (charPrecision == 1) {
            null
        } else {
            // This drops the last 5 bits of this geohash
            val newOrd = ord() ushr BITS_PER_CHAR

            // Left aligns the bits according to the new charPrecision (parent always has one less character)
            val newCombinedBits = newOrd shl (MAX_BIT_PRECISION - (charPrecision - 1) * BITS_PER_CHAR)

            // Add the new precision to the bits, and generate a new geohash object from it
            Geohash(latLonBits(newCombinedBits or (charPrecision - 1L)))
        }
    }

/**
 * Gets all the geohashes this geohash contains.
 *
 * @return [List] of geohashes this contains
 */
fun Geohash.children(): List<Geohash> {
    // We're already at cm accuracy, but if you're looking for something more accurate...
    // https://github.com/pablobaxter/frybits-geohash/blob/master/accuracy-message.png
    if (charPrecision == MAX_CHAR_PRECISION) return emptyList()

    // Get the ord for the first child hash
    val childStartOrd = ord() shl BITS_PER_CHAR

    // Get the ord for the last child hash
    val childEndOrd = childStartOrd + GEOHASH_CHARS.length - 1
    val insignificantBits = MAX_BIT_PRECISION - (charPrecision + 1) * BITS_PER_CHAR // Increase charPrecision
    val startGeohash = Geohash(latLonBits((childStartOrd shl insignificantBits) or (charPrecision + 1L)))
    val endGeohash = Geohash(latLonBits((childEndOrd shl insignificantBits) or (charPrecision + 1L)))

    // Generate all the hashes between the first and last geohash into a list
    return (startGeohash..endGeohash).toList()
}

/**
 * Steps to the next geohash in the Z-order (It's more like an N-order)
 * If the last geohash in the series is reached, this rolls back to the first
 *
 * @return Next [Geohash]
 */
@JsName("next")
@JvmName("next")
operator fun Geohash.inc(): Geohash {
    val insignificantBits = MAX_BIT_PRECISION - charPrecision * BITS_PER_CHAR
    val bits = ((ord() + 1) shl insignificantBits) or charPrecision.toLong()
    return Geohash(latLonBits(bits))
}

/**
 * Steps to the previous geohash in the Z-order (It's more like an N-order)
 * If the first geohash in the series is reached, this rolls to the last
 *
 * @return Previous [Geohash]
 */
@JsName("previous")
@JvmName("previous")
operator fun Geohash.dec(): Geohash {
    val insignificantBits = MAX_BIT_PRECISION - charPrecision * BITS_PER_CHAR
    val bits = ((ord() - 1) shl insignificantBits) or charPrecision.toLong()
    return Geohash(latLonBits(bits))
}

/**
 * Steps forward n times in the geohash Z-order at this geohash's precision
 */
@JvmSynthetic
operator fun Geohash.plus(steps: Int): Geohash {
    return this + steps.toLong()
}

/**
 * Steps forward n times in the geohash Z-order at this geohash's precision
 */
@JsName("stepForward")
@JvmName("stepForward")
operator fun Geohash.plus(steps: Long): Geohash {
    val insignificantBits = MAX_BIT_PRECISION - charPrecision * BITS_PER_CHAR
    val bits = ((ord() + steps) shl insignificantBits) or charPrecision.toLong()
    return Geohash(latLonBits(bits))
}

/**
 * Steps backward n times in the geohash Z-order at this geohash's precision
 */
@JvmSynthetic
operator fun Geohash.minus(steps: Int): Geohash {
    return this - steps.toLong()
}

/**
 * Steps backward n times in the geohash Z-order at this geohash's precision
 */
@JsName("stepBackward")
@JvmName("stepBackward")
operator fun Geohash.minus(steps: Long): Geohash {
    val insignificantBits = MAX_BIT_PRECISION - charPrecision * BITS_PER_CHAR
    val bits = ((ord() - steps) shl insignificantBits) or charPrecision.toLong()
    return Geohash(latLonBits(bits))
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
