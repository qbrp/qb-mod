package org.qbrp.main.engine.spectators.respawn

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.world.GameMode
import org.qbrp.main.core.mc.player.events.PlayerChangeGameModeEvent
import org.qbrp.main.core.keybinds.ServerKeybindCallback
import org.qbrp.deprecated.resources.data.config.ServerConfigData
import org.qbrp.main.core.mc.actionbar.ActionBarAPI

class RespawnManager(val config: ServerConfigData.Spectators, val actionBar: ActionBarAPI) {
    private val cachedGameModes = mutableMapOf<ServerPlayerEntity, GameMode>()
    private val notSpawnPlayers: MutableMap<String, ServerPlayerEntity> = mutableMapOf()

    init {
        val event = ServerKeybindCallback.getOrCreateEvent("spectators_spawn")
        event.register { player ->
            spawn(player)
            ActionResult.SUCCESS
        }
        PlayerChangeGameModeEvent.EVENT.register() { player, gamemode ->
            notSpawnPlayers[player.name.string]?.let {
                if (gamemode != GameMode.SPECTATOR) {
                    ignore(player)
                }
            }
            ActionResult.PASS
        }
    }

    private fun getGameMode(player: ServerPlayerEntity): GameMode {
        return cachedGameModes[player] ?: if (player.hasPermissionLevel(4)) GameMode.CREATIVE else GameMode.SURVIVAL
    }

    fun cachePlayerGameMode(player: ServerPlayerEntity) {
        cachedGameModes[player] = player.interactionManager.gameMode
    }

    fun ignore(player: ServerPlayerEntity) {
        if (notSpawnPlayers.containsKey(player.name.string)) {
            notSpawnPlayers.remove(player.name.string)
            actionBar.setActionBarStatus(player, "")
        }
    }

    fun giveSpectator(player: ServerPlayerEntity) {
        if (!notSpawnPlayers.containsKey(player.name.string)) {
            notSpawnPlayers[player.name.string] = player
            player.changeGameMode(GameMode.SPECTATOR)
            actionBar.setActionBarStatus(player, config.formatTooltip)
        }
    }

    fun spawn(player: ServerPlayerEntity) {
        if (notSpawnPlayers.containsKey(player.name.string)) {
            notSpawnPlayers.remove(player.name.string)
            player.changeGameMode(getGameMode(player))
            actionBar.setActionBarStatus(player, "")
        }
    }
}