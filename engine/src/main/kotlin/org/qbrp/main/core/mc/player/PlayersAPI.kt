package org.qbrp.main.core.mc.player

import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.core.game.storage.Storage

interface PlayersAPI {
    fun getPlayerSession(name: String): ServerPlayerObject?
    fun getPlayerSession(player: ServerPlayerEntity): ServerPlayerObject
    fun getPlayerSessionOrNull(player: ServerPlayerEntity): ServerPlayerObject?
    fun getPlayers(): Collection<ServerPlayerObject>
    val storage: Storage<ServerPlayerObject>
}