package com.nigelgott.terra.server

import org.slf4j.Logger
import org.slf4j.LoggerFactory


interface Loggable {}

fun Loggable.logger(): Logger {
    return LoggerFactory.getLogger(this.javaClass.name)
}