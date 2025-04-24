package org.qbrp.engine.client
import config.ClientConfig
import eu.midnightdust.lib.config.MidnightConfig
import net.fabricmc.api.ClientModInitializer
import org.koin.core.context.startKoin
import org.qbrp.engine.Engine
import icyllis.modernui.mc.ModernUIClient
import org.qbrp.core.Core
import org.qbrp.engine.client.core.RegistrationManager
import org.qbrp.engine.client.core.events.ClientHandlers
import org.qbrp.engine.client.core.events.ClientReceivers
import org.qbrp.engine.client.engine.chat.ChatModuleClient
import org.qbrp.engine.client.render.Render
import org.qbrp.engine.client.core.keybinds.KeybindsManager
import org.qbrp.engine.client.core.resources.ClientResources
import org.qbrp.engine.client.system.ClientModuleManager
import org.qbrp.system.modules.ModuleAPI

class EngineClient : ClientModInitializer {

    companion object {
        val moduleManager = ClientModuleManager()
        val keybindsManager = KeybindsManager()
        val registrationManager = RegistrationManager()

        inline fun <reified T : ModuleAPI> getAPI(): T? {
            return moduleManager.getAPI<T>()
        }

        inline fun <reified T : ModuleAPI> isApiAvailable(): Boolean {
            return moduleManager.isApiAvailable<T>()
        }
    }

    override fun onInitializeClient() {
        MidnightConfig.init(Core.MOD_ID, ClientConfig::class.java)
        //ClientResources.downloadPack()
        ClientHandlers.registerEvents()

        startKoin {
            moduleManager.initialize()
        }

        Render.initialize()

        ClientReceivers.register()
        keybindsManager.registerKeyBindings()
    }

}