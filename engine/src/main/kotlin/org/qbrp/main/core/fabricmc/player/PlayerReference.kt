package org.qbrp.main.core.fabricmc.player

import net.minecraft.server.network.ServerPlayerEntity

interface PlayerReference {
    val uuid: MinecraftPlayerId
}

data class ServerPlayerEntityReference(override val uuid: MinecraftPlayerId): PlayerReference

fun ServerPlayerEntity.asReference() = ServerPlayerEntityReference(MinecraftPlayerId(uuid))

fun MinecraftPlayerId.asReference() = ServerPlayerEntityReference(this)