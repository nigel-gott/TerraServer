package com.nigelgott.terra.server

import com.nigelgott.terra.protobufs.Heightmap
import com.nigelgott.terra.protobufs.Request
import java.net.Socket

class RequestHandler(val clientSocket: Socket) : Runnable {
    override fun run() {
        try {
            val requestMessage = Request.RequestMessage.parseFrom(clientSocket.inputStream)
            when (requestMessage.type) {
                Request.RequestMessage.RequestType.INITIAL_WORLD_STATE -> returnCurrentWorldState(clientSocket)
                else -> Unit
            }
        } finally {
            clientSocket.close()
        }
    }

    private fun returnCurrentWorldState(clientSocket: Socket) {
        val heightMapMessage = Heightmap.HeightMapMessage.newBuilder()
                .setX(0)
                .setY(0)
                .addHeight(0.2f)
                .addHeight(0.5f)
                .addHeight(0.9f)
                .addHeight(0.1f)
                .build()

        heightMapMessage.writeTo(clientSocket.outputStream)
    }


}