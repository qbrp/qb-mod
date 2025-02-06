package org.qbrp.engine.spectators

import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.core.game.registry.CommandsRepository
import org.qbrp.engine.spectators.respawn.RespawnManager
import org.qbrp.engine.spectators.respawn.SpawnCommand
import org.qbrp.system.utils.log.Loggers

class SpectatorsModule {
    private val logger = Loggers.get("spectatorsModule")
    private val spectatorRespawn = RespawnManager()
    private val commands = CommandsRepository.add(SpawnCommand(spectatorRespawn))

    val API = Api()

    inner class Api {
        fun setRespawnSpectator(player: ServerPlayerEntity) = spectatorRespawn.giveSpectator(player)
        fun removeRespawnSpectator(player: ServerPlayerEntity) = spectatorRespawn.spawn(player)
    }

    init {
        logger.success("SpectatorsModule загружен")
    }
}