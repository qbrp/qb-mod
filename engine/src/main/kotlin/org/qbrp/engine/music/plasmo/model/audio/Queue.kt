package org.qbrp.engine.music.plasmo.model.audio

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.context.GlobalContext
import org.qbrp.engine.Engine
import org.qbrp.engine.music.plasmo.MusicStorage
import org.qbrp.system.utils.log.Loggers

@JsonIgnoreProperties(ignoreUnknown = true)
open class Queue(
    var currentTrackIndex: Int = 0,
    var repeats: Int = 1,
    open val tracks: MutableList<String> = mutableListOf(), // Список имен треков
) {
    @JsonIgnore val storage: MusicStorage = GlobalContext.get().get()
    @JsonIgnore var currentRepeat = 0
    @JsonIgnore var onQueueFinished: () -> Unit = {}

    fun getCurrentTrack(): Track? {
        val trackName = tracks.getOrNull(currentTrackIndex) ?: return null
        return storage.getTrackOrThrow(trackName)
    }

    @JsonIgnore
    private val queueLock = Any()
    fun next() {
        synchronized(queueLock) {
            if (repeats == -1 || currentRepeat < repeats) {
                if (currentTrackIndex >= tracks.size - 1) {
                    currentTrackIndex = 0
                    if (repeats != -1) currentRepeat++
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

    companion object { val logger = Loggers.get("musicManager", "playback")}

}
