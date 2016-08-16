package com.nigelgott.terra.server

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket

fun main(args : Array<String>){
    val port = Integer.parseInt(args[0])

    val serverSocket = ServerSocket(port)

    print("Listening on port $port")
    val clientSocket = serverSocket.accept()

    print("Connection recieved wtf mate")
    val out = PrintWriter(clientSocket.outputStream, true)

    val input = BufferedReader(InputStreamReader(clientSocket.inputStream))

    val message = input.readLine()
    print("RECIEVED $message")

    if(message == "Knock Knock"){
        out.println("Who is there?\n")
    } else {
        out.println("Sorry?\n")
    }
    serverSocket.close()
    clientSocket.close()
}