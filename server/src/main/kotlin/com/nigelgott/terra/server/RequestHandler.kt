package com.nigelgott.terra.server

import com.nigelgott.terra.protobufs.Heightmap
import com.nigelgott.terra.protobufs.Request
import com.nigelgott.terra.protobufs.Response
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



        var columnOffsetMin = -1
        var columnOffsetMax = 1
        var rowOffsetMin = -1
        var rowOffsetMax = 1

        if(player.chunk.x == 0){
            rowOffsetMin = 0
        }
        if(player.chunk.x == worldState.NUM_CHUNKS){
            rowOffsetMax = 0
        }
        if(player.chunk.y == 0) {
            columnOffsetMin = 0
        }
        if(player.chunk.y == worldState.NUM_CHUNKS){
            columnOffsetMax = 0;
        }

        val numResponses = (columnOffsetMax - columnOffsetMin + 1) * (rowOffsetMax - rowOffsetMin + 1)
        Response.ResponseMessage
                .newBuilder()
                .setType(Response.ResponseMessage.ResponseType.TERRAIN)
                .setNumOfResponses(numResponses)
                .build()
                .writeDelimitedTo(clientSocket.outputStream)

        for (columnOffset in columnOffsetMin..columnOffsetMax) {
            for (rowOffset in rowOffsetMin..rowOffsetMax) {

                val chunk = worldState.chunks[columnOffset + player.chunk.y][rowOffset + player.chunk.x]

                val heightMapMessageBuilder = Heightmap.HeightMapMessage.newBuilder()
                        .setX(chunk.x)
                        .setY(chunk.y)

                for (row in chunk.heightmap) {
                    for (height in row) {
                        heightMapMessageBuilder.addHeight(height.toInt())
                    }
                }

                val heightMapMessage = heightMapMessageBuilder.build()

                logger.info("Sending chunk at (${chunk.x},${chunk.y}) with ${heightMapMessage.heightCount} heights to $clientSocket")

                heightMapMessage.writeDelimitedTo(clientSocket.outputStream)
            }
        }


    }


}