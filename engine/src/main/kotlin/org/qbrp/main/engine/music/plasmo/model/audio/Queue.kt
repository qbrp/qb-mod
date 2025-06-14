package org.qbrp.main.engine.music.plasmo.model.audio

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.koin.core.context.GlobalContext
import org.qbrp.main.engine.music.plasmo.MusicStorage
import org.qbrp.main.core.utils.log.LoggerUtil

@JsonIgnoreProperties(ignoreUnknown = true)
open class Queue(
    var currentTrackIndex: Int = 0,
    var repeats: Int = 1,
    open val tracks: MutableList<String> = mutableListOf(), // Список имен треков
) {
    @JsonIgnore val storage: MusicStorage = GlobalContext.get().get()
    @JsonIgnore var currentRepeat = 0
    @JsonIgnore var onQueueFinished: () -> Unit = {}

    @JsonIgnore
    fun getCurrentTrack(): Track? {
        val trackName = tracks.getOrNull(currentTrackIndex) ?: return null
        return storage.getTrackOrThrow(trackName)
    }

    fun copy(): Queue {
        return Queue(currentTrackIndex, repeats, tracks)
    }

    fun next() {
        if (tracks.isNotEmpty()) {
            if (repeats == -1 || currentRepeat < repeats) {
                if (currentTrackIndex >= tracks.size - 1) {
                    if (repeats != -1) currentRepeat++
                    if (currentRepeat < repeats || repeats == -1) {
                        currentTrackIndex = 0
                    } else {
                        onQueueFinished()
                    }
                } else {
                    currentTrackIndex++
                }
            } else {
                onQueueFinished()
            }
        }
    }

    fun validateQueue() {
        currentTrackIndex = tracks.indexOfFirst {
            storage.getTrack(it)?.name == getCurrentTrack()?.name
        }
        currentTrackIndex.coerceIn(0 , tracks.size)
    }

    fun addTrack(trackName: String) {
        tracks.add(trackName)
        validateQueue()
    }

    fun removeTrack(index: Int) {
        try {
            if (index in tracks.indices) {
                tracks.removeAt(index)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        validateQueue()
    }

    fun moveTo(fromIndex: Int, toIndex: Int): Boolean {
        try {
            if (fromIndex !in tracks.indices || toIndex !in tracks.indices) {
                return false
            }

            val movedTrack = tracks[fromIndex]

            tracks.removeAt(fromIndex)
            tracks.add(toIndex, movedTrack)

            validateQueue()
            return true
        } catch (e: Exception) {
            logger.log("Error moving track: ${e.message}")

            validateQueue()
            return false
        }
    }

    fun setPosition(index: Int) {
        if (index in 0..tracks.size - 1) {
            currentTrackIndex = index
        }
        validateQueue()
    }

    fun clearQueue() {
        tracks.clear()
        currentTrackIndex = 0
    }

    companion object { val logger = LoggerUtil.get("musicManager", "playback")}

}
