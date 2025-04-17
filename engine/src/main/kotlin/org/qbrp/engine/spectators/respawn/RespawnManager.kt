package org.qbrp.engine.spectators.respawn

import net.minecraft.command.argument.GameModeArgumentType.gameMode
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.world.GameMode
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.qbrp.core.game.player.events.PlayerChangeGameModeEvent
import org.qbrp.core.game.registry.CommandsRepository
import org.qbrp.core.keybinds.ServerKeybindCallback
import org.qbrp.core.resources.data.config.ServerConfigData
import org.qbrp.view.View

class RespawnManager: KoinComponent {
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
            View.vanillaHud.setActionBarStatus(player, "")
        }
    }

    fun giveSpectator(player: ServerPlayerEntity) {
        if (!notSpawnPlayers.containsKey(player.name.string)) {
            notSpawnPlayers[player.name.string] = player
            player.changeGameMode(GameMode.SPECTATOR)
            View.vanillaHud.setActionBarStatus(player, get<ServerConfigData>().spectators.formatTooltip)
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