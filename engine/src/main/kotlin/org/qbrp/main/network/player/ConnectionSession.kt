package org.qbrp.main.network.player

import org.qbrp.main.core.fabricmc.player.MinecraftPlayerId
import org.qbrp.main.core.fabricmc.player.PlayerReference
import org.qbrp.main.core.fabricmc.player.Username
import org.qbrp.main.core.util.PlayerId
import org.qbrp.main.network.transport.ServerNetworkContext
import java.util.UUID

data class ConnectionSession(
    val token: UUID,
    val username: Username,
    val tempId: MinecraftPlayerId,
    val isOp: Boolean
) : PlayerReference {
    override val uuid: MinecraftPlayerId
        get() = tempId
}

context(ctx: ServerNetworkContext)
fun ConnectionSession.disconnect(reason: DisconnectReason) {
    ctx.serverDependencies.connectionManager.disconnect(this, reason)
}