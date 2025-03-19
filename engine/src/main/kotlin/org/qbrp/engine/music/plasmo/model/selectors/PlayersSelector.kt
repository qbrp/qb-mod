package org.qbrp.engine.music.plasmo.model.selectors

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.MinecraftServer
import org.qbrp.core.ServerCore

class PlayersSelector(
    override val params: List<String>
) : Selector() {
    override val type: String = "players"

    constructor(playerName: String) : this(listOf(playerName))

    override fun match(player: ServerPlayerEntity): Boolean {
        val server = ServerCore.server
        val players = params[0]
            .trim()
            .split(",")
            .mapNotNull { server.playerManager.getPlayer(it) }
        return players.any { it == player }
    }
}