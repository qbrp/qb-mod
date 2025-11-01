package org.qbrp.client.network

import config.ClientConfig
import net.fabricmc.api.EnvType
import net.minecraft.client.MinecraftClient
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.client.ClientCore
import org.qbrp.client.core.mc.connection.ConnectEvent
import org.qbrp.client.core.mc.connection.DisconnectEvent
import org.qbrp.client.core.mc.exception.PlayerNotOnServerException
import org.qbrp.client.core.notifications.ClientNotificationSender
import org.qbrp.client.engine.chat.addons.ClientChatGroupsAPI
import org.qbrp.client.engine.contentpacks.ClientContentPacksAPI
import org.qbrp.client.engine.inventory.ClientInventory
import org.qbrp.client.engine.movement.Movement
import org.qbrp.client.engine.players.ClientPlayerManager
import org.qbrp.client.engine.players.ClientPlayerFactory
import org.qbrp.client.network.play.BodyIdTable
import org.qbrp.client.network.play.ClientPhysicsNetworkHandler
import org.qbrp.client.network.play.ClientPlayerNetworkHandler
import org.qbrp.client.network.play.Login
import org.qbrp.client.network.play.NetworkState
import org.qbrp.client.network.play.Play
import org.qbrp.client.network.play.registerClientInteractionHandler
import org.qbrp.client.network.play.runClientInputPacketSender
import org.qbrp.client.network.play.runClientInventoryHandler
import org.qbrp.client.network.play.runClientItemNetworkHandler
import org.qbrp.client.network.play.runClientPhysicsNetworkHandler
import org.qbrp.client.network.play.runInLoginState
import org.qbrp.client.network.play.sendModIds
import org.qbrp.client.network.transport.ClientPacketTransportProgram
import org.qbrp.client.network.transport.registerClientReceiver
import org.qbrp.client.network.transport.runAcknowledgeListeners
import org.qbrp.client.network.transport.sendC2SPacket
import org.qbrp.main.core.components.extensions.require
import org.qbrp.main.core.events.on
import org.qbrp.main.core.fabricmc.player.Username
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.core.util.PlayerId
import org.qbrp.main.engine.ModLoadedEvent
import org.qbrp.main.engine.physics.player.PlayerBodyManager
import org.qbrp.main.engine.physics.system.entity.asPhysical
import org.qbrp.main.engine.physics.system.service.PhysicsSimulation
import org.qbrp.main.network.player.ConnectionSession
import org.qbrp.main.engine.text.map.TextMappingRegistry
import org.qbrp.main.network.player.AUTHORIZE_SUCCESS_ENDPOINT
import org.qbrp.main.network.player.AUTHORIZE_TRY_ENDPOINT
import org.qbrp.main.network.player.AuthorizationPacket
import org.qbrp.main.network.player.COMMAND_EXECUTE_ENDPOINT
import org.qbrp.main.network.player.JOIN_GAME_ENDPOINT
import org.qbrp.main.network.player.NOTIFICATION_ENDPOINT
import org.qbrp.main.network.player.Notification
import org.qbrp.main.network.player.SERVER_INFORMATION_ENDPOINT
import org.qbrp.main.network.player.ServerInformation
import org.qbrp.main.network.player.map

@Autoload(env = EnvType.CLIENT, priority = Priorities.LOWEST)
class ClientNetworkModule : EngineModule("client-network") {
    private val program: ClientPacketTransportProgram by inject()
    private val contentPacksApi: ClientContentPacksAPI by inject()
    private val chatGroups: ClientChatGroupsAPI by inject()
    private val playerFactory: ClientPlayerFactory by inject()
    private val playerManager: ClientPlayerManager by inject()
    private val playerBodyManager: PlayerBodyManager by inject()
    private val inventoryManager: ClientInventory by inject()
    private val physicsNetworkHandler: ClientPhysicsNetworkHandler by inject()
    private val notificationManager: ClientNotificationSender by inject()
    private val textMappingRegistry: TextMappingRegistry by inject()

    var networkState: NetworkState? = null

    override fun getKoinModule(): Module = module {
        single { ClientPacketTransportProgram() }
        single { BodyIdTable() }
        single { ClientPhysicsNetworkHandler(get(), get(), get(), get()) }
        single {
            ClientPlayerNetworkHandler(
                this@ClientNetworkModule,
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                MinecraftClient.getInstance()
            )
        }
        single<NetworkState> { networkState ?: error("Игрок не на сервере") }
    }

    override fun onEnable() = on<ModLoadedEvent>() {
        MinecraftClient.getInstance().execute {
            program.runAcknowledgeListeners()
            runClientPhysicsNetworkHandler(physicsNetworkHandler, get())
            runClientItemNetworkHandler(get(), get(), get(), get(), get())
            runClientInventoryHandler(get(), get(), get())
            registerClientInteractionHandler(get())
            runClientInputPacketSender()
            get<ClientPlayerNetworkHandler>().run()

            COMMAND_EXECUTE_ENDPOINT
                .registerClientReceiver { onCommand(command) }

            NOTIFICATION_ENDPOINT
                .registerClientReceiver { onNotification(notification.map(textMappingRegistry)) }

            AUTHORIZE_SUCCESS_ENDPOINT
                .registerClientReceiver { onAuthorize(session) }

            SERVER_INFORMATION_ENDPOINT
                .registerClientReceiver { onServerInformation(information.map()) }

            JOIN_GAME_ENDPOINT
                .registerClientReceiver { onJoinGame(playerId, username) }

            on<ConnectEvent> {
                networkState = Login()
                sendModIds()
                authorize()
            }

            on<DisconnectEvent> { onDisconnect() }
        }
    }.let { Unit }

    private fun onJoinGame(playerId: PlayerId, username: Username) = runInLoginState(networkState) {
        val player = playerFactory.buildLocalPlayer(playerId)
        if (playerManager.isInstantiated) playerManager.destroy()
        playerManager.instantiateMainPlayer(player)

        playerBodyManager.addPlayerCharacter(player)
        inventoryManager.createInventories()
        physicsNetworkHandler.init(player)
        switchNetworkStateToPlay()
    }

    private fun onDisconnect() {
        physicsNetworkHandler.invalidate()
        networkState = null
    }

    private fun onAuthorize(connectionSession: ConnectionSession) = runInLoginState(networkState) {
        session = connectionSession
    }

    private fun onServerInformation(info: ServerInformation) = runInLoginState(networkState) {
        serverInformation = info
        chatGroups.loadChatGroups(info.chatGroups)
        val cp = info.contentPacks
        if (cp.enabled) {
            contentPacksApi.applyPack(ClientCore.getServerIp()!!, cp.downloadPort, cp.serverName)
        }
    }

    private fun onNotification(notification: Notification) {
        notificationManager.send(notification)
    }

    private fun onCommand(command: String) {
        val client = MinecraftClient.getInstance()
        (client.player?.networkHandler ?: throw PlayerNotOnServerException()).sendChatCommand(command)
    }

    private fun authorize(password: String = ClientConfig.accountCode) {
        AUTHORIZE_TRY_ENDPOINT.sendC2SPacket(AuthorizationPacket.Try(password))
    }

    fun switchNetworkStateToPlay() {
        val s = networkState!!
        networkState = Play(
            s.serverInformation!!,
            s.session!!,
            s.permissions!!
        )
    }
}