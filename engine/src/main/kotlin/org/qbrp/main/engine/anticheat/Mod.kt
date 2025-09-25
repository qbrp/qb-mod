package org.qbrp.main.engine.anticheat

import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.core.mc.context.MinecraftContext
import org.qbrp.main.core.mc.player.model.PlayerSnapshot

interface Mod {
    val id: String
    context(mc: MinecraftContext)
    fun ifFounded(player: PlayerSnapshot, notifier: OpNotificator)
}