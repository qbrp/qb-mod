package org.qbrp.main.core.networking.require

import net.minecraft.network.PacketByteBuf
import org.qbrp.main.core.game.IDGenerator
import org.qbrp.main.core.mc.player.model.PlayerReference
import org.qbrp.main.core.mc.server.context.ServerContext
import org.qbrp.main.core.networking.Packet
import org.qbrp.main.core.networking.ServerNetworkContext as ServerPacketContext
import kotlinx.coroutines.CompletableDeferred

context(ctx: ServerContext)
fun PlayerReference.requireSend(requireId: RequireId) {
    val requestId = RequestId(IDGenerator.nextId().toInt())
    PacketRequire(this, requireId, requestId).sendRequest()
}

context(ctx: ServerContext)
suspend fun PlayerReference.awaitRequire(requireId: RequireId): Pair<PacketByteBuf, ServerPacketContext> {
    val requestId = RequestId(IDGenerator.nextId().toInt())
    val packetRequire = PacketRequire(this, requireId, requestId)
    packetRequire.sendRequest()
    return packetRequire.result.await()
}

class PacketRequire(
    val player: PlayerReference,
    val requireId: RequireId,
    val requestId: RequestId
) {
    val result = CompletableDeferred<Pair<PacketByteBuf, ServerPacketContext>>()

    context(ctx: ServerContext)
    fun sendRequest() {
        ctx.postRequirePacketListener.sendRequest(this)
    }

    fun complete(buf: PacketByteBuf, context: ServerPacketContext) {
        result.complete(buf to context)
    }
}
