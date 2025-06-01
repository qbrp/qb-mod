package org.qbrp.main.core.game

import net.minecraft.server.world.ServerWorld
import org.qbrp.main.core.assets.prefabs.Prefab
import org.qbrp.main.core.game.model.components.Component
import org.qbrp.main.core.game.model.objects.BaseObject
import org.qbrp.main.core.game.model.storage.Storage
import org.qbrp.main.core.game.loop.Tick
import org.qbrp.main.core.modules.ModuleAPI

interface GameAPI: ModuleAPI {
    fun addTickTask(tickable: Tick<Unit>)
    fun addWorldTickTask(tickable: Tick<ServerWorld>)
    fun <T: BaseObject> instantiate(obj: T, prefab: Prefab.Tag): T
    fun enableComponent(component: Component, storage: Storage<Long, *>)
    fun disableComponent(component: Component, storage: Storage<Long, *>)
}