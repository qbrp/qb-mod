package org.qbrp.main.core.mc.player.events

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.world.GameMode
import org.qbrp.main.core.Core
import org.qbrp.main.core.events.allTrue
import org.qbrp.main.core.events.collect
import org.qbrp.main.core.events.withEventContext
import org.qbrp.main.core.mc.player.MinecraftPlayersModule
import org.qbrp.main.core.mc.player.gamemode.MinecraftGameMode
import org.qbrp.main.engine.players.model.engine

object ServerPlayerInteractionManagerMixinAccess {
    private val module by lazy { Core.getModule<MinecraftPlayersModule>() }

    fun emitChangeGameModeEvent(player: ServerPlayerEntity, gameMode: GameMode): Boolean {
        return module.withEventContext {
            PlayerChangeGameModeEvent(
                MinecraftGameMode.from(gameMode),
                player.engine()
            )
                .collect<Boolean>()
                .allTrue()
        }
    }
}