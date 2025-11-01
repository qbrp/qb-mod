package org.qbrp.client
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.minecraft.client.MinecraftClient
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.qbrp.client.controls.ClientControlsModule
import org.qbrp.client.core.mc.thread.ClientThreadModule
import org.qbrp.client.engine.ClientEngine
import org.qbrp.client.network.ClientNetworkModule
import org.qbrp.client.render.Render
import org.qbrp.main.core.Core
import org.qbrp.main.core.events.emit
import org.qbrp.main.core.util.log.InformationMessage
import org.qbrp.main.core.versions.VersionsUtil
import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.ModLoadedEvent

class ClientApplicationLoader : ClientModInitializer, KoinComponent {
    val modulesCount: Int get() = Core.modulesCount + ClientCore.modulesCount + Engine.modulesCount + ClientEngine.modulesCount + Render.modulesCount

    override fun onInitializeClient() {
        val informationMessage = InformationMessage(
            "lain1wakura",
            VersionsUtil.getVersion().toString())
        startKoin {
            modules(
                module {
                    single { MinecraftClient.getInstance() }
                    single { informationMessage }
                }
            )
            Core.import(ClientThreadModule::class.java)
            Core.initialize()
            ClientCore.initialize()
            Engine.initialize()
            ClientEngine.import(ClientNetworkModule::class.java)
            ClientEngine.import(ClientControlsModule::class.java)
            ClientEngine.initialize()
            ModLoadedEvent().emit()
        }

        ClientLifecycleEvents.CLIENT_STARTED.register {
            Render.initialize()
            informationMessage.print(modulesCount)
        }
    }
}