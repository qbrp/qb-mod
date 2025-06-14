package org.qbrp.main.engine.music.plasmo.playback

import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.engine.music.plasmo.MusicStorage
import org.qbrp.main.engine.music.plasmo.model.audio.Playable
import org.qbrp.main.engine.music.plasmo.model.audio.playback.PlayerSession
import org.qbrp.main.engine.music.plasmo.model.priority.Priorities
import org.qbrp.main.engine.music.plasmo.playback.player.MusicPlayerManager
import org.qbrp.main.core.utils.log.LoggerUtil
import java.util.Timer
import kotlin.concurrent.fixedRateTimer

class PlayerSelectionHandler(val musicPlayerManager: MusicPlayerManager, val storage: MusicStorage, val priorities: Priorities) {
    lateinit var timer: Timer
    val logger = LoggerUtil.get("musicManager", "playback")

    fun getSelectedPlaylists(player: ServerPlayerEntity): List<Playable> {
        val playlists = mutableListOf<Playable>()
        storage.getAllPlayable().forEach { playlist ->
            if (playlist.selector.match(player)) playlists.add(playlist)
        }
        return playlists.toList()

    }

    fun getPlayerSession(player: ServerPlayerEntity): PlayerSession? =
        getSelectedPlaylists(player)
            .filter { it.isManuallyDisabled != true }
            .minByOrNull { priorities.getIndex(it.priority) }
            ?.sessionManager?.getSession(player)

    fun startHandling() {
        timer = fixedRateTimer(
            name = "[qbrp/Plasmo] [SelectionHandler]",
            initialDelay = 0,
            period = 1000,
            daemon = true
        ) {
            try {
                musicPlayerManager.getAllPlayers().forEach { playerState ->
                    val playlists = getSelectedPlaylists(playerState.player)
                    if (playlists.isNotEmpty()) {
                        playlists
                            .filter { it.isManuallyDisabled != true }
                            .minByOrNull { priorities.getIndex(it.priority) } // Получаем плейлист с максимальным приоритетом
                            ?.let { playlist ->
                                playerState.sync(playlist) // Синхронизируем с найденным плейлистом
                            } ?: run {
                            playerState.desync() // Если плейлистов нет, десинхронизируем
                        }
                    } else {
                        playerState.desync() // Если нет выбранных плейлистов, десинхронизируем
                    }
                }
            } catch (e: Exception) {
                logger.error("${e.printStackTrace()}")
            }
        }
    }


    fun stopHandling() {
        timer.cancel()
    }
}