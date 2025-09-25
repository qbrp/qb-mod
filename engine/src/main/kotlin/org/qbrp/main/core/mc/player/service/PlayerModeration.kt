package org.qbrp.main.core.mc.player.service

import org.qbrp.main.core.mc.player.dataformats.MinecraftPlayerId
import org.qbrp.main.engine.players.model.networking.DisconnectReason

interface PlayerModeration {
    fun disconnect(playerId: MinecraftPlayerId, reason: DisconnectReason)
}