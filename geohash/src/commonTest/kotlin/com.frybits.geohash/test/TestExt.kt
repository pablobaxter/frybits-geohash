package com.frybits.geohash.test

import com.frybits.geohash.BITS_PER_CHAR
import com.frybits.geohash.Coordinate
import com.frybits.geohash.GEOHASH_CHARS
import com.frybits.geohash.Geohash
import com.frybits.geohash.MAX_CHAR_PRECISION
import kotlin.math.abs
import kotlin.math.pow
import kotlin.random.Random
import kotlin.test.assertTrue

/**
 * Frybits
 * Created by Pablo Baxter (Github: pablobaxter)
 */

const val REPEAT_TEST_COUNT = 2000

// For geohashes
const val TEST_GEOHASH_1 = "9wkrtyvvw3wd"
const val TEST_LAT_1 = 36.51264084
const val TEST_LON_1 = -106.26257842

const val TEST_GEOHASH_2 = "w18md"
const val TEST_LAT_2 = 9.426
const val TEST_LON_2 = 90.461

const val TEST_GEOHASH_3 = "000000z"
const val TEST_LAT_3 = -89.9952
const val TEST_LON_3 = -179.9897

// For lat/lon bits
const val TEST_BITS_GEOHASH_1 = "qm"
val TEST_COORD_1 = Coordinate(-14.1, 107.0)
const val TEST_CHAR_PRECISION_1 = 2
const val TEST_LAT_BITS_1 = 13L
const val TEST_LON_BITS_1 = 25L
const val TEST_COMBINED_BITS_1 = -5422333951354077184L + TEST_CHAR_PRECISION_1

const val TEST_BITS_GEOHASH_2 = "mgu3"
val TEST_COORD_2 = Coordinate(-23.64, 84.9)
const val TEST_CHAR_PRECISION_2 = 4
const val TEST_LAT_BITS_2 = 377L
const val TEST_LON_BITS_2 = 753L
const val TEST_COMBINED_BITS_2 = -7209084326955188224L + TEST_CHAR_PRECISION_2

const val TEST_BITS_GEOHASH_3 = TEST_GEOHASH_1
val TEST_COORD_3 = Coordinate(TEST_LAT_1, TEST_LON_1)
const val TEST_CHAR_PRECISION_3 = MAX_CHAR_PRECISION
const val TEST_LAT_BITS_3 = 754677298L
const val TEST_LON_BITS_3 = 219930426L
const val TEST_COMBINED_BITS_3 = 5703101922691578048L + TEST_CHAR_PRECISION_3

fun Random.latitude(): Double = nextDouble(-90.0, 90.0)

fun Random.longitude(): Double = nextDouble(-180.0, 180.0)

fun Random.precision(minCharPrecision: Int = 1, maxCharPrecision: Int = MAX_CHAR_PRECISION): Int {
    require(minCharPrecision >= 1 && maxCharPrecision <= MAX_CHAR_PRECISION)
    return nextInt(minCharPrecision, maxCharPrecision)
}

fun Random.geoHash(charPrecision: Int = Random.precision(), randomUppercase: Boolean = true): Geohash = Geohash(
    buildString {
        repeat(charPrecision) {
            append(GEOHASH_CHARS.random(this@geoHash).let { if (randomUppercase && Random.nextBoolean()) it.toUpperCase() else it })
        }
    })

fun Geohash.isLast(): Boolean {
    var thisOrd = ord()
    var consecutiveOnBits = 0
    while (thisOrd and 1 == 1L) {
        consecutiveOnBits++
        thisOrd = thisOrd ushr 1
    }
    return consecutiveOnBits == charPrecision * BITS_PER_CHAR
}

// This gives approximate lat/lon error given the precision
fun approxLatitudeError(precision: Int) = 90.0 / 2.0.pow(approxCoordinateBits(precision))
fun approxLongitudeError(precision: Int): Double {
    val bits = approxCoordinateBits(precision)
    // When precision is odd, there is one extra longitude bit, otherwise it has equal latitude and longitude bits
    return 180.0 / 2.0.pow(if (precision % 2 == 0) bits else bits + 1)
}

fun assertFloatEquals(expected: Float, actual: Float, epsilon: Float, message: String? = null) {
    assertTrue(message) { abs(actual - expected) < epsilon }
}

fun assertDoubleEquals(expected: Double, actual: Double, epsilon: Double, message: String? = null) {
    assertTrue(message) { abs(actual - expected) < epsilon }
}

// Gives number of bits used for either latitude or longitude
private fun approxCoordinateBits(precision: Int): Int = (precision * BITS_PER_CHAR / 2)
