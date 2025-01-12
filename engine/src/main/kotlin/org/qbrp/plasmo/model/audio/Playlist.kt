package org.qbrp.plasmo.model.audio

import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonSetter
import kotlinx.coroutines.*
import org.qbrp.plasmo.controller.view.PlaylistView
import org.qbrp.plasmo.controller.view.View
import org.qbrp.plasmo.MusicStorage
import org.qbrp.plasmo.model.Playable
import org.qbrp.plasmo.model.audio.Playlist.Playback
import org.qbrp.plasmo.model.priority.Priorities
import org.qbrp.plasmo.model.priority.Priority
import org.qbrp.plasmo.model.selectors.RegionSelector
import org.qbrp.plasmo.model.selectors.Selector
import org.qbrp.system.utils.log.Loggers
import java.util.UUID

@JsonIgnoreProperties(ignoreUnknown = true)
data class Playlist(
    var name: String,
    override var selector: Selector = RegionSelector("global"),
    override var priority: Priority = Priorities.lowest(),
    override var cycle: Int = -1,
    val tracks: MutableList<String> = mutableListOf<String>(),
) : Playable() {
    @JsonIgnore
    var playback: Playback = Playback()

    @JsonGetter("track")
    fun serializeTrack(): Int = playback.currentTrackIndex
    @JsonSetter("track")
    fun setTrack(trackIndex: Int) { playback.currentTrackIndex = trackIndex }
    @JsonGetter("currentTime")
    fun serializeCurrentTime(): Int = playback.currentTime
    @JsonSetter("currentTime")
    fun setCurrentTime(time: Int) { playback.currentTime = time }
    @JsonGetter("playing")
    fun isPlaying(): Boolean {
        return playback.playJob != null
    }
    @JsonSetter("playing")
    fun setPlay(playing: Boolean) {
        if (playing) { play() }
    }


    @JsonIgnore
    override fun getView(): View = PlaylistView(this)
    override fun play() = playback.startIfNotPlaying()
    override fun stop() = playback.stop()

    fun addTrack(name: String) {
        if (MusicStorage.isTrackExists(name)) tracks.add(name)
    }

    fun removeTrack(index: Int) {
        if (index in tracks.indices) {
            tracks.removeAt(index)
            playback.onTrackRemoved(index)
        }
    }

    fun moveTrack(from: Int, to: Int) {
        if (from in tracks.indices && to in tracks.indices && from != to) {
            tracks.add(to, tracks.removeAt(from))
            playback.onTrackMoved(from, to)
        }
    }

    @JsonIgnore
    override fun getCurrentTrack(): Track = playback.getCurrentTrack()

    inner class Playback(
        var currentTrackIndex: Int = 0,
        var currentTime: Int = 0,
        var currentCycle: Int = 0,
        @JsonIgnore
        var playJob: Job? = null,
        @JsonIgnore
        var playSession: String = UUID.randomUUID().toString() ) {

        fun startIfNotPlaying() {
            println(playJob?.isActive)
            println(tracks)
            if (playJob?.isActive != true && tracks.isNotEmpty()) start()
        }

        private fun start() {
            resetSession()
            log("Воспроизведение плейлиста: $name")
            playJob = CoroutineScope(Dispatchers.Default).launch {
                while (playNextSecond()) delay(1000)
                log("Плейлист $name завершен.")
            }
        }

        fun stop() {
            playJob?.cancel()
            playJob = null
            log("Остановлен плейлист: $name")
        }

        @JsonIgnore
        fun getCurrentTrack(): Track = MusicStorage.getTrack(tracks[currentTrackIndex])

        private fun playNextSecond(): Boolean {
            if (++currentTime > getCurrentTrack().endTimestamp) nextTrack()
            return playJob?.isActive == true
        }

        private fun nextTrack() {
            currentTime = 0
            if (++currentTrackIndex >= tracks.size) {
                if (cycle == -1 || ++currentCycle <= cycle) {
                    currentTrackIndex = 0
                } else {
                    stop()
                }
            }
            resetSession()
            log("Switched to track: ${getCurrentTrack().name}")
        }

        fun onTrackRemoved(index: Int) {
            if (index == currentTrackIndex) {
                stop()
                currentTrackIndex = 0
                currentTime = 0
                if (tracks.isNotEmpty()) start()
            } else if (index < currentTrackIndex) {
                currentTrackIndex--
            }
        }

        fun onTrackMoved(from: Int, to: Int) {
            if (from == currentTrackIndex || to == currentTrackIndex) {
                stop()
                currentTime = 0
                start()
            }
        }

        private fun resetSession() {
            playSession = UUID.randomUUID().toString()
        }
    }

    private fun log(message: String) = logger.log(message)

    companion object {
        private val logger = Loggers.get("plasmo", "playback")
    }
}