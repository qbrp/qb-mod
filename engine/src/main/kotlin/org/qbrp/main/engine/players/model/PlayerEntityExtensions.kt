package org.qbrp.main.engine.players.model

import net.minecraft.server.network.ServerPlayerEntity
import org.koin.core.context.GlobalContext
import org.qbrp.main.core.fabricmc.player.MinecraftPlayerId
import org.qbrp.main.core.fabricmc.player.Username
import org.qbrp.main.engine.players.model.service.ServerPlayerStorage
import org.qbrp.main.core.util.PlayerId

class PlayerProfile(val id: PlayerId, val username: Username, val minecraftPlayerId: MinecraftPlayerId)

fun ServerPlayerEntity.engine(): ServerPlayerObject {
    return Util.storage.getByPlayer(this)
}

fun ServerPlayerEntity.engineOrNull(): ServerPlayerObject? {
    return Util.storage.getByPlayerOrNull(this)
}

fun ServerPlayerEntity.sessionOrNull(): ServerPlayerObject? {
    return Util.storage.getByPlayerOrNull(this)
}

object Util {
    val storage = GlobalContext.get().get<ServerPlayerStorage>()
}