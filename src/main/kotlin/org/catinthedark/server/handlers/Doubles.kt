package org.catinthedark.server.handlers

import org.catinthedark.server.Handler
import org.catinthedark.server.Holder

@Handler
fun onDouble(msg: Double, h: Holder<GameContext>) {
    h.context.data += 1
    println("Double '$msg'. Holder: $h")
}