package org.qbrp.plasmo.contoller

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.qbrp.plasmo.model.Playable
import org.qbrp.plasmo.model.Region
import org.qbrp.plasmo.model.audio.Playlist
import org.qbrp.plasmo.model.audio.Track
import org.qbrp.plasmo.model.priority.Priorities
import org.qbrp.plasmo.model.priority.Priority
import org.qbrp.plasmo.model.selectors.Group
import org.qbrp.plasmo.model.selectors.RegionSelector
import org.qbrp.plasmo.model.selectors.Selector

object AddonStorage {
    private val tracks = mutableMapOf<String, Track>()
    val groups = mutableListOf<Group>()
    private val regions = mutableListOf<Region>(Region("global"))
    private val playable = mutableListOf<Playable>()
    private val playScope = CoroutineScope(Dispatchers.IO + Job())

    fun addDefaultPlaylist() {
        addPlaylist("Глобальный", RegionSelector("global"), Priorities.lowest())
    }

    fun addRegion(region: Region) = regions.add(region)
    fun getRegion(name: String): Region? { return regions.find { it.name == name } }
    fun removeRegion(region: Region) = regions.remove(region)

    fun isTrackExists(name: String): Boolean { return tracks.containsKey(name) }
    fun getTrack(name: String): Track = tracks[name]!!
    fun getDefaultPlaylist(): Playlist = playable.first() as Playlist

    fun getAllPlaylists(): List<Playable> = playable.toList()

    fun addTrack(name: String, link: String, cycle: Int = 1): Track {
        tracks[name] = (Track(link, name, cycle))
        return tracks[name]!!
    }

    fun addPlaylist(name: String, selector: Selector, priority: Priority, cycle: Int = -1) {
        playable.add(Playlist(name, selector, priority, cycle))
    }
}