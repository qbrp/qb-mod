package org.qbrp.engine.music.plasmo.model.audio

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.qbrp.engine.Engine
import org.qbrp.system.utils.log.Loggers
import su.plo.voice.api.server.PlasmoVoiceServer
import su.plo.voice.api.server.audio.source.ServerBroadcastSource

@JsonIgnoreProperties(ignoreUnknown = true)
data class Queue(
    var currentTrackIndex: Int = 0,
    var repeats: Int = 1,
    val tracks: MutableList<String> = mutableListOf(), // Список имен треков
) {
    @JsonIgnore lateinit var source: ServerBroadcastSource
    @JsonIgnore var radio: Radio? = null
    @JsonIgnore var onQueueFinished: () -> Unit = {}
    var currentRepeat = 0

    @JsonIgnore
    fun getCurrentTrack(): Track? {
        val trackName = tracks.getOrNull(currentTrackIndex) ?: return null
        return Engine.musicManagerModule.storage.getTrack(trackName)
    }

    fun checkPlayingTrack() {
        checkCurrentTrack()
        if (getCurrentTrack()?.name != radio?.track?.name) { clearRadio(); play() }
    }

    fun checkCurrentTrack() {
        if (tracks.size < currentTrackIndex) currentTrackIndex = tracks.size
    }

    fun clearRadio() {
        radio?.destroy(); radio = null }

    fun play() {
        if (tracks.isEmpty()) { return }
        radio?.audioPlayer?.isPaused?.let { if (!it) return }
        checkCurrentTrack()
        if (radio == null) createRadio(getCurrentTrack() as Track)
        else radio!!.resume()
    }

    fun createRadio(track: Track) {
        radio = Radio(source, track) {
            clearRadio()
            nextTrack()
        }
        logger.log("Создан проигрыватель: ${track.name}")
    }

    fun nextTrack() {
        if (repeats == -1 || currentRepeat < repeats) {
            if (currentTrackIndex >= tracks.size - 1) {
                currentTrackIndex = 0
                if (repeats != -1) currentRepeat++
            } else {
                currentTrackIndex++
            }
            play()
        } else {
            onQueueFinished.invoke()
        }
    }

    fun addTrack(trackName: String) = tracks.add(trackName)

    fun removeTrack(index: Int) {
        try {
            if (index in tracks.indices) {
                tracks.removeAt(index)
            }
            checkPlayingTrack()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun moveTo(fromIndex: Int, toIndex: Int): Boolean {
        try {
            if (fromIndex !in tracks.indices || toIndex !in tracks.indices) {
                return false
            }

            val movedTrack = tracks[fromIndex]

            tracks.removeAt(fromIndex)
            tracks.add(toIndex, movedTrack)

            checkPlayingTrack()

            return true
        } catch (e: Exception) {
            logger.log("Error moving track: ${e.message}")
            return false
        }
    }

    fun setPosition(index: Int) {
        if (index in 0..tracks.size - 1) {
            currentTrackIndex = index
            clearRadio(); play()
        }
    }

    fun clearQueue() {
        tracks.clear()
        currentTrackIndex = 0
    }

    companion object { val logger = Loggers.get("musicManager", "playback")}

}
