package org.qbrp.main.core.game

import net.minecraft.server.world.ServerWorld
import org.qbrp.main.core.assets.prefabs.Prefab
import org.qbrp.main.core.game.lifecycle.Lifecycle
import org.qbrp.main.core.game.model.objects.BaseObject
import org.qbrp.main.core.game.loop.Tick
import org.qbrp.main.core.modules.ModuleAPI

interface GameAPI: ModuleAPI {
    fun addTickTask(tickable: Tick<Unit>)
    fun addWorldTickTask(tickable: Tick<ServerWorld>)
    fun <T: BaseObject> instantiate(obj: T, prefab: Prefab.Tag, lifecycle: Lifecycle<T>): T
}