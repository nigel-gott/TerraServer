package com.nigelgott.terra.server.terrain

import com.nigelgott.terra.protobufs.Chunk
import com.nigelgott.terra.protobufs.Request
import com.nigelgott.terra.server.Loggable
import com.nigelgott.terra.server.logger
import com.nigelgott.terra.server.util.Point

class TerrainChunker(val heightmap: Array<ShortArray>, val chunkSize: Int) : Loggable {

    private val logger = logger()

    val numChunks = heightmap.size / chunkSize

    init {
        if (numChunks * chunkSize > heightmap.size) {
            throw IllegalStateException("Cannot split up heightmap with size ${heightmap.size} into squares of size $chunkSize")
        }
    }

    fun getChunks(chunkCoordsList: MutableList<Request.IntCoord>): MutableList<Chunk.ChunkMessage> {
        val chunks: MutableList<Chunk.ChunkMessage> = mutableListOf()
        for (requestedChunkCoord in chunkCoordsList) {
            val chunkCoord = Point(requestedChunkCoord.x, requestedChunkCoord.y)
            logger.info("Extracting chunk for $chunkCoord")
            chunks.add(getChunk(chunkCoord))
        }
        return chunks
    }

    private fun getChunk(chunkCoord: Point): Chunk.ChunkMessage {
        val chunkBuilder = Chunk.ChunkMessage.newBuilder()
                .setX(chunkCoord.x)
                .setY(chunkCoord.y)

        for (y in 0..chunkSize - 1) {
            for (x in 0..chunkSize - 1) {
                val height = heightmap[chunkCoord.y * chunkSize + y][chunkCoord.x * chunkSize + x].toInt()
                chunkBuilder.addHeights(height)
            }
        }

        return chunkBuilder.build()
    }

}