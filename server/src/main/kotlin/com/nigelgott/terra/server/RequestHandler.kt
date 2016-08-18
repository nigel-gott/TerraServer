package com.nigelgott.terra.server

import com.nigelgott.terra.protobufs.Request
import com.nigelgott.terra.protobufs.Response
import com.nigelgott.terra.server.terrain.TerrainChunker
import java.net.Socket

class RequestHandler(val worldState: WorldState, val clientSocket: Socket) : Runnable, Loggable {

    val logger = logger()

    override fun run() {
        logger.info("RequestHandler handling $clientSocket")
        try {
            val requestMessage = Request.RequestMessage.parseDelimitedFrom(clientSocket.inputStream)
            when (requestMessage.type) {
                Request.RequestMessage.RequestType.INITIAL_WORLD_STATE -> returnCurrentWorldState(requestMessage.playerName)
                else -> logger.error("Request Message $requestMessage from $clientSocket has an unrecognized value (${requestMessage.type}) out of possible enum values (${Request.RequestMessage.RequestType.values()})")
            }
        } finally {
            logger.info("Closing connection to $clientSocket")
            clientSocket.close()
        }
    }

    private fun returnCurrentWorldState(playerName: String) {
        val player = worldState.players[playerName]
        if (player == null) {
            logger.error("No player found with name $playerName")
            return
        }

        logger.info("Handling request for current world state")

        val chunks = TerrainChunker(worldState.terrain, 2048).getSurroundingChunks(player.coord)

        Response.ResponseMessage
                .newBuilder()
                .setType(Response.ResponseMessage.ResponseType.TERRAIN)
                .setNumOfResponses(chunks.size)
                .build()
                .writeDelimitedTo(clientSocket.outputStream)

        chunks.forEach { chunk -> chunk.writeDelimitedTo(clientSocket.outputStream) }
    }


}