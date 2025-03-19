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

class EngineClient : ClientModInitializer {

    companion object {
        lateinit var render: Render
        val keybindsManager = KeybindsManager()
        val registrationManager = RegistrationManager()
    }

    override fun onInitializeClient() {
        //ClientResources.downloadPack()
        ClientHandlers.registerEvents()

        startKoin {
            Engine.moduleManager.initialize()
        }
        render = Render()
            .apply { initialize() }

        ClientReceivers.register()
        keybindsManager.registerKeyBindings()
    }

}