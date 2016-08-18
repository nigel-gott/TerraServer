package com.nigelgott.terra.server

import com.nigelgott.terra.server.util.FloatPoint
import java.util.*

class WorldState(val terrain: Array<ShortArray>, val players : HashMap<String, Player>) {

}
class Player(var coord : FloatPoint) {
}

