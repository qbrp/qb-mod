package org.qbrp.plasmo.contoller

import org.qbrp.plasmo.contoller.player.SourceManager
import org.qbrp.plasmo.model.Playable
import org.qbrp.plasmo.model.audio.Playlist
import org.qbrp.plasmo.model.priority.Priorities
import java.util.Timer
import kotlin.concurrent.fixedRateTimer

class PlayerSelectionHandler(val sourceManager: SourceManager) {
    lateinit var timer: Timer

    fun startHandling() {
        timer = fixedRateTimer(
            name = "[qbrp/Plasmo] [SelectionHandler]",
            initialDelay = 0,
            period = 400,
            daemon = true
        ) {
            sourceManager.getAllPlayers().forEach { playerState ->
                val playlists = mutableListOf<Playable>()
                AddonStorage.getAllPlaylists().forEach { playlist ->
                    if (playlist.selector.match(playerState.player)) playlists.add(playlist)
                }
                if (playlists.isNotEmpty()) {
                    val playlist = playlists.maxByOrNull { Priorities.getIndex(it.priority) }!!
                    playerState.controller.sync((playlist as Playlist).getCurrentTrack(), playlist.currentTime, playlist.playSession)
                }
            }
        }
    }

    fun stopHandling() {
        timer.cancel()
    }
}