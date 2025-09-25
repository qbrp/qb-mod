package org.qbrp.main.core.game

import net.minecraft.server.world.ServerWorld
import org.qbrp.main.core.game.loop.Tick
import org.qbrp.main.core.modules.ModuleAPI

interface GameTicker: ModuleAPI {
    fun addTickTask(tickable: Tick<Unit>)
    fun addWorldTickTask(tickable: Tick<ServerWorld>)
}