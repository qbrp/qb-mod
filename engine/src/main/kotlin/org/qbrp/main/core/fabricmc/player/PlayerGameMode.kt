package org.qbrp.main.core.fabricmc.player

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.world.GameMode
import org.qbrp.main.core.events.Event
import org.qbrp.main.core.events.EventType
import org.qbrp.main.core.events.allTrue
import org.qbrp.main.core.events.collect
import org.qbrp.main.engine.players.model.PlayerObject
import org.qbrp.main.engine.players.model.engine

object ServerPlayerInteractionManagerMixinAccess {
    fun emitChangeGameModeEvent(player: ServerPlayerEntity, gameMode: GameMode): Boolean {
        return PlayerGameMode.ResolverType
            .collect(
                PlayerGameMode(
                    gameMode,
                    player.engine()
                )
            )
            .allTrue()
    }
}


data class PlayerGameMode(val gameMode: GameMode, val player: PlayerObject) : Event {
    companion object {
        val ResolverType = EventType<PlayerGameMode, Boolean>()
    }
}
