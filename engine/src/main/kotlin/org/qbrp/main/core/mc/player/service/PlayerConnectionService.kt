package org.qbrp.main.core.mc.player.service

import org.qbrp.main.core.mc.player.dataformats.MinecraftPlayerId
import org.qbrp.main.core.mc.player.getEntity
import org.qbrp.main.core.mc.server.thread.ServerThread
import org.qbrp.main.engine.players.model.networking.DisconnectReason

class PlayerConnectionService(private val playerManager: MinecraftPlayerManager, private val serverThread: ServerThread): PlayerModeration {
    override fun disconnect(playerId: MinecraftPlayerId, reason: DisconnectReason) {
        serverThread.assertOnServerThread()
        playerId.getEntity(playerManager).networkHandler!!.disconnect(reason.text)
    }
}