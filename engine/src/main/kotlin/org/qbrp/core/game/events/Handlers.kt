package org.qbrp.core.game.events

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import org.qbrp.core.regions.RegionSelectionProcessor
import org.qbrp.system.utils.time.TimerUpdater
import org.qbrp.core.visual.VisualDataStorage
import org.qbrp.engine.Engine
import kotlin.math.E

object Handlers {

    fun registerServerEvents() {
        ServerPlayConnectionEvents.JOIN.register { handler, sender, server ->
            VisualDataStorage.loadPlayer(player = handler.player)
            Engine.chatModule.API.sendDataToJoinedPlayer(handler.player)
            Engine.spectatorsModule.API.setRespawnSpectator(handler.player)
        }
        ServerPlayConnectionEvents.DISCONNECT.register { handler, server ->
            VisualDataStorage.unloadPlayer(player = handler.player,)
        }
        UseBlockCallback.EVENT.register(UseBlockCallback { player, world, hand, hitResult ->
            RegionSelectionProcessor.handleInteraction(player as ServerPlayerEntity, hand, hitResult)
            ActionResult.PASS
        })
    }

    fun registerBaseEvents() {
        ServerTickEvents.END_WORLD_TICK.register { server ->
            TimerUpdater.update()
        }
    }

}