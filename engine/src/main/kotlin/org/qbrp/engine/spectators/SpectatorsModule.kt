package org.qbrp.engine.spectators

import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.core.game.registry.CommandsRepository
import org.qbrp.engine.spectators.respawn.RespawnManager
import org.qbrp.engine.spectators.respawn.SpectatorCommands
import org.qbrp.system.utils.log.Loggers

class SpectatorsModule {
    private val logger = Loggers.get("spectatorsModule")
    private val spectatorRespawn = RespawnManager()
    init { CommandsRepository.add(SpectatorCommands()) }

    val API = Api()

    inner class Api {
        fun setRespawnSpectator(player: ServerPlayerEntity) = spectatorRespawn.giveSpectator(player)
        fun removeRespawnSpectator(player: ServerPlayerEntity) = spectatorRespawn.spawn(player)
        fun cachePlayerGamemode(player: ServerPlayerEntity) = spectatorRespawn.cachePlayerGameMode(player)
        fun removeRespawnMessage(player: ServerPlayerEntity) = spectatorRespawn.ignore(player)
    }

    init {
        logger.success("SpectatorsModule загружен")
    }
}