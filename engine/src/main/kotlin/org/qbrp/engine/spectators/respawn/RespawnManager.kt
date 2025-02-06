package org.qbrp.engine.spectators.respawn

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.world.GameMode
import org.qbrp.core.game.registry.CommandsRepository
import org.qbrp.view.View

class RespawnManager {

    private val notSpawnPlayers: MutableMap<String, ServerPlayerEntity> = mutableMapOf()

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
            player.changeGameMode(GameMode.SURVIVAL)
            View.vanillaHud.setActionBarStatus(player, "")
        }
    }
}