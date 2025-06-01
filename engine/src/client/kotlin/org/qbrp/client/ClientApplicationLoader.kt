package org.qbrp.client
import net.fabricmc.api.ClientModInitializer
import org.koin.core.context.startKoin
import org.qbrp.client.engine.ClientEngine
import org.qbrp.main.core.Core
import org.qbrp.main.engine.Engine

class ClientApplicationLoader : ClientModInitializer {
    override fun onInitializeClient() {
        startKoin {
            modules() //TODO: Добавить конфиг
            Core.initialize()
            ClientCore.initialize()
            Engine.initialize()
            ClientEngine.initialize()
        }
    }
}