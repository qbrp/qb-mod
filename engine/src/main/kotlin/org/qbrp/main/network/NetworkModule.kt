package org.qbrp.main.network

import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.main.core.events.on
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.engine.ModLoadedEvent
import org.qbrp.main.network.input.runPlayerInputHandler
import org.qbrp.main.network.interaction.runInteractionNetworkSynchronization
import org.qbrp.main.network.inventory.ServerInventories
import org.qbrp.main.network.inventory.runServerInventoriesHandler
import org.qbrp.main.network.physics.runPhysicsNetworkHandler
import org.qbrp.main.network.player.AuthorizationHandler
import org.qbrp.main.network.player.PlayerConnectHandler
import org.qbrp.main.network.player.runKeybindActivationHandler
import org.qbrp.main.network.player.runModIdsHandler
import org.qbrp.main.network.player.runPlayerMovementHandler
import org.qbrp.main.network.player.runServerAuthorizationListeners
import org.qbrp.main.network.player.runServerCharacterSynchronizer
import org.qbrp.main.network.transport.AcknowledgeProgram
import org.qbrp.main.network.transport.ServerDependencies
import org.qbrp.main.network.transport.startListening

@Autoload(priority = Priorities.LOWEST - 1)
class NetworkModule : EngineModule("network") {
    override fun getKoinModule(): Module = module {
        single {
            ServerDependencies(
                get(),
                get(),
                get(),
                getKoin()
            )
        }

        single { AcknowledgeProgram() }
        single { ServerConnectionManager(get(), get()) }
        single { ServerPacketTransportProgram(get(), get(), get(), get()) }

        single {
            PlayerConnectHandler(
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
            )
        }
        single { AuthorizationHandler(get(), get(), get()) }

        single { NetworkSystems(get(), get()) }
    }

    private val transport: ServerPacketTransportProgram by inject()
    private val acknowledge: AcknowledgeProgram by inject()

    override fun onEnable() {
        on<ModLoadedEvent> { initialize() }
    }

    private fun initialize() {
        transport.runTransportThread()
        acknowledge.startListening()

        runServerAuthorizationListeners(get())
        runServerCharacterSynchronizer()
        runServerInventoriesHandler(ServerInventories(), get())
        runModIdsHandler()
        runKeybindActivationHandler()
        runPlayerMovementHandler(get())
        runInteractionNetworkSynchronization(get(), get(), get(), get())
        runPhysicsNetworkHandler()
        runPlayerInputHandler()
    }
}