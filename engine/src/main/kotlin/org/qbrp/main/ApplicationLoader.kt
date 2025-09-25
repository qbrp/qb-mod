package org.qbrp.main

import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.qbrp.main.deprecated.resources.commands.ResourcesCommands
import org.qbrp.deprecated.resources.data.config.ConfigInitializationCallback
import org.qbrp.deprecated.resources.data.config.ServerConfigData
import org.qbrp.main.deprecated.resources.ServerResources
import org.qbrp.main.core.Core
import org.qbrp.main.core.Core.SERVER_NAME
import org.qbrp.main.core.events.emit
import org.qbrp.main.core.events.withEventContext
import org.qbrp.main.core.info.ServerInfoAPI
import org.qbrp.main.core.mc.commands.CommandsAPI
import org.qbrp.main.core.mc.registry.GameRegistries
import org.qbrp.main.core.mc.registry.ServerGameRegistries
import org.qbrp.main.core.utils.log.InformationMessage
import org.qbrp.main.core.versions.VersionsUtil
import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.ModLoadedEvent
import org.qbrp.main.mechanics.Mechanics

class ApplicationLoader : DedicatedServerModInitializer, KoinComponent {
    companion object {
        var initialized = false
        var shouldCrash: Boolean = false
    }
    val modulesCount: Int get() = Core.modulesCount + Engine.modulesCount

    override fun onInitializeServer() {
        val informationMessage = InformationMessage(
            "lain1wakura",
            VersionsUtil.getVersion().toString())

        startKoin {
            modules(
                module {
                    single { ServerResources.getConfig() }
                    single { informationMessage }
                    single<GameRegistries> { ServerGameRegistries() }
                    single { Core.context }
                }
            )
            Core.initialize()
            Core.get<GameRegistries>().enable()
            ServerLifecycleEvents.SERVER_STARTING.register { server ->
                loadKoinModules(module { single { server } })
                Engine.initialize()
                Mechanics.initialize()
                initialized = true

                withEventContext {
                    ModLoadedEvent().emit()
                    get<ServerInfoAPI>().COMPOSER.component(SERVER_NAME, Core.get<ServerConfigData>().serverName)
                    get<CommandsAPI>().add(ResourcesCommands())
                }
            }
            ServerLifecycleEvents.SERVER_STARTED.register { server ->
                ConfigInitializationCallback.EVENT.invoker().onConfigUpdated(ServerResources.getConfig())
                informationMessage.print(modulesCount)
            }
        }
    }
}