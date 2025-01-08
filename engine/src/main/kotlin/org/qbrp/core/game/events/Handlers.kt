package org.qbrp.core.game.events

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import org.qbrp.core.ServerCore
import org.qbrp.system.utils.time.TimerUpdater
import org.qbrp.visual.VisualDataStorage

object Handlers {

    fun registerServerEvents() {
        ServerPlayConnectionEvents.JOIN.register { handler, sender, server ->
            VisualDataStorage.loadPlayer(player = handler.player,)
        }
        ServerPlayConnectionEvents.DISCONNECT.register { handler, server ->
            VisualDataStorage.unloadPlayer(player = handler.player,)
            ServerCore.plasmoAddon.sourceManager.removePlayer(handler.player)
        }
    }

    fun registerBaseEvents() {
        ServerTickEvents.END_WORLD_TICK.register { server ->
            TimerUpdater.update()
        }
    }

}