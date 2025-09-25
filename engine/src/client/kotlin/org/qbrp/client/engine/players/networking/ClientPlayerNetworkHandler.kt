package org.qbrp.client.engine.players.networking

import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.qbrp.client.core.networking.v2.ClientContext
import org.qbrp.client.engine.players.ClientPlayerManagerImpl
import org.qbrp.client.engine.players.ClientPlayerStorage
import org.qbrp.client.engine.players.event.LocalPlayerPermissionsEvent
import org.qbrp.client.engine.players.event.LocalPlayerSyncEvent
import org.qbrp.client.engine.players.event.PlayerLoadEvent
import org.qbrp.client.engine.players.profile.PlayerProfileManager
import org.qbrp.client.engine.players.profile.PlayerProfileMapper
import org.qbrp.main.core.events.EventContext
import org.qbrp.main.core.events.emit
import org.qbrp.main.core.mc.player.permission.PERMISSIONS_CHANNEL
import org.qbrp.main.core.mc.player.permission.PermissionListener
import org.qbrp.main.core.mc.player.permission.PermissionService
import org.qbrp.main.core.mc.player.permission.PermissionsPacket
import org.qbrp.main.core.networking.PacketListenerActivator
import org.qbrp.main.engine.controls.model.InteractionService
import org.qbrp.main.engine.players.auth.SessionData
import org.qbrp.main.engine.players.model.PlayersModule
import org.qbrp.main.engine.players.model.mapping.PlayerListDto
import org.qbrp.main.engine.players.model.networking.PlayerListPacket
import org.qbrp.main.engine.players.model.networking.PlayerPacketListener
import org.qbrp.main.engine.players.model.networking.packet.PlayerInstantiatePacket
import org.qbrp.main.engine.players.model.networking.packet.PlayerSyncPacket
import org.qbrp.main.engine.players.model.networking.PlayerSynchronizer
import org.qbrp.main.engine.players.model.networking.packet.PlayerPermissions

class ClientPlayerNetworkHandler(
    private val packetActivator: PacketListenerActivator<ClientContext>,
    private val resolver: PlayerResolver,
    private val storage: ClientPlayerStorage,
    private val factory: ClientPlayerFactory,
    private val playerProfileManager: PlayerProfileManager,
    private val playerProfileMapper: PlayerProfileMapper,
    private val playerManagerImpl: ClientPlayerManagerImpl,
    private val events: EventContext
) : PlayerPacketListener, PermissionListener, KoinComponent {
    fun run() {
        packetActivator.apply {
            run(
                PlayerSynchronizer.CHANNEL,
                resolver,
                { buf, ctx -> PlayerSyncPacket(buf) })
            run(
                PlayersModule.PLAYER_LIST_CHANNEL,
                this@ClientPlayerNetworkHandler,
                { buf, ctx -> PlayerListPacket(buf) })
            run(
                PlayersModule.INSTANTIATE_CHANNEL,
                this@ClientPlayerNetworkHandler,
                { buf, ctx -> PlayerInstantiatePacket(buf, get<SessionData>()) })
            run(
                PERMISSIONS_CHANNEL,
                this@ClientPlayerNetworkHandler,
                { buf, ctx -> PermissionsPacket(buf) })
        }
    }

    override fun onPermissions(perms: PlayerPermissions) = with(events) {
        LocalPlayerPermissionsEvent(perms).emit()
    }

    override fun onLocalPlayerInstantiatedOnServer(
        session: SessionData,
        entityName: String
    ) = with(events) {
        val player = factory.buildLocalPlayer(session)
        storage.remove(player.id) //TODO
        storage.add(player)
        playerManagerImpl.instantiate(player)
        LocalPlayerSyncEvent(player).emit()
        PlayerLoadEvent(player).emit()
    }

    override fun onPlayerList(list: PlayerListDto) {
        val updatedPlayerProfileList = playerProfileMapper.fromDto(list)
        updatedPlayerProfileList.forEach { playerProfileManager.addOrUpdateProfile(it) }
    }
}