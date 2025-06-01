package org.qbrp.main.core.mc.player

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.world.GameMode
import org.qbrp.main.core.game.model.storage.Storage
import org.qbrp.main.core.game.prefabs.RuntimePrefab

interface PlayersAPI {
    fun getPlayerSession(name: String): PlayerObject?
    fun getPlayerSession(player: ServerPlayerEntity): PlayerObject
    fun getPlayers(): Collection<PlayerObject>
    val storage: Storage<Long, PlayerObject>
}