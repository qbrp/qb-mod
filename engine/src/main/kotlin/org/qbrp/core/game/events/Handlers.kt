package org.qbrp.core.game.events

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import org.qbrp.core.groups.GroupSelection
import org.qbrp.core.regions.RegionSelection
import org.qbrp.system.utils.time.TimerUpdater
import org.qbrp.core.visual.VisualDataStorage
import org.qbrp.engine.Engine

object Handlers {

    fun registerServerEvents() {
        ServerPlayConnectionEvents.JOIN.register { handler, sender, server ->
            VisualDataStorage.loadPlayer(player = handler.player)
            Engine.chatModule.API.sendDataToJoinedPlayer(handler.player)
            Engine.spectatorsModule.API.setRespawnSpectator(handler.player)
        }
        ServerPlayConnectionEvents.DISCONNECT.register { handler, server ->
            Engine.spectatorsModule.API.cachePlayerGamemode(handler.player)
            VisualDataStorage.unloadPlayer(player = handler.player,)
        }
        UseBlockCallback.EVENT.register(UseBlockCallback { player, world, hand, hitResult ->
            RegionSelection.handleInteraction(player as ServerPlayerEntity, hand, hitResult)
            ActionResult.PASS
        })
        UseEntityCallback.EVENT.register { player, world, hand, entity, hitResult ->
            if (entity is ServerPlayerEntity) GroupSelection.handleInteraction(player as ServerPlayerEntity, entity)
            ActionResult.PASS
        }
    }

    fun registerBaseEvents() {
        ServerTickEvents.END_WORLD_TICK.register { server ->
            TimerUpdater.update()
        }
    }

}