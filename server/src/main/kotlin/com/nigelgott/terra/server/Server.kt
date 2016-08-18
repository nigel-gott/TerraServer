package com.nigelgott.terra.server

import org.slf4j.LoggerFactory
import java.net.ServerSocket
import java.util.concurrent.Executors

fun main(args : Array<String>){
    val port = Integer.parseInt(args[0])

    val serverSocket = ServerSocket(port)

    val logger = LoggerFactory.getLogger("Main")
    logger.info("Listening on port $port")

    val executor = Executors.newCachedThreadPool()

    try {
        while(true){
            val clientSocket = serverSocket.accept()
            clientSocket.tcpNoDelay = true
            try {
                logger.info("New connection opened: $clientSocket")
                executor.submit(RequestHandler(clientSocket))
            } catch (e : Exception) {
                clientSocket.close()
                throw RuntimeException(e)
            }
        }
    } finally {
        executor.shutdown()
        serverSocket.close()
    }
}

