package org.qbrp.main.engine.players.auth

import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.core.mc.player.model.PlayerSnapshot
import org.qbrp.main.core.networking.PacketListener

interface AuthListener: PacketListener {
    fun onAuthRequest(player: PlayerSnapshot, password: String)
}