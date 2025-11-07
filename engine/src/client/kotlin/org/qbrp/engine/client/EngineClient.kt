package org.qbrp.engine.client

import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.MinecraftClient
import org.koin.core.context.startKoin
import org.qbrp.engine.Engine
import org.qbrp.engine.client.core.RegistrationManager
import org.qbrp.engine.client.core.events.registerClientEvents
import org.qbrp.engine.client.core.events.registerClientReceivers
import org.qbrp.engine.client.engine.chat.ChatModuleClient
import org.qbrp.engine.client.core.keybinds.KeybindsManager
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
        //ClientResources.downloadPack()
        startKoin {
            moduleManager.initialize()
        }
        registerClientReceivers(MinecraftClient.getInstance())
        registerClientEvents()
        keybindsManager.registerKeyBindings()
    }

}