package com.frybits.geohash

import com.frybits.geohash.internal.encompassesCompletely
import com.frybits.geohash.internal.maxCharsToCover

/**
 * Frybits
 * Created by Pablo Baxter (Github: pablobaxter)
 */

sealed class GeohashQuery {

    abstract fun geohashes(): List<Geohash>
}

class BoxQuery(queryBox: BoundingBox) : GeohashQuery() {

    private val queryingHashes: Set<Geohash> = if (!queryBox.intersects180Meridian) {
        generateSearchHashes(queryBox)
    } else {
        val westQueryBox = BoundingBox(queryBox.southWestPoint, Coordinate(queryBox.northEastPoint.latitude, 180.0))
        val eastQueryBox =
            BoundingBox(Coordinate(queryBox.southEastPoint.latitude, -180.0), queryBox.northEastPoint)
        generateSearchHashes(westQueryBox) + generateSearchHashes(eastQueryBox)
    }

    override fun geohashes(): List<Geohash> = queryingHashes.toList()

    private fun generateSearchHashes(bbox: BoundingBox): Set<Geohash> {
        val maxCharsToCover = bbox.maxCharsToCover()
        val tempHashSet = mutableSetOf<Geohash>()
        if (maxCharsToCover == 0) { // This bbox covers the world
            // Get the global range
            val globalRange = Geohash("0")..Geohash("z")

            // Only add the geohashes that intersect our query box
            globalRange.filterTo(tempHashSet) { hash ->
                hash.boundingBox.intersects(bbox)
            }
        } else {
            Geohash(bbox.centerCoordinate, maxCharsToCover)
                .surroundingGeohashes()
                .filterTo(tempHashSet) { geohash -> geohash.boundingBox.intersects(bbox) }
        }

        val resultHashSet = mutableSetOf<Geohash>()
        tempHashSet.filterTo(resultHashSet) { hash ->
            bbox.encompassesCompletely(hash.boundingBox)
        }

        return tempHashSet.flatMapTo(resultHashSet) { parent ->
            parent.children().filter { hash -> hash.boundingBox.intersects(bbox) }
        }
    }
}

