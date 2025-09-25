package org.qbrp.client.core.networking.v2

import io.netty.buffer.Unpooled
import net.minecraft.network.PacketByteBuf
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.qbrp.main.core.networking.Packet
import org.qbrp.main.core.networking.require.RequireId

fun KoinComponent.onRequire(id: RequireId, handler: RequireHandler) {
    get<RequirePacketHandlerManager>().registerHandler(id, handler)
}

fun ClientRequireContext.postPacket(p: Packet<*>) {
    val buf = PacketByteBuf(Unpooled.buffer())
    p.write(buf)
    post(buf)
}