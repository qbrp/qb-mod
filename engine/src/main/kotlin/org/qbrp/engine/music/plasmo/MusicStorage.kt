package org.qbrp.engine.music.plasmo

import org.koin.core.parameter.parametersOf
import org.koin.core.component.get
import org.qbrp.engine.music.plasmo.model.audio.Playable
import org.qbrp.engine.music.plasmo.model.audio.Playlist
import org.qbrp.engine.music.plasmo.model.audio.Queue
import org.qbrp.engine.music.plasmo.model.audio.shadow.Shadow
import org.qbrp.engine.music.plasmo.model.audio.Track
import org.qbrp.engine.music.plasmo.model.priority.Priorities
import org.qbrp.engine.music.plasmo.model.priority.Priority
import org.qbrp.engine.music.plasmo.model.selectors.RegionSelector
import org.qbrp.engine.music.plasmo.model.selectors.Selector
import su.plo.voice.api.server.PlasmoVoiceServer
import kotlin.collections.forEach
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.concurrent.fixedRateTimer

class MusicStorage(val database: MusicDatabaseService,
                   val priorities: Priorities,
                   val voiceServer: PlasmoVoiceServer): KoinComponent {
    private val fabric: PlayableFabric = PlayableFabric(voiceServer, this)
    private val tracks = mutableMapOf<String, Track>()
    private val playable = mutableListOf<Playable>()

    fun loadFromDatabase() {
        try {
            database.openTracks()
                .forEach { track -> tracks[track.name] = track }
            database.openPlaylists()
                .sortedBy { it.type != "playlist" }
                .forEach { addPlaylist(fabric.build(it)) }
        } catch (e: Exception) {
            e.printStackTrace()
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

    fun getTrackOrThrow(name: String): Track = tracks[name]!!
    fun getTrack(name: String): Track? = tracks[name]
    fun getAllTracks(): List<Track> = tracks.values.toList()
    fun deleteTrack(name: String) {
        database.archiveTrack(getTrackOrThrow(name))
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
        val queue = get<Queue>(parameters = { parametersOf(0, cycle, mutableListOf<String>()) })
        val playlist = get<Playlist>(parameters = { parametersOf(name, selector, priority, queue) })
        addPlaylist(playlist)
        return getPlayable(name) as Playable
    }

    fun addShadow(originalName: String, name: String, selector: Selector, priority: Priority, cycle: Int = -1): Shadow {
        addPlaylist(Shadow(originalName, name, selector, priority, voiceServer, get()))
        return getPlayable(name) as Shadow
    }

    fun addPlaylist(playlist: Playable) {
        playable.add(playlist)
    }
}