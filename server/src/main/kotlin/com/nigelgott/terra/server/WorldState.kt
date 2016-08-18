package com.nigelgott.terra.server

import java.util.*

class WorldState {

    val NUM_CHUNKS = 4

    val chunks = initChunks()

    val players = HashMap<String, Player>()

    private fun initChunks(): Array<Array<Chunk>> {
        return Array(NUM_CHUNKS, { column -> Array(NUM_CHUNKS, { row -> initChunk(column, row) }) })
    }

    private fun initChunk(column: Int, row: Int): Chunk {
        return Chunk(column, row)
    }


}

class Player(var chunk: Chunk, var x: Float, var y: Float) {


}

val noiseGen = OpenSimplexNoise()

class Chunk(val x: Int, val y: Int) : Loggable {

    val HEIGHT_MAP_SIZE = 2048;


    val heightmap = initHeightMap()

    private fun initHeightMap(): Array<ShortArray> {
        val heightmap = Array(HEIGHT_MAP_SIZE, { y ->
            ShortArray(HEIGHT_MAP_SIZE,
                    {
                        x ->
                        val noiseDouble = noiseGen.eval(x.toDouble() / 24, y.toDouble() / 24)
                        val absNoiseDouble = Math.abs(noiseDouble)
                        val scaleToFillShort =  Math.pow(2.0,16.0) / 2;
                        val scaled = absNoiseDouble * scaleToFillShort
                        val toShort = scaled.toShort()
                        if(toShort < 0){
                            logger().error("wtf generated $toShort from $scaled, $scaleToFillShort, $absNoiseDouble, $noiseDouble")
                        }
                        toShort
                    })
        })
        logger().info("Generated chunk for $x,$y")
        return heightmap
    }


}
