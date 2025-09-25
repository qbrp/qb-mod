package org.qbrp.main.core.mc.player.permission

import org.qbrp.main.core.networking.PacketListener
import org.qbrp.main.engine.players.model.networking.packet.PlayerPermissions

interface PermissionListener : PacketListener {
    fun onPermissions(perms: PlayerPermissions)
}