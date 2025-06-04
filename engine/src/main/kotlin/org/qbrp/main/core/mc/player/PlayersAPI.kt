package org.qbrp.main.core.mc.player

import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.core.game.storage.Storage

interface PlayersAPI {
    fun getPlayerSession(name: String): PlayerObject?
    fun getPlayerSession(player: ServerPlayerEntity): PlayerObject
    fun getPlayerSessionOrNull(player: ServerPlayerEntity): PlayerObject?
    fun getPlayers(): Collection<PlayerObject>
    val storage: Storage<PlayerObject>
}