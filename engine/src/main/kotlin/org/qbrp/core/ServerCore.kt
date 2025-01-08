package org.qbrp.core

import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import org.qbrp.core.game.events.Handlers
import org.qbrp.core.game.events.ServerReceivers
import org.qbrp.core.resources.ServerResources
import org.qbrp.plasmo.Addon
import org.qbrp.system.networking.http.WebServer
import su.plo.voice.api.server.PlasmoVoiceServer

class ServerCore : DedicatedServerModInitializer {
    companion object { lateinit var plasmoAddon: Addon }
    lateinit var webServer: WebServer

    override fun onInitializeServer() {
        ServerResources.buildResources()
        webServer = WebServer().also { it.start() }
        Handlers.registerServerEvents()
        Handlers.registerBaseEvents()
        ServerReceivers.register()
        ServerLifecycleEvents.SERVER_STARTED.register { server ->
            plasmoAddon = Addon(server)
            PlasmoVoiceServer.getAddonsLoader().load(plasmoAddon)
        }
        ServerLifecycleEvents.SERVER_STOPPED.register { server ->
            webServer.stop()
        }
    }
}
