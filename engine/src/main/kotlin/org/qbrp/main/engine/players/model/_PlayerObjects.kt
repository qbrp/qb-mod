package org.qbrp.main.engine.players.model

import net.minecraft.server.network.ServerPlayerEntity
import org.koin.core.context.GlobalContext
import org.qbrp.main.core.mc.player.model.PlayerSnapshot
import org.qbrp.main.core.networking.ServerNetworkContext
import org.qbrp.main.engine.players.model.service.ServerPlayerStorage

val PlayerSnapshot.engine get() = Util.storage.getByPlayer(this)

val ServerNetworkContext.enginePlayer get() = Util.storage.getByPlayer(this.player)

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