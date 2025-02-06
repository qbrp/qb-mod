package org.qbrp.core

import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.server.MinecraftServer
import org.qbrp.core.game.events.Handlers
import org.qbrp.core.game.events.ServerReceivers
import org.qbrp.core.game.registry.CommandsRepository
import org.qbrp.core.regions.Regions
import org.qbrp.core.regions.commands.RegionCommands
import org.qbrp.core.resources.ServerResources
import org.qbrp.engine.Engine
import org.qbrp.engine.chat.system.TextTagsTransformer
import org.qbrp.engine.music.plasmo.playback.lavaplayer.AudioManager
import org.qbrp.system.networking.http.WebServer
import org.qbrp.view.View

class ServerCore : DedicatedServerModInitializer {
    companion object {
        lateinit var webServer: WebServer
        lateinit var server: MinecraftServer
    }

    override fun onInitializeServer() {
        ServerResources.buildResources()
        webServer = WebServer().also { it.start() }
        Handlers.registerServerEvents()
        AudioManager.registerSources()
        Regions.load()
        ServerLifecycleEvents.SERVER_STARTED.register { server ->
            ServerCore.server = server
            CommandsRepository.add(RegionCommands())
            initializeMainModules(server)
            CommandsRepository.initCommands(server.commandManager.dispatcher)
        }
        ServerLifecycleEvents.SERVER_STOPPED.register { server ->
            webServer.stop()
        }
        ServerReceivers.register()
    }

    fun initializeMainModules(server: MinecraftServer) {
        Engine().initialize(server)
        View().initialize()
    }
}
