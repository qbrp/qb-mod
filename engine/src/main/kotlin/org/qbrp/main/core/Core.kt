package org.qbrp.main.core

import config.ClientConfig
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.server.MinecraftServer
import org.koin.core.component.get
import org.qbrp.main.core.assets.AssetsAPI
import org.qbrp.main.deprecated.resources.ServerResources
import org.qbrp.deprecated.resources.data.config.ConfigInitializationCallback
import org.qbrp.deprecated.resources.data.config.ServerConfigData
import org.qbrp.main.ApplicationLayer
import org.qbrp.main.engine.ModInitializedEvent
import org.qbrp.main.core.info.ServerInfoAPI
import org.qbrp.main.core.mc.registry.GameRegistries
import org.qbrp.main.core.utils.networking.messages.components.ClusterEntry
object Core: ApplicationLayer("org.qbrp.main.core") {
    val ASSETS: AssetsAPI by lazy { get() }
    val SERVER_NAME = ClusterEntry<String>("core.server-name")
    const val MOD_ID = "qbrp"

    lateinit var server: MinecraftServer
    fun isServer() = ::server.isInitialized
    fun initializeAnd(runnable: Runnable) {
        initialize()
        ServerLifecycleEvents.SERVER_STARTED.register {
            runnable.run()
        }
    }

    override fun initialize() {
        get<GameRegistries>().enable()
        if (FabricLoader.getInstance().environmentType == EnvType.CLIENT) {
            eu.midnightdust.lib.config.MidnightConfig.init(MOD_ID, ClientConfig::class.java)
            super.initialize()
        } else {
            ServerLifecycleEvents.SERVER_STARTING.register {
                server = it
                ServerResources.buildResources()
                super.initialize()
                get<ServerInfoAPI>().COMPOSER.component(SERVER_NAME, get<ServerConfigData>().serverName)
            }
        }
    }
}