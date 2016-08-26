package com.nigelgott.terra.server

import com.nigelgott.terra.protobufs.Request
import com.nigelgott.terra.protobufs.Request.RequestMessage.RequestType.TERRAIN_CHUNKS
import com.nigelgott.terra.protobufs.Request.RequestMessage.RequestType.WORLD_STATE
import com.nigelgott.terra.protobufs.Response
import com.nigelgott.terra.server.terrain.TerrainChunker
import sun.plugin.dom.exception.InvalidStateException
import java.net.Socket

class RequestHandler(val worldState: WorldState, val clientSocket: Socket) : Runnable, Loggable {

    val logger = logger()

    override fun run() {
        logger.info("RequestHandler handling $clientSocket")
        try {
            while (!clientSocket.isClosed) {
                val requestMessage = Request.RequestMessage.parseDelimitedFrom(clientSocket.inputStream)
                logger.info("Received $requestMessage with type ${requestMessage.type}")
                val player = lookUpPlayer(requestMessage)
                when (requestMessage.type) {
                    WORLD_STATE -> returnWorldParams(player)
                    TERRAIN_CHUNKS -> returnChunks()
                    else -> throw IllegalStateException("Request Message $requestMessage from $clientSocket has an unrecognized value (${requestMessage.type}) out of possible enum values (${Request.RequestMessage.RequestType.values()})")
                }
            }
        } catch (e : Exception) {
            logger.error("Request handler for $clientSocket failed with $e")
            logger.error("Trace: ${e.stackTrace}")
        } finally {
            logger.info("Closing connection to $clientSocket")
            clientSocket.close()
        }
    }

    private fun lookUpPlayer(requestMessage: Request.RequestMessage): Player {
        return worldState.players[requestMessage.playerName] ?: throw InvalidStateException("No player found with name ${requestMessage.playerName}");
    }

    private fun returnWorldParams(player: Player) {
        val worldStateParamsMessage = Response.WorldState.newBuilder()
                .setWorldSize(worldState.terrain.size)
                .setPlayerLocation(
                        Request.FloatCoord.newBuilder()
                        .setX(player.coord.x)
                        .setY(player.coord.y))
                .build()
        logger.info("Returning world state of ${worldStateParamsMessage.playerLocation.x},${worldStateParamsMessage.playerLocation.y} size ${worldStateParamsMessage.worldSize}")
        worldStateParamsMessage.writeDelimitedTo(clientSocket.outputStream)
    }

    private fun returnChunks() {
        val chunkRequest = Request.ChunkRequest.parseDelimitedFrom(clientSocket.inputStream)

        logger.info("Handling chunk request with size ${chunkRequest.chunkSize} for coords :")
        chunkRequest.chunkCoordsList.forEach { logger.info("${it.x},${it.y}") }
        val chunks = TerrainChunker(worldState.terrain, chunkRequest.chunkSize).getChunks(chunkRequest.chunkCoordsList)
        chunks.forEach { it.writeDelimitedTo(clientSocket.outputStream) }
    }


}