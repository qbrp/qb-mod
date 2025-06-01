package org.qbrp.main.core.game.loop

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.server.world.ServerWorld

class GameTicker {
    val tickables: MutableList<Tick<Unit>> = mutableListOf()
    val worldTickables: MutableList<Tick<ServerWorld>> = mutableListOf()

    fun addTickTask(tickable: Tick<Unit>) {
        tickables.add(tickable)
    }

    fun addWorldTickTask(tickable: Tick<ServerWorld>) {
        worldTickables.add(tickable)
    }

    fun startTicking() {
        ServerTickEvents.END_SERVER_TICK.register {
            tickables.forEach { tickable ->
                tickable.tick(Unit)
            }
        }
        ServerTickEvents.END_WORLD_TICK.register {
            worldTickables.forEach { tickable ->
                tickable.tick(it)
            }
        }
    }

}