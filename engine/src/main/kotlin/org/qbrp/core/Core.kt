package org.qbrp.core

import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import org.qbrp.core.resources.ServerResources
import org.qbrp.system.networking.http.WebServer

class Core : DedicatedServerModInitializer {

    companion object {
        const val MOD_ID = "qbrp"
    }

    lateinit var webServer: WebServer

    override fun onInitializeServer() {
        ServerResources.buildResources()
        webServer = WebServer().also { it.start() }
        ServerLifecycleEvents.SERVER_STARTED.register { server -> }
        ServerLifecycleEvents.SERVER_STOPPED.register { server ->
            webServer.stop()
        }

    }
}
