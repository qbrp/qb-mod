package org.qbrp.engine.client

import com.google.gson.JsonObject
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import org.qbrp.engine.client.core.resources.ClientResources
import org.qbrp.engine.client.system.networking.ClientNetworkManager
import org.qbrp.system.networking.JsonContent
import org.qbrp.system.networking.Message

class EngineClient : ClientModInitializer {

    override fun onInitializeClient() {
        ClientResources.downloadPack()
        ClientPlayConnectionEvents.JOIN.register { client, handler, world ->
            val message = Message("packet", JsonContent().apply { json = JsonObject() })
            ClientNetworkManager.responseRequest(message, JsonContent::class) { println("received") }
        }
        //Game.init()
    }
}
