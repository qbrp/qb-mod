package org.qbrp.core

import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.server.MinecraftServer
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.qbrp.core.game.events.Handlers
import org.qbrp.core.game.events.ServerReceivers
import org.qbrp.core.game.player.PlayerManager
import org.qbrp.core.game.registry.CommandsRepository
import org.qbrp.core.keybinds.ServerKeybinds
import org.qbrp.core.regions.Regions
import org.qbrp.core.regions.commands.RegionCommands
import org.qbrp.core.resources.ServerResources
import org.qbrp.core.resources.data.config.ConfigInitializationCallback
import org.qbrp.engine.Engine
import org.qbrp.engine.music.plasmo.playback.lavaplayer.AudioManager
import org.qbrp.system.networking.ServerInformation
import org.qbrp.system.networking.http.WebServer
import org.qbrp.system.utils.log.InformationMessage
import org.qbrp.view.View

class ServerCore : DedicatedServerModInitializer {
    companion object {
        lateinit var webServer: WebServer
        lateinit var server: MinecraftServer
        fun isServer() = ::server.isInitialized
        val informationMessage = InformationMessage()
        lateinit var engine: Engine
    }

    override fun onInitializeServer() {
        ServerResources.buildResources()
        ServerResources.buildContent()
        webServer = WebServer().also { it.start() }
        Handlers.registerServerEvents()
        AudioManager.registerSources()
        Regions.load()
        PlayerManager.loadCommand()
        ServerLifecycleEvents.SERVER_STARTED.register { server ->
            ServerCore.server = server
            CommandsRepository.add(RegionCommands())
            initializeMainModules(server)
            CommandsRepository.initCommands(server.commandManager.dispatcher)
            ServerKeybinds.composeServerInfo()
            ServerInformation.build()
            ConfigInitializationCallback.EVENT.invoker().onConfigUpdated(ServerResources.getConfig())
            informationMessage.print()
        }
        ServerLifecycleEvents.SERVER_STOPPED.register { server ->
            webServer.stop()
        }
        ServerReceivers.register()
    }

    fun initializeMainModules(server: MinecraftServer) {
        engine = Engine()
        startKoin {
            modules(
                module {
                    single { server }
                    single { ServerResources.getConfig() }
                }
            )
            engine.initialize()
        }
        View().initialize()
    }
}
