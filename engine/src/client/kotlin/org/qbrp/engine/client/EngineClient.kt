package org.qbrp.engine.client

import net.fabricmc.api.ClientModInitializer
import org.qbrp.engine.client.core.resources.ClientResources

class EngineClient : ClientModInitializer {

    override fun onInitializeClient() {
        ClientResources.downloadPack()
    }
}
