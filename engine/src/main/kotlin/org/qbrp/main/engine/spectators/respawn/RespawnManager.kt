package org.qbrp.main.engine.spectators.respawn

import org.qbrp.deprecated.resources.data.config.ServerConfigData
import org.qbrp.main.core.mc.actionbar.ActionBarAPI
import org.qbrp.main.core.mc.context.MinecraftContext
import org.qbrp.main.core.mc.player.dataformats.MinecraftPlayerId
import org.qbrp.main.core.mc.player.gamemode.MinecraftGameMode
import org.qbrp.main.core.mc.player.model.PlayerReference
import org.qbrp.main.core.mc.player.model.PlayerSnapshot
import org.qbrp.main.core.mc.player.isOp
import org.qbrp.main.core.mc.player.setGameMode
import org.qbrp.main.core.mc.text.emptyMcText
import org.qbrp.main.core.mc.text.miniMessage
import kotlin.collections.mutableMapOf

class RespawnManager(val config: ServerConfigData.Spectators, val actionBar: ActionBarAPI) {
    private val cachedGameModes = mutableMapOf<PlayerReference, MinecraftGameMode>()
    private val notSpawnPlayers = mutableMapOf<MinecraftPlayerId, PlayerReference>()

    context(ctx: MinecraftContext)
    private fun getGameMode(player: PlayerReference): MinecraftGameMode {
        return cachedGameModes[player] ?: if (player.isOp) MinecraftGameMode.CREATIVE else MinecraftGameMode.SURVIVAL
    }

    fun cachePlayerGameMode(player: PlayerSnapshot) {
        cachedGameModes[player] = player.serverState.gameMode
    }

    context(ctx: MinecraftContext)
    fun ignore(player: PlayerReference) {
        notSpawnPlayers.remove(player.uuid)?.let {
            actionBar.setActionBarStatus(it.uuid, emptyMcText())
        }
    }

    context(ctx: MinecraftContext)
    fun giveSpectator(player: PlayerReference) {
        if (!notSpawnPlayers.contains(player.uuid)) {
            notSpawnPlayers[player.uuid] = player
            player.setGameMode(MinecraftGameMode.SPECTATOR)
            actionBar.setActionBarStatus(player.uuid, miniMessage(config.formatTooltip))
        }
    }

    context(ctx: MinecraftContext)
    fun spawn(player: PlayerReference) {
        notSpawnPlayers.remove(player.uuid)?.let {
            it.setGameMode(getGameMode(player))
            actionBar.setActionBarStatus(player.uuid, emptyMcText())
        }
    }
}