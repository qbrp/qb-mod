package org.qbrp.main.network.player

import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.fabricmc.player.EMPTY_PERMISSIONS
import org.qbrp.main.core.fabricmc.player.PermissionSnapshot
import org.qbrp.main.core.fabricmc.player.PlayerPermissions
import org.qbrp.main.core.fabricmc.player.toSerializableSnapshot
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.network.transport.NetworkChannel
import org.qbrp.main.network.transport.Packet
import org.qbrp.main.network.transport.PacketSendTask
import org.qbrp.main.network.transport.wrapEndpoint

val PERMISSION_ENDPOINT = NetworkChannel<PermissionsPacket>().wrapEndpoint()

@Serializable
data class PermissionsPacket(val perms: PermissionSnapshot) : Packet

fun broadcastPermissionPackets() {
    PERMISSION_ENDPOINT.broadcast { it.getPermissionsPacket() }
}

fun ServerPlayerObject.taskPermissionsPacket(): PacketSendTask {
    return PERMISSION_ENDPOINT
        .taskS2C(getPermissionsPacket(), this)
}

internal fun ServerPlayerObject.getPermissionsPacket() = PermissionsPacket(get<PlayerPermissions>()?.toSerializableSnapshot() ?: EMPTY_PERMISSIONS)