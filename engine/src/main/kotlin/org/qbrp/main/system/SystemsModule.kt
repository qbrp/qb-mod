package org.qbrp.main.system

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.main.core.components.ComponentRegistry
import org.qbrp.main.core.fabricmc.MinecraftAdapterSystem
import org.qbrp.main.core.fabricmc.WorldSystem
import org.qbrp.main.core.fabricmc.ActionBarSystem
import org.qbrp.main.core.fabricmc.server.MinecraftServerThread
import org.qbrp.main.core.fabricmc.server.LogicThread
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.engine.physics.player.AnyMinecraftEntities
import org.qbrp.main.engine.physics.player.MinecraftEntities
import org.qbrp.main.engine.physics.player.PlayerMovementSystem
import org.qbrp.main.engine.physics.player.PlayerPhysicsSystem
import org.qbrp.main.engine.physics.player.PlayerTeleportationSystem
import org.qbrp.main.engine.physics.player.ServerMinecraftEntities
import org.qbrp.main.engine.spectators.SpectatingSystem
import org.qbrp.main.engine.world.model.World
import org.qbrp.main.network.player.PlayerConnectHandler
import kotlin.math.sign

@Autoload
class SystemsModule : EngineModule("server-systems") {
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private val mainWorld: World by inject()
    private val engineSystem: EngineServerSystem by inject()
    private val playerConnectHandler: PlayerConnectHandler by inject()

    override fun getKoinModule(): Module = module {
        single<ServerMinecraftEntities> { ServerMinecraftEntitiesAccess(get(), get()) }
        single<AnyMinecraftEntities> { get<ServerMinecraftEntities>() }
        single<LogicThread> { MinecraftServerThread(get()) }

        single { MinecraftAdapterSystem(get(), get()) }
        single { SpectatingSystem(get()) }
        single { WorldSystem(get()) }
        single { ActionBarSystem() }
        single {
            EngineServerSystem(
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get()
            )
        }
        single { PlayerMovementSystem() }
        single { PlayerPhysicsSystem() }
        single { PlayerTeleportationSystem() }
    }

    override fun onEnable() = runBlocking {
        ServerTickEvents.START_WORLD_TICK.register {
            engineSystem.updateSystems()
        }
        ServerPlayConnectionEvents.DISCONNECT.register { handler, _, ->
            engineSystem.disconnect(handler.player)
        }
        ServerPlayConnectionEvents.JOIN.register { handler, sender, server ->
            engineSystem.connect(handler.player)
        }
    }

}