package org.qbrp.main.engine.anticheat

import net.minecraft.server.network.ServerPlayerEntity

interface Mod {
    val id: String
    fun ifFounded(player: ServerPlayerEntity)
}