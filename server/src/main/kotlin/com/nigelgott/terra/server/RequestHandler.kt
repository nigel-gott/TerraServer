package com.nigelgott.terra.server

import com.nigelgott.terra.protobufs.Heightmap
import com.nigelgott.terra.protobufs.Request
import java.net.Socket

class RequestHandler(val clientSocket: Socket) : Runnable, Loggable {

    val logger = logger()

    override fun run() {
        logger.info("RequestHandler handling $clientSocket")
        try {
            val requestMessage = Request.RequestMessage.parseDelimitedFrom(clientSocket.inputStream)
            when (requestMessage.type) {
                Request.RequestMessage.RequestType.INITIAL_WORLD_STATE -> returnCurrentWorldState()
                else -> logger.error("Request Message $requestMessage from $clientSocket has an unrecognized value (${requestMessage.type}) out of possible enum values (${Request.RequestMessage.RequestType.values()})")
            }
        } finally {
            logger.info("Closing connection to $clientSocket")
            clientSocket.close()
        }
    }

    private fun returnCurrentWorldState() {
        logger.info("Handling request for current world state")

        val heightMapMessageBuilder = Heightmap.HeightMapMessage.newBuilder()
                .setX(0)
                .setY(0)

        for(y in 0..2048){
            for(x in 0..2048){
                val height =  (x % 10)
                heightMapMessageBuilder.addHeight(height)
            }
        }

        val heightMapMessage = heightMapMessageBuilder.build()

        logger.info("Sending heightmap with ${heightMapMessage.heightCount} heights to $clientSocket")

        heightMapMessage.writeDelimitedTo(clientSocket.outputStream)
    }


}