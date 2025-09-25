package org.qbrp.main.core.networking.require

import net.minecraft.network.PacketByteBuf
import org.qbrp.main.core.mc.server.context.ServerContext
import org.qbrp.main.core.networking.NetworkingModule
import org.qbrp.main.core.networking.PacketListener
import org.qbrp.main.core.networking.ServerNetworkContext as ServerNetworkingContext

class RequireManager: PacketListener {
    private val requests: MutableMap<RequestId, PacketRequire> = mutableMapOf()

    fun onPacket(buf: PacketByteBuf, context: ServerNetworkingContext) {
        val requestId = RequestId(buf.readInt())
        requests[requestId]?.complete(buf, context)
    }

    context(ctx: ServerContext)
    fun sendRequest(require: PacketRequire) {
        requests[require.requestId] = require
        ctx.packetSender.send(
            RequirePacket(require.requireId, require.requestId),
            NetworkingModule.REQUIRE_CHANNEL,
            require.player
        )
    }
}