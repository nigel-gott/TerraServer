package com.nigelgott.terra.server.terrain

import com.nigelgott.terra.protobufs.Heightmap
import com.nigelgott.terra.server.Loggable
import com.nigelgott.terra.server.logger
import com.nigelgott.terra.server.util.FloatPoint
import com.nigelgott.terra.server.util.Point

class TerrainChunker(val heightmap: Array<ShortArray>, val chunkSize: Int) : Loggable {

    private val logger = logger()

    val numChunks = heightmap.size / chunkSize

    init {
        if (numChunks * chunkSize != heightmap.size) {
            throw IllegalStateException("Cannot split up heightmap with size ${heightmap.size} into squares of size $chunkSize")
        }
    }

    fun getSurroundingChunks(coord: FloatPoint): MutableList<Heightmap.HeightMapMessage> {
        val chunkCoord = coord.truncate(chunkSize)

        var columnOffsetMin = -1
        var columnOffsetMax = 1
        var rowOffsetMin = -1
        var rowOffsetMax = 1

        if (chunkCoord.x == 0) {
            rowOffsetMin = 0
        }
        if (chunkCoord.x == numChunks) {
            rowOffsetMax = 0
        }
        if (chunkCoord.y == 0) {
            columnOffsetMin = 0
        }
        if (chunkCoord.y == numChunks) {
            columnOffsetMax = 0
        }

        val chunks: MutableList<Heightmap.HeightMapMessage> = mutableListOf()
        for (columnOffset in columnOffsetMin..columnOffsetMax) {
            for (rowOffset in rowOffsetMin..rowOffsetMax) {
                val surroundingChunkCoord = Point(rowOffset, columnOffset) + chunkCoord
                logger.info("Extracting chunk for $surroundingChunkCoord")
                chunks.add(getChunk(surroundingChunkCoord))
            }
        }
        return chunks
    }

    private fun getChunk(chunkCoord: Point): Heightmap.HeightMapMessage {
        val heightMapMessageBuilder = Heightmap.HeightMapMessage.newBuilder()
                .setX(chunkCoord.x)
                .setY(chunkCoord.y)

        for (y in 0..chunkSize - 1) {
            for (x in 0..chunkSize - 1) {
                val height = heightmap[chunkCoord.y * chunkSize + y][chunkCoord.x * chunkSize + x].toInt()
                heightMapMessageBuilder.addHeight(height)
            }
        }

        return heightMapMessageBuilder.build()
    }

}