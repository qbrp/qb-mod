package org.qbrp.engine.music.plasmo.model.audio.playback

import org.qbrp.engine.music.plasmo.controller.view.PlaylistView
import org.qbrp.engine.music.plasmo.model.audio.Playable
import org.qbrp.engine.music.plasmo.model.audio.Queue
import su.plo.voice.api.server.audio.source.ServerDirectSource
import kotlin.concurrent.timer

data class PlayerSession(
    var playable: Playable,
    val source: ServerDirectSource,
    var queue: Queue,
    var radio: Radio? = null
) {
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
        //radio?.fadeOut() ?: return true
        //return radio!!.isFadedOut()
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

    fun cancelUnsubscribe() {
        //println("Cancelling fade out")
        //radio!!.cancelFadeOut()
        radio?.audioPlayer?.volume = 100
        unsubscribeTimer?.cancel()
        unsubscribeTimer = null
        unsubscribeTimePassed = false
    }

    private fun handleTrackFinished() {
        nextTrack()
        createRadio()
    }

    fun createRadio(time: Long = 0) {
        queue.getCurrentTrack()?.let { track ->
            radio?.destroy()
            radio = Radio(source, track) {
                handleTrackFinished()
            }.apply {
                audioPlayer.playingTrack.position = time
            }
        }
    }
}