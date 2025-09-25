package org.qbrp.main.core.mc.player.events

import org.qbrp.main.core.events.AbstractCancellableEvent
import org.qbrp.main.core.mc.context.MinecraftContext
import org.qbrp.main.core.mc.player.model.PlayerSnapshot
import org.qbrp.main.core.mc.player.disconnect
import org.qbrp.main.core.mc.player.service.MinecraftServerPlayerManager
import org.qbrp.main.core.mc.server.thread.ServerThread
import org.qbrp.main.engine.players.model.networking.DisconnectReason

data class AsyncMinecraftPlayerJoinEvent(
    val player: PlayerSnapshot,
    val serverThread: ServerThread,
    val playerManager: MinecraftServerPlayerManager
) : AbstractCancellableEvent() {
    context(ctx: MinecraftContext)
    fun disconnect(reason: DisconnectReason) {
        serverThread.execute {
            if (cancelled) throw IllegalStateException()
            player.disconnect(reason)
            cancel()
        }
    }

    fun sendTabPlayerListPacket() {
        playerManager.sendTabPlayerListPacket(player.uuid)
    }
}