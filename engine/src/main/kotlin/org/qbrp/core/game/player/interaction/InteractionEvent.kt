package org.qbrp.core.game.player.interaction

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.world.GameMode
import org.qbrp.core.game.player.ServerPlayerSession

fun interface InteractionEvent {
    fun onInteraction(player: ServerPlayerSession, intent: Intent)
}