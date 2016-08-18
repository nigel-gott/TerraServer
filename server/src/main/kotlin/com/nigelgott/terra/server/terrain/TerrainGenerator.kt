package com.nigelgott.terra.server.terrain

import com.nigelgott.terra.server.Loggable
import com.nigelgott.terra.server.OpenSimplexNoise
import com.nigelgott.terra.server.logger
import com.nigelgott.terra.server.util.Point


val noiseGen = OpenSimplexNoise()

class TerrainGenerator(val size: Int) : Loggable {

    val logger = logger()

    fun generate(): Array<ShortArray> {
        logger.info("Generating a terrain of size $size")
        val array = Array(size, { y ->
            ShortArray(size,
                    { x ->
                        generateHeightForCoord(Point(x, y))
                    })
        })
        logger.info("Finished generating terrain")
        return array
    }

    private fun generateHeightForCoord(coord: Point): Short {
        val noiseDouble = noiseGen.eval(coord.x.toDouble() / 24, coord.y.toDouble() / 24)
        val absNoiseDouble = Math.abs(noiseDouble)
        val scaleToFillShort = Math.pow(2.0, 16.0) / 2
        val scaled = absNoiseDouble * scaleToFillShort
        return scaled.toShort()
    }
}

