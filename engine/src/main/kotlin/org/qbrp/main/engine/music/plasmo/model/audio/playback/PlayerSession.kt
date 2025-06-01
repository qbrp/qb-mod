package org.qbrp.main.engine.music.plasmo.model.audio.playback

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.qbrp.main.engine.music.plasmo.view.PlaylistView
import org.qbrp.main.engine.music.plasmo.model.audio.Playable
import org.qbrp.main.engine.music.plasmo.model.audio.Queue
import su.plo.voice.api.server.audio.source.ServerDirectSource
import kotlin.concurrent.timer
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.qbrp.main.engine.music.plasmo.playback.player.PlayerState

data class PlayerSession(
    var playable: Playable,
    val source: ServerDirectSource,
    var queue: Queue,
    val state: PlayerState,
    var radio: Radio? = null,
): KoinComponent {
    private var unsubscribeTimer: java.util.Timer? = null
    private var unsubscribeTimePassed = false

    fun getView(): PlaylistView = PlaylistView(playable, this)

    fun nextTrack() {
        queue.next()
    }

    fun destroy(): Boolean {
        radio?.destroy()
        source.remove()
        return true
    }

    // Возвращает булеву, готов ли игрок отписаться
    fun handleUnsubscribe(): Boolean {
        radio?.let { it.audioPlayer.volume = 0 } ?: return true
        if (unsubscribeTimer == null) {
            unsubscribeTimer = timer("unsubscribeTimer", false, 700, Long.MAX_VALUE) {
                unsubscribeTimePassed = true
                this.cancel()
                unsubscribeTimer = null
            }
        }
        return unsubscribeTimePassed
    }

    fun fadeOut() {
        radio?.let { it.audioPlayer.volume = 0 }
        Thread.sleep(700)
    }

    fun cancelUnsubscribe() {
        radio?.audioPlayer?.volume = 100
        unsubscribeTimer?.cancel()
        unsubscribeTimer = null
        unsubscribeTimePassed = false
    }

    private suspend fun handleTrackFinished() {
        nextTrack()
        createRadio()
    }

    suspend fun createRadio(time: Long = 0) {
        withContext(Dispatchers.Default) {
            if (radio != null) fadeOut()
            val track = queue.getCurrentTrack()
            if (track != null) {
                radio?.destroy()
                radio = get<Radio>(parameters = {
                    parametersOf(source, queue.getCurrentTrack(), {
                        runBlocking { handleTrackFinished() }
                    })
                }).apply {
                    audioPlayer.playingTrack.position = time
                }
            }
            radio!!.audioPlayer?.volume = 100
        }
    }
}