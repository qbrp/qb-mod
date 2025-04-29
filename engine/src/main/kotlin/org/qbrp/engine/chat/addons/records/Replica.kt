package org.qbrp.engine.chat.addons.records

import org.qbrp.core.mc.player.PlayerManager

class Replica(author: String, message: String, val volume: Int): Line(
    PlayerManager.getPlayerSession(author)?.displayName!!, message) {
}