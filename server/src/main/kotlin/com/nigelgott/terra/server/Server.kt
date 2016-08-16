package com.nigelgott.terra.server

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import com.nigelgott.terra.protobufs.Heightmap.HeightMapMessage
import com.nigelgott.terra.protobufs.Request
import java.util.concurrent.Executors

fun main(args : Array<String>){
    val port = Integer.parseInt(args[0])

    val serverSocket = ServerSocket(port)

    print("Listening on port $port")

    try {
        val executor = Executors.newCachedThreadPool()
        while(true){
            val clientSocket = serverSocket.accept()
            try {
                executor.submit(RequestHandler(clientSocket))
            } catch (e : Exception) {
                clientSocket.close();
            }
        }
    } finally {
        serverSocket.close()
    }
}