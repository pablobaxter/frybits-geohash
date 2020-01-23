package com.frybits.geohash

import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Frybits
 * Created by Pablo Baxter (Github: pablobaxter)
 */

//fun geohashesToEncompassCircle(
//    latitude: Double,
//    longitude: Double,
//    radius: Float,
//    minHashLength: Int = 1,
//    maxHashLength: Int = MAX_CHAR_PRECISION
//): List<Geohash> {
//
//    val includedHashes = mutableSetOf<Geohash>()
//    val hashRange = Geohash("0")..Geohash("z")
//    hashRange.forEach {
//
//        // Quick check to see if hash contains circle center
//        if (it.contains(latitude, longitude)) {
//            includedHashes.add(it)
//            return@forEach
//        }
//
//        includedHashes.
//    }
//}
//
//fun distanceBetweenLatLons(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
//    val earthRadius = 6371000
//
//    val dLatRadians = (lat2 - lat1).toRadians()
//    val dLonRadians = (lon2 - lon1).toRadians()
//    val a = sin(dLatRadians / 2).pow(2) + cos(lat1.toRadians()) *
//            cos(lat2.toRadians()) * sin(dLonRadians / 2).pow(2)
//    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
//    return earthRadius * c
//}
//
//internal fun Double.toRadians() = this / 180.0 * PI
