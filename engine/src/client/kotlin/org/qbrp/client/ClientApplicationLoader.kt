package org.qbrp.client
import net.fabricmc.api.ClientModInitializer
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.qbrp.client.core.registry.ClientGameRegistries
import org.qbrp.client.engine.ClientEngine
import org.qbrp.deprecated.resources.data.config.ConfigInitializationCallback
import org.qbrp.main.core.Core
import org.qbrp.main.core.mc.registry.GameRegistries
import org.qbrp.main.core.mc.registry.ServerGameRegistries
import org.qbrp.main.core.utils.log.InformationMessage
import org.qbrp.main.core.versions.VersionsUtil
import org.qbrp.main.deprecated.resources.ServerResources
import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.ModInitializedEvent

class ClientApplicationLoader : ClientModInitializer {
    val modulesCount: Int get() = Core.modulesCount + ClientCore.modulesCount + Engine.modulesCount

    override fun onInitializeClient() {
        val informationMessage = InformationMessage(
            "lain1wakura",
            VersionsUtil.getVersion().toString())
        startKoin {
            modules(
                module {
                    single { informationMessage }
                    single<GameRegistries> { ClientGameRegistries() }
                }
            )
            Core.initialize()
            ClientCore.initialize()
            Engine.initialize()
            ClientEngine.initialize()
            ModInitializedEvent.EVENT.invoker().event()
            informationMessage.print(modulesCount)
        }
    }
}