package org.qbrp.main.engine.music.plasmo.playback.player

import net.minecraft.server.MinecraftServer
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import su.plo.voice.api.server.PlasmoVoiceServer

class MusicPlayerManager(val server: MinecraftServer): KoinComponent {
    val playerManager = server.playerManager
    private val players: MutableMap<String, VoicePlayerState> = mutableMapOf()

    fun addPlayer(playerName: String) {
        val player = playerManager.getPlayer(playerName)!!
        val voiceServer by inject<PlasmoVoiceServer>()
        players.put(player.name.string, VoicePlayerState(voiceServer, player))
    }
    fun removePlayer(playerName: String) {
        players[playerName]?.handleDisconnect(); players.remove(playerName) }
    fun getPlayerState(playerName: String): VoicePlayerState = players[playerName]!!
    fun getAllPlayers() = players.values
}