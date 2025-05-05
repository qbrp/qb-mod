package org.qbrp.engine.game

import net.minecraft.server.world.ServerWorld
import org.qbrp.core.assets.prefabs.Prefab
import org.qbrp.core.game.lifecycle.Lifecycle
import org.qbrp.core.game.lifecycle.LifecycleManager
import org.qbrp.core.game.model.Stateful
import org.qbrp.core.game.model.objects.BaseEntity
import org.qbrp.core.game.model.objects.BaseObject
import org.qbrp.core.game.model.tick.Tick
import org.qbrp.core.game.prefabs.RuntimePrefab
import org.qbrp.core.game.serialization.ObjectJsonField
import org.qbrp.system.modules.ModuleAPI

interface GameAPI: ModuleAPI {
    fun getPlayerPrefab(): RuntimePrefab.Tag
    fun addTickTask(tickable: Tick<Unit>)
    fun addWorldTickTask(tickable: Tick<ServerWorld>)
    fun <T: BaseObject> instantiate(obj: T, prefab: Prefab.Tag): T
}