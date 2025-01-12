package org.qbrp.plasmo.playback

import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.plasmo.MusicStorage
import org.qbrp.plasmo.playback.player.SourceManager
import org.qbrp.plasmo.model.Playable
import org.qbrp.plasmo.model.audio.Playlist
import org.qbrp.plasmo.model.priority.Priorities
import org.qbrp.system.utils.log.Loggers
import java.util.Timer
import kotlin.concurrent.fixedRateTimer

class PlayerSelectionHandler(val sourceManager: SourceManager) {
    lateinit var timer: Timer
    val logger = Loggers.get("plasmo", "playback")

    fun getSelectedPlaylists(player: ServerPlayerEntity): List<Playable> {
        val playlists = mutableListOf<Playable>()
        MusicStorage.getAllPlaylists().forEach { playlist ->
            if (playlist.selector.match(player)) playlists.add(playlist)
        }
        return playlists.toList()
    }

    fun startHandling() {
        timer = fixedRateTimer(
            name = "[qbrp/Plasmo] [SelectionHandler]",
            initialDelay = 0,
            period = 400,
            daemon = true
        ) {
            try {
                sourceManager.getAllPlayers().forEach { playerState ->
                    val playlists = getSelectedPlaylists(playerState.player)
                    if (playlists.isNotEmpty()) {
                        val playlist = playlists.maxByOrNull { Priorities.getIndex(it.priority) }!!
                        playerState.controller.sync((playlist as Playlist).playback)
                    } else {
                        playerState.controller.stopTrack()
                    }
                }
            } catch (e: Exception) {
                logger.error("${e.message}")
            }
        }
    }

    fun stopHandling() {
        timer.cancel()
    }
}