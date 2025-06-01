package org.qbrp.main.core.game

import net.minecraft.server.world.ServerWorld
import org.koin.core.component.get
import org.qbrp.main.core.assets.prefabs.Prefab
import org.qbrp.main.core.game.lifecycle.LifecycleManager
import org.qbrp.main.core.game.model.Stateful
import org.qbrp.main.core.game.model.components.Behaviour
import org.qbrp.main.core.game.model.components.Component
import org.qbrp.main.core.game.model.components.test.TestInvoke
import org.qbrp.main.core.game.model.components.test.TestPrint
import org.qbrp.main.core.game.model.objects.BaseObject
import org.qbrp.main.core.game.model.objects.TestObject
import org.qbrp.main.core.game.model.storage.GlobalStorage
import org.qbrp.main.core.game.model.storage.Storage
import org.qbrp.main.core.game.loop.Tick
import org.qbrp.main.core.game.prefabs.RuntimePrefabStorage
import org.qbrp.main.core.game.loop.GameTicker
import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.ModInitializedEvent
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.GameModule
import org.qbrp.main.core.modules.LoadPriority
import org.qbrp.main.core.modules.QbModule
import org.qbrp.main.core.storage.StorageAPI

@Autoload(LoadPriority.HIGHEST)
class GameEngine : QbModule("game-engine"), GameAPI {
    override fun onLoad() {
        val registry = getLocal<ComponentsRegistry>()
        registry.register(TestPrint::class.java)
        registry.register(TestInvoke::class.java)

        ModInitializedEvent.EVENT.register() {
            Engine.modules.forEach { module ->
                if (module is GameModule) {
                    module.registerComponents(registry)
                }
            }
            getLocal<GameTicker>().startTicking()
        }
    }

    fun test() {
        val lifecycle = get<LifecycleManager<BaseObject>>()
        TestObject(lifecycle).apply {
            script()
            save()
        }
    }

    override fun getKoinModule() = innerWithApi({
        single<GameAPI> { this@GameEngine }
        single { RuntimePrefabStorage() }
    }, {
        scoped { ComponentsRegistry() }
        scoped<Storage<Long, BaseObject>> { GlobalStorage() }
        scoped { get<StorageAPI>().getTable("test") }
        scoped { GameTicker() }
    })


    override fun addTickTask(tickable: Tick<Unit>) {
        getLocal<GameTicker>().addTickTask(tickable)
    }

    override fun addWorldTickTask(tickable: Tick<ServerWorld>) {
        getLocal<GameTicker>().addWorldTickTask(tickable)
    }

    override fun <T : BaseObject> instantiate(
        obj: T,
        prefab: Prefab.Tag
    ): T {
        prefab.mergeAndPut(obj as Stateful)
        obj.lifecycle.onCreated(obj)
        return obj
    }

    override fun enableComponent(
        component: Component,
        storage: Storage<Long, *>
    ) {
        storage.getAll().forEach {
            (it.state.getComponentByName(getLocal<ComponentsRegistry>().getComponentName(component)) as? Behaviour)?.enable()
        }
    }

    override fun disableComponent(
        component: Component,
        storage: Storage<Long, *>
    ) {
        storage.getAll().forEach {
            (it.state.getComponentByName(getLocal<ComponentsRegistry>().getComponentName(component)) as? Behaviour)?.disable()
        }
    }
}