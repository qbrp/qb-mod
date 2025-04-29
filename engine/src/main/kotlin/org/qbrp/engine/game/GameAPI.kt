package org.qbrp.engine.game

import net.minecraft.server.world.ServerWorld
import org.qbrp.core.game.model.tick.Tick
import org.qbrp.core.game.prefabs.Prefab
import org.qbrp.system.modules.ModuleAPI

interface GameAPI: ModuleAPI {
    fun getPlayerPrefab(): Prefab.Tag
    fun addTickTask(tickable: Tick<Unit>)
    fun addWorldTickTask(tickable: Tick<ServerWorld>)
}