package org.qbrp.engine.spectators.respawn

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.world.GameMode
import org.qbrp.core.game.registry.CommandsRepository
import org.qbrp.view.View

class RespawnManager {
    private val cachedGameModes = mutableMapOf<ServerPlayerEntity, GameMode>()
    private val notSpawnPlayers: MutableMap<String, ServerPlayerEntity> = mutableMapOf()

    private fun getGameMode(player: ServerPlayerEntity): GameMode {
        return cachedGameModes[player] ?: if (player.hasPermissionLevel(4)) GameMode.CREATIVE else GameMode.SURVIVAL
    }

    fun cachePlayerGameMode(player: ServerPlayerEntity) {
        cachedGameModes[player] = player.interactionManager.gameMode
    }

    fun ignore(player: ServerPlayerEntity) {
        if (notSpawnPlayers.containsKey(player.name.string)) {
            notSpawnPlayers.remove(player.name.string)
            View.vanillaHud.setActionBarStatus(player, "")
        }
    }

    fun giveSpectator(player: ServerPlayerEntity) {
        if (!notSpawnPlayers.containsKey(player.name.string)) {
            notSpawnPlayers[player.name.string] = player
            player.changeGameMode(GameMode.SPECTATOR)
            View.vanillaHud.setActionBarStatus(player, "&6Выберите место появления и напишите /qbs")
        }
    }

    fun spawn(player: ServerPlayerEntity) {
        if (notSpawnPlayers.containsKey(player.name.string)) {
            notSpawnPlayers.remove(player.name.string)
            player.changeGameMode(getGameMode(player))
            View.vanillaHud.setActionBarStatus(player, "")
        }
    }
}