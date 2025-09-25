package org.qbrp.main.core.networking

import org.qbrp.main.core.mc.context.MinecraftContext
import org.qbrp.main.engine.players.model.ServerPlayerObject

interface PacketListener {
    fun onPacket(p: Packet<PacketListener>) {
        p.apply(this)
    }
    context(ctx: MinecraftContext)
    fun onPacket(p: Packet<PacketListener>, player: ServerPlayerObject) {
        onPacket(p)
    }
}