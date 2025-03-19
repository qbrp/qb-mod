package org.qbrp.core.game.events

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import org.qbrp.core.game.player.PlayerManager
import org.qbrp.core.game.player.registration.PlayerRegistrationCallback
import org.qbrp.core.groups.GroupSelection
import org.qbrp.core.regions.RegionSelection
import org.qbrp.system.utils.time.TimerUpdater
import org.qbrp.core.visual.VisualDataStorage
import org.qbrp.engine.Engine
import org.qbrp.system.VersionChecker
import org.qbrp.system.networking.ServerInformationComposer

object Handlers {

    fun registerServerEvents() {
        ServerPlayConnectionEvents.INIT.register() { handler, server ->
            VersionChecker.addPlayerTask(handler.player)
        }
        ServerPlayConnectionEvents.JOIN.register { handler, sender, server ->
            Engine.moduleManager.sendModuleInformation(handler.player)
            ServerInformationComposer.send(handler.player)
            PlayerManager.handleConnected(handler.player)
        }
        PlayerRegistrationCallback.EVENT.register { session, _ ->
            VisualDataStorage.loadPlayer(player = session.entity)
        }
        ServerPlayConnectionEvents.DISCONNECT.register { handler, server ->
            VisualDataStorage.unloadPlayer(player = handler.player,)
            PlayerManager.handleDisconnected(handler.player)
        }
        UseBlockCallback.EVENT.register(UseBlockCallback { player, world, hand, hitResult ->
            RegionSelection.handleInteraction(player as ServerPlayerEntity, hand, hitResult)
            ActionResult.PASS
        })
        UseEntityCallback.EVENT.register { player, world, hand, entity, hitResult ->
            if (entity is ServerPlayerEntity) GroupSelection.handleInteraction(player as ServerPlayerEntity, entity)
            ActionResult.PASS
        }
        ServerTickEvents.END_WORLD_TICK.register { server ->
            PlayerManager.playersList.forEach { it.handler.handleTick() }
        }
    }


    fun registerBaseEvents() {
        ServerTickEvents.END_WORLD_TICK.register { server ->
            TimerUpdater.update()
        }
    }

}