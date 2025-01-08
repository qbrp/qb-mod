package org.qbrp.plasmo.model.audio

import klite.Session
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.qbrp.plasmo.contoller.AddonStorage
import org.qbrp.plasmo.contoller.lavaplayer.AudioManager
import org.qbrp.plasmo.contoller.player.SourceManager
import org.qbrp.plasmo.model.Playable
import org.qbrp.plasmo.model.priority.Priorities
import org.qbrp.plasmo.model.priority.Priority
import org.qbrp.plasmo.model.selectors.RegionSelector
import org.qbrp.plasmo.model.selectors.Selector
import org.qbrp.system.utils.log.Loggers
import java.util.UUID

class Playlist(
    var name: String,
    override var selector: Selector = RegionSelector("global"),
    override var priority: Priority = Priorities.lowest(),
    override var cycle: Int = -1
) : Playable() {
    val tracks = mutableListOf<String>()
    @Transient
    private var playJob: Job? = null
    @Transient
    var playSession: String = UUID.randomUUID().toString()

    private var currentCycle = 0
    private var currentTrack = 0
    var currentTime = 0

    fun updateSession() {
        playSession = UUID.randomUUID().toString()
    }

    override fun play() {
        if (tracks.isEmpty() || playJob?.isActive == true) return
        log("Воспроизведение плейлиста $name начато")
        playJob = CoroutineScope(Dispatchers.Default).launch {
            while (playNextSecond()) { log("Время: $currentTime"); delay(1000) }
            log("Воспроизведение плейлиста $name завершено")
        }
    }

    override fun stop() {
        playJob?.cancel()
        playJob = null
        log("Воспроизведение плейлиста $name остановлено")
    }

    private fun playNextSecond(): Boolean {
        if (++currentTime > getCurrentTrack().endTimestamp) nextTrack()
        return playJob?.isActive == true
    }

    private fun nextTrack() {
        currentTime = 0
        if (++currentTrack >= tracks.size) {
            if (cycle == -1 || ++currentCycle <= cycle) {
                currentTrack = 0
            } else {
                currentTrack = 0
                stop()
            }
        }
        log("Плейлист $name переключился на трек: ${getCurrentTrack().name}")
        updateSession()
    }


    fun addTrack(name: String) { if (AddonStorage.isTrackExists(name)) tracks.add(name) }
    override fun getCurrentTrack(): Track = AddonStorage.getTrack(tracks[currentTrack])
    private fun log(message: String) = logger.log(message)

    companion object { val logger = Loggers.get("plasmo", "playback") }
}
