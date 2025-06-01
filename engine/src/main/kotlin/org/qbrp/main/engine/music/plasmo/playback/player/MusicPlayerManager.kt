package org.qbrp.main.engine.music.plasmo.playback.player

import net.minecraft.server.MinecraftServer
import net.minecraft.server.PlayerManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import su.plo.voice.api.server.PlasmoVoiceServer

class MusicPlayerManager(val server: MinecraftServer): KoinComponent {
    val playerManager = server.playerManager
    private val players: MutableMap<String, PlayerState> = mutableMapOf()

    fun addPlayer(playerName: String) {
        val player = playerManager.getPlayer(playerName)!!
        val voiceServer by inject<PlasmoVoiceServer>()
        players.put(player.name.string, PlayerState(voiceServer, player))
    }
    fun removePlayer(playerName: String) {
        players[playerName]?.handleDisconnect(); players.remove(playerName) }
    fun getPlayerState(playerName: String): PlayerState = players[playerName]!!
    fun getAllPlayers() = players.values
}