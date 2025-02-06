package org.qbrp.engine.client
import net.fabricmc.api.ClientModInitializer
import org.qbrp.engine.client.core.events.ClientHandlers
import org.qbrp.engine.client.core.events.ClientReceivers
import org.qbrp.engine.client.engine.chat.ChatModuleClient
import org.qbrp.engine.client.render.Render
import org.qbrp.engine.client.core.keybinds.KeybindsManager
import org.qbrp.engine.client.engine.spectators.SpectatorsModuleClient
class EngineClient : ClientModInitializer {

    companion object {
        private lateinit var chatClientModule: ChatModuleClient
        lateinit var render: Render
        val keybindsManager = KeybindsManager()
        val spectatorsModule = SpectatorsModuleClient()

        fun getChatModuleAPI(): ChatModuleClient.API? = if (::chatClientModule.isInitialized) chatClientModule.api else null
        fun getChatModuleAPIorThrow(): ChatModuleClient.API = chatClientModule.api
    }

    override fun onInitializeClient() {
        //ClientResources.downloadPack()
        ClientHandlers.registerEvents()
        ClientHandlers.registerChunkLoadEvents()
        ClientReceivers.register()
        chatClientModule = ChatModuleClient()
        render = Render().apply { initialize() }
    }

}