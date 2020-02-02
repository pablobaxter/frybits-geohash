package com.frybits.geohash

import com.frybits.geohash.internal.encompassesCompletely
import com.frybits.geohash.internal.maxCharsToCover

/**
 * Frybits
 * Created by Pablo Baxter (Github: pablobaxter)
 */

class BoxQuery(val queryBox: BoundingBox) : GeohashQuery {

    private val queryingHashes = mutableSetOf<Geohash>()

    init {
        val maxCharsCovering = queryBox.maxCharsToCover()
        val hashesToParse: List<Geohash> = if (maxCharsCovering == 0) {
            var g = Geohash("0")
            val tempHashSet = mutableSetOf<Geohash>()
            while (!tempHashSet.contains(g)) {
                val temp = g++
                if (temp.boundingBox.intersects(queryBox)) {
                    tempHashSet.add(temp)
                }
            }
            tempHashSet.toList()
        } else {
            Geohash(queryBox.centerCoordinate, queryBox.maxCharsToCover())
                .surroundingGeohashes()
                .filter { geohash -> geohash.boundingBox.intersects(queryBox) }
        }

        hashesToParse.filterTo(queryingHashes) { hash ->
            queryBox.encompassesCompletely(hash.boundingBox)
        }

        hashesToParse.flatMapTo(queryingHashes) { parent ->
            if (queryingHashes.contains(parent)) return@flatMapTo emptyList<Geohash>()
            return@flatMapTo parent.children().filter { geohash -> geohash.boundingBox.intersects(queryBox) }
        }
    }

    override fun geohashes(): List<Geohash> = queryingHashes.toList()
}
