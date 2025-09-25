package org.qbrp.main.core.mc.player.service

import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.core.mc.player.dataformats.MinecraftPlayerId
import org.qbrp.main.core.mc.player.model.PlayerReference

interface MinecraftPlayerManager {
    fun getById(id: MinecraftPlayerId): ServerPlayerEntity?
    fun sendTabPlayerListPacket(id: MinecraftPlayerId)
    val players: Collection<PlayerReference>
}