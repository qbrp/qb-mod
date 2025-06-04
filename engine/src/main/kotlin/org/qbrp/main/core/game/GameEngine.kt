package org.qbrp.main.core.game

import net.minecraft.server.world.ServerWorld
import org.qbrp.main.core.assets.prefabs.Prefab
import org.qbrp.main.core.game.lifecycle.Lifecycle
import org.qbrp.main.core.game.model.Stateful
import org.qbrp.main.core.game.model.components.test.TestInvoke
import org.qbrp.main.core.game.model.components.test.TestPrint
import org.qbrp.main.core.game.model.objects.BaseObject
import org.qbrp.main.core.game.model.objects.TestObject
import org.qbrp.main.core.game.storage.GlobalStorage
import org.qbrp.main.core.game.storage.Storage
import org.qbrp.main.core.game.loop.Tick
import org.qbrp.main.core.game.prefabs.RuntimePrefabStorage
import org.qbrp.main.core.game.loop.GameTicker
import org.qbrp.main.engine.ModInitializedEvent
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.LoadPriority
import org.qbrp.main.core.modules.QbModule
import org.qbrp.main.core.storage.StorageAPI

@Autoload(both = true, priority = LoadPriority.HIGHEST)
class GameEngine : QbModule("game-engine"), GameAPI {
    override fun onLoad() {
        val registry = getLocal<ComponentsRegistry>()
        registry.register(TestPrint::class.java)
        registry.register(TestInvoke::class.java)

        ModInitializedEvent.EVENT.register() {
            ComponentRegistryInitializationEvent.EVENT.invoker().onInitialized(registry)
            getLocal<GameTicker>().startTicking()
        }
    }

    fun test() {
        TestObject().apply {
            script()
        }
    }

    override fun getKoinModule() = innerWithApi({
        single<GameAPI> { this@GameEngine }
        single { RuntimePrefabStorage() }
        single { ComponentsRegistry() }
    }, {
        scoped<Storage<BaseObject>> { GlobalStorage() }
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
        prefab: Prefab.Tag,
        lifecycle: Lifecycle<T>
    ): T {
        prefab.mergeAndPut(obj as Stateful)
        lifecycle.onCreated(obj)
        return obj
    }
}