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
import org.koin.core.component.inject
import org.qbrp.main.deprecated.resources.ServerResources
import org.qbrp.main.ApplicationLayer
import org.qbrp.main.core.components.ComponentRegistry
import org.qbrp.main.core.events.EventBus
import org.qbrp.main.core.mc.player.permission.PermissionService
import org.qbrp.main.core.mc.player.service.MinecraftPlayerManager
import org.qbrp.main.core.mc.player.service.PlayerModeration
import org.qbrp.main.core.mc.player.service.attribute.MinecraftPlayerAttributeService
import org.qbrp.main.core.mc.server.command.ServerCommandExecutor
import org.qbrp.main.core.mc.server.thread.ServerThread
import org.qbrp.main.core.networking.sender.ServerPacketSender
import org.qbrp.main.core.networking.require.RequireManager
import org.qbrp.main.core.notifications.service.NotificationManager
import org.qbrp.main.core.utils.networking.messages.components.ClusterEntry

object Core: ApplicationLayer("org.qbrp.main.core") {
    const val MOD_ID = "qbrp"
    private lateinit var server: MinecraftServer

    val ASSETS: AssetsAPI by lazy { get() }
    val SERVER_NAME = ClusterEntry<String>("core.server-label")
    val componentRegistry: ComponentRegistry by inject()
    val context: CoreContext by lazy {
        object : CoreContext, ComponentRegistry by componentRegistry {
            override val packetSender: ServerPacketSender by inject()
            override val postRequirePacketListener: RequireManager by inject()
            override val eventBus: EventBus by inject()
            override val playerModeration: PlayerModeration by inject()
            override val permissionService: PermissionService by inject()
            override val playerManager: MinecraftPlayerManager by inject()
            override val serverThread: ServerThread by inject()
            override val commandExecutor: ServerCommandExecutor by inject()
            override val attributeService: MinecraftPlayerAttributeService by inject()
            override val notificationManager: NotificationManager by inject()
        }
    }

    override fun initialize() {
        val envType = FabricLoader.getInstance().environmentType
        if (envType == EnvType.CLIENT) onClient()
        ServerLifecycleEvents.SERVER_STARTING.register { onServer(it) }
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