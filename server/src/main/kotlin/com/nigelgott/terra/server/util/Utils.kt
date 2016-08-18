package com.nigelgott.terra.server.util

data class Point(val x: Int, val y: Int) {
    infix operator fun plus(other: Point): Point = Point(x + other.x, y + other.y)
}

data class FloatPoint(val x: Float, val y: Float) {
    fun truncate(divisor: Int): Point = Point((x / divisor).toInt(), (y / divisor).toInt())
}