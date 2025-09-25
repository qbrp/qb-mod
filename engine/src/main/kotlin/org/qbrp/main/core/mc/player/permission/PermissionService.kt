package org.qbrp.main.core.mc.player.permission

import org.qbrp.main.core.mc.context.MinecraftContext
import org.qbrp.main.core.mc.player.dataformats.MinecraftPlayerId
import org.qbrp.main.core.mc.player.model.PlayerReference
import org.qbrp.main.engine.players.model.networking.packet.PlayerPermissions

interface PermissionService {
    fun isOp(entity: MinecraftPlayerId): Boolean
    fun hasPermission(id: MinecraftPlayerId, permission: Permission): Boolean
    fun permissionsOf(id: MinecraftPlayerId): PlayerPermissions
    context(mc: MinecraftContext) fun sendPermissionsPacket(player: PlayerReference)
}