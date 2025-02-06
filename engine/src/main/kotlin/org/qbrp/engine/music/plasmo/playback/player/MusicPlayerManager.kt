package org.qbrp.engine.music.plasmo.playback.player

import net.minecraft.server.PlayerManager

class MusicPlayerManager(val playerManager: PlayerManager) {
    private val players: MutableMap<String, PlayerState> = mutableMapOf()

    fun addPlayer(playerName: String) {
        val player = playerManager.getPlayer(playerName)!!
        players.put(player.name.string, PlayerState(player))
    }
    fun removePlayer(playerName: String) {
        players[playerName]?.desync(); players.remove(playerName) }
    fun getPlayerState(playerName: String): PlayerState = players[playerName]!!
    fun getAllPlayers() = players.values
}