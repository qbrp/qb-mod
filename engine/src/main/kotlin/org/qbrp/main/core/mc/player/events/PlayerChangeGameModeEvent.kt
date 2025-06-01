package org.qbrp.main.core.mc.player.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.world.GameMode

fun interface PlayerChangeGameModeEvent {
    fun changeGameMode(player: ServerPlayerEntity, gameMode: GameMode): ActionResult

    companion object {
        val EVENT: Event<PlayerChangeGameModeEvent> = EventFactory.createArrayBacked(
            PlayerChangeGameModeEvent::class.java
        ) { listeners: Array<out PlayerChangeGameModeEvent> ->
            PlayerChangeGameModeEvent { player, gameMode ->
                for (listener in listeners) {
                    if (listener.changeGameMode(player, gameMode) != ActionResult.PASS) {
                        return@PlayerChangeGameModeEvent ActionResult.FAIL
                    }
                }
                ActionResult.PASS
            }
        }
    }
}