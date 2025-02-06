package org.qbrp.engine.music.plasmo

import org.qbrp.engine.music.plasmo.model.audio.Playable
import org.qbrp.engine.music.plasmo.model.audio.Playlist
import org.qbrp.engine.music.plasmo.model.audio.Queue
import org.qbrp.engine.music.plasmo.model.audio.Track
import org.qbrp.engine.music.plasmo.model.priority.Priorities
import org.qbrp.engine.music.plasmo.model.priority.Priority
import org.qbrp.engine.music.plasmo.model.selectors.RegionSelector
import org.qbrp.engine.music.plasmo.model.selectors.Selector
import su.plo.voice.api.server.PlasmoVoiceServer
import kotlin.collections.forEach
import kotlin.concurrent.fixedRateTimer

class MusicStorage(val database: MusicDatabaseService, val priorities: Priorities, val voiceServer: PlasmoVoiceServer) {
    private val tracks = mutableMapOf<String, Track>()
    private val playable = mutableListOf<Playable>()

    fun loadFromDatabase() {
        try {
            database.openTracks()
                .forEach { track -> tracks[track.name] = track }
            database.openPlaylists(voiceServer).forEach {
                addPlaylist(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun startSaveLifecycle() {
        fixedRateTimer(
            name = "[qbrp/Plasmo] [MusicStorage]",
            initialDelay = 0,
            period = 10000,
            daemon = true
        ) {
            try {
                tracks.values.forEach { database.saveTrack(it) }
                playable.forEach { database.savePlaylist(it as Playlist) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addDefaultPlaylist() {
        if (tracks.isEmpty()) {
            createTrack("Made in Abyss", "https://kappa.vgmsite.com/soundtracks/made-in-abyss-ost/pmxedjjeon/1-01%20Made%20in%20Abyss.mp3", 1)
        }
        if (playable.isEmpty()) {
            addPlaylist("Глобальный", RegionSelector("world"), priorities.lowest(), -1)
            getDefaultPlaylist().queue.addTrack("Made in Abyss")
        }
    }

    fun isTrackExists(name: String): Boolean {
        return tracks.containsKey(name)
    }

    fun getTrack(name: String): Track = tracks[name]!!
    fun getAllTracks(): List<Track> = tracks.values.toList()
    fun deleteTrack(name: String) {
        database.archiveTrack(getTrack(name))
        tracks.remove(name)
    }
    fun getDefaultPlaylist(): Playlist = playable.first() as Playlist

    fun getAllPlayable(): List<Playable> = playable.toList()
    fun getPlayable(name: String): Playable? = playable.find { it.name == name }
    fun deletePlayable(name: String) {
        database.archivePlayable(getPlayable(name) as Playable)
        playable.removeIf { it.name == name }
    }
    fun changePlayableName(name: String, newName: String) {
        database.archivePlayable(getPlayable(name) as Playable)
        playable.find { it.name == name }?.name = newName
    }

    fun createTrack(name: String, link: String, cycle: Int = 1): Track {
        tracks[name] = Track(link, name, cycle).apply { setEndFromAudio() }
        return tracks[name]!!
    }

    fun addPlaylist(name: String, selector: Selector, priority: Priority, cycle: Int = -1): Playable {
        addPlaylist(Playlist(name, selector, priority, voiceServer).apply { initQueue(Queue(0, cycle)) })
        return getPlayable(name) as Playable
    }

    fun addPlaylist(playlist: Playlist) {
        playable.add(playlist)
    }
}