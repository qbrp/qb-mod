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
import org.qbrp.main.deprecated.resources.ServerResources
import org.qbrp.main.core.Core
import org.qbrp.main.core.events.emit
import org.qbrp.main.core.fabricmc.commands.CommandsAPI
import org.qbrp.main.core.fabricmc.registerItems
import org.qbrp.main.core.util.log.InformationMessage
import org.qbrp.main.core.versions.VersionsUtil
import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.ModLoadedEvent
import org.qbrp.main.network.NetworkModule
import org.qbrp.main.system.SystemsModule

class ApplicationLoader : DedicatedServerModInitializer, KoinComponent {
    companion object {
        var initialized = false
        var shouldCrash: Boolean = false
    }
    val modulesCount: Int get() = Core.modulesCount + Engine.modulesCount

    override fun onInitializeServer() {
        val informationMessage = InformationMessage(
            "lain1wakura",
            VersionsUtil.getVersion().toString()
        )

        startKoin {
            modules(
                module {
                    single { ServerResources.getConfig() }
                    single { informationMessage }
                }
            )
            registerItems()
            Core.initialize()
            ServerLifecycleEvents.SERVER_STARTING.register { server ->
                loadKoinModules(module { single { server } })
                Engine.import(NetworkModule::class.java)
                Engine.import(SystemsModule::class.java)
                Engine.initialize()
                initialized = true
                ModLoadedEvent().emit()
                get<CommandsAPI>().add(ResourcesCommands())
            }
            ServerLifecycleEvents.SERVER_STARTED.register { server ->
                ConfigInitializationCallback.EVENT.invoker().onConfigUpdated(ServerResources.getConfig())
                informationMessage.print(modulesCount)
            }
        }
    }
}