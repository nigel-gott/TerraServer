package com.nigelgott.terra.server

import com.nigelgott.terra.server.terrain.TerrainGenerator
import com.nigelgott.terra.server.util.FloatPoint
import org.slf4j.LoggerFactory
import java.net.ServerSocket
import java.util.*
import java.util.concurrent.Executors

fun main(args : Array<String>){
    val port = Integer.parseInt(args[0])

    val serverSocket = ServerSocket(port)

    val logger = LoggerFactory.getLogger("Main")
    logger.info("Listening on port $port")

    val executor = Executors.newCachedThreadPool()

    val worldState = initializeWorldState(2048 * 10)

    try {
        while(true){
            val clientSocket = serverSocket.accept()
            clientSocket.tcpNoDelay = true
            try {
                logger.info("New connection opened: $clientSocket")
                executor.submit(RequestHandler(worldState, clientSocket))
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

private fun initializeWorldState(terrainSize: Int): WorldState {
    val terrain = TerrainGenerator(terrainSize).generate()
    val players = HashMap<String, Player>()
    players.put("Nigel", Player(FloatPoint(10.0f, 10.0f)))
    val worldState = WorldState(terrain, players)
    return worldState
}

