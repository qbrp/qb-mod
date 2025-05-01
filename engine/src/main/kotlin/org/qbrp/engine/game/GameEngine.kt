package org.qbrp.engine.game

import com.mojang.datafixers.kinds.App
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.server.world.ServerWorld
import org.koin.core.component.get
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.qbrp.core.EngineInitializedEvent
import org.qbrp.core.ServerCore
import org.qbrp.core.game.ComponentsRegistry
import org.qbrp.core.game.database.ObjectDatabaseService
import org.qbrp.core.game.lifecycle.LifecycleManager
import org.qbrp.core.game.model.components.test.TestInvoke
import org.qbrp.core.game.model.components.test.TestPrint
import org.qbrp.core.game.model.objects.BaseObject
import org.qbrp.core.game.model.objects.TestObject
import org.qbrp.core.game.model.storage.GlobalStorage
import org.qbrp.core.game.model.storage.Storage
import org.qbrp.core.game.model.tick.Tick
import org.qbrp.core.game.prefabs.Prefab
import org.qbrp.core.game.prefabs.PrefabField
import org.qbrp.core.game.prefabs.RuntimePrefabStorage
import org.qbrp.core.mc.player.model.PlayerPrefab
import org.qbrp.core.resources.data.config.ServerConfigData
import org.qbrp.engine.Engine
import org.qbrp.engine.game.loop.GameTicker
import org.qbrp.engine.players.characters.appearance.Appearance
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.GameModule
import org.qbrp.system.modules.ModuleAPI
import org.qbrp.system.modules.QbModule

@Autoload(10)
class GameEngine : QbModule("game-engine"), GameAPI {
    override fun getAPI(): GameAPI = this

    override fun load() {
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
        TestObject("test", lifecycle).apply {
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
//        single<LifecycleManager<BaseObject>>(qualifier = named("baseLifecycleManager")) {
//            LifecycleManager(
//                get<Storage<Long, BaseObject>>(),
//                get<ObjectDatabaseService>(qualifier = named("baseDatabaseService"))
//            )
//        }
        single { GameTicker() }
    }

    override fun getPlayerPrefab(): Prefab.Tag {
        return get<RuntimePrefabStorage>().getPrefabTag("load", "player")!!
    }

    override fun addTickTask(tickable: Tick<Unit>) {
        get<GameTicker>().addTickTask(tickable)
    }

    override fun addWorldTickTask(tickable: Tick<ServerWorld>) {
        get<GameTicker>().addWorldTickTask(tickable)
    }
}