package org.qbrp.engine.client
import net.fabricmc.api.ClientModInitializer
import org.koin.core.context.startKoin
import org.qbrp.engine.Engine
import org.qbrp.engine.client.core.RegistrationManager
import org.qbrp.engine.client.core.events.ClientHandlers
import org.qbrp.engine.client.core.events.ClientReceivers
import org.qbrp.engine.client.engine.chat.ChatModuleClient
import org.qbrp.engine.client.render.Render
import org.qbrp.engine.client.core.keybinds.KeybindsManager
import org.qbrp.engine.client.system.ClientModuleManager
import org.qbrp.system.modules.ModuleAPI

class EngineClient : ClientModInitializer {

    companion object {
        lateinit var render: Render
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
        //ClientResources.downloadPack()
        ClientHandlers.registerEvents()

        startKoin {
            moduleManager.initialize()
        }

        render = Render()
            .apply { initialize() }

        ClientReceivers.register()
        keybindsManager.registerKeyBindings()
    }

}