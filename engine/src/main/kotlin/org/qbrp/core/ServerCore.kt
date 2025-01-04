package org.qbrp.core

import com.google.gson.JsonObject
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import org.qbrp.core.resources.ServerResources
import org.qbrp.system.networking.JsonContent
import org.qbrp.system.networking.Message
import org.qbrp.system.networking.NetworkManager
import org.qbrp.system.networking.ServerReceiver
import org.qbrp.system.networking.http.WebServer

class ServerCore : DedicatedServerModInitializer {
    lateinit var webServer: WebServer

    override fun onInitializeServer() {
        ServerResources.buildResources()

        ServerReceiver("packet", JsonContent::class) { message, context, receiver ->
            receiver.response(Message(
                "packet",
                JsonContent()
                    .apply { json = JsonObject() }),
                context)
        }.register()

        webServer = WebServer().also { it.start() }
        ServerLifecycleEvents.SERVER_STARTED.register { server -> }
        ServerLifecycleEvents.SERVER_STOPPED.register { server ->
            webServer.stop()
        }
    }
}
