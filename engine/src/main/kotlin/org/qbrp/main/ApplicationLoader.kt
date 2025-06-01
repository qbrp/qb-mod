package org.qbrp.main

import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import org.koin.core.context.GlobalContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.qbrp.deprecated.resources.data.config.ConfigInitializationCallback
import org.qbrp.main.deprecated.resources.ServerResources
import org.qbrp.main.core.Core
import org.qbrp.main.core.utils.format.Format.asMiniMessage
import org.qbrp.main.core.utils.log.InformationMessage
import org.qbrp.main.core.versions.VersionsUtil
import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.ModInitializedEvent

class ApplicationLoader : DedicatedServerModInitializer {
    var initialized = false
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
                }
            )
            ServerLifecycleEvents.SERVER_STARTING.register { server ->
                loadKoinModules(module { single { server } })
            }
            Core.initializeAnd() {
                Engine.initialize()
                ModInitializedEvent.EVENT.invoker().event()
                initialized = true
            }
        }

        ServerPlayConnectionEvents.INIT.register { handler, _ ->
            if (!initialized) { handler.disconnect("<red>Engine инициализируется.<newline>Перезайдите через несколько секунд.".asMiniMessage()) }
        }

        ModInitializedEvent.EVENT.register {
            ConfigInitializationCallback.EVENT.invoker().onConfigUpdated(ServerResources.getConfig())
            informationMessage.print(modulesCount)
        }
    }
}