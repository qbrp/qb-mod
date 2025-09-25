package core

import org.qbrp.main.core.mc.player.dataformats.MinecraftPlayerId
import org.qbrp.main.core.mc.player.service.PlayerModeration
import org.qbrp.main.engine.players.model.networking.DisconnectReason

class ExceptionConnectionHandler: PlayerModeration {
    override fun disconnect(
        playerId: MinecraftPlayerId,
        reason: DisconnectReason
    ) {
        throw RuntimeException("Player disconnected")
    }
}