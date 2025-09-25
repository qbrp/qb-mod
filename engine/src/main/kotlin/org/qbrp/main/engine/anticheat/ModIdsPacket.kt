package org.qbrp.main.engine.anticheat

import net.minecraft.network.PacketByteBuf
import org.qbrp.main.core.mc.context.MinecraftContext
import org.qbrp.main.core.mc.player.model.PlayerSnapshot
import org.qbrp.main.core.networking.Packet
import org.qbrp.main.core.networking.readStringList
import org.qbrp.main.core.networking.writeStringList

class ModIdsPacket(val mods: List<String>, val player: PlayerSnapshot? = null, val context: MinecraftContext? = null): Packet<ModListHandler> {
    constructor(buf: PacketByteBuf, player: PlayerSnapshot, ctx: MinecraftContext) : this(buf.readStringList(), player, ctx)
    override fun write(buf: PacketByteBuf) {
        buf.writeStringList(mods)
    }

    override fun apply(listener: ModListHandler) {
        with(context!!) { listener.onModIds(player!!, mods) }
    }
}