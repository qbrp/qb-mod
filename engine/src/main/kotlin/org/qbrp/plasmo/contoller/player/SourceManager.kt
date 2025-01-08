package org.qbrp.plasmo.contoller.player

import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.plasmo.contoller.lavaplayer.AudioManager
import su.plo.voice.api.server.PlasmoVoiceServer

class SourceManager {
    companion object { lateinit var voiceServer: PlasmoVoiceServer }
    private val players: MutableMap<String, PlayerState> = mutableMapOf()

    fun addPlayer(player: ServerPlayerEntity) = players.put(player.name.string, PlayerState(player))
    fun removePlayer(player: ServerPlayerEntity) {
        val plr = players[player.name.string]
        plr?.controller?.remove()
        players.remove(player.name.string)
    }
    fun getPlayerState(player: ServerPlayerEntity): PlayerState = players[player.name.string]!!
    fun getAllPlayers() = players.values
}