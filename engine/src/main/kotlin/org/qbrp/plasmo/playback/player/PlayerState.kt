package org.qbrp.plasmo.playback.player

import net.minecraft.server.network.ServerPlayerEntity

class PlayerState(val player: ServerPlayerEntity) {
    val controller = PlayerController(player.name.string)
}