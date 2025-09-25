package org.qbrp.main.core.mc.player.permission

import net.luckperms.api.LuckPermsProvider
import org.litote.kmongo.util.idValue
import org.qbrp.main.core.mc.context.MinecraftContext
import org.qbrp.main.core.mc.player.dataformats.MinecraftPlayerId
import org.qbrp.main.core.mc.player.getEntity
import org.qbrp.main.core.mc.player.model.PlayerReference
import org.qbrp.main.core.mc.player.service.MinecraftPlayerManager
import org.qbrp.main.core.networking.sender.ServerPacketSender
import org.qbrp.main.engine.players.model.networking.packet.PlayerPermissions

class PermissionServiceImpl(
    private val playerManager: MinecraftPlayerManager,
    private val packetSender: ServerPacketSender
) : PermissionService {
    private val luckPerms by lazy { LuckPermsProvider.get() }

    override fun isOp(entity: MinecraftPlayerId): Boolean {
        val entity = try {
            entity.getEntity(playerManager)
        } catch (e: Exception) {
            null
        }
        return entity?.hasPermissionLevel(4) ?: false
    }

    override fun hasPermission(id: MinecraftPlayerId, permission: Permission): Boolean {
        if (permission.isEmpty) return true
        if (isOp(id)) return true
        val user = id.luckPermsData ?: return false

        return user
            .permissionData
            .checkPermission(permission.qualifiedName)
            .asBoolean()
    }

    override fun permissionsOf(id: MinecraftPlayerId): PlayerPermissions {
        val user = id.luckPermsData ?: return PlayerPermissions.EMPTY
        val permissions = user.permissionData.permissionMap.map { Permission(it.key) }
        val isOp = isOp(id)
        return PlayerPermissions(permissions, isOp)
    }

    context(mc: MinecraftContext)
    override fun sendPermissionsPacket(player: PlayerReference) {
        packetSender.sendImmediately(
            PermissionsPacket(permissionsOf(player.uuid)),
            PERMISSIONS_CHANNEL,
            player
        )
    }

    private val MinecraftPlayerId.luckPermsData get() = luckPerms.userManager.getUser(value)?.cachedData
}

const val PERMISSIONS_CHANNEL = "permissions"