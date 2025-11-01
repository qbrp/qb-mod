package org.qbrp.main.core

import config.ClientConfig
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.server.MinecraftServer
import org.koin.core.component.get
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import org.qbrp.main.core.assets.AssetsAPI
import eu.midnightdust.lib.config.MidnightConfig
import org.qbrp.main.deprecated.resources.ServerResources
import org.qbrp.main.ApplicationLayer
import org.qbrp.main.core.components.ComponentRegistry
import org.qbrp.main.core.events.on
import org.qbrp.main.core.util.networking.messages.components.ClusterEntry
import org.qbrp.main.engine.ModLoadedEvent
import org.qbrp.main.system.registerComponents

object Core : ApplicationLayer("org.qbrp.main.core") {
    const val MOD_ID = "qbrp"
    private lateinit var server: MinecraftServer

    val ASSETS: AssetsAPI by lazy { get() }
    val SERVER_NAME = ClusterEntry<String>("core.server-label")
    val env: EnvType = FabricLoader.getInstance().environmentType
    val isClient get() = env == EnvType.CLIENT

    override fun initialize() {
        if (env == EnvType.CLIENT) onClient()
        ServerLifecycleEvents.SERVER_STARTING.register {
            onServer(it)
        }
        on<ModLoadedEvent>() {
            get<ComponentRegistry>().registerComponents()
        }
    }

    private fun onClient() {
        MidnightConfig.init(MOD_ID, ClientConfig::class.java)
        super.initialize()
    }

    private fun onServer(server: MinecraftServer) {
        this.server = server
        loadKoinModules(module { single { server } })
        ServerResources.buildResources()
        super.initialize()
    }

    fun isServer() = ::server.isInitialized
}