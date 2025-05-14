package org.qbrp.engine.game

import jdk.jfr.internal.LongMap
import net.minecraft.server.world.ServerWorld
import org.koin.core.component.get
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.qbrp.core.EngineInitializedEvent
import org.qbrp.core.assets.prefabs.Prefab
import org.qbrp.core.game.ComponentsRegistry
import org.qbrp.core.game.database.ObjectDatabaseService
import org.qbrp.core.game.lifecycle.LifecycleManager
import org.qbrp.core.game.model.Stateful
import org.qbrp.core.game.model.components.Behaviour
import org.qbrp.core.game.model.components.Component
import org.qbrp.core.game.model.components.test.TestInvoke
import org.qbrp.core.game.model.components.test.TestPrint
import org.qbrp.core.game.model.objects.BaseEntity
import org.qbrp.core.game.model.objects.BaseObject
import org.qbrp.core.game.model.objects.TestObject
import org.qbrp.core.game.model.storage.GlobalStorage
import org.qbrp.core.game.model.storage.Storage
import org.qbrp.core.game.model.tick.Tick
import org.qbrp.core.game.prefabs.RuntimePrefab
import org.qbrp.core.game.prefabs.PrefabField
import org.qbrp.core.game.prefabs.RuntimePrefabStorage
import org.qbrp.core.game.serialization.ObjectJsonField
import org.qbrp.core.mc.player.model.PlayerPrefab
import org.qbrp.core.resources.data.config.ServerConfigData
import org.qbrp.engine.Engine
import org.qbrp.engine.game.loop.GameTicker
import org.qbrp.engine.players.characters.appearance.Appearance
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.GameModule
import org.qbrp.system.modules.QbModule

@Autoload(10)
class GameEngine : QbModule("game-engine"), GameAPI {
    override fun getAPI(): GameAPI = this

    override fun onLoad() {
        val registry = get<ComponentsRegistry>()
        val prefabsStorage = get<RuntimePrefabStorage>()
        prefabsStorage.addPrefab(PlayerPrefab())
        registry.register(TestPrint::class.java)
        registry.register(TestInvoke::class.java)
        registry.register(Appearance::class.java)
        getPlayerPrefab().components += PrefabField { Appearance() }
        //test()
        EngineInitializedEvent.EVENT.register() {
            Engine.moduleManager.modules.forEach { module ->
                if (module is GameModule) {
                    module.registerComponents(registry)
                }
            }
            get<GameTicker>().startTicking()
        }
    }

    fun test() {
        val lifecycle = get<LifecycleManager<BaseObject>>(qualifier = named("baseLifecycleManager"))
        TestObject(lifecycle).apply {
            script()
            save()
        }
    }

    override fun getKoinModule() = module {
        single { ComponentsRegistry() }
        single { RuntimePrefabStorage() }
        single<Storage<Long, BaseObject>> { GlobalStorage() }
        single<ObjectDatabaseService>(qualifier = named("baseDatabaseService")) {
            ObjectDatabaseService(get<ServerConfigData>().databases.nodeUri, "global").apply {
                connect()
            }
        }
        single { GameTicker() }
    }

    override fun getPlayerPrefab(): RuntimePrefab.Tag {
        return get<RuntimePrefabStorage>().getPrefabTag("load", "player")!!
    }

    override fun addTickTask(tickable: Tick<Unit>) {
        get<GameTicker>().addTickTask(tickable)
    }

    override fun addWorldTickTask(tickable: Tick<ServerWorld>) {
        get<GameTicker>().addWorldTickTask(tickable)
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
            (it.state.getComponentByName(get<ComponentsRegistry>().getComponentName(component)) as? Behaviour)?.enable()
        }
    }

    override fun disableComponent(
        component: Component,
        storage: Storage<Long, *>
    ) {
        storage.getAll().forEach {
            (it.state.getComponentByName(get<ComponentsRegistry>().getComponentName(component)) as? Behaviour)?.disable()
        }
    }
}