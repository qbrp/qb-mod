package org.qbrp.plasmo.contoller.player

import net.minecraft.server.network.ServerPlayerEntity

class PlayerState(val player: ServerPlayerEntity) {
    val controller = PlayerController(player.name.string)
}